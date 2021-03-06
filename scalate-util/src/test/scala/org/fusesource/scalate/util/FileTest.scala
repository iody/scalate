/**
 * Copyright (C) 2009-2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.scalate.util

import org.fusesource.scalate.FunSuiteSupport
import java.io.File
import IOUtil._

class FileTest extends FunSuiteSupport {
  test("using rich file API to navigate") {
    val f: File = baseDir

    val sources = f / "src" / "main" / "scala"

    assertResult(true) { sources.exists }

    val f2: File = sources
    assertResult(true) { f2.exists }

    info("created file: " + sources.file)
  }

  test("getting text of a file") {
    val file: File = baseDir / "src/test/resources/dummy.txt"

    val t = file.text.trim
    assertResult("hello world!") { t }

    info("Loaded file: " + file + " as text: " + t)
  }

  test("getting text of a file with include") {
    val file: File = baseDir / "src/test/resources/dummyWithInclude.txt"

    val t = IOUtil.loadTextFile(file).trim.replace("\r", "")

    assertResult("My header 1\nhello world!") { t }

    info("Loaded file: " + file + " as text: " + t)
  }

  test("getting text of a file with two includes") {
    val file: File = baseDir / "src/test/resources/dummyWithTwoIncludes.txt"

    val t = IOUtil.loadTextFile(file).trim.replace("\r", "")

    assertResult("My header 1\nMy Second Header\ngood bye world!") { t }

    info("Loaded file: " + file + " as text: " + t)
  }

  test("getting text of a file with include in the middle") {
    val file: File = baseDir / "src/test/resources/dummyWithIncludeInMiddle.txt"

    val t = IOUtil.loadTextFile(file).trim.replace("\r", "")

    assertResult("hello world!\nMy header 1\nAFTER WORLD!") { t }

    info("Loaded file: " + file + " as text: " + t)
  }

  test("getting text of a file with nested include") {
    val file: File = baseDir / "src/test/resources/dummyWithNestedInclude.txt"

    val t = IOUtil.loadTextFile(file).trim.replace("\r", "")

    assertResult("My header 1\nhello world!\nEnd of 2012 is here") { t }

    info("Loaded file: " + file + " as text: " + t)
  }

  test("working with names") {
    val file = baseDir / "foo.txt"

    info("name: " + file.name + " extension: " + file.extension)

    assertResult("txt", "extension") { file.extension }
    assertResult("foo", "nameDropExtension") { file.nameDropExtension }
  }

  test("Finding files") {
    assertResult(None) {
      baseDir.recursiveFind(_.name == "doesNotExist.xml")
    }

    assertResult(Some(new File(baseDir, "src"))) {
      baseDir.recursiveFind(_.name == "src")
    }

    assertResult(Some(new File(baseDir, "src/test/scala/org/fusesource/scalate/util/FileTest.scala"))) {
      baseDir.recursiveFind(_.name == "FileTest.scala")
    }
  }

  test("relative URIs") {
    assertResult("src") {
      (baseDir / "src").relativeUri(baseDir)
    }

    assertResult("src/test/scala/org/fusesource/scalate/util/FileTest.scala") {
      new File(baseDir, "src/test/scala/org/fusesource/scalate/util/FileTest.scala").relativeUri(baseDir)
    }
  }

  assertNameSplit("foo", "foo", "")
  assertNameSplit("foo.", "foo", "")
  assertNameSplit("foo.txt", "foo", "txt")
  assertNameSplit("foo.bar.txt", "foo.bar", "txt")
  assertNameSplit(".txt", "", "txt")
  assertNameSplit(".", "", "")

  def assertNameSplit(name: String, expectedName: String, expectedExt: String) {
    test("splitName: " + name) {
      info("Name " + name + " -> name: " + Files.dropExtension(name) + " extension: " + Files.extension(name))

      assertResult(expectedExt, "extension") { Files.extension(name) }
      assertResult(expectedName, "name without extension") { Files.dropExtension(name) }
    }
  }
}
