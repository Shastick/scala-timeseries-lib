package io.sqooba.oss.timeseries.window

import java.util.concurrent.TimeUnit

import io.sqooba.oss.timeseries.immutable.TSEntry
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.immutable.Queue

class IntegralAggregatorSpec extends FlatSpec with Matchers {

  "An IntegratingAggregatorSpec" should "be initialized at 0 by default" in {
    new IntegralAggregator[Double](TimeUnit.MILLISECONDS).currentValue shouldBe Some(.0)
  }

  it should "properly take the passed initial value into account" in {
    new IntegralAggregator[Double](TimeUnit.MILLISECONDS, 42.0).currentValue shouldBe Some(42.0)
  }

  it should "ignore the current windows content when adding an entry" in {
    new IntegralAggregator[Double](TimeUnit.MILLISECONDS, 42.0)
      .addEntry(TSEntry(0, .0, 1), null)
  }

  it should "properly rely on the passed entry's integral function" in {
    val aggSec = new IntegralAggregator[Double](TimeUnit.SECONDS)

    aggSec.addEntry(TSEntry(0, 10, 1), Queue())
    aggSec.currentValue shouldBe Some(10.0)

    aggSec.dropHead(Queue(TSEntry(0, 20, 1), TSEntry(1, 10, 1)))
    aggSec.currentValue shouldBe Some(-10.0)

    val aggMs = new IntegralAggregator[Double](TimeUnit.MILLISECONDS)
    aggMs.addEntry(TSEntry(0, 10, 100), Queue())
    aggMs.currentValue shouldBe Some(1.0)

    aggMs.dropEntry(TSEntry(0, 20, 100))
    aggMs.currentValue shouldBe Some(-1.0)
  }

}
