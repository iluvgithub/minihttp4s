package com.myway.gvolpe.eventdriven.sandbox.http

import cats.Monad
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.*
import com.comcast.ip4s.*
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint.Full
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

object HttpServerMain extends IOApp:

  private val simpleEndpoint0: Endpoint[Unit, Unit, Unit, String, Any] =
    makeSimpleEndpoint("")
  private val simpleEndpoint: Endpoint[Unit, Unit, Unit, String, Any] =
    makeSimpleEndpoint("simple")

  private def makeSimpleEndpoint(pageName:String): Endpoint[Unit, Unit, Unit, String, Any] =
    endpoint.get
      .in(pageName)
      .out(htmlBodyUtf8)
      .description("Returns a simple page")

  private final val HTTP_SIMPLE           = s"<html><body><h1> simple page </h1></body></html>"
  private val simpleRoute: HttpRoutes[IO] = make(simpleEndpoint, HTTP_SIMPLE)

  private final val HTTP_SIMPLE           = s"<html><body><h1>Go to /docs to see swagger</h1></body></html>"
  private val simpleRoute0: HttpRoutes[IO] = make(simpleEndpoint0, HTTP_SIMPLE)

  def make(
    endPoint: Endpoint[Unit, Unit, Unit, String, Any],
    httpContents: String
  ): HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(endPoint.serverLogicSuccess[IO] { name =>
      IO.pure(httpContents)
    })

  private val swaggerRoutes: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(
      SwaggerInterpreter().fromEndpoints[IO](
        simpleEndpoint0 :: simpleEndpoint :: SandboxRoutes[IO]().endPoints,
        "Mini API",
        "1.0"
      )
    )

  override def run(args: List[String]): IO[ExitCode] =
    for
      portAsString <- getPort(args)
      _            <- IO.println(s"Open server on port:$portAsString")
      server = EmberServerBuilder
        .default[IO]
        .withHost(host"0.0.0.0")
        .withPort(portAsString)
        .withHttpApp(
          Router(
            "/" -> simpleRoute0,
            "/" -> simpleRoute,
            "/" -> SandboxRoutes[IO]().routes,
            "/" -> swaggerRoutes
          ).orNotFound
        )
        .build
      _ <- IO.println(s"Ready server on port:$portAsString")
      _ <- server.useForever
    yield ExitCode.Success

  private def getPort(args: List[String]): IO[Port] =
    val defaultPort = port"8080"
    args.headOption match {
      case Some(portStr) =>
        IO.fromOption(Port.fromString(portStr))(
          new IllegalArgumentException(s"Invalid port number: $portStr")
        )
      case None =>
        IO.pure(defaultPort) // Fallback if no argument is passed
    }

case class IOEndPointRoute[F[_]: Monad, I, O](
  endPoint: PublicEndpoint[I, Unit, O, Any],
  service: I => F[O],
  isValid: O => Boolean
):
  def toFull: Full[Unit, Unit, I, Unit, O, Any, F] =
    endPoint.serverLogic[F](i =>
      service(i).map(o =>
        if (isValid(o))
          Right(o)
        else
          Left(new RuntimeException(s"Invalid output:$o from input:$i"))
      )
    )
