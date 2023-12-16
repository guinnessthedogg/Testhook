package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 8.0 权限委托实现
 */
@RequiresApi(api = AndroidVersione.ANDROID_8)
internal open class PermissionDelegatemImplV26 : PermissionDelegatemImplV23() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.REQUEST_INSTALL_PACKAGES)) {
            return isGrantedInstallPermission(context)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.PICTURE_IN_PICTURE)) {
            return isGrantedPictureInPicturePermission(context)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.READ_PHONE_NUMBERS) ||
            PermissionUtilsv.equalsPermission(permission, Permissioni.ANSWER_PHONE_CALLS)
        ) {
            return PermissionUtilsv.checkSelfPermission(context, permission)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.REQUEST_INSTALL_PACKAGES)) {
            return false
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.PICTURE_IN_PICTURE)) {
            return false
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.READ_PHONE_NUMBERS) ||
            PermissionUtilsv.equalsPermission(permission, Permissioni.ANSWER_PHONE_CALLS)
        ) {
            return !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                    !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
        }
        return super.isPermissionPermanentDenied(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.REQUEST_INSTALL_PACKAGES)) {
            return getInstallPermissionIntent(context)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.PICTURE_IN_PICTURE)) {
            return getPictureInPicturePermissionIntent(context)
        }
        return super.getPermissionIntent(context, permission)
    }

    companion object {
        /**
         * 是否有安装权限
         */
        private fun isGrantedInstallPermission(context: Context): Boolean {
            return context.packageManager.canRequestPackageInstalls()
        }

        /**
         * 获取安装权限设置界面意图
         */
        private fun getInstallPermissionIntent(context: Context): Intent {
            var intent: Intent? = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            intent!!.data = PermissionUtilsv.getPackageNameUri(context)
            if (!PermissionUtilsv.areActivityIntent(context, (intent))) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }

        /**
         * 是否有画中画权限
         */
        private fun isGrantedPictureInPicturePermission(context: Context): Boolean {
            val appOps: AppOpsManager = context.applicationContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode: Int = if (AndroidVersione.isAndroid10) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                    context.applicationInfo.uid, context.packageName
                )
            } else {
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                    context.applicationInfo.uid, context.packageName
                )
            }
            return mode == AppOpsManager.MODE_ALLOWED
        }

        /**
         * 获取画中画权限设置界面意图
         */
        private fun getPictureInPicturePermissionIntent(context: Context): Intent {
            // android.provider.Settings.ACTION_PICTURE_IN_PICTURE_SETTINGS
            var intent: Intent? = Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS")
            intent!!.data = PermissionUtilsv.getPackageNameUri(context)
            if (!PermissionUtilsv.areActivityIntent(context, (intent))) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }
    }
}