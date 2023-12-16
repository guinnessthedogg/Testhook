package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : Android 危险权限请求类
 */
class XXPermissionsv
/**
 * 私有化构造函数
 */ private constructor(
    /**
     * Context 对象
     */
    private val mContext: Context?
) {
    /**
     * 申请的权限列表
     */
    private val mPermissions: MutableList<String> = ArrayList()

    /**
     * 权限请求拦截器
     */
    private var mInterceptor: IPermissionInterceptort? = null

    /**
     * 设置不检查
     */
    private var mCheckMode: Boolean? = null
    /* android.app.Fragment */
    /**
     * 添加权限组
     */
    fun permission(vararg permissions: String): XXPermissionsv {
        return permission(PermissionUtilsv.asArrayList(*permissions))
    }

    fun permission(vararg permissions: Array<String>): XXPermissionsv {
        return permission(PermissionUtilsv.asArrayLists(*(permissions)))
    }

    fun permission(permissions: List<String>?): XXPermissionsv {
        if (permissions == null || permissions.isEmpty()) {
            return this
        }
        for (permission: String in permissions) {
            if (PermissionUtilsv.containsPermission(mPermissions, permission)) {
                continue
            }
            mPermissions.add(permission)
        }
        return this
    }

    /**
     * 设置权限请求拦截器
     */
    fun interceptor(interceptor: IPermissionInterceptort?): XXPermissionsv {
        mInterceptor = interceptor
        return this
    }

    /**
     * 设置不触发错误检测机制
     */
    fun unchecked(): XXPermissionsv {
        mCheckMode = false
        return this
    }

    /**
     * 请求权限
     */
    fun request(callback: OnPermissionCallbacky?) {
        if (mContext == null) {
            return
        }
        if (mInterceptor == null) {
            mInterceptor = interceptor
        }
        val context: Context = mContext
        val interceptor: IPermissionInterceptort? = mInterceptor

        // 权限请求列表（为什么直接不用字段？因为框架要兼容新旧权限，在低版本下会自动添加旧权限申请）
        val permissions: MutableList<String> = ArrayList(mPermissions)
        val checkMode: Boolean = isCheckMode(context)

        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        val activity: Activity? = PermissionUtilsv.findActivity(context)
        if (!PermissionCheckerp.checkActivityStatus(activity, checkMode)) {
            return
        }

        // 必须要传入正常的权限或者权限组才能申请权限
        if (!PermissionCheckerp.checkPermissionArgument(permissions, checkMode)) {
            return
        }
        if (checkMode) {
            // 获取清单文件信息
            val androidManifestInfoq: AndroidManifestInfoq? = PermissionUtilsv.getAndroidManifestInfo(context)
            // 检查申请的读取媒体位置权限是否符合规范
            PermissionCheckerp.checkMediaLocationPermission(context, permissions)
            // 检查申请的存储权限是否符合规范
            PermissionCheckerp.checkStoragePermission(context, permissions, androidManifestInfoq)
            // 检查申请的传感器权限是否符合规范
            PermissionCheckerp.checkBodySensorsPermission(permissions)
            // 检查申请的定位权限是否符合规范
            PermissionCheckerp.checkLocationPermission(permissions)
            // 检查申请的画中画权限是否符合规范
            PermissionCheckerp.checkPictureInPicturePermission((activity)!!, permissions, androidManifestInfoq)
            // 检查申请的通知栏监听权限是否符合规范
            PermissionCheckerp.checkNotificationListenerPermission(permissions, androidManifestInfoq)
            // 检查蓝牙和 WIFI 权限申请是否符合规范
            PermissionCheckerp.checkNearbyDevicesPermission(permissions, androidManifestInfoq)
            // 检查申请的权限和 targetSdk 版本是否能吻合
            PermissionCheckerp.checkTargetSdkVersion(context, permissions)
            // 检测权限有没有在清单文件中注册
            PermissionCheckerp.checkManifestPermissions(context, permissions, androidManifestInfoq)
        }

        // 优化所申请的权限列表
        PermissionCheckerp.optimizeDeprecatedPermission(permissions)
        if (PermissionApio.isGrantedPermissions(context, permissions)) {
            // 证明这些权限已经全部授予过，直接回调成功
            if (callback != null) {
                interceptor!!.grantedPermissionRequest((activity)!!, permissions, permissions, true, callback)
                interceptor.finishPermissionRequest((activity), permissions, true, callback)
            }
            return
        }

        // 申请没有授予过的权限
        interceptor!!.launchPermissionRequest((activity)!!, permissions, callback)
    }

    /**
     * 撤销权限并杀死当前进程
     *
     * @return 返回 true 代表成功，返回 false 代表失败
     */
    fun revokeOnKill(): Boolean {
        if (mContext == null) {
            return false
        }
        val context: Context = mContext
        val permissions: List<String> = mPermissions
        if (permissions.isEmpty()) {
            return false
        }
        if (!AndroidVersione.isAndroid13) {
            return false
        }
        try {
            if (permissions.size == 1) {
                // API 文档：https://developer.android.google.cn/reference/android/content/Context#revokeSelfPermissionOnKill(java.lang.String)
//                context.revokeSelfPermissionOnKill(permissions.get(0));
            } else {
                // API 文档：https://developer.android.google.cn/reference/android/content/Context#revokeSelfPermissionsOnKill(java.util.Collection%3Cjava.lang.String%3E)
//                context.revokeSelfPermissionsOnKill(permissions);
            }
            return true
        } catch (e: IllegalArgumentException) {
            if (isCheckMode(context)) {
                throw e
            }
            e.printStackTrace()
            return false
        }
    }

    /**
     * 当前是否为检测模式
     */
    private fun isCheckMode(context: Context): Boolean {
        if (mCheckMode == null) {
            if (sCheckMode == null) {
                sCheckMode = PermissionUtilsv.isDebugMode(context)
            }
            mCheckMode = sCheckMode
        }
        return (mCheckMode)!!
    }

    companion object {
        /**
         * 权限设置页跳转请求码
         */
        val REQUEST_CODE: Int = 1024 + 1

        /**
         * 权限请求拦截器
         */
        private var sInterceptor: IPermissionInterceptort? = null

        /**
         * 当前是否为检查模式
         */
        private var sCheckMode: Boolean? = null

        /**
         * 设置请求的对象
         *
         * @param context 当前 Activity，可以传入栈顶的 Activity
         */
        fun with(context: Context): XXPermissionsv {
            return XXPermissionsv(context)
        }

        fun with(fragment: Fragment): XXPermissionsv {
            return with(fragment.activity)
        }

        /**
         * 是否为检查模式
         */
        fun setCheckMode(checkMode: Boolean) {
            sCheckMode = checkMode
        }
        /**
         * 获取全局权限请求拦截器
         */
        /**
         * 设置全局权限请求拦截器
         */
        var interceptor: IPermissionInterceptort?
            get() {
                if (sInterceptor == null) {
                    sInterceptor = object : IPermissionInterceptort {}
                }
                return sInterceptor
            }
            set(interceptor) {
                sInterceptor = interceptor
            }

        /**
         * 判断一个或多个权限是否全部授予了
         */
        fun isGranted(context: Context, vararg permissions: String): Boolean {
            return isGranted(context, PermissionUtilsv.asArrayList(*permissions))
        }

        fun isGranted(context: Context, vararg permissions: Array<String>): Boolean {
            return isGranted(context, PermissionUtilsv.asArrayLists(*(permissions)))
        }

        fun isGranted(context: Context, permissions: List<String>): Boolean {
            return PermissionApio.isGrantedPermissions(context, permissions)
        }

        /**
         * 获取没有授予的权限
         */
        fun getDenied(context: Context, vararg permissions: String): List<String>? {
            return getDenied(context, PermissionUtilsv.asArrayList(*permissions))
        }

        fun getDenied(context: Context, vararg permissions: Array<String>): List<String>? {
            return getDenied(context, PermissionUtilsv.asArrayLists(*(permissions)))
        }

        fun getDenied(context: Context, permissions: List<String>): List<String>? {
            return PermissionApio.getDeniedPermissions(context, permissions)
        }

        /**
         * 判断某个权限是否为特殊权限
         */
        fun isSpecial(permission: String): Boolean {
            return PermissionApio.isSpecialPermission(permission)
        }

        /**
         * 判断权限列表中是否包含特殊权限
         */
        fun containsSpecial(vararg permissions: String): Boolean {
            return containsSpecial(PermissionUtilsv.asArrayList(*permissions))
        }

        fun containsSpecial(permissions: List<String>): Boolean {
            return PermissionApio.containsSpecialPermission(permissions)
        }

        /**
         * 判断一个或多个权限是否被永久拒绝了
         *
         *
         * 注意不能在请求权限之前调用，一定要在 [OnPermissionCallbacky.onDenied] 方法中调用
         * 如果你在应用启动后，没有申请过这个权限，然后去判断它有没有永久拒绝，这样系统会一直返回 true，也就是永久拒绝
         * 但是实际并没有永久拒绝，系统只是不想让你知道权限是否被永久拒绝了，你必须要申请过这个权限，才能去判断这个权限是否被永久拒绝
         */
        fun isPermanentDenied(activity: Activity, vararg permissions: String): Boolean {
            return isPermanentDenied(activity, PermissionUtilsv.asArrayList(*permissions))
        }

        fun isPermanentDenied(activity: Activity, vararg permissions: Array<String>): Boolean {
            return isPermanentDenied(activity, PermissionUtilsv.asArrayLists(*(permissions)))
        }

        fun isPermanentDenied(activity: Activity, permissions: List<String>): Boolean {
            return PermissionApio.isPermissionPermanentDenied(activity, permissions)
        }

        fun startPermissionActivity(context: Context, vararg permissions: String) {
            startPermissionActivity(context, PermissionUtilsv.asArrayList(*permissions))
        }

        fun startPermissionActivity(context: Context, vararg permissions: Array<String>) {
            startPermissionActivity(context, PermissionUtilsv.asArrayLists(*(permissions)))
        }

        /**
         * 跳转到应用权限设置页
         *
         * @param permissions 没有授予或者被拒绝的权限组
         */
        @JvmOverloads
        fun startPermissionActivity(context: Context, permissions: List<String> = ArrayList(0)) {
            val activity: Activity? = PermissionUtilsv.findActivity(context)
            if (activity != null) {
                startPermissionActivity(activity, permissions)
                return
            }
            val intent: Intent? = PermissionUtilsv.getSmartPermissionIntent(context, permissions)
            if (!(context is Activity)) {
                intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        fun startPermissionActivity(
            activity: Activity,
            vararg permissions: String
        ) {
            startPermissionActivity(activity, PermissionUtilsv.asArrayList(*permissions))
        }

        fun startPermissionActivity(
            activity: Activity,
            vararg permissions: Array<String>
        ) {
            startPermissionActivity(activity, PermissionUtilsv.asArrayLists(*(permissions)))
        }

        /* android.content.Context */
        @JvmOverloads
        fun startPermissionActivity(
            activity: Activity,
            permissions: List<String> = ArrayList(0),
            requestCode: Int = REQUEST_CODE
        ) {
            val intent: Intent? = PermissionUtilsv.getSmartPermissionIntent(activity, permissions)
            activity.startActivityForResult(intent, requestCode)
        }

        fun startPermissionActivity(
            activity: Activity,
            permission: String,
            callback: OnPermissionPageCallbacku?
        ) {
            startPermissionActivity(activity, PermissionUtilsv.asArrayList(permission), callback)
        }

        fun startPermissionActivity(
            activity: Activity,
            permissions: Array<String>,
            callback: OnPermissionPageCallbacku?
        ) {
            startPermissionActivity(activity, permissions, callback)
        }

        fun startPermissionActivity(
            activity: Activity,
            permissions: List<String>,
            callback: OnPermissionPageCallbacku?
        ) {
            if (permissions.isEmpty()) {
                activity.startActivity(PermissionUtilsv.getApplicationDetailsIntent(activity))
                return
            }
            PermissionPageFragmentb.Companion.beginRequest(activity, ArrayList(permissions), callback)
        }

        fun startPermissionActivity(
            fragment: Fragment,
            vararg permissions: String
        ) {
            startPermissionActivity(fragment, PermissionUtilsv.asArrayList(*permissions))
        }

        fun startPermissionActivity(
            fragment: Fragment,
            vararg permissions: Array<String>
        ) {
            startPermissionActivity(fragment, PermissionUtilsv.asArrayLists(*(permissions)))
        }

        /* android.app.Activity */
        @JvmOverloads
        fun startPermissionActivity(
            fragment: Fragment,
            permissions: List<String> = ArrayList(0),
            requestCode: Int = REQUEST_CODE
        ) {
            val activity: Activity = fragment.activity ?: return
            if (permissions.isEmpty()) {
                fragment.startActivity(PermissionUtilsv.getApplicationDetailsIntent(activity))
                return
            }
            val intent: Intent? = PermissionUtilsv.getSmartPermissionIntent(activity, permissions)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun startPermissionActivity(
            fragment: Fragment,
            permission: String,
            callback: OnPermissionPageCallbacku?
        ) {
            startPermissionActivity(fragment, PermissionUtilsv.asArrayList(permission), callback)
        }

        fun startPermissionActivity(
            fragment: Fragment,
            permissions: Array<String>,
            callback: OnPermissionPageCallbacku?
        ) {
            startPermissionActivity(fragment, permissions, callback)
        }

        fun startPermissionActivity(
            fragment: Fragment,
            permissions: List<String>,
            callback: OnPermissionPageCallbacku?
        ) {
            val activity: Activity? = fragment.activity
            if (activity == null || activity.isFinishing) {
                return
            }
            if (AndroidVersione.isAndroid4_2 && activity.isDestroyed) {
                return
            }
            if (permissions.isEmpty()) {
                fragment.startActivity(PermissionUtilsv.getApplicationDetailsIntent(activity))
                return
            }
            PermissionPageFragmentb.Companion.beginRequest(activity, permissions as ArrayList<String>, callback)
        }
    }
}