package com.myway.gvolpe.eventdriven.sandbox.kitten

import cats.*
import cats.syntax.all.*
import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.syntax.*
import munit.Assertions.assertEquals
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class PersonTest extends AnyFunSuite with Matchers:

  test("automated show"):
    // arrange
    val p = Person("HP Lovecraft ", 47)
    // act
    val out = p.show
    // assert
    out shouldBe "Person(name = HP Lovecraft , age = 47)"

  test("Person should support equality with Eq"):
    val alice1 = Person("Alice", 30)
    val alice2 = Person("Alice", 30)
    val bob    = Person("Bob", 30)

    assertEquals(alice1 === alice2, true)
    assertEquals(alice1 === bob, false)

  test("Person should be ordered by its fields"):
    val alice = Person("Alice", 30)
    val bob   = Person("Bob", 30)

    assert(Order[Person].compare(alice, bob) < 0)
    assert(Order[Person].compare(bob, alice) > 0)
    assert(Order[Person].compare(alice, alice) == 0)

    val cole29 = Person("Cole", 29)
    val cole30 = Person("Cole", 30)
    assert(Order[Person].compare(cole29, cole30) < 0)
    assert(Order[Person].compare(cole30, cole29) > 0)

  test("Person should have a Show instance"):
    val alice = Person("Alice", 30)

    assertEquals(
      alice.show,
      "Person(name = Alice, age = 30)"
    )

  test("Person encode"):
    // arrange
    val p = Person("HP Lovecraft ", 47)

    given Encoder[Person] = deriveEncoder[Person]

    // act
    val out: Json = p.asJson
    // assert
    out.noSpaces shouldBe "{\"name\":\"HP Lovecraft \",\"age\":47}"

  test("Person decode"):
    // arrange
    val jsonString = "{\"name\":\"HP Lovecraft \",  \"age\":47  }"
    val p          = Person("HP Lovecraft ", 47)
    import io.circe.parser.parse
    given Decoder[Person] = deriveDecoder[Person]

    val opt: Option[Json] = parse(jsonString).toOption
    // act
    val out = opt.flatMap(_.as[Person].toOption)
    // assert
    out shouldBe Some(p)

  test("Person decode direct"):
    // arrange
    val jsonString = "{\"name\":\"HP Lovecraft \",  \"age\":47  }"
    val p          = Person("HP Lovecraft ", 47)
    import io.circe.parser.parse
    given Decoder[Person] = deriveDecoder[Person]

    val opt: Option[Json] = parse(jsonString).toOption
    // act
    val out = opt.flatMap(_.as[Person].toOption)
    // assert
    out shouldBe Some(p)
