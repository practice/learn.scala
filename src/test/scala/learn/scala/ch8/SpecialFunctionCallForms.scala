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

  def whatIsType(args: String*): Boolean = {
    args.isInstanceOf[Array[String]]
    //      classOf[args].toString
  }

  it should "함수 안에서는 Array[String] 타입으로 나타난다고 함. 하지만 아래 코드는 false를 리턴함. " +
    "args가 Array[String]타입인지 판단하는 코드를 어케 넣지?" in {
    whatIsType("hello", "world") should be (false)
  }

  it should "반복 파라미터라고 해서 Array를 전달할 수는 없음. 대신 arr: _* 형식으로 전달해야 함." in {
    val arr = Array("What's", "up", "doc?")
    // whatIsType(arr) // 컴파일 오류.
    whatIsType(arr: _*)
  }

  "Named arguments" should "파라미터이름 = 값 형식으로 넘기면 됨." in {
    def speed(distance: Float, time: Float): Float = {
      distance / time
    }
    speed(time = 10, distance = 100) should be (10)
  }

  def printTime(prefix: String = "time="): String = {
    prefix + System.currentTimeMillis()
  }

  "default parameter values" should "" in {
    printTime().startsWith("time=") should be (true)
    printTime("mytime: ").startsWith("mytime: ") should be (true)
    printTime("mytime=").startsWith("mytime: ") should be (false)
  }

  def printTime2(prefix: String = "time=", postfix: String = "@twitter.com"): String = {
    prefix + System.currentTimeMillis() + postfix
  }

  "default parameter values combined with named arguments" should "" in {
    printTime().startsWith("time=") should be (true)
    printTime("mytime: ").startsWith("mytime: ") should be (true)
    printTime("mytime=").startsWith("mytime: ") should be (false)

    printTime2(prefix="mytime: ").startsWith("mytime: ") should be (true)
    printTime2(prefix="mytime: ").endsWith("@twitter.com") should be (true)
    printTime2(postfix="@facebook.com").endsWith("@facebook.com") should be (true)
  }

  "tail recursion" should "별로 적을게 없네." in {
    // 제약 2가지. (JVM의 한계 때문)
    // 1. 서로 교차하는 함수간 호출은 tail call optimization이 안됨.
    // 2. 리커전의 마지막 호출이 function value인 경우도 안됨. 그냥 자기 자신을 호출해야 함. 간접적으로 function value로 간 후, 결국 자신이 호출된다 하더라도 안됨.
    // val funVal = nestedFun _
    // def nestedFun(x: Int) {
    //   if (x != 0) { println(x); funValue(x - 1) }
    // }
  }
}
