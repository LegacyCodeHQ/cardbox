package com.legacycode.cardbox

import com.legacycode.cardbox.model.PackageNameResult
import com.legacycode.cardbox.model.PackageNameResult.DefaultPackage
import com.legacycode.cardbox.model.PackageNameResult.NotClassFile
import com.legacycode.cardbox.model.PackageNameResult.PackageName
import java.io.EOFException
import java.io.InputStream
import org.apache.bcel.classfile.ClassFormatException
import org.apache.bcel.classfile.ClassParser

/**
 * Retrieves the package name of a compiled .class file from an `InputStream`.
 */
class PackageNameFromClassUseCase {
  companion object {
    private const val NONSENSE_FILE_NAME = "Applesauce.class"
  }

  fun invoke(classFileInputStream: InputStream): PackageNameResult {
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
}
