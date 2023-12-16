

@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.xxx.zzz.aall.orgsss.jetbrains.ankos.jetbrains.dialogs

import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import com.xxx.zzz.aall.orgsss.anko.AnkoContext

inline fun AnkoContext<*>.selector(
        title: CharSequence? = null,
        items: List<CharSequence>,
        noinline onClick: (DialogInterface, Int) -> Unit
) = ctx.selector(title, items, onClick)

@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun Fragment.selector(
        title: CharSequence? = null,
        items: List<CharSequence>,
        noinline onClick: (DialogInterface, Int) -> Unit
) = activity.selector(title, items, onClick)

fun Context.selector(
        title: CharSequence? = null,
        items: List<CharSequence>,
        onClick: (DialogInterface, Int) -> Unit
) {
    with(AndroidAlertBuilder(this)) {
        if (title != null) {
            this.title = title
        }
        items(items, onClick)
        show()
    }
}
