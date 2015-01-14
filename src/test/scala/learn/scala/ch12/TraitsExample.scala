package learn.scala.ch12

import learn.scala.ch6.Rational
import org.scalatest.{Matchers, FlatSpec}

import scala.collection.mutable.ArrayBuffer

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

  abstract class IntQueue {
    def put(x: Int)
    def get(): Int
  }
  class BasicIntQueue extends IntQueue {
    private val buf = new ArrayBuffer[Int]
    override def put(x: Int): Unit = buf += x
    override def get(): Int = buf.remove(0)
  }

  /**
   * 1. trait가 IntQueue를 상속함에 주의. 이 trait는 IntQueue 하위 클래스에만 mixin 할 수 있음.
   * BasicIntQueue에만 mixin할 수 있고 Rational 클래스에는 안됨.
   * 2. abstract 메소드에서 super 호출을 하고 있음. 여기서 super는 동적바인딩 됨에 주의. super.put이 구현된 구체클래스에 mixin할 때에 성공함.
   * 그래서 abstract override 라고 지정함. abstract override는 trait에서만 가능함.
   */
  trait Doubling extends IntQueue {
    abstract override def put(x: Int) { super.put(x * 2) }
  }

  "Traits as stackable modification" should "trait가 추상클래스를 상속하고, 기존 메소드를 override함" in {
    // trait 없이 평이한 사용
    val queue = new BasicIntQueue
    queue.put(10)
    queue.put(20)
    queue.get() should be (10)
    queue.get() should be (20)

    // 이제 Doubling을 이용하자.
    class MyQueue extends BasicIntQueue with Doubling

    val myQueue = new MyQueue()

    myQueue.put(10)
    myQueue.get() should be (20)

    // MyQueue는 클래스 body가 없으므로 간단히 아래와 같이
    val oneLineQueue = new BasicIntQueue with Doubling
    oneLineQueue.put(20)
    oneLineQueue.get() should be (40)
  }

  trait Incrementing extends IntQueue {
    abstract override def put(x: Int) { super.put(x + 1) }
  }
  trait Filtering extends IntQueue {
    abstract override def put(x: Int) { if (x >= 0) super.put(x) }
  }
  it should "Incrementing, Filtering trait 구현" in {
      val queue = new BasicIntQueue with Incrementing with Filtering
      queue.put(-1); queue.put(0); queue.put(1);
      queue.get() should be (1)
      queue.get() should be (2)
  }
  it should "trait 순서는 중요함. 뒤쪽 trait가 먼저 호출됨." in {
    val queue = new BasicIntQueue with Filtering with Incrementing
    queue.put(-1); queue.put(0); queue.put(1);
    queue.get() should be (0)
    queue.get() should be (1)
    queue.get() should be (2)
  }
}
