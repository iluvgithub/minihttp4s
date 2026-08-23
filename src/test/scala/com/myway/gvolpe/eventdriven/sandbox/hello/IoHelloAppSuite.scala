package com.myway.gvolpe.eventdriven.sandbox.hello

import cats.Show
import cats.effect.*
import cats.effect.std.Console
import munit.CatsEffectSuite

import java.nio.charset.Charset

class IoHelloAppSuite extends CatsEffectSuite:

  test("greet returns the expected greeting"):
    IoHelloApp.greet[IO]("Alice").map { result =>
      assertEquals(result, "Hello, Alice!")
    }

  test("greet returns the expected greeting"):
    for
      out <- Ref.of[IO, Vector[String]](Vector.empty)
      console           = TestConsole("Alice", out)
      given Console[IO] = console
      _       <- IoHelloApp.program[IO]
      printed <- out.get
    yield assertEquals(
      printed,
      Vector(
        "What's your name? ",
        "Hello, Alice!"
      )
    )

final case class TestConsole(
  input: String,
  output: Ref[IO, Vector[String]]
) extends Console[IO] {

  override def readLine: IO[String] =
    IO.pure(input)

  override def print[A](a: A)(implicit S: Show[A]): IO[Unit] =
    output.update(_ :+ a.toString)

  override def println[A](a: A)(implicit S: Show[A]): IO[Unit] =
    output.update(_ :+ a.toString)

  override def error[A](a: A)(implicit S: Show[A]): IO[Unit] =
    IO.unit

  override def errorln[A](a: A)(implicit S: Show[A]): IO[Unit] =
    IO.unit

  override def readLineWithCharset(charset: Charset): IO[String] = ???
}
