package com.legacycode.cardbox.model

import java.io.File

sealed class PackageNameResult {
  val displayText: String
    get() = when (this) {
      is PackageName -> value
      DefaultPackage -> "(default package)"
      NotClassFile -> "Uh oh! Not a class file."
    }

  data class PackageName(val value: String) : PackageNameResult() {
    companion object {
      private const val DOT = '.'
      private val SEPARATOR = File.separatorChar
    }

    fun toPathSegment(): String =
      value.replace(DOT, SEPARATOR)
  }

  object DefaultPackage : PackageNameResult() {
    private val simpleName by lazy { this::class.java.simpleName }

    override fun toString(): String {
      return simpleName
    }
  }

  object NotClassFile : PackageNameResult() {
    private val simpleName by lazy { this::class.java.simpleName }

    override fun toString(): String {
      return simpleName
    }
  }
}
