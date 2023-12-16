

@file:Suppress("unused", "NOTHING_TO_INLINE")
package com.xxx.zzz.aall.orgsss.jetbrains.ankos

import android.view.View
import com.xxx.zzz.aall.orgsss.anko.AnkoInternals

@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class AnkoViewDslMarker


inline fun <T : View> T.applyRecursively(noinline f: (View) -> Unit): T {
    AnkoInternals.applyRecursively(this, f)
    return this
}