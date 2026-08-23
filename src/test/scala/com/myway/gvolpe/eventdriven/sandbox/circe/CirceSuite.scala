package com.myway.gvolpe.eventdriven.sandbox.circe

import io.circe
import io.circe.parser.decode
import io.circe.syntax.*
import munit.CatsEffectSuite
import org.scalatest.matchers.should.Matchers.shouldBe
class CirceSuite extends CatsEffectSuite:

  test("to json"):
    // arrange
    val address = Address("Baker", 221, Some("B"))
    // act
    val jsonString = address.asJson.noSpaces
    // assert
    assertEquals(jsonString, "{\"streetName\":\"Baker\",\"streetNumber\":221,\"flat\":\"B\"}")

  test("from Json ok"):
    // arrange
    val jsonString0 = "{\"streetName\":\"Baker\",\"streetNumber\":220}"
    val jsonString  = "{\"streetName\":\"Baker\",\"streetNumber\":221,\"flat\":\"B\"}"
    // act
    val decoded0: Either[circe.Error, Address] = decode[Address](jsonString0)
    val decoded: Either[circe.Error, Address]  = decode[Address](jsonString)
    // assert
    assertEquals(decoded0, Right(Address("Baker", 220, None)))
    assertEquals(decoded, Right(Address("Baker", 221, Some("B"))))

  test("from Json not ok"):
    // arrange
    val jsonString = "{\"WRONG_FIELD\":\"Baker\",\"streetNumber\":221,\"flat\":\"B\"}"
    // act
    val decoded: Either[circe.Error, Address] = decode[Address](jsonString)
    // assert
    assertEquals(
      decoded.fold(_.toString, _ => ""),
      "DecodingFailure at .streetName: Missing required field"
    )

  test("from/to json extra case"):
    // arrange
    val person = Person(40, "Joe")
    val one    = Digits.One
    val two    = Digits.Two("dos")
    // act
    val jPerson = person.asJson.noSpaces
    val jOne    = one.asJson.noSpaces
    val jTwo    = two.asJson.noSpaces
    // assert
    assertEquals(jPerson, "{\"age\":40,\"name\":\"Joe\"}")
    assertEquals(jOne, "{\"One\":{}}")
    assertEquals(jTwo, "{\"Two\":{\"name\":\"dos\"}}")

    assertEquals(person, decode[Person](jPerson).toOption.get)
    assertEquals(one, decode[Digits](jOne).toOption.get)
    assertEquals(two, decode[Digits](jTwo).toOption.get)

    test("from json extra case"):
      // arrange
      val jsonString = "{\"age\":40,\"name\":\"Joe\"}"
      // act
      val out: Option[Person] = PersonSyntax.parseToPerson(jsonString)
      // assert
      out shouldBe Some(Person(30, "Joe"))
