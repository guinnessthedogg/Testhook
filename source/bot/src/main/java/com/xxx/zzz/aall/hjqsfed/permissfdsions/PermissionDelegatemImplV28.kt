package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.content.Context
import androidx.annotation.RequiresApi

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/07/03
 * desc   : Android 9.0 权限委托实现
 */
@RequiresApi(api = AndroidVersione.ANDROID_9)
internal open class PermissionDelegatemImplV28 : PermissionDelegatemImplV26() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCEPT_HANDOVER)) {
            return PermissionUtilsv.checkSelfPermission(context, permission)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCEPT_HANDOVER)) {
            return !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                    !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
        }
        return super.isPermissionPermanentDenied(activity, permission)
    }
}