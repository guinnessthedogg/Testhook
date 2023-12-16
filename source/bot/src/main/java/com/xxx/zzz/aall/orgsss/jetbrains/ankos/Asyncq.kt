

@file:Suppress("unused")
package com.xxx.zzz.aall.orgsss.anko

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


fun Context.runOnUiThread(f: Context.() -> Unit) {
    if (Looper.getMainLooper() === Looper.myLooper()) f() else ContextHelper.handler.post { f() }
}


@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun Fragment.runOnUiThread(crossinline f: () -> Unit) {
    activity?.runOnUiThread { f() }
}

class AnkoAsyncContext<T>(val weakRef: WeakReference<T>)


fun <T> AnkoAsyncContext<T>.onComplete(f: (T?) -> Unit) {
    val ref = weakRef.get()
    if (Looper.getMainLooper() === Looper.myLooper()) {
        f(ref)
    } else {
        ContextHelper.handler.post { f(ref) }
    }
}


fun <T> AnkoAsyncContext<T>.uiThread(f: (T) -> Unit): Boolean {
    val ref = weakRef.get() ?: return false
    if (Looper.getMainLooper() === Looper.myLooper()) {
        f(ref)
    } else {
        ContextHelper.handler.post { f(ref) }
    }
    return true
}


fun <T: Activity> AnkoAsyncContext<T>.activityUiThread(f: (T) -> Unit): Boolean {
    val activity = weakRef.get() ?: return false
    if (activity.isFinishing) return false
    activity.runOnUiThread { f(activity) }
    return true
}

fun <T: Activity> AnkoAsyncContext<T>.activityUiThreadWithContext(f: Context.(T) -> Unit): Boolean {
    val activity = weakRef.get() ?: return false
    if (activity.isFinishing) return false
    activity.runOnUiThread { activity.f(activity) }
    return true
}

@JvmName("activityContextUiThread")
fun <T: Activity> AnkoAsyncContext<AnkoContext<T>>.activityUiThread(f: (T) -> Unit): Boolean {
    val activity = weakRef.get()?.owner ?: return false
    if (activity.isFinishing) return false
    activity.runOnUiThread { f(activity) }
    return true
}

@JvmName("activityContextUiThreadWithContext")
fun <T: Activity> AnkoAsyncContext<AnkoContext<T>>.activityUiThreadWithContext(f: Context.(T) -> Unit): Boolean {
    val activity = weakRef.get()?.owner ?: return false
    if (activity.isFinishing) return false
    activity.runOnUiThread { activity.f(activity) }
    return true
}

@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
fun <T: Fragment> AnkoAsyncContext<T>.fragmentUiThread(f: (T) -> Unit): Boolean {
    val fragment = weakRef.get() ?: return false
    if (fragment.isDetached) return false
    val activity = fragment.activity ?: return false
    activity.runOnUiThread { f(fragment) }
    return true
}

@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
fun <T: Fragment> AnkoAsyncContext<T>.fragmentUiThreadWithContext(f: Context.(T) -> Unit): Boolean {
    val fragment = weakRef.get() ?: return false
    if (fragment.isDetached) return false
    val activity = fragment.activity ?: return false
    activity.runOnUiThread { activity.f(fragment) }
    return true
}
private val crashLogger = { throwable : Throwable -> throwable.printStackTrace() }

fun <T> T.doAsync(
    exceptionHandler: ((Throwable) -> Unit)? = crashLogger,
    task: AnkoAsyncContext<T>.() -> Unit
): Future<Unit> {
    val context = AnkoAsyncContext(WeakReference(this))
    return BackgroundExecutor.submit {
        return@submit try {
            context.task()
        } catch (thr: Throwable) {
            val result = exceptionHandler?.invoke(thr)
            if (result != null) {
                result
            } else {
                Unit
            }
        }
    }
}

fun <T> T.doAsync(
    exceptionHandler: ((Throwable) -> Unit)? = crashLogger,
    executorService: ExecutorService,
    task: AnkoAsyncContext<T>.() -> Unit
): Future<Unit> {
    val context = AnkoAsyncContext(WeakReference(this))
    return executorService.submit<Unit> {
        try {
            context.task()
        } catch (thr: Throwable) {
            exceptionHandler?.invoke(thr)
        }
    }
}

fun <T, R> T.doAsyncResult(
    exceptionHandler: ((Throwable) -> Unit)? = crashLogger,
    task: AnkoAsyncContext<T>.() -> R
): Future<R> {
    val context = AnkoAsyncContext(WeakReference(this))
    return BackgroundExecutor.submit {
        try {
            context.task()
        } catch (thr: Throwable) {
            exceptionHandler?.invoke(thr)
            throw thr
        }
    }
}

fun <T, R> T.doAsyncResult(
    exceptionHandler: ((Throwable) -> Unit)? = crashLogger,
    executorService: ExecutorService,
    task: AnkoAsyncContext<T>.() -> R
): Future<R> {
    val context = AnkoAsyncContext(WeakReference(this))
    return executorService.submit<R> {
        try {
            context.task()
        } catch (thr: Throwable) {
            exceptionHandler?.invoke(thr)
            throw thr
        }
    }
}

internal object BackgroundExecutor {
    var executor: ExecutorService =
        Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors())

    fun <T> submit(task: () -> T): Future<T> = executor.submit(task)

}

private object ContextHelper {
    val handler = Handler(Looper.getMainLooper())
}
