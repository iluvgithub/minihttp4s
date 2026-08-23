package com.myway.gvolpe.eventdriven.sandbox.kitten
import cats.*
import cats.derived.*

case class Person(
  name: String,
  age: Int
) derives Eq,
      Order,
      Show
