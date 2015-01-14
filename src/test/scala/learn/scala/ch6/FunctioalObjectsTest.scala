package learn.scala.ch6

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by shawn on 15. 1. 14..
 */
class FunctioalObjectsTest extends FlatSpec with Matchers {
  "Rational class example" should "test Rational class" in {
    val x = new Rational(1,2)
    val y = new Rational(2,3)

    x.toString should be ("1/2")
//    new Rational(1,2).lessThan(new Rational(2,3)) should be (true)
    x < y should be (true)
    new Rational(66,42).toString should be ("11/7")

    (x + y).toString should be ("7/6")
    (x + x * y).toString should be ("5/6")
    ((x + x) * y).toString should be ("2/3")
    (x + (x * y)).toString should be ("5/6")
    (y * 2).toString should be ("4/3")
//    (2 * y).toString
  }

}
