package learn.scala.ch15

import learn.scala.ch10.Element
import learn.scala.ch10.Element._
import org.scalatest.{Matchers, FlatSpec}

import scala.collection.immutable.{IndexedSeq}

/**
 * Created by shawn on 15. 1. 16..
 */

abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr

class CaseClassesAndPatternMatching extends FlatSpec with Matchers {
  "A simple example" should "factory method, val parameters, toString/hashCode/equals impl, copy method" in {

    class C // is the same as class C {}

    /**
     * case class adds a factory method with the name of the class
     */
    val v = Var("x")
    val op = BinOp("+", Number(1), v)

    /**
     * All arguments in the parameter list implicitly get a 'val' prefix, so they are maintained as fields.
     */
    v.name should be ("x")
    op.left should be (Number(1))

    /**
     * the compiler adds “natural” implementations of methods toString, hashCode, and equals to your class.
     * They will print, hash, and compare a whole tree consisting of the class and (recursively) all its arguments.
     */
    op.toString should be ("BinOp(+,Number(1.0),Var(x))")
    (op.right == Var("x")) should be (true)

    /**
     * 컴파일러가 copy 메소드를 추가한다. named parameter를 이용해서 바꾸고 싶은 부분만 지정하면 된다.
     */
    op.copy(operator = "-").toString should be ("BinOp(-,Number(1.0),Var(x))")
  }

  def simplifyTop(expr: Expr): Expr = expr match {
    case UnOp("-", UnOp("-", e)) => e   // double negation
    case BinOp("+", e, Number(0)) => e  // Adding zero
    case BinOp("*", e, Number(1)) => e  // multiplying by one
    case _ => expr
  }

  it should "pattern matching" in {
    simplifyTop(BinOp("*", Var("x"), Number(1))) should be (Var("x"))
  }

  "Kinds of patterns" should "Wildcard, Constant, Variable patterns" in {
    // a simple name starting with a lowercase letter is taken to be a pattern variable; all other references are taken to be constants
    import math.{E, Pi}
    val out = E match {
      case Pi => "strange math? Pi = " + Pi   // constant pattern used.
      case _ => "OK"
    }
    out.startsWith("strange") should be (false)

    val pi = Pi
    val out2 = E match {
      case pi => "strange math? Pi = " + Pi   // variable pattern used.
      // case _ => "OK"  // unreachable code
    }
    out2.startsWith("strange") should be (true)
  }

  it should "Constructor patterns" in {
    simplifyTop(BinOp("*", Var("x"), Number(1))) should be (Var("x"))
  }
  it should "Sequence patterns: case class 뿐 아니라 List, Array도 가능" in {
    def startsWithZero(expr: List[Int]): String = expr match {
      case List(0, _, _) => "found"
      case _ => "not found"
    }
    val expr1 = List(0, 4, 5)
    val expr2 = List(0, 4)
    startsWithZero(expr1) should be ("found")
    startsWithZero(expr2) should be ("not found")

    def startsWithZeroLong(expr: List[Int]): String = expr match {
      case List(0, _*) => "found"     // 임의 길이의 원소들을 매치하려면 마지막에 _* 사용
      case _ => "not found"
    }
    val expr3 = List(2, 3, 4, 5)
    startsWithZeroLong(expr1) should be ("found")
    startsWithZeroLong(expr2) should be ("found")
    startsWithZeroLong(expr3) should be ("not found")
  }

  it should "Tuple patterns" in {
    def tupleMatch(expr: Any) = expr match {
      case (a, b, c) => "3-tuple"
      case _ => "any"
    }
    tupleMatch(("a", 3, Var("x"))) should be ("3-tuple")
    tupleMatch(("a", 3)) should be ("any")
  }

  it should "Typed patterns: 타입 확인 및 캐스트" in {
    def generalSize(x: Any): Int = x match {
      case s: String => s.length
      case m: Map[_,_] => m.size
      case _ => -1
    }
    generalSize("abc") should be (3)
    generalSize(Map(1 -> 'a', 2 -> 'b')) should be (2)
    generalSize(math.Pi) should be (-1)
  }

  it should "type erasure in type patterns: Map[Int,Int]의 경우 [Int,Int]의 정보는 알 수 없다." in {
    def isIntIntMap(x: Any): Boolean = x match {
      case m: Map[Int,Int] => true    // Warning: non-variable type argument Int in type pattern scala.collection.immutable.Map[Int,Int] (the underlying of Map[Int,Int]) is unchecked since it is eliminated by erasure
      case _ => false
    }
    isIntIntMap(Map(1 -> 1)) should be (true)
    isIntIntMap(Map("abc" -> "abc")) should be (true) // [Int,Int]정보는 사라졌다.
  }

  it should "erasure모델은 배열의 경우는 예외다.(Java와 같다)" in {
    def isStringArray(x: Any): String = x match {
      case a: Array[String] => "yes"
      case _ => "no"
    }
    isStringArray(Array("abc")) should be ("yes")
    isStringArray(Array(1,2,3)) should be ("no")
  }

  it should "variable binding via the @ sign" in {
    def extractInnerAbs(x: Expr) = x match {
      case UnOp("abs", e @ UnOp("abs", _)) => e
      case _ =>
    }
    extractInnerAbs(UnOp("abs", UnOp("abs", Var("x")))) should be (UnOp("abs", Var("x")))
  }

  "Pattern guards" should "pattern 다음에 if로 시작함" in {
    /**
     * BinOp("+", Var("x"), Var("x")) 형식을 BinOp("*", Var("x"), Number(2)) 로 바꾸고 싶을 경우
     */
    def simplifyAdd(e: Expr): Expr = e match {
      // case BinOp("+", x, x) => BinOp("*", x, Number(2))   // This fails, because Scala restricts patterns to be linear: a pattern variable may only appear once in a pattern.
      case BinOp("+", x, y) if (x == y) => BinOp("*", x, Number(2))   // pattern guard를 추가함
      case _ => e
    }
  }

  "Pattern overlaps" should "패턴은 순서대로 적용됨. 순서가 중요함." in {
    def simplifyAll(expr: Expr): Expr = expr match {
      case UnOp("-", UnOp("-", e)) => simplifyAll(e)   // ‘-’ is its own inverse
      case BinOp("+", e, Number(0)) => simplifyAll(e)   // ‘0’ is a neutral element for ‘+’
      case BinOp("*", e, Number(1)) => simplifyAll(e)   // ‘1’ is a neutral element for ‘*’
      case UnOp(op, e) => UnOp(op, simplifyAll(e))
      case BinOp(op, l, r) => BinOp(op, simplifyAll(l), simplifyAll(r))
      case _ => expr
    }
  }

  "Sealed classes" should "" in {

  }

  "Patterns everywhere" should "patterns in variable definitions" in {
    val (number, string) = (123, "abc")
    number should be (123)
    string should be ("abc")

    val BinOp(op, left, right) = BinOp("*", Number(5), Number(1))
    op should be ("*")
    left should be (Number(5))
    right should be (Number(1))
  }

  it should "Case sequences as partial functions" in {
    def withDefault: Option[Int] => Int = {
      case Some(x) => x     // case 하나를 function literal로 볼 수 있다. 패턴은 파라미터, 오른쪽은 body.
      case None => 0
    }

    withDefault(Some(10)) should be (10)
    withDefault(None) should be (0)

    // a sequence of cases gives you a partial function. 이거 무슨 말인지 모르겠다.
    // see also http://twitter.github.io/scala_school/ko/pattern-matching-and-functional-composition.html
    // ...
  }

  it should "Patterns in for expressions: generated values that do not match the pattern are discarded" in {
    val capitals = Map("France" -> "Paris", "Japan" -> "Tokyo")
    val cities = for ((country, city) <- capitals) yield s"$country-$city"
    cities.size should be (2)
    cities.head should be ("France-Paris")
    cities.drop(1).head should be ("Japan-Tokyo")

    val results = List(Some("apple"), None, Some("orange"))
    val fruits = for (Some(fruit) <- results) yield fruit
    fruits.size should be (2)
    fruits.head should be ("apple")
    fruits.drop(1).head should be ("orange")
  }

  "A larger example" should "" in {
    class ExprFormatter {
      // Contains operators in groups of increasing precedence
      private val opGroups = Array(
        Set("|", "||"),
        Set("&", "&&"),
        Set("^"),
        Set("==", "!="),
        Set("<", "<=", ">", ">="),
        Set("+", "-"),
        Set("*", "%")
      )
      private val precedence = {
        val assocs: IndexedSeq[(String, Int)] = for {
          i <- 0 until opGroups.length
          op <- opGroups(i)
        } yield op -> i   // the association op -> i is nothing else but the pair (op, i)
        assocs.toMap
      }

      private val unaryPrecedence = opGroups.length
      private val fractionPrecedence = -1

      private def format(e: Expr, enclPrec: Int): Element =
        e match {
          case Var(name) =>
            elem(name)
          case Number(num) =>
            def stripDot(s: String) =
              if (s endsWith ".0") s.substring(0, s.length - 2)
              else s
            elem(stripDot(num.toString))
          case UnOp(op, arg) =>
            elem(op) beside format(arg, unaryPrecedence)
          case BinOp("/", left, right) =>
            val top = format(left, fractionPrecedence)
            val bot = format(right, fractionPrecedence)
            val line = elem('-', top.width max bot.width, 1)
            val frac = top above line above bot
            if (enclPrec != fractionPrecedence) frac
            else elem(" ") beside frac beside elem(" ")
          case BinOp(op, left, right) =>
            val opPrec = precedence(op)
            val l = format(left, opPrec)
            val r = format(right, opPrec + 1)
            val oper = l beside elem(" "+ op +" ") beside r
            if (enclPrec <= opPrec) oper
            else elem("(") beside oper beside elem(")")
        }
      def format(e: Expr): Element = format(e, 0)
    }
  }
}
