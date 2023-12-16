

@file:Suppress("unused")
package com.xxx.zzz.aall.orgsss.anko

import android.view.View
import android.view.ViewGroup
import java.util.ConcurrentModificationException
import java.util.NoSuchElementException


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("forEach(action)", "androidx.core.view.forEach"))
inline fun ViewGroup.forEachChild(action: (View) -> Unit) {
    for (i in 0..childCount - 1) {
        action(getChildAt(i))
    }
}


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("forEachIndexed(action)", "androidx.core.view.forEachIndexed"))
inline fun ViewGroup.forEachChildWithIndex(action: (Int, View) -> Unit) {
    for (i in 0..childCount - 1) {
        action(i, getChildAt(i))
    }
}


inline fun ViewGroup.firstChild(predicate: (View) -> Boolean): View {
    return firstChildOrNull(predicate)
            ?: throw NoSuchElementException("No element matching predicate was found.")
}


inline fun ViewGroup.firstChildOrNull(predicate: (View) -> Boolean): View? {
    for (i in 0..childCount - 1) {
        val child = getChildAt(i)
        if (predicate(child)) {
            return child
        }
    }
    return null
}


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("children", "androidx.core.view.children"))
fun View.childrenSequence(): Sequence<View> = ViewChildrenSequence(this)


fun View.childrenRecursiveSequence(): Sequence<View> = ViewChildrenRecursiveSequence(this)

private class ViewChildrenSequence(private val view: View) : Sequence<View> {
    override fun iterator(): Iterator<View> {
        if (view !is ViewGroup) return emptyList<View>().iterator()
        return ViewIterator(view)
    }

    private class ViewIterator(private val view: ViewGroup) : Iterator<View> {
        private var index = 0
        private val count = view.childCount

        override fun next(): View {
            if (!hasNext()) throw NoSuchElementException()
            return view.getChildAt(index++)
        }

        override fun hasNext(): Boolean {
            checkCount()
            return index < count
        }

        private fun checkCount() {
            if (count != view.childCount) throw ConcurrentModificationException()
        }
    }
}

private class ViewChildrenRecursiveSequence(private val view: View) : Sequence<View> {
    override fun iterator(): Iterator<View> {
        if (view !is ViewGroup) return emptyList<View>().iterator()
        return RecursiveViewIterator(view)
    }

    private class RecursiveViewIterator(view: View) : Iterator<View> {
        private val sequences = arrayListOf(view.childrenSequence())
        private var current = sequences.removeLast().iterator()

        override fun next(): View {
            if (!hasNext()) throw NoSuchElementException()
            val view = current.next()
            if (view is ViewGroup && view.childCount > 0) {
                sequences.add(view.childrenSequence())
            }
            return view
        }

        override fun hasNext(): Boolean {
            if (!current.hasNext() && sequences.isNotEmpty()) {
                current = sequences.removeLast().iterator()
            }
            return current.hasNext()
        }

        @Suppress("NOTHING_TO_INLINE")
        private inline fun <T : Any> MutableList<T>.removeLast(): T {
            if (isEmpty()) throw NoSuchElementException()
            return removeAt(size - 1)
        }
    }
}

