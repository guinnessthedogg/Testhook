

@file:Suppress("unused")
package com.xxx.zzz.aall.orgsss.anko

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewManager


inline fun <T : View> ViewManager.ankoView(factory: (ctx: Context) -> T, theme: Int, init: T.() -> Unit): T {
    val ctx = AnkoInternals.wrapContextIfNeeded(AnkoInternals.getContext(this), theme)
    val view = factory(ctx)
    view.init()
    AnkoInternals.addView(this, view)
    return view
}

inline fun <T : View> Context.ankoView(factory: (ctx: Context) -> T, theme: Int, init: T.() -> Unit): T {
    val ctx = AnkoInternals.wrapContextIfNeeded(this, theme)
    val view = factory(ctx)
    view.init()
    AnkoInternals.addView(this, view)
    return view
}

inline fun <T : View> Activity.ankoView(factory: (ctx: Context) -> T, theme: Int, init: T.() -> Unit): T {
    val ctx = AnkoInternals.wrapContextIfNeeded(this, theme)
    val view = factory(ctx)
    view.init()
    AnkoInternals.addView(this, view)
    return view
}

inline fun <reified T : View> ViewManager.customView(theme: Int = 0, init: T.() -> Unit): T =
        ankoView({ ctx -> AnkoInternals.initiateView(ctx, T::class.java) }, theme) { init() }

inline fun <reified T : View> Context.customView(theme: Int = 0, init: T.() -> Unit): T =
        ankoView({ ctx -> AnkoInternals.initiateView(ctx, T::class.java) }, theme) { init() }

inline fun <reified T : View> Activity.customView(theme: Int = 0, init: T.() -> Unit): T =
        ankoView({ ctx -> AnkoInternals.initiateView(ctx, T::class.java) }, theme) { init() }