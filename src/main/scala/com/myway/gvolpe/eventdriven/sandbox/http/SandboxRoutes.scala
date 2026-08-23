package com.myway.gvolpe.eventdriven.sandbox.http

import cats.Monad
import cats.effect.Async
import cats.effect.std.Console
import cats.implicits.*
import com.myway.gvolpe.eventdriven.sandbox.circe.PersonSyntax.parseToPerson
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import sttp.tapir.*
import sttp.tapir.server.http4s.Http4sServerInterpreter

final class SandboxRoutes[F[_]: Monad: Async: Console] extends Http4sDsl[F]:

  // Health  (GET)
  val healthEndPointRoute = IOEndPointRoute(
    endpoint.get
      .in("health")
      .out(emptyOutput)
      .description("Health check endpoint"),
    _ => Monad[F].pure(()),
    _ => true
  )

  // Hello (GET)
  def hello(s: String) =
    s"""<html>
       |  <body>
       |    <h1>Hello $s</h1>
       |  </body>
       |</html>""".stripMargin

  val helloEndPointRoute = IOEndPointRoute(
    endpoint.get
      .in("hello" / path[String]("name"))
      .out(htmlBodyUtf8)
      .description("Returns an HTML page saying hello"),
    name =>
      for x <- Monad[F].pure(name)
      yield hello(x),
    _ => true
  )

  // greet (POST)
  val greetPostEndpointRoute: IOEndPointRoute[F, String, String] =
    IOEndPointRoute[F, String, String](
      endpoint.post
        .in("greet")
        .in(stringBody)
        .out(htmlBodyUtf8)
        .description("Accepts a name in the POST body and returns HTML"),
      json =>
        for
          s   <- Monad[F].pure(json)
          out <- Monad[F].pure(parseToPerson(s).map(_.toString).getOrElse(""))
        yield hello(out),
      x => hello("").length < x.length
    )

  // ALL
  private val allIOEndPointRoute =
    List(healthEndPointRoute, helloEndPointRoute, greetPostEndpointRoute)

  def endPoints[X] = allIOEndPointRoute.map(_._1)

  private def endPointsFull = allIOEndPointRoute.map(_.toFull)

  def routes: HttpRoutes[F] = Http4sServerInterpreter[F]().toRoutes(endPointsFull)
