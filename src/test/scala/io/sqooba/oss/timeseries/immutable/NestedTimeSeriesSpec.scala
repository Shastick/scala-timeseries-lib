package io.sqooba.oss.timeseries.immutable

import io.sqooba.oss.timeseries.bucketing.TimeBucketer
import io.sqooba.oss.timeseries.{TimeSeries, TimeSeriesTestBench}
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class NestedTimeSeriesSpec extends AnyFlatSpec with Matchers with TimeSeriesTestBench {

  private def bucketAndCreate[T](entries: Seq[TSEntry[T]]): TimeSeries[T] =
    NestedTimeSeries.ofOrderedEntriesSafe(
      TimeBucketer
        .bucketEntries(entries.toStream, Stream.from(-1000, 200).map(_.toLong), 3)
        .map(_.map(TimeSeries.ofOrderedEntriesSafe(_)))
        .filter(_.value.nonEmpty)
    )

  "A NestedTimeSeries" should behave like nonEmptyNonSingletonDoubleTimeSeries(bucketAndCreate)

  it should behave like nonEmptyNonSingletonGenericTimeSeries(bucketAndCreate)
}
