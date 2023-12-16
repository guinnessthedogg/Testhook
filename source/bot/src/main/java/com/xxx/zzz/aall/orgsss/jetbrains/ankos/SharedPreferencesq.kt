


package com.xxx.zzz.aall.orgsss.anko

import android.content.SharedPreferences


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("edit(modifier)", "androidx.core.content.edit"))
inline fun SharedPreferences.apply(modifier: SharedPreferences.Editor.() -> Unit) {
    val editor = this.edit()
    editor.modifier()
    editor.apply()
}


@Deprecated(message = "Use the Android KTX version", replaceWith = ReplaceWith("edit(true, modifier)", "androidx.core.content.edit"))
inline fun SharedPreferences.commit(modifier: SharedPreferences.Editor.() -> Unit) {
    val editor = this.edit()
    editor.modifier()
    editor.commit()
}
