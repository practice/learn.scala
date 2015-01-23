package learn.scala.ch8

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by shawn on 15. 1. 21..
 */
class FunctionsAndClosures extends FlatSpec with Matchers {
  "8.6 Partially applied functions" should "개별 파라미터를 지칭하기 위해 _를 사용하기도 하지만 전체 파라미터 리스트를 _로 대체할 수 있다." in {
    val someNumbers = List(0,1,2,3,4,5)
    someNumbers.foreach(println(_))   // 개별 파라미터 대체
    someNumbers.foreach(println _)    // 전체 파라미터 대체. 함수 이름과 _ 사이에 스페이스가 필요하다. 붙여 쓰면 println_ 라는 이름의 함수 호출이 된다.

    // 전체 파라미터를 대체하는 용법은 partially applied function을 만드는 것이다.
    def sum(a: Int, b: Int, c: Int): Int = a + b + c
    // PAF는 파라미터의 일부만 제공하거나, 아예 제공하지 않는 것이다.
    /**
     * a는 생성된 function value를 가리키고, 이것은 파라미터가 3개인 apply 메소드가 있다.
     * a(1,2,3)은 apply메소드를 호출하는 것.
     */
    val a = sum _   // a: (Int, Int, Int) => Int = <function3>. 내부적으로 function value를 만들어서 a에 대입
    a.apply(1,2,3) should be (6)
    a(1,2,3) should be (6)  // a short form of above

    // Note: 일반적인 메소드나 내부 함수는 변수에 대입하거나 파라미터로 넘길 수 없지만 이렇게 function value로 감싸면 가능해 짐.

    // 이제 진짜 partially applied function이다.
    val b = sum(1, _: Int, 3) // b: (Int) => Int = <function1>
    b(2) should be (6)  // b.apply(2)

    // PAF를 만들 때, 함수가 와야 할 자리라면 _를 생략할 수 있다.
    someNumbers.foreach(println)    // instead of 'println _'
    // val c = sum     // compile errore
  }
}
