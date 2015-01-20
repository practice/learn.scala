package learn.scala.ch10

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by shawn on 15. 1. 20..
 */

object Element {
  /**
   * Note that now the contents parameter is prefixed by val. This is a shorthand that defines at the same time a parameter and field with the same name.
   * @param contents
   */
  private class ArrayElement(val contents: Array[String]) extends Element {   // The override modifier is optional if a member implements an abstract member with the same name.
  }

  private class LineElement(s: String) extends ArrayElement(Array(s)) {
    override def height = 1     // height and width in class LineElement override concrete definitions in class Element, override is required.
    override def width = s.length
  }

  private class UniformElement(ch: Char, override val width: Int, override val height: Int) extends Element {
    private val line = ch.toString * width
    def contents = Array.fill(height)(line)
  }

  def elem(contents: Array[String]): Element = new ArrayElement(contents)
  def elem(ch: Char, width: Int, height: Int): Element = new UniformElement(ch, width, height)
  def elem(line: String): Element = new LineElement(line)
}

abstract class Element {
  import Element.elem
  def contents: Array[String]
  def height: Int = contents.length
  def width: Int = if (height == 0) 0 else contents(0).length

  def above(that: Element): Element = elem(this.contents ++ that.contents)
  def beside(that: Element): Element = {
//    val cnts: Array[String] = new Array[String](this.contents.length)
//    for (i <- 0 until this.contents.length) cnts(i) = this.contents(i) + that.contents(i)
    elem(
      for (
        (line1, line2) <- this.contents zip that.contents
      ) yield line1 + line2
    )
  }

  def widen(w: Int): Element =
    if (w <= width) this
    else {
      val left = elem(' ', (w - width) / 2, height)
      val right = elem(' ', w - width - left.width, height)
      left beside this beside right
    }
  def heighten(h: Int): Element =
    if (h <= height) this
    else {
      val top = elem(' ', width, (h - height) / 2)
      val bot = elem(' ', width, h - height - top.height)
      top above this above bot
    }

  override def toString = contents.mkString("\n")
}

class CompositionAndInheritance extends FlatSpec with Matchers {
  import Element.elem
  "Polymorphism and dynamic binding" should "" in {
    elem('A', 2, 3).contents should be (Array("AA", "AA", "AA"))
  }

  "Implementing above, beside, and toString" should "" in {

  }

  "Putting it all together" should "" in {
    // Spiral object
  }
}
