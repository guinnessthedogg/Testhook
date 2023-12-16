

@file:Suppress("unused", "NOTHING_TO_INLINE")
package com.xxx.zzz.aall.orgsss.jetbrains.ankos.jetbrains.collections


inline fun <T> List<T>.forEachByIndex(f: (T) -> Unit) {
    val lastIndex = size - 1
    for (i in 0..lastIndex) {
        f(get(i))
    }
}


inline fun <T> List<T>.forEachWithIndex(f: (Int, T) -> Unit) {
    val lastIndex = size - 1
    for (i in 0..lastIndex) {
        f(i, get(i))
    }
}


inline fun <T> List<T>.forEachReversedByIndex(f: (T) -> Unit) {
    var i = size - 1
    while (i >= 0) {
        f(get(i))
        i--
    }
}


inline fun <T> List<T>.forEachReversedWithIndex(f: (Int, T) -> Unit) {
    var i = size - 1
    while (i >= 0) {
        f(i, get(i))
        i--
    }
}


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("toKotlinPair()", "androidx.core.util.toKotlinPair"))
inline fun <F, S> android.util.Pair<F, S>.toKotlinPair(): Pair<F, S> = first to second


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("toAndroidPair()", "androidx.core.util.toAndroidPair"))
inline fun <F, S> Pair<F, S>.toAndroidPair(): android.util.Pair<F, S> = android.util.Pair(first, second)
