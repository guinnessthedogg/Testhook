

@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.xxx.zzz.aall.orgsss.jetbrains.ankos.jetbrains.dialogs

import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import com.xxx.zzz.aall.orgsss.anko.AnkoContext

inline fun <D : DialogInterface> AnkoContext<*>.selector(
    noinline factory: AlertBuilderFactory<D>,
    title: CharSequence? = null,
    items: List<CharSequence>,
    noinline onClick: (DialogInterface, CharSequence, Int) -> Unit
) = ctx.selector(factory, title, items, onClick)

@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun <D : DialogInterface> Fragment.selector(
    noinline factory: AlertBuilderFactory<D>,
    title: CharSequence? = null,
    items: List<CharSequence>,
    noinline onClick: (DialogInterface, CharSequence, Int) -> Unit
) = activity.selector(factory, title, items, onClick)

fun <D : DialogInterface> Context.selector(
    factory: AlertBuilderFactory<D>,
    title: CharSequence? = null,
    items: List<CharSequence>,
    onClick: (DialogInterface, CharSequence, Int) -> Unit
) {
    with(factory(this)) {
        if (title != null) {
            this.title = title
        }
        items(items, onClick)
        show()
    }
}
