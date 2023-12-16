

@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.xxx.zzz.aall.orgsss.anko

import android.os.Build
import android.view.View
import android.widget.RelativeLayout.*
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi


fun LayoutParams.topOf(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(ABOVE, id)
}


fun LayoutParams.above(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(ABOVE, id)
}


fun LayoutParams.bottomOf(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(BELOW, id)
}


fun LayoutParams.below(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(BELOW, id)
}


inline fun LayoutParams.leftOf(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(LEFT_OF, id)
}


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.startOf(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(START_OF, id)
}


inline fun LayoutParams.rightOf(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(RIGHT_OF, id)
}


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.endOf(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(END_OF, id)
}


inline fun LayoutParams.sameLeft(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(ALIGN_LEFT, id)
}


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.sameStart(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(ALIGN_START, id)
}


inline fun LayoutParams.sameTop(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(ALIGN_TOP, id)
}


inline fun LayoutParams.sameRight(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(ALIGN_RIGHT, id)
}


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.sameEnd(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(ALIGN_END, id)
}


inline fun LayoutParams.sameBottom(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(ALIGN_BOTTOM, id)
}


inline fun LayoutParams.topOf(@IdRes id: Int) = addRule(ABOVE, id)


inline fun LayoutParams.above(@IdRes id: Int) = addRule(ABOVE, id)


inline fun LayoutParams.below(@IdRes id: Int) = addRule(BELOW, id)


inline fun LayoutParams.bottomOf(@IdRes id: Int) = addRule(BELOW, id)


inline fun LayoutParams.leftOf(@IdRes id: Int) = addRule(LEFT_OF, id)


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.startOf(@IdRes id: Int): Unit = addRule(START_OF, id)


inline fun LayoutParams.rightOf(@IdRes id: Int) = addRule(RIGHT_OF, id)


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.endOf(@IdRes id: Int): Unit = addRule(END_OF, id)


inline fun LayoutParams.sameLeft(@IdRes id: Int) = addRule(ALIGN_LEFT, id)


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.sameStart(@IdRes id: Int): Unit = addRule(ALIGN_START, id)


inline fun LayoutParams.sameTop(@IdRes id: Int) = addRule(ALIGN_TOP, id)


inline fun LayoutParams.sameRight(@IdRes id: Int) = addRule(ALIGN_RIGHT, id)


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.sameEnd(@IdRes id: Int): Unit = addRule(ALIGN_END, id)


inline fun LayoutParams.sameBottom(@IdRes id: Int) = addRule(ALIGN_BOTTOM, id)


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.alignStart(@IdRes id: Int): Unit = addRule(ALIGN_START, id)


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.alignEnd(@IdRes id: Int): Unit = addRule(ALIGN_END, id)


inline fun LayoutParams.alignParentTop() = addRule(ALIGN_PARENT_TOP)


inline fun LayoutParams.alignParentRight() = addRule(ALIGN_PARENT_RIGHT)


inline fun LayoutParams.alignParentBottom() = addRule(ALIGN_PARENT_BOTTOM)


inline fun LayoutParams.alignParentLeft() = addRule(ALIGN_PARENT_LEFT)


inline fun LayoutParams.centerHorizontally() = addRule(CENTER_HORIZONTAL)


inline fun LayoutParams.centerVertically() = addRule(CENTER_VERTICAL)


inline fun LayoutParams.centerInParent() = addRule(CENTER_IN_PARENT)


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.alignParentStart(): Unit = addRule(ALIGN_PARENT_START)


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
inline fun LayoutParams.alignParentEnd(): Unit = addRule(ALIGN_PARENT_END)


inline fun LayoutParams.baselineOf(view: View) {
    val id = view.id
    if (id == View.NO_ID) throw AnkoException("Id is not set for $view")
    addRule(ALIGN_BASELINE, id)
}


inline fun LayoutParams.baselineOf(@IdRes id: Int) = addRule(ALIGN_BASELINE, id)
