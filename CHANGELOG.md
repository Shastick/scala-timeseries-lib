# Change log

## 4.0.2 - 15.08.2019

- Update the groupid as well. 

## 4.0.1 - 14.08.2019

- Rename package to `io.sqooba.public.timeseries`. (Should have been part of 4.0.0...)

## 4.0.0 - 12.08.2019
This is a major release, as some constructors/build functions have been removed.

- Clean builders for both time-series implementations
- Introduce some tooling for (more) efficient windowed aggregations, migrate the integration logic to use it. More details in the `ReversibleAggregator` trait.
- Add column-based timeseries implementation.
- Fix somecompiler warnings.
- Cleanup some confusing or seldom used constructors.
- Add generic test bench for timeseries implementations.
- Add flag indicating (dis)-continuity to the `TimeSeries` trait, set it at build time
- Add strict merge operator to the trait
- Remove duplicate trim statement in merge.
- Implement nonStrictMinus for numeric timeseries.
- Removed some isEmpty checks in VectorTimeSeries.
- Add compress flag to timeseries builder.
- Add `isCompressed` flag to the interface.

## 3.3.3 
- Fix a bug with TSEntry's appendEntry function when the value was the same and reduced the previous entry's domain.

## 3.3.2 (same as 3.3.1, plus properly migrated sort calls)
- `TSEntry[T]` is now `@specialized`
- `TSEntry` now has its own ordering to (hopefully) avoid unnecessary boxing/unboxing

## 3.3.0
- `TimeSeries.ofOrderedEntriesUnsafe` now calls a constructor function on `VectorTimeSeries` that does not sort the passed entries, which was adding a non-negligible performance hit. There are still open questions about boxing/unboxing when sorting.

## 3.2.1
- Add 'entryAt' function to the `TimeSeries` interface

## 3.2.0

- Add bucketing functions to the `TimeSeries` interface

## 3.1.2

- Moving to `kaeter` to manage versioning, skipped 3.1.1.

## 3.1.0

### New features

- New `nonStrictPlus` function that will let you do sums over time domains that are not perfectly aligned
- New //discrete// trimming, slicing and splitting to avoid the modification of existing entries in a time series. Useful when dealing with aggregates for which splitting an entry makes no sense.

### Bug fixes:

- Resampling is not subject to stack overflows anymore

## 3.0.0

### Breaking retro-compatibility:

- `EmptyTimeSeries` is now an object instead of a class
- `TimeSeriesBuilder` has two significant changes:

    - It throws an exception if entries' timestamp are not strictly increasing
    - Its type has changed from `Builder[TSEntry, Vector[TSEntry]]` to `Builder[TSEntry, TimeSeries]`,
      thus the `result` method has a different signature

- `VectorTimeSeries`'s constructor is not publicly available anymore, methods of `TimeSeries` companion
  object should be used instead.
- `VectorTimeSeries` must contain at least two elements, otherwise `TSEntry` or `EmptyTimeSeries` should be used
- `TSValue` has been removed
- Removed parenthesis in methods having neither an argument nor a side-effect

### New features:

- Created `TimeDomain` to represent a set of timestamps using our conventions
- Created `looseDomain` method that returns an approximation of the domain. By loose-domain, we mean
  the tight-domain on which we know that the time-series is not defined outside but might be defined
  inside.
- Added `TimeSeries.fallback` which takes another time-series and returns another time-series which
  has the value of `this` if it's defined, and the value of the argument otherwise
- Added `isEmpty` and `nonEmpty` methods in time-series

### Bug fixes:

- `mergeEithers` fixed when dealing with time-series that have the same undefined internal domain
- `append`/`prepend` now return compressed time-series
