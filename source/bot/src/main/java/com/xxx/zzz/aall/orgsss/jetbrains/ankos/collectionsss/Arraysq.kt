

@file:Suppress("unused", "NOTHING_TO_INLINE")
package com.xxx.zzz.aall.orgsss.jetbrains.ankos.jetbrains.collections

import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import java.util.ConcurrentModificationException


@Deprecated(message = "Use the native Kotlin version", replaceWith = ReplaceWith("forEach(f)"))
inline fun <T> Array<T>.forEachByIndex(f: (T) -> Unit) {
    val lastIndex = size - 1
    for (i in 0..lastIndex) {
        f(get(i))
    }
}


@Deprecated(message = "Use the native Kotlin version", replaceWith = ReplaceWith("forEachIndexed(f)"))
inline fun <T> Array<T>.forEachWithIndex(f: (Int, T) -> Unit) {
    val lastIndex = size - 1
    for (i in 0..lastIndex) {
        f(i, get(i))
    }
}


inline fun <T> Array<T>.forEachReversedByIndex(f: (T) -> Unit) {
    var i = size - 1
    while (i >= 0) {
        f(get(i))
        i--
    }
}


inline fun <T> Array<T>.forEachReversedWithIndex(f: (Int, T) -> Unit) {
    var i = size - 1
    while (i >= 0) {
        f(i, get(i))
        i--
    }
}


inline fun <T> SparseArray<T>.asSequence(): Sequence<T> = SparseArraySequence(this)


inline fun <T> SparseBooleanArray.asSequence(): Sequence<Boolean> = SparseBooleanArraySequence(this)


inline fun <T> SparseIntArray.asSequence(): Sequence<Int> = SparseIntArraySequence(this)

@PublishedApi
internal class SparseArraySequence<T>(private val a: SparseArray<T>) : Sequence<T> {
    override fun iterator(): Iterator<T> = SparseArrayIterator()

    private inner class SparseArrayIterator : Iterator<T> {
        private var index = 0
        private val size = a.size()

        override fun hasNext() = size > index

        override fun next(): T {
            if (a.size() != size) throw ConcurrentModificationException()
            return a.valueAt(index++)
        }
    }
}

@PublishedApi
internal class SparseBooleanArraySequence(private val a: SparseBooleanArray) : Sequence<Boolean> {
    override fun iterator(): Iterator<Boolean> = SparseIntArrayIterator()

    private inner class SparseIntArrayIterator : Iterator<Boolean> {
        private var index = 0
        private val size = a.size()

        override fun hasNext() = size > index

        override fun next(): Boolean {
            if (a.size() != size) throw ConcurrentModificationException()
            return a.get(index++)
        }
    }
}

@PublishedApi
internal class SparseIntArraySequence(private val a: SparseIntArray) : Sequence<Int> {
    override fun iterator(): Iterator<Int> = SparseIntArrayIterator()

    private inner class SparseIntArrayIterator : Iterator<Int> {
        private var index = 0
        private val size = a.size()

        override fun hasNext() = size > index

        override fun next(): Int {
            if (a.size() != size) throw ConcurrentModificationException()
            return a.get(index++)
        }
    }
}
