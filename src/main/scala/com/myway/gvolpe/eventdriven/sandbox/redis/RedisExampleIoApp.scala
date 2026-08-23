package com.myway.gvolpe.eventdriven.sandbox.redis

import cats.Monad
import cats.effect.*
import cats.effect.kernel.Resource
import cats.effect.std.Console
import cats.syntax.all.*
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import dev.profunktor.redis4cats.{Redis, RedisCommands}
object RedisExampleIoApp extends IOApp.Simple:
  // sudo systemctl start redis-server
  // ps aux | grep redis-server
  val redisConnection = "redis://localhost"
  val key             = "language"

  val resources: Resource[IO, RedisCommands[IO, String, String]] =
    for
      _ <- Resource.eval(IO.println(s"Preparing Redis environment:${redisConnection}"))
      redis <- Redis[IO]
        .utf8("redis://localhost")
        .evalTap(_ => IO.println(s"Redis connected${redisConnection}"))
        .onFinalize(IO.println(s"Redis released:${redisConnection}"))
    yield redis

  def run: IO[Unit] = resources.use(setGet)

  def setGet[F[_]: Monad: Console](redis: RedisCommands[F, String, String]): F[Unit] =
    setGet(redis, key, "ScalaNew")

  def setGet[F[_]: Monad: Console, K, V](redis: RedisCommands[F, K, V], k: K, v: V): F[Unit] =
    for
      _        <- redis.set(k, v)
      optValue <- redis.get(k)
      value = optValue.getOrElse(s"not found key:${k}")
      _ <- Console[F].println(s"Read from Redis: $value")
    yield ()
