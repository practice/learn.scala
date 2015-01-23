package learn.scala.ch9

import java.io.{File, PrintWriter}
import java.util.Date

import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

object FileMatcher {
  private def filesHere = new File(".").listFiles()

  def filesEnding(query: String) = {
    for (file <- filesHere; if file.getName.endsWith(query))
      yield file
  }

  def filesContaining(query: String) =
    for (file <- filesHere; if file.getName.contains(query))
      yield file

  def filesRegex(query: String) =
    for (file <- filesHere; if file.getName.matches(query))
      yield file
}

object FileMatcher2 {
  private def filesHere = new File(".").listFiles()

  def filesEnding(query: String) = {
    filesMatching(query, (filename: String, query: String) => filename.endsWith(query))
  }

  def filesContaining(query: String) =
    filesMatching(query, (filename: String, query: String) => {
      filename.contains(query)
    })

  def filesRegex(query: String) = {
    filesMatching(query, _.matches(_)) // uses two bound variables. no free variables.
  }

  /**
   * function value를 인수로 받음.
   */
  def filesMatching(query: String, matcher: (String, String) => Boolean) = {
    for (file <- filesHere; if matcher(file.getName, query))
    yield file
  }
}

object FileMatcher3 {
  private def filesHere = new File(".").listFiles()

  def filesMatching3(matcher: String => Boolean) = {
    for (file <- filesHere; if matcher(file.getName)) // matcher()에서 query가 사라짐.
    yield file
  }

  def filesEnding(query: String) = {
    filesMatching3((filename: String) => filename.endsWith(query))
  }

  def filesContaining(query: String) =
    filesMatching3(_.contains(query)) // one bound variable: _, one free variable: query

  def filesRegex(query: String) = {
    filesMatching3(_.matches(query))
  }
}

class ControlAbstraction extends FlatSpec with Matchers {
  "Reducing code duplication" should "" in {
  }

  "Curring" should "()로 구분된 파라미터 목록을 여러개 사용하는 함수. ()가 여러개임." in {
    def curriedSum(x: Int)(y: Int) = x + y

    curriedSum(3)(8) should be (11)

    val addOne = curriedSum(1)_
    addOne(2) should be (3)

    // curriedSum()() 과 같은 형식은 두 번의 함수호출로 생각할 수 있다. 첫번째 호출은 x를 받아서 function value를 만들어준다.
    // 두번째 함수호출은 y를 받는다.
    // 하지만 여러개의 ()를 사용함으로써,
    // 좀 더 간단히 partially applied function을 만들 수 있게 해 준다.
    /**
     * 두번의 함수중 첫번째는 이런 모양의 호출로 볼 수 있다.
     */
    def first(x: Int) = (y: Int) => x + y // first: (x: Int)(Int) => Int
    /**
     * 두번째 함수
     */
    val second = first(1) // second: (Int) => Int = <function1>
    second(5) should be (6)

    val onePlus = curriedSum(1)_
    onePlus(2) should be (3)
  }

  "Writing new control structures" should "함수를 받는 함수를 만들면 된다" in {
    def twice(op: Double => Double, x: Double) = op(op(x))
    twice(_ + 1, 5) should be (7.0)
  }

  it should "loan pattern을 쓰는 경우" in {
    def withPrinterWriter(file: File, op: PrintWriter => Unit) {
      val writer = new PrintWriter(file)
      try {
        op(writer)
      } finally {
        writer.close
      }
    }
    val file = File.createTempFile("learn.scala", "tmp")
    withPrinterWriter(file, writer => writer.println("learning scala"))
    Source.fromFile(file).getLines().next() should be ("learning scala")
  }

  it should "파라미터가 하나인 경우 () 대신 {}를 쓸 수 있다. " +
    "또 currying을 사용하면 파라미터 리스트를 독자적인 ()의 파라미터로 만들 수 있으므로, ()를 {}로 대체할 수 있다." in {

    println ( "Hello world" )
    println { "Hello world" }

    def withPrinterWriter(file: File)(op: PrintWriter => Unit) {
      val writer = new PrintWriter(file)
      try {
        op(writer)
      } finally {
        writer.close
      }
    }
    // withPrinterWriter(file, writer => writer.println("learning scala"))
    // 이제 이렇게 호출할 수 있다.
    val file = File.createTempFile("learn.scala.2", "tmp")
    withPrinterWriter(file) {
      w => w.println("learning scala functional programming")
    }
    Source.fromFile(file).getLines().next() should be ("learning scala functional programming")
  }

  "By-name parameters" should "인자로 넘기는 함수가 인자를 가지지 않을 때 ()=> 부분을 없애 준다" in {
    var assertionsEnabled = true
    def myAssert(predicate: () => Boolean) {
      if (assertionsEnabled && !predicate())
        throw new AssertionError
    }
    myAssert(() => 5 > 3)
    // myAssert(5 > 3) // won't work. missing () =>

    def byNameAssert(predicate: => Boolean) {
      if (assertionsEnabled && !predicate)
        throw new AssertionError
    }
    // byNameAssert(() => 5 > 3)
    byNameAssert(5 > 3)
    // 위에서 넘기는 것은 function이라는 것에 주의. predicate: Boolean 이 아니고, predicate: => Boolean이다.
    // 그러므로 byNameAssert전에 계산되는 것이 아니라, byNameAssert 내부에서 실행됨. 즉 lazy eval.
    // 즉, a function value will be created whose apply method will evaluate 5 > 3, and this function value will be passed to byNameAssert.
    // 이 function value는 assertionsEnabled 값에 따라 호출될 수도 있고 안될 수도 있다.
  }
}
