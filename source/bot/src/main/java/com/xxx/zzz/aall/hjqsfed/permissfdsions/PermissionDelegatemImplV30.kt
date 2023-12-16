package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 11 权限委托实现
 */
@RequiresApi(api = AndroidVersione.ANDROID_11)
internal open class PermissionDelegatemImplV30 : PermissionDelegatemImplV29() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.MANAGE_EXTERNAL_STORAGE)) {
            return isGrantedManageStoragePermission
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.MANAGE_EXTERNAL_STORAGE)) {
            return false
        }
        return super.isPermissionPermanentDenied(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.MANAGE_EXTERNAL_STORAGE)) {
            return getManageStoragePermissionIntent(context)
        }
        return super.getPermissionIntent(context, permission)
    }

    companion object {
        /**
         * 是否有所有文件的管理权限
         */
        private val isGrantedManageStoragePermission: Boolean
            get() {
                return Environment.isExternalStorageManager()
            }

        /**
         * 获取所有文件的管理权限设置界面意图
         */
        private fun getManageStoragePermissionIntent(context: Context): Intent {
            var intent: Intent? = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent!!.data = PermissionUtilsv.getPackageNameUri(context)
            if (!PermissionUtilsv.areActivityIntent(context, (intent))) {
                intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            }
            if (!PermissionUtilsv.areActivityIntent(context, (intent))) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }
    }
}