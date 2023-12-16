package com.xxx.zzz.aall.hjqsfed.permissfdsions

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/11/11
 * desc   : 清单文件解析 Bean 类
 */
internal class AndroidManifestInfoq {
    /**
     * 权限节点信息
     */
    val permissionInfoList: MutableList<PermissionInfo?> = ArrayList()

    /**
     * Activity 节点信息
     */
    val activityInfoList: MutableList<ActivityInfo?> = ArrayList()

    /**
     * Service 节点信息
     */
    val serviceInfoList: MutableList<ServiceInfo?> = ArrayList()

    /**
     * 应用包名
     */
    var packageName: String? = null

    /**
     * 使用 sdk 信息
     */
    var usesSdkInfo: UsesSdkInfo? = null

    /**
     * Application 节点信息
     */
    var applicationInfo: ApplicationInfo? = null

    internal class UsesSdkInfo {
        var minSdkVersion = 0
    }

    internal class PermissionInfo {
        var name: String? = null
        var maxSdkVersion = 0
        var usesPermissionFlags = 0
        fun neverForLocation(): Boolean {
            return usesPermissionFlags and REQUESTED_PERMISSION_NEVER_FOR_LOCATION != 0
        }

        companion object {
            /**
             * [PackageInfo.REQUESTED_PERMISSION_NEVER_FOR_LOCATION]
             */
            private const val REQUESTED_PERMISSION_NEVER_FOR_LOCATION = 0x00010000
        }
    }

    internal class ApplicationInfo {
        var name: String? = null
        var requestLegacyExternalStorage = false
    }

    internal class ActivityInfo {
        var name: String? = null
        var supportsPictureInPicture = false
    }

    internal class ServiceInfo {
        var name: String? = null
        var permission: String? = null
    }
}