package io.sqooba.oss.timeseries.archive

import fi.iki.yak.ts.compression.gorilla.{GorillaCompressor, LongArrayOutput, Pair}
import io.sqooba.oss.timeseries.validation.TimestampValidator

import scala.collection.immutable.SortedMap
import scala.util.Success

/** Provides methods to construct and parse a GorillaArray. */
object GorillaArray {

  /**
    * Compresses a map of timestamps to longs. This is usually a raw timeseries
    * without the validities that are defined by TimeSeries of this library.
    *
    * @note No two entries should be further apart in time than 24h (if time is
    *       measured in milliseconds).
    *
    * @param map sorted tuples
    * @param blockTimestamp timestamp that is written in the header of the block
    * @return a gorilla encoded byte array
    */
  def compressTimestampTuples(map: SortedMap[Long, Long], blockTimestamp: Long): GorillaArray = {
    require(map.nonEmpty, "The map to compress needs to contain at least one element.")

    TimestampValidator.validateGorillaFirst(blockTimestamp, map.head._1)
    TimestampValidator.validateGorilla(map.toSeq.map(_._1))

    val output     = new LongArrayOutput()
    val compressor = new GorillaCompressor(blockTimestamp, output)

    map.foreach(elem => compressor.addValue(elem._1, elem._2))
    compressor.close()

    longArray2byteArray(output.getLongArray)
  }

  /** Like #compressTimestampTuples with the first entry's timestamp as block timestamp. */
  def compressTimestampTuples(map: SortedMap[Long, Long]): GorillaArray = {
    require(map.nonEmpty, "The map to compress needs to contain at least one element.")
    compressTimestampTuples(map, map.head._1)
  }

  /**
    * Decompresses a map of timestamps to longs that was compressed by
    * 'compressTimestampTuples'.
    *
    * @param array a gorilla encoded byte array
    * @return a sorted raw timeseries as a map
    */
  def decompressTimestampTuples(array: GorillaArray): SortedMap[Long, Long] = {
    val decompressor = wrapTryDecompressor(array)

    // the appending converts the stream to a map
    SortedMap[Long, Long]() ++ Stream
      .continually(decompressor.map(_.readPair()))
      .takeWhile {
        case Success(_: Pair) => true
        case Success(null)    => false
        case _ =>
          throw new IllegalArgumentException(s"The passed byte array is not a valid compressed timeseries.")
      }
      .map(t => t.get.getTimestamp -> t.get.getLongValue)
  }
}
