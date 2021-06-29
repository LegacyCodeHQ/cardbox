package io.redgreen.testassist

import java.io.File
import java.io.InputStream

inline fun <reified T> getResourceStream(
  resourceFilePath: String
): InputStream {
  val separator = File.separator
  return T::class.java.getResourceAsStream("$separator$resourceFilePath")!!
}
