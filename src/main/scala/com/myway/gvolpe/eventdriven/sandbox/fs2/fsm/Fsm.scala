package com.myway.gvolpe.eventdriven.sandbox.fs2.fsm
import cats.syntax.all.*
import cats.{Functor, Id}

case class Fsm[F[_], S, I, O](run: (S, I) => F[(S, O)]):
  def runS(using F: Functor[F]): (S, I) => F[S] =
    (s, i) => run(s, i).map(_._1)

object Fsm:
  def id[S, I, O](run: (S, I) => Id[(S, O)]) = Fsm(run)
