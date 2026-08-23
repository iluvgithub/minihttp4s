package com.myway.gvolpe.eventdriven.sandbox.typeclass
// https://www.youtube.com/watch?v=ffzWhyS3Ovg
import cats.effect.{IO, IOApp, Sync}
import cats.implicits.toFunctorOps

import java.time.Instant

case class Timestamp(value: Instant)

trait Time[F[_]]:
  def timestamp: F[Timestamp]

object Time:

  given [F[_]: Sync]: Time[F] with

    def timestamp: F[Timestamp] =
      Sync[F].delay(Instant.now()).map(Timestamp(_))

object Main extends IOApp.Simple:

  def program: IO[Unit] =
    for
      timestamp <- summon[Time[IO]].timestamp
      _         <- IO.println(s"Current timestamp: ${timestamp.value}")
    yield ()

  def run: IO[Unit] =
    program
