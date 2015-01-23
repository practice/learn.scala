package learn.scala.ch16

import org.scalatest.{Matchers, FlatSpec}

import scala.collection.mutable

/**
 * Created by shawn on 15. 1. 20..
 */
class WorkingWithLists extends FlatSpec with Matchers {
  "List literals" should "" in {
    /**
     * First, lists are immutable. That is, elements of a list cannot be changed by assignment.
     * Second, lists have a recursive structure (i.e., a linked list),1 whereas arrays are flat.
     */
    val fruit = List("apples", "oranges", "pears")
    val nums = List(1, 2, 3, 4)
    val diag3 =
      List(
        List(1, 0, 0),
        List(0, 1, 0),
        List(0, 0, 1)
      )
    val empty = List()
  }

  "The List type" should "" in {
    /**
     * homogeneous: the elements of a list all have the same type
     */
    /**
     * covariant: if S is a subtype of T, then List[S] is a subtype of List[T]
     * Note that the empty list has type List[Nothing].
     */
    val xs: List[String] = List()   // List[Nothing] is subtype of List[String]
  }

  "Constructing lists" should "" in {
    /**
     * Nil and ::(cons)
     */
    val fruit = "apples" :: ("oranges" :: ("pears" :: Nil))
    val nums = 1 :: (2 :: (3 :: (4 :: Nil)))
    val diag3 = (1 :: (0 :: (0 :: Nil))) ::
                (0 :: (1 :: (0 :: Nil))) ::
                (0 :: (0 :: (1 :: Nil))) :: Nil
    val empty = Nil

    /**
     * Because it ends in a colon, the :: operation associates to the right.
     * A :: B :: C is interpreted as A :: (B :: C). Therefore, you can drop the parentheses in the previous definitions.
     */
    val anotherNums = 1 :: 2 :: 3 :: 4 :: Nil
  }

  "Basic operations on lists" should "head, tail, isEmpty" in {
    /**
     * insertion sort
     */
    def isort(xs: List[Int]): List[Int] = {
      if (xs.isEmpty) Nil
      else insert(xs.head, isort(xs.tail))
    }

    def insert(x: Int, xs: List[Int]): List[Int] = {
      if (xs.isEmpty || x <= xs.head) x :: xs
      else xs.head :: insert(x, xs.tail)
    }

    isort(3::5::1::9::6::Nil) should be (1::3::5::6::9::Nil)
  }

  "List patterns" should "" in {
    val fruit = "apples" :: "oranges" :: "pears" :: Nil
    val List(a, b, c) = fruit   // matches lists of length 3, and binds the three elements to the pattern variables a, b, and c
    a should be ("apples")
    b should be ("oranges")
    c should be ("pears")
    val d :: e :: rest = fruit  // matches lists of length 2 or greater
    d should be ("apples")
    e should be ("oranges")
    rest should be ("pears" :: Nil)

    /**
     * TODO: read box article "About pattern matching on Lists"
     */

    /**
     * insertion sort implemented with pattern matching
     */
    def isort(xs: List[Int]): List[Int] = xs match {
      case List()   => List()
      case x :: xs1 => insert(x, isort(xs1))
    }
    def insert(x: Int, xs: List[Int]): List[Int] = xs match {
      case List() => List(x)
      case y :: ys => if (x <= y) x :: xs
      else y :: insert(x, ys)
    }
  }

  "16.6 First-order methods on class List" should "A method is first-order if it does not take any functions as arguments." in {
  }

  it should "Concatenating two lists" in {
    // use :::
    val l = List(1,2) ::: List(3,4,5)
    l should be (List(1,2,3,4,5))
    // xs ::: ys ::: zs is interpreted like this: xs ::: (ys ::: zs). associates to the right.
  }

  it should "The Divide and Conquer principle" in {
    // :::를 직접 구현해보자.
    def append[T](xs: List[T], ys: List[T]): List[T] = {
      xs match {
        case Nil => ys
        case x :: xs1 => x :: append(xs1, ys)
      }
    }
    append(List(1,2), List(3,4,5)) should be (List(1,2,3,4,5))
  }

  it should "Taking the length of a list: length" in {
    // On lists, unlike arrays, length is a relatively expensive operation.
    // It needs to traverse the whole list to find its end and therefore takes time proportional to the number of elements in the list.
    // That’s why it’s not a good idea to replace a test such as xs.isEmpty by xs.length == 0.
  }

  val abcde = List('a', 'b', 'c', 'd', 'e')

  it should "Accessing the end of a list: init and last" in {
    abcde.last should be ('e')
    abcde.init should be (List('a', 'b', 'c', 'd'))

    // Unlike head and tail, which both run in constant time, init and last need to traverse the whole list
    // to compute their result. They therefore take time proportional to the length of the list.
  }

  it should "Reversing lists: reverse" in {
    val xs = List(1,2,3,4)
    xs.reverse.init should be (xs.tail.reverse)   // reverse.init == tail.reverse
    xs.reverse.tail should be (xs.init.reverse)   // reverse.tail == init.reverse
    xs.reverse.head should be (xs.last)
    xs.reverse.last should be (xs.head)

    /**
     * reverse를 :::를 사용해서 구현.
     * 효율적이지 못함. ::: 시간은 앞 리스트의 길이에 비례함. 따라서,
     * n + (n-1) + ... 1 = (1+n)*n/2
     */
    def rev[T](xs: List[T]): List[T] = {
      xs match {
        case Nil => Nil
        case x :: xs1 => rev(xs1) ::: List(x)
      }
    }

    rev(xs) should be (List(4,3,2,1))
  }

  it should "Prefixes and suffixes: drop, take, and splitAt" in {
    // xs splitAt n equals (xs take n, xs drop n): a pair(Tuple2) of two lists
    val drop = abcde.drop(2)
    val take = abcde.take(2)
    abcde.splitAt(2) should be (take, drop)
  }

  it should "Element selection: apply and indices" in {
    abcde.apply(2) should be ('c')  // rare in Scala
    abcde(2) should be ('c')        // rare in Scala

    // One reason why random element selection is less popular for lists than for arrays is that xs(n) takes time proportional to the index n.
    // 실제 apply구현은 (xs.drop(n)).head

    /**
     * The indices method returns a list consisting of all valid indices of a given list
     */
    abcde.indices should be (0 until abcde.length)    // Range(0,1,2,3,4)
  }

  it should "Flattening a list of lists: flatten" in {
    // The flatten method takes a list of lists and flattens it out to a single list:
    // It can only be applied to lists whose elements are all lists. Trying to flatten any other list will give a compilation error.
    List(List(1, 2), List(3), List(), List(4, 5)).flatten should be (List(1,2,3,4,5))
    val fruit = List("apples", "oranges", "pears")
    fruit.map(_.toCharArray).flatten should be (List('a', 'p', 'p', 'l', 'e', 's', 'o', 'r', 'a', 'n', 'g', 'e', 's', 'p', 'e', 'a', 'r', 's'))
  }

  it should "Zipping lists: zip and unzip" in {
    // The zip operation takes two lists and forms a list of pairs:
    (abcde.indices zip abcde) should be (List((0, 'a'), (1, 'b'), (2, 'c'), (3, 'd'), (4, 'e')))
    val zipped = abcde zip List(1,2,3)
    zipped should be (List(('a', 1), ('b', 2), ('c', 3))) // in case of different length, unmatched elements are dropped.
    abcde.zipWithIndex should be (List(('a',0), ('b',1), ('c',2), ('d',3), ('e',4)))
    zipped.unzip should be ((List('a', 'b', 'c'), List(1,2,3)))
  }

  it should "Displaying lists: toString and mkString" in {
    abcde.mkString("[", ",", "]") should be ("[a,b,c,d,e]")
    val buf = new mutable.StringBuilder
    abcde.addString(buf, "(", ";", ")")
    buf.result() should be ("(a;b;c;d;e)")
  }

  it should "Converting lists: iterator, toArray, copyToArray" in {
    abcde.toArray should be (Array('a', 'b', 'c', 'd', 'e'))
    abcde.toArray.toList should be (abcde)

    // copyToArray
    val arr2 = new Array[Int](10)
    arr2 should be (Array(0,0,0,0,0,0,0,0,0,0))
    List(1,2,3).copyToArray(arr2, 3)
    arr2 should be (Array(0,0,0,1,2,3,0,0,0,0))

    // list iterator
    val it: Iterator[Char] = abcde.iterator
    it.next should be ('a')
    it.next should be ('b')
  }

  it should "Example: Merge sort" in {
    /**
     * Complexity: order (n log(n))
     */
    def msort[T](less: (T, T) => Boolean)(xs: List[T]): List[T] = {
      def merge(xs: List[T], ys: List[T]): List[T] = {
        (xs, ys) match {
          case (Nil, _) => ys
          case (_, Nil) => xs
          case (x :: xs1, y :: ys1) =>
            if (less(x, y)) x :: merge(xs1, ys)
            else y :: merge(xs, ys1)
        }
      }
      val n = xs.length / 2
      if (n == 0) xs
      else {
        val (ys, zs) = xs.splitAt(n)
        merge(msort(less)(ys), msort(less)(zs))
      }
    }
    msort((x: Int, y: Int) => x < y)(3::5::1::9::6::Nil) should be (1::3::5::6::9::Nil)

    // The msort function is a classical example of the currying
    val intSort = msort((x: Int, y: Int) => x < y) _
    val reverseIntSort = msort((x: Int, y: Int) => x > y) _

    val mixedInts = List(4, 1, 9, 0, 5, 8, 3, 6, 2, 7)
    intSort(mixedInts) should be (List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
    reverseIntSort(mixedInts) should be (List(9, 8, 7, 6, 5, 4, 3, 2, 1, 0))
  }

  "16.7 Higher-order methods on class List" should "" in {

  }
}
