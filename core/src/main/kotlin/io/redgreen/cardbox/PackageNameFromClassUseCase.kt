package io.redgreen.cardbox

import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.NotClassFile
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.EOFException
import java.io.File
import java.io.InputStream
import org.apache.bcel.classfile.ClassFormatException
import org.apache.bcel.classfile.ClassParser

/**
 * Retrieves the package name of a compiled .class file from an `InputStream`.
 */
class PackageNameFromClassUseCase {
  fun invoke(classFileInputStream: InputStream): Result {
    val classParser = ClassParser(classFileInputStream, NONSENSE_FILE_NAME)
    return try {
      val packageName = classParser.parse().packageName
      if (packageName.isNotBlank()) {
        PackageName(packageName)
      } else {
        DefaultPackage
      }
    } catch (e: ClassFormatException) {
      NotClassFile
    } catch (e: EOFException) {
      NotClassFile
    }
  }

  sealed class Result {
    data class PackageName(val value: String) : Result() {
      fun toPathSegment(): String =
        value.replace(DOT, SEPARATOR)

      companion object {
        private const val DOT = '.'
        private val SEPARATOR = File.separatorChar
      }
    }

    object DefaultPackage : Result() {
      private val simpleName by lazy { this::class.java.simpleName }

      override fun toString(): String {
        return simpleName
      }
    }

    object NotClassFile : Result() {
      private val simpleName by lazy { this::class.java.simpleName }

      override fun toString(): String {
        return simpleName
      }
    }
  }

  companion object {
    private const val NONSENSE_FILE_NAME = "Applesauce.class"
  }
}
