package com.myway.gvolpe.eventdriven.sandbox.circe

import io.circe.{Codec, Json}

trait Circe

case class Address(
  streetName: String,
  streetNumber: Int,
  flat: Option[String]
) derives Codec.AsObject

case class Person(
  age: Int,
  name: String
) derives Codec.AsObject

enum Digits derives Codec.AsObject:
  case One
  case Two(name: String)

object PersonSyntax:

  import io.circe.parser.parse
  def parseToPerson(jsonString: String): Option[Person] =
    for
      json   <- parse(jsonString).toOption
      person <- json.as[Person].toOption
    yield person
