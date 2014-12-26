package learn.scala.ch8

import scala.io.Source

/**
 * Created by shawn on 14. 12. 23..
 */
object FunctionsAndClosures {

}

object LongLines {
  /**
   * 아래는 메소드 방식의 함수.
   */
  def processFile(filename: String, width: Int): Unit = {
    val source = Source.fromFile(filename)
    for (line <- source.getLines())
      processLine(filename, width, line)
  }

  /**
   * private으로 선언됨에 주의
   */
  private def processLine(filename: String, width: Int, line: String) {
    if (line.length > width)
      println(filename + ": " + line.trim)
  }
}

/**
 * main app using above LoneLines object.
 * $ scala FindLongLines 45 LongLines.scala
 * LongLines.scala: def processFile(filename: String, width: Int) {
 */
object FindLongLines {
  def main(args: Array[String]) {
    val width = args(0).toInt
    for (arg <- args.drop(1)) {
      LongLines.processFile(arg, width)
    }
  }
}

/**
 * Local functions
 * 작은 도움함수들을 감추기.
 * Java에서는 private 사용함. 스칼라는 함수안에 함수를 넣을 수 있음. 로컬변수처럼 로컬함수가 됨. 로컬함수는 밖에서 볼 수 없음.
 */
object LongLines2 {
  def processFile(filename: String, width: Int): Unit = {
    /**
     * 로컬함수로 바꾸었음.
     * private이 필요 없음.
     * filename: String, width: Int 파라미터도 필요 없음.
     */
    def processLine(line: String) {
      if (line.length > width)
        println(filename + ": " + line.trim)
    }
    val source = Source.fromFile(filename)
    for (line <- source.getLines())
      processLine(filename)
  }
}

/**
 * 일급함수.
 * function literal(source) -> function value(runtime)
 */
object FirstClassFunctionTest {
  def main(args: Array[String]) {
    testFunctionLiteral

    /**
     * collection의 foreach는 function을 인수로 받음.
     */
    val someNumbers = List(-11, -10, -5, 0, 5, 10)
    
    useFunctionAsParam(someNumbers)
    testPartiallyAppliedFunction(someNumbers)
    testClosures
  }

  def testFunctionLiteral = {
    /**
     * x를 x+1로 매핑하는 함수
     */
    var increase = (x: Int) => x + 1
    val inc1 = increase(10)

    /**
     * 변수에 다른 함수를 할당할 수도 있음.여러줄을 사용하려면 {} block 을 사용. 마지막 표현식이 리턴값.
     */
    increase = (x: Int) => {
      println("plus 9999")
      x + 9999
    }
    val inc2 = increase(10)

    println(inc1, inc2)
    /**
     * Every function value is an instance of some class that extends one of several FunctionN traits in package scala, 
     * such as Function0 for functions with no parameters, Function1 for functions with one parameter, and so on.
     * Each FunctionN trait has an apply method used to invoke the function. 
     */
  }

  def useFunctionAsParam(nums: List[Int]) {
    nums.foreach((x: Int) => println(x))

    // filter도 마찬가지.
    nums.filter((x:Int) => x > 0)

    // (x:Int) 대신 x 라고만 써도 된다. 타입을 지정 않았다. someNumbers로부터 유추 가능. This is called 'target typing'.
    nums.filter(x => x > 0)

    // this time using placeholder
    nums.filter(_ > 0)

    // '_' 를 사용시, 타입정보를 주어야 할 때가 있음.
    // val f = _ + _
    val f = (_:Int) + (_:Int)
    f(5,10)

    // Note that _ + _ 는 두개의 파라미터 함수로 변환됨.
    // 그래서 함수리터럴 내부에서 각 파라미터는 한 번만 나와야 됨.
    // 두개 이상의 언더스코어는 파라미터가 여러개라는 뜻임. (하나의 파라미터가 반복적으로 적용된다는 의미가 아님)
    // The first underscore represents the first parameter, the second underscore the second parameter,
    // the third underscore the third parameter, and so on.
  }

  def testPartiallyAppliedFunction(nums: List[Int]) {
    nums.foreach(println(_))
    nums.foreach(println _) // replaced an entire parameter list with an underscore
    nums.foreach(x => println(x)) // above equals with this code.
    
    // println _ 라고 썼을 때, 이것은 전체 파라미터 리스트를 _ 로 대체한 것임.
    
    // 아래 함수가 있을 때,
    def sum(a: Int, b: Int, c: Int) = a + b + c
    
    // apply sum function to 1,2,3
    sum(1,2,3)
    
    // sum에 파라미터를 제공하는 것이 아니고, _만 제공하면 이게 partially applied function.
    val a = sum _
    
    a(1,2,3)
    
    // above is a short form for:
    a.apply(1,2,3) // apply() invokes sum(1,2,3)
    
    // middle argument is missing.
    val b = sum(1, _: Int, 3)
    b(2) // 6
    b(5) // 9
    
    // 아래는 println _ 에서 _ 가 생략된 것. 모든 파라미터가 생략된 partially applied function의 경우(println _, sum _ 처럼), 
    // 언더스코어를 생략할 수 있다. 그리고 이런 용법은 함수가 필요한 자리에서만 가능하다.
    nums.foreach(println)
  }
  
  def testClosures {
    println("== testClosures ==")
    // (x: Int) => x + more
    // more: free variable, x: bound variable
    // 위 함수를 아무데서나 사용할 수는 없다. more 때문이다.
    var more = 1
    val addMore = (x:Int) => x + more
    println(addMore(10)) // 11
    
    // 위와 같은 function literal에 의해 실행시간에 만들어진 function value가 closure.
    // 자유변수를 캡처함으로써, function literal을 닫는다(closing)는 뜻.

    // free variable 값이 바뀌어도 바뀐 값을 가져온다.
    more = 9999
    println(addMore(10)) // 10009

    // Intuitively, Scala’s closures capture variables themselves, not the value to which variables refer.
    // 자바 inner class의 경우 final의 경우에만 허용하기 때문에 이 차이가 의미 없다.
    val someNumbers = List(-11, -10, -5, 0, 5, 10)
    var sum = 0
    someNumbers.foreach(sum += _)
    println(sum)

    // 아래에서 more는 closure 생성시 사용된 more가 유지된다.
    def makeIncreaser(more: Int) = (x: Int) => x + more
    val inc1 = makeIncreaser(1)
    val inc9999 = makeIncreaser(9999)
    println(inc1(10))
    println(inc9999(10))
  }
}

