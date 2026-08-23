package com.myway.gvolpe.eventdriven.sandbox.typeclass

import cats.effect.*
import munit.CatsEffectSuite
class TimestampTest extends CatsEffectSuite:

  test("timestamp"):
    for
      before <- IO.realTimeInstant
      actual <- summon[Time[IO]].timestamp
      after  <- IO.realTimeInstant
    yield
      assert(actual.value.compareTo(before) >= 0)
      assert(actual.value.compareTo(after) <= 0)
