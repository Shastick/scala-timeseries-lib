package io.sqooba.public.timeseries.immutable

import io.sqooba.public.timeseries.TimeSeriesTestBench
import org.scalatest.{FlatSpec, Matchers}

class VectorTimeSeriesSpec extends FlatSpec with Matchers with TimeSeriesTestBench {

  "A VectorTimeSeries" should behave like nonEmptyNonSingletonTimeSeries(
    VectorTimeSeries.ofOrderedEntriesUnsafe(_),
    VectorTimeSeries.ofOrderedEntriesUnsafe(_)
  )

  // TODO add test for constructor using the 'ofEntriesSafe' function.
}
