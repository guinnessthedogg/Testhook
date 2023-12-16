package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.content.Context
import androidx.annotation.RequiresApi

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/26
 * desc   : Android 13 权限委托实现
 */
@RequiresApi(api = AndroidVersione.ANDROID_13)
internal class PermissionDelegatemImplV33 : PermissionDelegatemImplV31() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.BODY_SENSORS_BACKGROUND)) {
            // 有后台传感器权限的前提条件是要有前台的传感器权限
            return PermissionUtilsv.checkSelfPermission(context, Permissioni.BODY_SENSORS) &&
                    PermissionUtilsv.checkSelfPermission(context, Permissioni.BODY_SENSORS_BACKGROUND)
        }
        if ((PermissionUtilsv.equalsPermission(permission, Permissioni.POST_NOTIFICATIONS) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.NEARBY_WIFI_DEVICES) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_IMAGES) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_VIDEO) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_AUDIO))
        ) {
            return PermissionUtilsv.checkSelfPermission(context, permission)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.BODY_SENSORS_BACKGROUND)) {
            if (!PermissionUtilsv.checkSelfPermission(activity, Permissioni.BODY_SENSORS)) {
                return !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.BODY_SENSORS)
            }
            return !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                    !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
        }
        if ((PermissionUtilsv.equalsPermission(permission, Permissioni.POST_NOTIFICATIONS) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.NEARBY_WIFI_DEVICES) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_IMAGES) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_VIDEO) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_AUDIO))
        ) {
            return !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                    !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
        }
        return super.isPermissionPermanentDenied(activity, permission)
    }
}