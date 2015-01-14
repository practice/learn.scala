package learn.scala.ch6

/**
 * Created by shawn on 15. 1. 14..
 */
class Rational(n: Int, d: Int) extends Ordered[Rational] {
  require(d != 0)
  private val g = gcd(n.abs, d.abs)
  val numer = n / g
  val denom = d / g

  def this(n: Int) = this(n, 1) // auxiliary constructor
  override def toString = numer + "/" + denom
  def +(that: Rational): Rational = new Rational(numer * that.denom + that.numer * denom, denom * that.denom)
  def + (i: Int): Rational = new Rational(numer + i * denom, denom)
  def - (that: Rational): Rational = new Rational(numer * that.denom - that.numer * denom, denom * that.denom)
  def - (i: Int): Rational = new Rational(numer - i * denom, denom)
  def *(that: Rational): Rational = new Rational(numer * that.numer, denom * that.denom)
  def * (i: Int): Rational = new Rational(numer * i, denom)
  def / (that: Rational): Rational = new Rational(numer * that.denom, denom * that.numer)
  def / (i: Int): Rational = new Rational(numer, denom * i)

//  def lessThan(that: Rational) =
//  def < (that: Rational) = this.lessThan(that)
//  def > (that: Rational) = that < this
//  def <= (that: Rational) = (this < that) || (this == that)
//  def >= (that: Rational) = (this > that) || (this == that)
//  def max(that: Rational): Rational = if (this.lessThan(that)) that else this


  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

  override def compare(that: Rational): Int = (this.numer * that.denom) - (that.numer * this.denom)
}
