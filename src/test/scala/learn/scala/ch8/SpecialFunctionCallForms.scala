package learn.scala.ch8

import org.scalatest.{FlatSpec, Matchers}

class SpecialFunctionCallForms extends FlatSpec with Matchers {
  "Repeated parameters" should "타입 뒤에 *를 넣어서 만듦" in {
    def echo(args: String*): Seq[String] = {
      val ret = for (arg <- args) yield arg
      ret
    }
    echo() should be (Array())
    echo("hello", "world") should be (Array("hello", "world"))
  }

  it should "함수 안에서는 Array[String] 타입으로 나타난다고 함. 하지만 아래 코드는 false를 리턴함. " +
    "args가 Array[String]타입인지 판단하는 코드를 어케 넣지?" in {
    def whatIsType(args: String*): Boolean = {
      args.isInstanceOf[Array[String]]
//      classOf[args].toString
    }
    whatIsType("hello", "world") should be (false)
  }
}
