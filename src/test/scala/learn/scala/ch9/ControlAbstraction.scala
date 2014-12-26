package learn.scala.ch9

import java.io.File

import org.scalatest.{FlatSpec, Matchers}

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
}
