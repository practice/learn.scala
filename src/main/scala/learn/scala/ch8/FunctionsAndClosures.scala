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
      processLine(filename, width, line)
  }
}

