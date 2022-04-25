package com.github.manosbatsis.scrudbeans.extensions

val String.isValid: Boolean
	get() = this.isNotBlank() && this.isNotEmpty()

fun String.toList(vararg strings: String): List<String> {
	return listOf(*strings)
}