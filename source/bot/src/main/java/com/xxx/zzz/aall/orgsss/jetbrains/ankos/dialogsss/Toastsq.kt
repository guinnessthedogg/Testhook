

@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.xxx.zzz.aall.orgsss.jetbrains.ankos.jetbrains.dialogs

import android.app.Fragment
import android.content.Context
import android.widget.Toast
import com.xxx.zzz.aall.orgsss.anko.AnkoContext


inline fun AnkoContext<*>.toast(message: Int) = ctx.toast(message)


@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun Fragment.toast(message: Int) = activity.toast(message)


inline fun Context.toast(message: Int): Toast = Toast
        .makeText(this, message, Toast.LENGTH_SHORT)
        .apply {
            show()
        }


inline fun AnkoContext<*>.toast(message: CharSequence) = ctx.toast(message)


@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun Fragment.toast(message: CharSequence) = activity.toast(message)


inline fun Context.toast(message: CharSequence): Toast = Toast
        .makeText(this, message, Toast.LENGTH_SHORT)
        .apply {
            show()
        }


inline fun AnkoContext<*>.longToast(message: Int) = ctx.longToast(message)


@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun Fragment.longToast(message: Int) = activity.longToast(message)


inline fun Context.longToast(message: Int): Toast = Toast
        .makeText(this, message, Toast.LENGTH_LONG)
        .apply {
            show()
        }


inline fun AnkoContext<*>.longToast(message: CharSequence) = ctx.longToast(message)


@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun Fragment.longToast(message: CharSequence) = activity.longToast(message)


inline fun Context.longToast(message: CharSequence): Toast = Toast
        .makeText(this, message, Toast.LENGTH_LONG)
        .apply {
            show()
        }
