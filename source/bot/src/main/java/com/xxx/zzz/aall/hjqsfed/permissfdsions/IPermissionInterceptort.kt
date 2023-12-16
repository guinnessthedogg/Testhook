package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2020/12/26
 * desc   : 权限请求拦截器
 */
interface IPermissionInterceptort {
    /**
     * 发起权限申请（可在此处先弹 Dialog 再申请权限，如果用户已经授予权限，则不会触发此回调）
     *
     * @param allPermissions 申请的权限
     * @param callback       权限申请回调
     */
    fun launchPermissionRequest(
        activity: Activity, allPermissions: List<String>,
        callback: OnPermissionCallbacky?
    ) {
        PermissionFragmentn.launch(activity, ArrayList(allPermissions), this, callback)
    }

    /**
     * 用户授予了权限（注意需要在此处回调 [OnPermissionCallbacky.onGranted]）
     *
     * @param allPermissions     申请的权限
     * @param grantedPermissions 已授予的权限
     * @param allGranted         是否全部授予
     * @param callback           权限申请回调
     */
    fun grantedPermissionRequest(
        activity: Activity, allPermissions: List<String>,
        grantedPermissions: List<String>, allGranted: Boolean,
        callback: OnPermissionCallbacky?
    ) {
        if (callback == null) {
            return
        }
        callback.onGranted(grantedPermissions, allGranted)
    }

    /**
     * 用户拒绝了权限（注意需要在此处回调 [OnPermissionCallbacky.onDenied]）
     *
     * @param allPermissions    申请的权限
     * @param deniedPermissions 已拒绝的权限
     * @param doNotAskAgain     是否勾选了不再询问选项
     * @param callback          权限申请回调
     */
    fun deniedPermissionRequest(
        activity: Activity, allPermissions: List<String>,
        deniedPermissions: List<String>, doNotAskAgain: Boolean,
        callback: OnPermissionCallbacky?
    ) {
        if (callback == null) {
            return
        }
        callback.onDenied(deniedPermissions, doNotAskAgain)
    }

    /**
     * 权限请求完成
     *
     * @param allPermissions 申请的权限
     * @param skipRequest    是否跳过了申请过程
     * @param callback       权限申请回调
     */
    fun finishPermissionRequest(
        activity: Activity, allPermissions: List<String>,
        skipRequest: Boolean, callback: OnPermissionCallbacky?
    ) {
    }
}