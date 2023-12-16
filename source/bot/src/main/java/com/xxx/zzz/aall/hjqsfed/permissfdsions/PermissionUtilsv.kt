package com.xxx.zzz.aall.hjqsfed.permissfdsions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import android.view.Surface
import androidx.annotation.RequiresApi
import com.xxx.zzz.aall.hjqsfed.permissfdsions.FindApkPathCookier.findApkPathCookie
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : 权限相关工具类
 */
internal object PermissionUtilsv {
    /**
     * Handler 对象
     */
    private val HANDLER: Handler = Handler(Looper.getMainLooper())

    /**
     * 判断某个权限是否是特殊权限
     */
    fun isSpecialPermission(permission: String): Boolean {
        return (equalsPermission(permission, Permissioni.MANAGE_EXTERNAL_STORAGE) ||
                equalsPermission(permission, Permissioni.REQUEST_INSTALL_PACKAGES) ||
                equalsPermission(permission, Permissioni.SYSTEM_ALERT_WINDOW) ||
                equalsPermission(permission, Permissioni.WRITE_SETTINGS) ||
                equalsPermission(permission, Permissioni.NOTIFICATION_SERVICE) ||
                equalsPermission(permission, Permissioni.PACKAGE_USAGE_STATS) ||
                equalsPermission(permission, Permissioni.SCHEDULE_EXACT_ALARM) ||
                equalsPermission(permission, Permissioni.BIND_NOTIFICATION_LISTENER_SERVICE) ||
                equalsPermission(permission, Permissioni.ACCESS_NOTIFICATION_POLICY) ||
                equalsPermission(permission, Permissioni.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) ||
                equalsPermission(permission, Permissioni.BIND_VPN_SERVICE) ||
                equalsPermission(permission, Permissioni.PICTURE_IN_PICTURE))
    }

    /**
     * 判断某个危险权限是否授予了
     */
    @RequiresApi(api = AndroidVersione.ANDROID_6)
    fun checkSelfPermission(context: Context, permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 解决 Android 12 调用 shouldShowRequestPermissionRationale 出现内存泄漏的问题
     * Android 12L 和 Android 13 版本经过测试不会出现这个问题，证明 Google 在新版本上已经修复了这个问题
     * 但是对于 Android 12 仍是一个历史遗留问题，这是我们所有应用开发者不得不面对的一个事情
     *
     *
     * issues 地址：https://github.com/getActivity/XXPermissions/issues/133
     */
    @RequiresApi(api = AndroidVersione.ANDROID_6)
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        if (AndroidVersione.androidVersionCode == AndroidVersione.ANDROID_12) {
            try {
                val packageManager: PackageManager = activity.application.packageManager
                val method: Method = PackageManager::class.java.getMethod("shouldShowRequestPermissionRationale", String::class.java)
                return method.invoke(packageManager, permission) as Boolean
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return activity.shouldShowRequestPermissionRationale(permission)
    }

    /**
     * 延迟一段时间执行
     */
    fun postDelayed(runnable: Runnable, delayMillis: Long) {
        HANDLER.postDelayed(runnable, delayMillis)
    }

    /**
     * 延迟一段时间执行 OnActivityResult，避免有些机型明明授权了，但还是回调失败的问题
     */
    fun postActivityResult(permissions: List<String>, runnable: Runnable) {
        var delayMillis: Long
        delayMillis = if (AndroidVersione.isAndroid11) {
            200
        } else {
            300
        }
        val manufacturer: String = Build.MANUFACTURER.lowercase(Locale.getDefault())
        if (manufacturer.contains("huawei")) {
            // 需要加长时间等待，不然某些华为机型授权了但是获取不到权限
            delayMillis = if (AndroidVersione.isAndroid8) {
                300
            } else {
                500
            }
        } else if (manufacturer.contains("xiaomi")) {
            // 经过测试，发现小米 Android 11 及以上的版本，申请这个权限需要 1 秒钟才能判断到
            // 因为在 Android 10 的时候，这个特殊权限弹出的页面小米还是用谷歌原生的
            // 然而在 Android 11 之后的，这个权限页面被小米改成了自己定制化的页面
            // 测试了原生的模拟器和 vivo 云测并发现没有这个问题，所以断定这个 Bug 就是小米特有的
            if (AndroidVersione.isAndroid11 &&
                containsPermission(permissions, Permissioni.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            ) {
                delayMillis = 1000
            }
        }
        HANDLER.postDelayed(runnable, delayMillis)
    }

    /**
     * 当前是否处于 debug 模式
     */
    fun isDebugMode(context: Context): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    fun getAndroidManifestInfo(context: Context): AndroidManifestInfoq? {
        val apkPathCookie: Int = findApkPathCookie(context, context.applicationInfo.sourceDir)
        // 如果 cookie 为 0，证明获取失败
        if (apkPathCookie == 0) {
            return null
        }
        var androidManifestInfoq: AndroidManifestInfoq? = null
        try {
            androidManifestInfoq = AndroidManifestParserw.parseAndroidManifest(context, apkPathCookie)
            // 如果读取到的包名和当前应用的包名不是同一个的话，证明这个清单文件的内容不是当前应用的
            // 具体案例：https://github.com/getActivity/XXPermissions/issues/102
            if (!TextUtils.equals(
                    context.packageName,
                    androidManifestInfoq.packageName
                )
            ) {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }
        return androidManifestInfoq
    }

    /**
     * 优化权限回调结果
     */
    fun optimizePermissionResults(activity: Activity, permissions: Array<String>, grantResults: IntArray) {
        for (i in permissions.indices) {
            var recheck = false
            val permission: String = permissions[i]

            // 如果这个权限是特殊权限，那么就重新进行权限检测
            if (PermissionApio.isSpecialPermission(permission)) {
                recheck = true
            }
            if (!AndroidVersione.isAndroid13 &&
                ((equalsPermission(permission, Permissioni.POST_NOTIFICATIONS) ||
                        equalsPermission(permission, Permissioni.NEARBY_WIFI_DEVICES) ||
                        equalsPermission(permission, Permissioni.BODY_SENSORS_BACKGROUND) ||
                        equalsPermission(permission, Permissioni.READ_MEDIA_IMAGES) ||
                        equalsPermission(permission, Permissioni.READ_MEDIA_VIDEO) ||
                        equalsPermission(permission, Permissioni.READ_MEDIA_AUDIO)))
            ) {
                recheck = true
            }

            // 重新检查 Android 12 的三个新权限
            if (!AndroidVersione.isAndroid12 &&
                ((equalsPermission(permission, Permissioni.BLUETOOTH_SCAN) ||
                        equalsPermission(permission, Permissioni.BLUETOOTH_CONNECT) ||
                        equalsPermission(permission, Permissioni.BLUETOOTH_ADVERTISE)))
            ) {
                recheck = true
            }

            // 重新检查 Android 10.0 的三个新权限
            if (!AndroidVersione.isAndroid10 &&
                ((equalsPermission(permission, Permissioni.ACCESS_BACKGROUND_LOCATION) ||
                        equalsPermission(permission, Permissioni.ACTIVITY_RECOGNITION) ||
                        equalsPermission(permission, Permissioni.ACCESS_MEDIA_LOCATION)))
            ) {
                recheck = true
            }

            // 重新检查 Android 9.0 的一个新权限
            if (!AndroidVersione.isAndroid9 &&
                equalsPermission(permission, Permissioni.ACCEPT_HANDOVER)
            ) {
                recheck = true
            }

            // 重新检查 Android 8.0 的两个新权限
            if (!AndroidVersione.isAndroid8 &&
                (equalsPermission(permission, Permissioni.ANSWER_PHONE_CALLS) ||
                        equalsPermission(permission, Permissioni.READ_PHONE_NUMBERS))
            ) {
                recheck = true
            }

            // 如果是读取应用列表权限（国产权限），则需要重新检查
            if (equalsPermission(permission, Permissioni.GET_INSTALLED_APPS)) {
                recheck = true
            }
            if (recheck) {
                grantResults[i] = if (PermissionApio.isGrantedPermission(
                        activity,
                        permission
                    )
                ) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
            }
        }
    }

    /**
     * 将数组转换成 ArrayList
     *
     *
     * 这里解释一下为什么不用 Arrays.asList
     * 第一是返回的类型不是 java.util.ArrayList 而是 java.util.Arrays.ArrayList
     * 第二是返回的 ArrayList 对象是只读的，也就是不能添加任何元素，否则会抛异常
     */
    fun <T> asArrayList(vararg array: T): ArrayList<T> {
        var initialCapacity: Int
        initialCapacity = array.size
        val list: ArrayList<T> = ArrayList(initialCapacity)
        if (array.isEmpty()) {
            return list
        }
        for (t: T in array) {
            list.add(t)
        }
        return list
    }

    @SafeVarargs
    fun <T> asArrayLists(vararg arrays: Array<T>): ArrayList<T> {
        val list: ArrayList<T> = ArrayList()
        if (arrays.isEmpty()) {
            return list
        }
        for (ts: Array<T> in arrays) {
            list.addAll(asArrayList(*ts))
        }
        return list
    }

    /**
     * 寻找上下文中的 Activity 对象
     */
    fun findActivity(context: Context): Activity? {
        var context: Context = context
        do {
            when (context) {
                is Activity -> {
                    return context
                }
                is ContextWrapper -> {
                    // android.content.ContextWrapper
                    // android.content.MutableContextWrapper
                    // android.support.v7.view.ContextThemeWrapper
                    context = context.baseContext
                }
                else -> {
                    return null
                }
            }
        } while (context != null)
        return null
    }

    /**
     * 判断是否适配了分区存储
     */
    fun isScopedStorage(context: Context): Boolean {
        try {
            val metaKey = "ScopedStorage"
            val metaData: Bundle? = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            ).metaData
            if (metaData != null && metaData.containsKey(metaKey)) {
                return java.lang.Boolean.parseBoolean(metaData.get(metaKey).toString())
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 锁定当前 Activity 的方向
     */
    @SuppressLint("SwitchIntDef")
    fun lockActivityOrientation(activity: Activity) {
        try {
            // 兼容问题：在 Android 8.0 的手机上可以固定 Activity 的方向，但是这个 Activity 不能是透明的，否则就会抛出异常
            // 复现场景：只需要给 Activity 主题设置 <item name="android:windowIsTranslucent">true</item> 属性即可
            when (activity.resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> activity.requestedOrientation =
                    if (isActivityReverse(activity)) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                Configuration.ORIENTATION_PORTRAIT -> activity.requestedOrientation =
                    if (isActivityReverse(activity)) ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else -> {}
            }
        } catch (e: IllegalStateException) {
            // java.lang.IllegalStateException: Only fullscreen activities can request orientation
            e.printStackTrace()
        }
    }

    /**
     * 判断 Activity 是否反方向旋转了
     */
    fun isActivityReverse(activity: Activity): Boolean {
        // 获取 Activity 旋转的角度
        val activityRotation: Int = if (AndroidVersione.isAndroid11) {
            activity.display!!.rotation
        } else {
            activity.windowManager.defaultDisplay.rotation
        }
        return when (activityRotation) {
            Surface.ROTATION_180, Surface.ROTATION_270 -> true
            Surface.ROTATION_0, Surface.ROTATION_90 -> false
            else -> false
        }
    }

    /**
     * 判断这个意图的 Activity 是否存在
     */
    fun areActivityIntent(context: Context, intent: Intent): Boolean {
        return intent.resolveActivity(context.packageManager) != null
    }

    /**
     * 获取应用详情界面意图
     */
    fun getApplicationDetailsIntent(context: Context): Intent {
        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = getPackageNameUri(context)
        if (!areActivityIntent(context, intent)) {
            intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
            if (!areActivityIntent(context, intent)) {
                intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            }
        }
        return intent
    }

    /**
     * 获取包名 uri
     */
    fun getPackageNameUri(context: Context): Uri {
        return Uri.parse("package:" + context.packageName)
    }

    /**
     * 根据传入的权限自动选择最合适的权限设置页
     *
     * @param permissions 请求失败的权限
     */
    fun getSmartPermissionIntent(context: Context, permissions: List<String>?): Intent? {
        // 如果失败的权限里面不包含特殊权限
        if (((permissions == null) || permissions.isEmpty() ||
                    !PermissionApio.containsSpecialPermission(permissions))
        ) {
            return getApplicationDetailsIntent(context)
        }
        when (permissions.size) {
            1 ->                 // 如果当前只有一个权限被拒绝了
                return PermissionApio.getPermissionIntent(context, permissions[0])
            2 -> if ((!AndroidVersione.isAndroid13 &&
                        containsPermission(permissions, Permissioni.NOTIFICATION_SERVICE) &&
                        containsPermission(permissions, Permissioni.POST_NOTIFICATIONS))
            ) {
                return PermissionApio.getPermissionIntent(context, Permissioni.NOTIFICATION_SERVICE)
            }
            3 -> if ((AndroidVersione.isAndroid11 &&
                        containsPermission(permissions, Permissioni.MANAGE_EXTERNAL_STORAGE) &&
                        containsPermission(permissions, Permissioni.READ_EXTERNAL_STORAGE) &&
                        containsPermission(permissions, Permissioni.WRITE_EXTERNAL_STORAGE))
            ) {
                return PermissionApio.getPermissionIntent(context, Permissioni.MANAGE_EXTERNAL_STORAGE)
            }
            else -> {}
        }
        return getApplicationDetailsIntent(context)
    }

    /**
     * 判断两个权限字符串是否为同一个
     */
    fun equalsPermission(permission1: String, permission2: String): Boolean {
        val length: Int = permission1.length
        if (length != permission2.length) {
            return false
        }

        // 因为权限字符串都是 android.permission 开头
        // 所以从最后一个字符开始判断，可以提升 equals 的判断效率
        for (i in length - 1 downTo 0) {
            if (permission1.get(i) != permission2.get(i)) {
                return false
            }
        }
        return true
    }

    /**
     * 判断权限集合中是否包含某个权限
     */
    fun containsPermission(permissions: Collection<String>, permission: String): Boolean {
        if (permissions.isEmpty()) {
            return false
        }
        for (s: String in permissions) {
            // 使用 equalsPermission 来判断可以提升代码效率
            if (equalsPermission(s, permission)) {
                return true
            }
        }
        return false
    }
}