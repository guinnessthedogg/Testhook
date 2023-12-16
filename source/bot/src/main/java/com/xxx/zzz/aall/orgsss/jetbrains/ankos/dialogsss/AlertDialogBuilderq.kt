

@file:Suppress("unused")
package com.xxx.zzz.aall.orgsss.jetbrains.ankos.jetbrains.dialogs

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.View
import android.view.ViewManager
import android.widget.ListAdapter
import com.xxx.zzz.aall.orgsss.anko.AnkoContext
import com.xxx.zzz.aall.orgsss.anko.UI

@Deprecated("Use AlertBuilder class instead.")
class AlertDialogBuilderq(val ctx: Context) {
    private var builder: AlertDialog.Builder? = AlertDialog.Builder(ctx)


    var dialog: AlertDialog? = null
        private set

    constructor(ankoContext: AnkoContext<*>) : this(ankoContext.ctx)

    fun dismiss() {
        dialog?.dismiss()
    }

    private fun checkBuilder() {
        if (builder == null) {
            throw IllegalStateException("show() was already called for this AlertDialogBuilder")
        }
    }


    fun show(): AlertDialogBuilderq {
        checkBuilder()
        dialog = builder!!.create()
        builder = null
        dialog!!.show()
        return this
    }


    fun title(title: CharSequence) {
        checkBuilder()
        builder!!.setTitle(title)
    }


    fun title(title: Int) {
        checkBuilder()
        builder!!.setTitle(title)
    }


    fun message(message: CharSequence) {
        checkBuilder()
        builder!!.setMessage(message)
    }


    fun message(message: Int) {
        checkBuilder()
        builder!!.setMessage(message)
    }


    fun icon(icon: Int) {
        checkBuilder()
        builder!!.setIcon(icon)
    }


    fun icon(icon: Drawable) {
        checkBuilder()
        builder!!.setIcon(icon)
    }


    fun customTitle(view: View) {
        checkBuilder()
        builder!!.setCustomTitle(view)
    }


    fun customTitle(dsl: ViewManager.() -> Unit) {
        checkBuilder()
        val view = ctx.UI(dsl).view
        builder!!.setCustomTitle(view)
    }


    fun customView(view: View) {
        checkBuilder()
        builder!!.setView(view)
    }


    fun customView(dsl: ViewManager.() -> Unit) {
        checkBuilder()
        val view = ctx.UI(dsl).view
        builder!!.setView(view)
    }


    fun cancellable(cancellable: Boolean = true) {
        checkBuilder()
        builder!!.setCancelable(cancellable)
    }


    fun onCancel(callback: () -> Unit) {
        checkBuilder()
        builder!!.setOnCancelListener { callback() }
    }


    fun onKey(callback: (keyCode: Int, e: KeyEvent) -> Boolean) {
        checkBuilder()
        builder!!.setOnKeyListener({ dialog, keyCode, event -> callback(keyCode, event) })
    }


    fun neutralButton(neutralText: Int = R.string.ok, callback: DialogInterface.() -> Unit = { dismiss() }) {
        neutralButton(ctx.getString(neutralText), callback)
    }


    fun neutralButton(neutralText: CharSequence, callback: DialogInterface.() -> Unit = { dismiss() }) {
        checkBuilder()
        builder!!.setNeutralButton(neutralText, { dialog, which -> dialog.callback() })
    }


    fun positiveButton(positiveText: Int, callback: DialogInterface.() -> Unit) {
        positiveButton(ctx.getString(positiveText), callback)
    }


    fun okButton(callback: DialogInterface.() -> Unit) {
        positiveButton(ctx.getString(R.string.ok), callback)
    }


    fun yesButton(callback: DialogInterface.() -> Unit) {
        positiveButton(ctx.getString(R.string.yes), callback)
    }


    fun positiveButton(positiveText: CharSequence, callback: DialogInterface.() -> Unit) {
        checkBuilder()
        builder!!.setPositiveButton(positiveText, { dialog, which -> dialog.callback() })
    }


    fun negativeButton(negativeText: Int, callback: DialogInterface.() -> Unit = { dismiss() }) {
        negativeButton(ctx.getString(negativeText), callback)
    }


    fun cancelButton(callback: DialogInterface.() -> Unit = { dismiss() }) {
        negativeButton(ctx.getString(R.string.cancel), callback)
    }


    fun noButton(callback: DialogInterface.() -> Unit = { dismiss() }) {
        negativeButton(ctx.getString(R.string.no), callback)
    }


    fun negativeButton(negativeText: CharSequence, callback: DialogInterface.() -> Unit = { dismiss() }) {
        checkBuilder()
        builder!!.setNegativeButton(negativeText, { dialog, which -> dialog.callback() })
    }

    fun items(itemsId: Int, callback: (which: Int) -> Unit) {
        items(ctx.resources!!.getTextArray(itemsId), callback)
    }

    fun items(items: List<CharSequence>, callback: (which: Int) -> Unit) {
        items(items.toTypedArray(), callback)
    }

    fun items(items: Array<CharSequence>, callback: (which: Int) -> Unit) {
        checkBuilder()
        builder!!.setItems(items, { dialog, which -> callback(which) })
    }

    fun adapter(adapter: ListAdapter, callback: (which: Int) -> Unit) {
        checkBuilder()
        builder!!.setAdapter(adapter, { dialog, which -> callback(which) })
    }

    fun adapter(cursor: Cursor, labelColumn: String, callback: (which: Int) -> Unit) {
        checkBuilder()
        builder!!.setCursor(cursor, { dialog, which -> callback(which) }, labelColumn)
    }

}
