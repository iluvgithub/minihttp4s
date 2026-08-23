package com.myway.gvolpe.eventdriven.sandbox.http

import cats.effect.IO
import munit.CatsEffectSuite
import munit.Clue.generate
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.Method.*
import org.http4s.Status.*
import org.http4s.client.Client
import org.http4s.client.dsl.io.*
import org.http4s.headers.`Content-Type`
import org.typelevel.ci.CIString

class SandboxRoutesTest extends CatsEffectSuite:

  private val routes =
    new SandboxRoutes[IO].routes.orNotFound

  private val client =
    Client.fromHttpApp(routes)

  test("GET /health should return 200"):

    client
      .run(Request[IO](GET, uri"/health"))
      .use { response =>
        for
          _ <- IO(())
          _ = assertEquals(response.status, Ok)
        yield ()
      }

  test("GET /hello/:name should return an HTML greeting"):

    client
      .run(Request[IO](GET, uri"/hello/Pascal"))
      .use { response =>
        for
          body <- response.as[String]

          _ = assertEquals(response.status, Ok)
          _ = assert(body.contains("<h1>Hello Pascal</h1>"))
          _ = assert(body.contains("<html>"))
          _ = assert(body.contains("</html>"))
        yield ()
      }

  test("POST /greet should parse a Person and return an HTML greeting"):

    val request =
      Request[IO](POST, uri"/greet")
        .withEntity("""{"name":"Pascal","age":42}""")
        .putHeaders(
          Header.Raw(
            CIString("Content-Type"),
            "application/json"
          )
        )

    client
      .run(request)
      .use { response =>
        for
          body <- response.as[String]

          _ = assertEquals(response.status, Ok)
          _ = assert(body.contains("<h1>Hello"))
          _ = assert(body.contains("Person(42,Pascal)"))
        yield ()
      }

  test("POST /greet should return error when pushing wrong string"):

    val request =
      Request[IO](POST, uri"/greet")
        .withEntity("""{"xxx":"yyy","age":42}""")
        .putHeaders(
          Header.Raw(
            CIString("Content-Type"),
            "application/json"
          )
        )

    client
      .run(request)
      .use { response =>
        for
          body <- response.as[String]
          _ = assertEquals(response.status, BadRequest)
        yield ()
      }
