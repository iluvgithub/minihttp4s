package com.myway.free

import cats.*
import cats.free.FreeT

object Bouncing:

  type Bouncer[A] = FreeT[Function0, Id, A]

  def done[A](a: A): Bouncer[A] = FreeT.pure(a)

  def more[A](k: () => Bouncer[A]): Bouncer[A] = FreeT.roll(k)

  private val interpretation: Function0 ~> Id = new ~>[Function0, Id]:
    def apply[X](fx: () => X): X = fx()

  def solve[A](b: Bouncer[A]): A = b.runM(x => interpretation(x))
