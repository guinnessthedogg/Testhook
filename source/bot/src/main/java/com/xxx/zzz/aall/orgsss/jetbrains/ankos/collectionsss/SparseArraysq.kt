

package com.xxx.zzz.aall.orgsss.jetbrains.ankos.jetbrains.collections

import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import java.util.ConcurrentModificationException


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("forEach(action)", "androidx.core.util.forEach"))
inline fun <E> SparseArray<E>.forEach(action: (Int, E) -> Unit) {
    val size = this.size()
    for (i in 0..size - 1) {
        if (size != this.size()) throw ConcurrentModificationException()
        action(this.keyAt(i), this.valueAt(i))
    }
}


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("forEach(action)", "androidx.core.util.forEach"))
inline fun SparseBooleanArray.forEach(action: (Int, Boolean) -> Unit) {
    val size = this.size()
    for (i in 0..size - 1) {
        if (size != this.size()) throw ConcurrentModificationException()
        action(this.keyAt(i), this.valueAt(i))
    }
}


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("forEach(action)", "androidx.core.util.forEach"))
inline fun SparseIntArray.forEach(action: (Int, Int) -> Unit) {
    val size = this.size()
    for (i in 0..size - 1) {
        if (size != this.size()) throw ConcurrentModificationException()
        action(this.keyAt(i), this.valueAt(i))
    }
}
