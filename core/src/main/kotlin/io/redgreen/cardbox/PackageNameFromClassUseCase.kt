package io.redgreen.cardbox

import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.DefaultPackage
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.NotClassFile
import io.redgreen.cardbox.PackageNameFromClassUseCase.Result.PackageName
import java.io.EOFException
import java.io.InputStream
import org.apache.bcel.classfile.ClassParser

class PackageNameFromClassUseCase {
  companion object {
    private const val NONSENSE_FILE_NAME = "Applesauce.class"
  }

  fun invoke(classFileInputStream: InputStream): Result {
    val classParser = ClassParser(classFileInputStream, NONSENSE_FILE_NAME)
    return try {
      val packageName = classParser.parse().packageName
      if (packageName.isNotBlank()) {
        PackageName(packageName)
      } else {
        DefaultPackage
      }
    } catch (e: EOFException) {
      NotClassFile
    }
  }

  sealed class Result {
    data class PackageName(val value: String) : Result()
    object DefaultPackage : Result()
    object NotClassFile : Result()
  }
}
