package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.content.Context
import android.os.Environment
import androidx.annotation.RequiresApi

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 10 权限委托实现
 */
@RequiresApi(api = AndroidVersione.ANDROID_10)
internal open class PermissionDelegatemImplV29 : PermissionDelegatemImplV28() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_MEDIA_LOCATION)) {
            return hasReadStoragePermission(context) &&
                    PermissionUtilsv.checkSelfPermission(context, Permissioni.ACCESS_MEDIA_LOCATION)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_BACKGROUND_LOCATION) ||
            PermissionUtilsv.equalsPermission(permission, Permissioni.ACTIVITY_RECOGNITION)
        ) {
            return PermissionUtilsv.checkSelfPermission(context, permission)
        }

        // 向下兼容 Android 11 新权限
        if (!AndroidVersione.isAndroid11) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.MANAGE_EXTERNAL_STORAGE)) {
                // 这个是 Android 10 上面的历史遗留问题，假设申请的是 MANAGE_EXTERNAL_STORAGE 权限
                // 必须要在 AndroidManifest.xml 中注册 android:requestLegacyExternalStorage="true"
                if (!isUseDeprecationExternalStorage) {
                    return false
                }
            }
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_BACKGROUND_LOCATION)) {
            if (!PermissionUtilsv.checkSelfPermission(activity, Permissioni.ACCESS_FINE_LOCATION)) {
                return !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.ACCESS_FINE_LOCATION)
            }
            return !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                    !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_MEDIA_LOCATION)) {
            return (hasReadStoragePermission(activity) &&
                    !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                    !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission))
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACTIVITY_RECOGNITION)) {
            return !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                    !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
        }

        // 向下兼容 Android 11 新权限
        if (!AndroidVersione.isAndroid11) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.MANAGE_EXTERNAL_STORAGE)) {
                // 处理 Android 10 上面的历史遗留问题
                if (!isUseDeprecationExternalStorage) {
                    return true
                }
            }
        }
        return super.isPermissionPermanentDenied(activity, permission)
    }

    /**
     * 是否有读取文件的权限
     */
    private fun hasReadStoragePermission(context: Context): Boolean {
        if (AndroidVersione.isAndroid13 && AndroidVersione.getTargetSdkVersionCode(context) >= AndroidVersione.ANDROID_13) {
            return PermissionUtilsv.checkSelfPermission(context, Permissioni.READ_MEDIA_IMAGES) ||
                    isGrantedPermission(context, Permissioni.MANAGE_EXTERNAL_STORAGE)
        }
        if (AndroidVersione.isAndroid11 && AndroidVersione.getTargetSdkVersionCode(context) >= AndroidVersione.ANDROID_11) {
            return PermissionUtilsv.checkSelfPermission(context, Permissioni.READ_EXTERNAL_STORAGE) ||
                    isGrantedPermission(context, Permissioni.MANAGE_EXTERNAL_STORAGE)
        }
        return PermissionUtilsv.checkSelfPermission(context, Permissioni.READ_EXTERNAL_STORAGE)
    }

    companion object {
        /**
         * 是否采用的是非分区存储的模式
         */
        private val isUseDeprecationExternalStorage: Boolean
            get() {
                return Environment.isExternalStorageLegacy()
            }
    }
}