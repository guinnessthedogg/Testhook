package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 4.0 权限委托实现
 */
@RequiresApi(api = AndroidVersione.ANDROID_4_0)
internal open class PermissionDelegatemImplV14 : PermissionDelegatem {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        // 检测通知栏权限
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.NOTIFICATION_SERVICE)) {
            return isGrantedNotifyPermission(context)
        }

        // 检测获取使用统计权限
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.PACKAGE_USAGE_STATS)) {
            return isGrantedPackagePermission(context)
        }

        // 检测通知栏监听权限
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            return isGrantedNotificationListenerPermission(context)
        }

        // 检测 VPN 权限
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.BIND_VPN_SERVICE)) {
            return isGrantedVpnPermission(context)
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 13 新权限
        if (!AndroidVersione.isAndroid13) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.POST_NOTIFICATIONS)) {
                return isGrantedNotifyPermission(context)
            }
        }

        /* ---------------------------------------------------------------------------------------- */return true
    }

    override fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        return false
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.NOTIFICATION_SERVICE)) {
            return getNotifyPermissionIntent(context)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.PACKAGE_USAGE_STATS)) {
            return getPackagePermissionIntent(context)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.BIND_NOTIFICATION_LISTENER_SERVICE)) {
            return getNotificationListenerIntent(context)
        }
        if (PermissionUtilsv.equalsPermission(permission, Permissioni.BIND_VPN_SERVICE)) {
            return getVpnPermissionIntent(context)
        }

        /* ---------------------------------------------------------------------------------------- */

        // 向下兼容 Android 13 新权限
        if (!AndroidVersione.isAndroid13) {
            if (PermissionUtilsv.equalsPermission(permission, Permissioni.POST_NOTIFICATIONS)) {
                return getNotifyPermissionIntent(context)
            }
        }

        /* ---------------------------------------------------------------------------------------- */return PermissionUtilsv.getApplicationDetailsIntent(
            context
        )
    }

    companion object {
        /**
         * 是否有通知栏权限
         */
        private fun isGrantedNotifyPermission(context: Context): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        /**
         * 获取通知栏权限设置界面意图
         */
        private fun getNotifyPermissionIntent(context: Context): Intent {
            var intent: Intent? = null
            if (AndroidVersione.isAndroid8) {
                intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                //intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
            }
            if (intent == null || !PermissionUtilsv.areActivityIntent(context, intent)) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }

        /**
         * 是否通知栏监听的权限
         */
        private fun isGrantedNotificationListenerPermission(context: Context): Boolean {
            if (AndroidVersione.isAndroid4_3) {
                val packageNames: Set<String> = NotificationManagerCompat.getEnabledListenerPackages(context)
                return packageNames.contains(context.packageName)
            }
            return true
        }

        /**
         * 获取通知监听设置界面意图
         */
        private fun getNotificationListenerIntent(context: Context): Intent {
            var intent: Intent? = null
            if (AndroidVersione.isAndroid11) {
                val androidManifestInfoq: AndroidManifestInfoq? = PermissionUtilsv.getAndroidManifestInfo(context)
                var serviceInfo: AndroidManifestInfoq.ServiceInfo? = null
                if (androidManifestInfoq != null) {
                    for (info: AndroidManifestInfoq.ServiceInfo? in androidManifestInfoq.serviceInfoList) {
                        if (!TextUtils.equals(info!!.permission, Permissioni.BIND_NOTIFICATION_LISTENER_SERVICE)) {
                            continue
                        }
                        if (serviceInfo != null) {
                            // 证明有两个这样的 Service，就不跳转到权限详情页了，而是跳转到权限列表页
                            serviceInfo = null
                            break
                        }
                        serviceInfo = info
                    }
                }
                if (serviceInfo != null) {
                    intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
                    intent.putExtra(
                        Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                        ComponentName(context, (serviceInfo.name)!!).flattenToString()
                    )
                    if (!PermissionUtilsv.areActivityIntent(context, intent)) {
                        intent = null
                    }
                }
            }
            if (intent == null) {
                intent = if (AndroidVersione.isAndroid5_1) {
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                } else {
                    // android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                    Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                }
            }
            if (!PermissionUtilsv.areActivityIntent(context, intent)) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }

        /**
         * 是否有使用统计权限
         */
        private fun isGrantedPackagePermission(context: Context): Boolean {
            if (AndroidVersione.isAndroid5) {
                val appOps: AppOpsManager = context.applicationContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode: Int = if (AndroidVersione.isAndroid10) {
                    appOps.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        context.applicationInfo.uid, context.packageName
                    )
                } else {
                    appOps.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        context.applicationInfo.uid, context.packageName
                    )
                }
                return mode == AppOpsManager.MODE_ALLOWED
            }
            return true
        }

        /**
         * 获取使用统计权限设置界面意图
         */
        private fun getPackagePermissionIntent(context: Context): Intent {
            var intent: Intent? = null
            if (AndroidVersione.isAndroid5) {
                intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                if (AndroidVersione.isAndroid10) {
                    // 经过测试，只有在 Android 10 及以上加包名才有效果
                    // 如果在 Android 10 以下加包名会导致无法跳转
                    intent.data = PermissionUtilsv.getPackageNameUri(context)
                }
            }
            if (intent == null || !PermissionUtilsv.areActivityIntent(context, intent)) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }

        /**
         * 是否有 VPN 权限
         */
        private fun isGrantedVpnPermission(context: Context): Boolean {
            return VpnService.prepare(context) == null
        }

        /**
         * 获取 VPN 权限设置界面意图
         */
        private fun getVpnPermissionIntent(context: Context): Intent {
            var intent: Intent? = VpnService.prepare(context)
            if (intent == null || !PermissionUtilsv.areActivityIntent(context, intent)) {
                intent = PermissionUtilsv.getApplicationDetailsIntent(context)
            }
            return intent
        }
    }
}