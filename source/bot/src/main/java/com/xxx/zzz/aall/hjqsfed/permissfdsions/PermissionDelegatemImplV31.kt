package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresApi

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 12 权限委托实现
 */
@RequiresApi(api = AndroidVersione.ANDROID_12)
internal open class PermissionDelegatemImplV31 : PermissionDelegatemImplV30() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        // 检测闹钟权限
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.SCHEDULE_EXACT_ALARM)) {
            return isGrantedAlarmPermission(context)
        }
        if ((PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_SCAN) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_CONNECT) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_ADVERTISE))
        ) {
            return PermissionUtilsv.checkSelfPermission(context, permission)
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.SCHEDULE_EXACT_ALARM)) {
            return false
        }
        if ((PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_SCAN) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_CONNECT) ||
                    PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_ADVERTISE))
        ) {
            return !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                    !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
        }
        if (activity.applicationInfo.targetSdkVersion >= AndroidVersione.ANDROID_12 &&
            PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_BACKGROUND_LOCATION)
        ) {
            if (!PermissionUtilsv.checkSelfPermission(activity, Permissioni.ACCESS_FINE_LOCATION) &&
                !PermissionUtilsv.checkSelfPermission(activity, Permissioni.ACCESS_COARSE_LOCATION)
            ) {
                return !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.ACCESS_FINE_LOCATION) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.ACCESS_COARSE_LOCATION)
            }
            return !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                    !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
        }
        return super.isPermissionPermanentDenied(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.SCHEDULE_EXACT_ALARM)) {
            return getAlarmPermissionIntent(context)
        }
        return super.getPermissionIntent(context, permission)
    }

    companion object {
        /**
         * 是否有闹钟权限
         */
        private fun isGrantedAlarmPermission(context: Context): Boolean {
            return true //context.getSystemService(AlarmManager.class).canScheduleExactAlarms();
        }

        /**
         * 获取闹钟权限设置界面意图
         */
        private fun getAlarmPermissionIntent(context: Context): Intent? {
//      Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
//      intent.setData(PermissionUtils.getPackageNameUri(context));
//      if (!PermissionUtils.areActivityIntent(context, intent)) {
//         intent = PermissionUtils.getApplicationDetailsIntent(context);
//      }
//      return intent;
            return null
        }
    }
}