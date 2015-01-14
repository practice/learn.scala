package learn.scala.ch12

import learn.scala.ch6.Rational
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by shawn on 15. 1. 13..
 */
class TraitsExample extends FlatSpec with Matchers {
  trait Philosophical {
    def philosophize = "I consume memory, therefore I am!"
  }

  "How traits work" should "extends, with를 사용, " in {

    class Frog extends Philosophical {
      override def toString = "green"
    }
    new Frog().philosophize should be ("I consume memory, therefore I am!")
  }

  it should "traits의 메소드를 override할 수도 있다. " +
    "traits는 java interface와 유사하다. " +
    "필드와 상태를 가질 수 있다. " +
    "traits는 클래스와 더 유사하다. 하지만 클래스 파라미터를 가질 수는 없다." in {
    class Animal
    trait HasLeg
    class Frog2 extends Animal with Philosophical with HasLeg {
      override def toString = "green"
      override def philosophize = "It ain't easy being " + toString + "!"
    }
    new Frog2().philosophize should be ("It ain't easy being green!")

    // trait NoPoint(x: Int, y: Int) // Does not compile
  }

  it should "클래스의 super 호출은 정적으로 bind된다. traits는 동적으로 bind됨." +
  "클래스의 경우 super가 어떤 메소드를 호출하는지 알 수 있지만 trait는 구체클래스에 mixed in 될 때 결정된다." in {
  }

  "Thin versus rich interfaces" should "One major use of traits is to automatically add methods to a class in terms of methods the class already has. " +
    "That is, traits can enrich a thin interface, making it into a rich interface." +
    "To enrich an interface using traits, simply define a trait with a small number of " +
    "abstract methods - the thin part of the trait’s interface - and a potentially large " +
    "number of concrete methods, all implemented in terms of the abstract methods. " +
    "Then you can mix the enrichment trait into a class, implement the thin portion of the interface, " +
    "and end up with a class that has all of the rich interface available." in {
  }

  "Example Rectangular Objects" should "아래 코드에서 left, right, width...같은 중복을 enrichment trait을 이용하여 제거해보자." in {
    class Point(val x: Int, val y: Int)
    class RectangleNoTrait(val topLeft: Point, val bottomRight: Point) {
      def left = topLeft.x
      def right = bottomRight.x
      def width = right - left
      // and many more geometric methods...
    }
    abstract class ComponentNoTrait {
      def topLeft: Point
      def bottomRight: Point

      def left = topLeft.x
      def right = bottomRight.x
      def width = right - left
      // and many more geometric methods...
    }
  }

  it should "super class를 만들듯이 trait를 만들면 된다." in {
    class Point(val x: Int, val y: Int)
    trait Rectangular {
      def topLeft: Point
      def bottomRight: Point

      def left = topLeft.x
      def right = bottomRight.x
      def width = right - left
      // and many more ...
    }

    abstract class Component extends Rectangular {
      // ...
    }

    class Rectangle(val topLeft: Point, val bottomRight: Point) extends Rectangular {
      // ...
    }

    val rect: Rectangle = new Rectangle(new Point(1,1), new Point(10,10))
    rect.left should be (1)
    rect.right should be (10)
    rect.width should be (9)
  }

  "The Ordered trait" should "Rational 클래스가 Ordered trait를 상속함" in {
    val half = new Rational(1,2)
    val third = new Rational(1,3)
    (half < third) should be (false)
    (half > third) should be (true)
  }

  
}
