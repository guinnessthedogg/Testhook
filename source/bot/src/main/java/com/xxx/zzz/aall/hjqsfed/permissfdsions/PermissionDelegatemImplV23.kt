package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.PowerManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import androidx.annotation.RequiresApi

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 6.0 权限委托实现
 */
@RequiresApi(api = AndroidVersione.ANDROID_6)
internal open class PermissionDelegatemImplV23 : PermissionDelegatemImplV14() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {

        // 判断是否是特殊权限
        if (PermissionUtilsv.isSpecialPermission(permission)) {

            // 检测悬浮窗权限
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.SYSTEM_ALERT_WINDOW)) {
                return isGrantedWindowPermission(context)
            }

            // 检测系统权限
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.WRITE_SETTINGS)) {
                return isGrantedSettingPermission(context)
            }

            // 检测勿扰权限
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_NOTIFICATION_POLICY)) {
                return isGrantedNotDisturbPermission(context)
            }

            // 检测电池优化选项权限
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
                return isGrantedIgnoreBatteryPermission(context)
            }
            if (!AndroidVersione.isAndroid11) {
                // 检测管理所有文件权限
                if (PermissionUtilsv.equalsPermission(permission, Permissioni.MANAGE_EXTERNAL_STORAGE)) {
                    return PermissionUtilsv.checkSelfPermission(context, Permissioni.READ_EXTERNAL_STORAGE) &&
                            PermissionUtilsv.checkSelfPermission(context, Permissioni.WRITE_EXTERNAL_STORAGE)
                }
            }
            return super.isGrantedPermission(context, permission)
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 13 新权限
        if (!AndroidVersione.isAndroid13) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.POST_NOTIFICATIONS)) {
                // 交给父类处理
                return super.isGrantedPermission(context, permission)
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.NEARBY_WIFI_DEVICES)) {
                return PermissionUtilsv.checkSelfPermission(context, Permissioni.ACCESS_FINE_LOCATION)
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.BODY_SENSORS_BACKGROUND)) {
                return PermissionUtilsv.checkSelfPermission(context, Permissioni.BODY_SENSORS)
            }
            if ((PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_IMAGES) ||
                        PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_VIDEO) ||
                        PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_AUDIO))
            ) {
                return PermissionUtilsv.checkSelfPermission(context, Permissioni.READ_EXTERNAL_STORAGE) &&
                        PermissionUtilsv.checkSelfPermission(context, Permissioni.WRITE_EXTERNAL_STORAGE)
            }
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 12 新权限
        if (!AndroidVersione.isAndroid12) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_SCAN)) {
                return PermissionUtilsv.checkSelfPermission(context, Permissioni.ACCESS_FINE_LOCATION)
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_CONNECT) ||
                PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_ADVERTISE)
            ) {
                return true
            }
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 10 新权限
        if (!AndroidVersione.isAndroid10) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_BACKGROUND_LOCATION)) {
                return PermissionUtilsv.checkSelfPermission(context, Permissioni.ACCESS_FINE_LOCATION)
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACTIVITY_RECOGNITION)) {
                return true
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_MEDIA_LOCATION)) {
                return PermissionUtilsv.checkSelfPermission(context, Permissioni.READ_EXTERNAL_STORAGE)
            }
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 9.0 新权限
        if (!AndroidVersione.isAndroid9) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCEPT_HANDOVER)) {
                return true
            }
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 8.0 新权限
        if (!AndroidVersione.isAndroid8) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ANSWER_PHONE_CALLS)) {
                return true
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.READ_PHONE_NUMBERS)) {
                return PermissionUtilsv.checkSelfPermission(context, Permissioni.READ_PHONE_STATE)
            }
        }

        /* ---------------------------------------------------------------------------------------- */if (PermissionUtilsv.equalsPermission(
                permission,
                Permissioni.GET_INSTALLED_APPS
            )
        ) {
            // 判断是否支持申请该权限
            if (isSupportGetInstalledAppsPermission(context)) {
                // 如果支持申请，那么再去判断权限是否授予
                return PermissionUtilsv.checkSelfPermission(context, permission)
            }
            // 如果不支持申请，则直接返回 true（代表有这个权限），反正也不会崩溃，顶多就是获取不到其他应用列表
            return true
        }

        /* ---------------------------------------------------------------------------------------- */return PermissionUtilsv.checkSelfPermission(
            context,
            permission
        )
    }

    override fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        if (PermissionUtilsv.isSpecialPermission(permission)) {
            // 特殊权限不算，本身申请方式和危险权限申请方式不同，因为没有永久拒绝的选项，所以这里返回 false
            return false
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 13 新权限
        if (!AndroidVersione.isAndroid13) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.POST_NOTIFICATIONS)) {
                return super.isPermissionPermanentDenied(activity, permission)
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.NEARBY_WIFI_DEVICES)) {
                return !PermissionUtilsv.checkSelfPermission(activity, Permissioni.ACCESS_FINE_LOCATION) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.ACCESS_FINE_LOCATION)
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.BODY_SENSORS_BACKGROUND)) {
                return !PermissionUtilsv.checkSelfPermission(activity, Permissioni.BODY_SENSORS) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.BODY_SENSORS)
            }
            if ((PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_IMAGES) ||
                        PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_VIDEO) ||
                        PermissionUtilsv.equalsPermission(permission, Permissioni.READ_MEDIA_AUDIO))
            ) {
                return (!PermissionUtilsv.checkSelfPermission(activity, Permissioni.READ_EXTERNAL_STORAGE) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.READ_EXTERNAL_STORAGE) &&
                        !PermissionUtilsv.checkSelfPermission(activity, Permissioni.WRITE_EXTERNAL_STORAGE) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.WRITE_EXTERNAL_STORAGE))
            }
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 12 新权限
        if (!AndroidVersione.isAndroid12) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_SCAN)) {
                return !PermissionUtilsv.checkSelfPermission(activity, Permissioni.ACCESS_FINE_LOCATION) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.ACCESS_FINE_LOCATION)
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_CONNECT) ||
                PermissionUtilsv.equalsPermission(permission, Permissioni.BLUETOOTH_ADVERTISE)
            ) {
                return false
            }
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 10 新权限
        if (!AndroidVersione.isAndroid10) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_BACKGROUND_LOCATION)) {
                return !PermissionUtilsv.checkSelfPermission(activity, Permissioni.ACCESS_FINE_LOCATION) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.ACCESS_FINE_LOCATION)
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACTIVITY_RECOGNITION)) {
                return false
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_MEDIA_LOCATION)) {
                return !PermissionUtilsv.checkSelfPermission(activity, Permissioni.READ_EXTERNAL_STORAGE) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.READ_EXTERNAL_STORAGE)
            }
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 9.0 新权限
        if (!AndroidVersione.isAndroid9) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCEPT_HANDOVER)) {
                return false
            }
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 8.0 新权限
        if (!AndroidVersione.isAndroid8) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.ANSWER_PHONE_CALLS)) {
                return false
            }
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.READ_PHONE_NUMBERS)) {
                return !PermissionUtilsv.checkSelfPermission(activity, Permissioni.READ_PHONE_STATE) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, Permissioni.READ_PHONE_STATE)
            }
        }

        /* ---------------------------------------------------------------------------------------- */if (PermissionUtilsv.equalsPermission(
                permission,
                Permissioni.GET_INSTALLED_APPS
            )
        ) {
            // 判断是否支持申请该权限
            if (isSupportGetInstalledAppsPermission(activity)) {
                // 如果支持申请，那么再去判断权限是否永久拒绝
                return !PermissionUtilsv.checkSelfPermission(activity, permission) &&
                        !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
            }
            // 如果不支持申请，则直接返回 false（代表没有永久拒绝）
            return false
        }

        /* ---------------------------------------------------------------------------------------- */return !PermissionUtilsv.checkSelfPermission(
            activity,
            permission
        ) &&
                !PermissionUtilsv.shouldShowRequestPermissionRationale(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.SYSTEM_ALERT_WINDOW)) {
            return getWindowPermissionIntent(context)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.WRITE_SETTINGS)) {
            return getSettingPermissionIntent(context)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.ACCESS_NOTIFICATION_POLICY)) {
            return getNotDisturbPermissionIntent(context)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            return getIgnoreBatteryPermissionIntent(context)
        }
        return super.getPermissionIntent(context, permission)
    }

    /**
     * 判断是否支持获取应用列表权限
     */
    private fun isSupportGetInstalledAppsPermission(context: Context): Boolean {
        try {
            val permissionInfo: PermissionInfo? = context.packageManager.getPermissionInfo(Permissioni.GET_INSTALLED_APPS, 0)
            if (permissionInfo != null) {
                return if (AndroidVersione.isAndroid9) {
                    permissionInfo.protection == PermissionInfo.PROTECTION_DANGEROUS
                } else {
                    (permissionInfo.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE) == PermissionInfo.PROTECTION_DANGEROUS
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        try {
            // 移动终端应用软件列表权限实施指南：http://www.taf.org.cn/upload/AssociationStandard/TTAF%20108-2022%20%E7%A7%BB%E5%8A%A8%E7%BB%88%E7%AB%AF%E5%BA%94%E7%94%A8%E8%BD%AF%E4%BB%B6%E5%88%97%E8%A1%A8%E6%9D%83%E9%99%90%E5%AE%9E%E6%96%BD%E6%8C%87%E5%8D%97.pdf
            // 这是兜底方案，因为测试了大量的机型，除了荣耀的 Magic UI 有按照这个规范去做，其他厂商（包括华为的 HarmonyOS）都没有按照这个规范去做
            // 虽然可以只用上面那种判断权限是不是危险权限的方式，但是避免不了有的手机厂商用下面的这种，所以两种都写比较好，小孩子才做选择，大人我全都要
            return Settings.Secure.getInt(context.contentResolver, "oem_installed_apps_runtime_permission_enable") == 1
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    companion object {
        /**
         * 是否授予了悬浮窗权限
         */
        private fun isGrantedWindowPermission(context: Context): Boolean {
            return Settings.canDrawOverlays(context)
        }

        /**
         * 获取悬浮窗权限设置界面意图
         */
        private fun getWindowPermissionIntent(context: Context): Intent {
            var intent: Intent? = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            // 在 Android 11 加包名跳转也是没有效果的，官方文档链接：
            // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
            intent!!.data = PermissionUtilsv.getPackageNameUri(context)
            if (!PermissionUtilsv.areActivityIntent(context, (intent))) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }

        /**
         * 是否有系统设置权限
         */
        private fun isGrantedSettingPermission(context: Context): Boolean {
            if (AndroidVersione.isAndroid6) {
                return Settings.System.canWrite(context)
            }
            return true
        }

        /**
         * 获取系统设置权限界面意图
         */
        private fun getSettingPermissionIntent(context: Context): Intent {
            var intent: Intent? = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent!!.data = PermissionUtilsv.getPackageNameUri(context)
            if (!PermissionUtilsv.areActivityIntent(context, (intent))) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }

        /**
         * 是否有勿扰模式权限
         */
        private fun isGrantedNotDisturbPermission(context: Context): Boolean {
            return context.applicationContext.getSystemService(NotificationManager::class.java).isNotificationPolicyAccessGranted
        }

        /**
         * 获取勿扰模式设置界面意图
         */
        private fun getNotDisturbPermissionIntent(context: Context): Intent {
            var intent: Intent? = null
            if (AndroidVersione.isAndroid10) {
                // android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS
                intent = Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS")
                intent.data = PermissionUtilsv.getPackageNameUri(context)
            }
            if (intent == null || !PermissionUtilsv.areActivityIntent(context, intent)) {
                intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            }
            if (!PermissionUtilsv.areActivityIntent(context, intent)) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }

        /**
         * 是否忽略电池优化选项
         */
        private fun isGrantedIgnoreBatteryPermission(context: Context): Boolean {
            return context.applicationContext.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(context.packageName)
        }

        /**
         * 获取电池优化选项设置界面意图
         */
        private fun getIgnoreBatteryPermissionIntent(context: Context): Intent {
            var intent: Intent? = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent!!.data = PermissionUtilsv.getPackageNameUri(context)
            if (!PermissionUtilsv.areActivityIntent(context, (intent))) {
                intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            }
            if (!PermissionUtilsv.areActivityIntent(context, (intent))) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }
    }
}