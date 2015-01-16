package learn.scala.ch15

import org.scalatest.{Matchers, FlatSpec}

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
     * the compiler adds a copy method to your class for making modified copies. 
     * You specify the changes you’d like to make by using named parameters.
     */
    op.copy(operator = "-").toString should be ("BinOp(-,Number(1.0),Var(x))")
  }

  it should "pattern matching" in {

  }
}
