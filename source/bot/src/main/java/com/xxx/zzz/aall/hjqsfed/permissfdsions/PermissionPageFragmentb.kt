package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/01/17
 * desc   : 权限页跳转 Fragment
 */
class PermissionPageFragmentb : Fragment(), Runnable {
    /**
     * 权限回调对象
     */
    private var mCallBack: OnPermissionPageCallbacku? = null

    /**
     * 权限申请标记
     */
    private var mRequestFlag: Boolean = false

    /**
     * 是否申请了权限
     */
    private var mStartActivityFlag: Boolean = false

    /**
     * 绑定 Activity
     */
    fun attachActivity(activity: Activity) {
        activity.fragmentManager.beginTransaction().add(this, this.toString()).commitAllowingStateLoss()
    }

    /**
     * 解绑 Activity
     */
    fun detachActivity(activity: Activity) {
        activity.fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
    }

    /**
     * 设置权限监听回调监听
     */
    fun setCallBack(callback: OnPermissionPageCallbacku?) {
        mCallBack = callback
    }

    /**
     * 权限申请标记（防止系统杀死应用后重新触发请求的问题）
     */
    fun setRequestFlag(flag: Boolean) {
        mRequestFlag = flag
    }

    override fun onResume() {
        super.onResume()

        // 如果当前 Fragment 是通过系统重启应用触发的，则不进行权限申请
        if (!mRequestFlag) {
            detachActivity(activity)
            return
        }
        if (mStartActivityFlag) {
            return
        }
        mStartActivityFlag = true
        val arguments: Bundle? = arguments
        val activity: Activity? = activity
        if (arguments == null || activity == null) {
            return
        }
        val permissions: List<String>? = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        startActivityForResult(PermissionUtilsv.getSmartPermissionIntent(getActivity(), permissions), XXPermissionsv.Companion.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != XXPermissionsv.REQUEST_CODE) {
            return
        }
        val activity: Activity? = activity
        val arguments: Bundle? = arguments
        if (activity == null || arguments == null) {
            return
        }
        val allPermissions: ArrayList<String>? = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        if (allPermissions == null || allPermissions.isEmpty()) {
            return
        }
        PermissionUtilsv.postActivityResult(allPermissions, this)
    }

    override fun run() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        if (!isAdded) {
            return
        }
        val activity: Activity = activity ?: return
        val callback: OnPermissionPageCallbacku? = mCallBack
        mCallBack = null
        if (callback == null) {
            detachActivity(activity)
            return
        }
        val arguments: Bundle = arguments
        val allPermissions: List<String>? = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        val grantedPermissions: List<String> = PermissionApio.getGrantedPermissions(activity, (allPermissions)!!)
        if (grantedPermissions.size == allPermissions.size) {
            callback.onGranted()
        } else {
            callback.onDenied()
        }
        detachActivity(activity)
    }

    companion object {
        /**
         * 请求的权限组
         */
        private val REQUEST_PERMISSIONS: String = "request_permissions"

        /**
         * 开启权限申请
         */
        fun beginRequest(
            activity: Activity, permissions: ArrayList<String>,
            callback: OnPermissionPageCallbacku?
        ) {
            val fragment = PermissionPageFragmentb()
            val bundle = Bundle()
            bundle.putStringArrayList(REQUEST_PERMISSIONS, permissions)
            fragment.arguments = bundle
            // 设置保留实例，不会因为屏幕方向或配置变化而重新创建
            fragment.retainInstance = true
            // 设置权限申请标记
            fragment.setRequestFlag(true)
            // 设置权限回调监听
            fragment.setCallBack(callback)
            // 绑定到 Activity 上面
            fragment.attachActivity(activity)
        }
    }
}