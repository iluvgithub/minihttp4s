package com.myway.free

import com.myway.free.Bouncing.Bouncer
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.shouldBe

class BouncingTest extends AnyFunSuite:

  test("add sum"):
    // arrange
    def f0(x: Int): Bouncer[Long] = if (x <= 0) Bouncing.done(0L)
    else Bouncing.more(() => f0(x - 1)).map(_ + x)
    def f(x: Int): Long = Bouncing.solve(f0(x))
    val n               = 100000
    // act
    val out = f(n)
    // assert
    out shouldBe 1L * n * (n + 1) / 2
