package com.myway.gvolpe.eventdriven.sandbox.fs2.fsm
import cats.effect.IO
import fs2.Stream
import munit.CatsEffectSuite

class FsmSuite extends CatsEffectSuite:

  test("to json"):
    // arrange
    val n                                  = 5000
    val stream: Stream[IO, Int]            = Stream.emits[IO, Int](List.range(1, n + 1))
    val fsm: Fsm[IO, Int, Int, Long]       = Fsm((s, i) => IO.pure((s + 1, 0L + s * i)))
    val outStream: Stream[IO, (Int, Long)] = stream.evalMapAccumulate(1)(fsm.run)
    // act
    val ioList: IO[List[Long]] = outStream.compile.toList.map(_.map(_._2))
    // assert
    for list <- ioList
    yield assertEquals(
      list.sum,
      1L * n * (n + 1) * (2 * n + 1) / 6
    )
