package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2021/12/31
 * desc   : 权限判断类
 */
internal object PermissionApio {
    private val DELEGATE: PermissionDelegatem

    init {
        if (AndroidVersione.isAndroid13) {
            DELEGATE = PermissionDelegatemImplV33()
        } else if (AndroidVersione.isAndroid12) {
            DELEGATE = PermissionDelegatemImplV31()
        } else if (AndroidVersione.isAndroid11) {
            DELEGATE = PermissionDelegatemImplV30()
        } else if (AndroidVersione.isAndroid10) {
            DELEGATE = PermissionDelegatemImplV29()
        } else if (AndroidVersione.isAndroid9) {
            DELEGATE = PermissionDelegatemImplV28()
        } else if (AndroidVersione.isAndroid8) {
            DELEGATE = PermissionDelegatemImplV26()
        } else if (AndroidVersione.isAndroid6) {
            DELEGATE = PermissionDelegatemImplV23()
        } else {
            DELEGATE = PermissionDelegatemImplV14()
        }
    }

    /**
     * 判断某个权限是否授予
     */
    fun isGrantedPermission(context: Context, permission: String): Boolean {
        return DELEGATE.isGrantedPermission(context, permission)
    }

    /**
     * 判断某个权限是否被永久拒绝
     */
    fun isPermissionPermanentDenied(activity: Activity, permission: String): Boolean {
        return DELEGATE.isPermissionPermanentDenied(activity, permission)
    }

    /**
     * 获取权限设置页意图
     */
    fun getPermissionIntent(context: Context, permission: String): Intent? {
        return DELEGATE.getPermissionIntent(context, permission)
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    fun isSpecialPermission(permission: String): Boolean {
        return PermissionUtilsv.isSpecialPermission(permission)
    }

    /**
     * 判断某个权限集合是否包含特殊权限
     */
    fun containsSpecialPermission(permissions: List<String>?): Boolean {
        if (permissions == null || permissions.isEmpty()) {
            return false
        }
        for (permission in permissions) {
            if (isSpecialPermission(permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 判断某些权限是否全部被授予
     */
    fun isGrantedPermissions(context: Context, permissions: List<String>): Boolean {
        if (permissions.isEmpty()) {
            return false
        }
        for (permission in permissions) {
            if (!isGrantedPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    /**
     * 获取已经授予的权限
     */
    fun getGrantedPermissions(context: Context, permissions: List<String>): List<String> {
        val grantedPermission: MutableList<String> = ArrayList(permissions.size)
        for (permission in permissions) {
            if (isGrantedPermission(context, permission)) {
                grantedPermission.add(permission)
            }
        }
        return grantedPermission
    }

    /**
     * 获取已经拒绝的权限
     */
    fun getDeniedPermissions(context: Context, permissions: List<String>): List<String> {
        val deniedPermission: MutableList<String> = ArrayList(permissions.size)
        for (permission in permissions) {
            if (!isGrantedPermission(context, permission)) {
                deniedPermission.add(permission)
            }
        }
        return deniedPermission
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     *
     * @param activity    Activity对象
     * @param permissions 请求的权限
     */
    fun isPermissionPermanentDenied(activity: Activity, permissions: List<String>): Boolean {
        for (permission in permissions) {
            if (isPermissionPermanentDenied(activity, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    fun getDeniedPermissions(permissions: List<String>, grantResults: IntArray): List<String> {
        val deniedPermissions: MutableList<String> = ArrayList()
        for (i in grantResults.indices) {
            // 把没有授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i])
            }
        }
        return deniedPermissions
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions  需要请求的权限组
     * @param grantResults 允许结果组
     */
    fun getGrantedPermissions(permissions: List<String>, grantResults: IntArray): List<String> {
        val grantedPermissions: MutableList<String> = ArrayList()
        for (i in grantResults.indices) {
            // 把授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions[i])
            }
        }
        return grantedPermissions
    }
}