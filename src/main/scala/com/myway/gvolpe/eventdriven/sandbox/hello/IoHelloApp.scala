package com.myway.gvolpe.eventdriven.sandbox.hello

import cats.effect.std.Console
import cats.Monad
import cats.effect.*
import cats.syntax.all.*

object IoHelloApp extends IOApp.Simple:

  def greet[F[_]: Monad](name: String): F[String] =
    Monad[F].pure(s"Hello, $name!")

  def program[F[_]: Monad](using C: Console[F]): F[Unit] =
    for
      _        <- C.print("What's your name? ")
      name     <- C.readLine
      greeting <- greet[F](name)
      _        <- C.println(greeting)
    yield ()

  override def run: IO[Unit] =
    program[IO]
