package com.github.manosbatsis.scrudbeans.api.annotation

/**
 * Handy for use with the No-arg compiler plugin,
 * see https://kotlinlang.org/docs/no-arg-plugin.html
 *
 * Typically used on data classes for compatibility with
 * JPA, Jackson, Spring config properties and other plumbing.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class NoArg
