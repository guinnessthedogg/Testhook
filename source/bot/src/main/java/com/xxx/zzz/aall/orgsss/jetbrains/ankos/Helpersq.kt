

@file:Suppress("unused")
package com.xxx.zzz.aall.orgsss.anko

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Build
import androidx.annotation.Keep

open class AnkoException(message: String = "") : RuntimeException(message)


val Int.gray: Int
    get() = this or (this shl 8) or (this shl 16)


val Int.opaque: Int
    get() = this or 0xff000000.toInt()


fun Int.withAlpha(alpha: Int): Int {
    require(alpha in 0..0xFF)
    return this and 0x00FFFFFF or (alpha shl 24)
}

enum class ScreenSize {
    SMALL,
    NORMAL,
    LARGE,
    XLARGE
}

enum class UiMode {
    NORMAL,
    CAR,
    DESK,
    TELEVISION,
    APPLIANCE,
    WATCH
}

enum class Orientation {
    PORTRAIT,
    LANDSCAPE,
    SQUARE
}


inline fun <T: Any> Context.configuration(
    screenSize: ScreenSize? = null,
    density: ClosedRange<Int>? = null,
    language: String? = null,
    orientation: Orientation? = null,
    long: Boolean? = null,
    fromSdk: Int? = null,
    sdk: Int? = null,
    uiMode: UiMode? = null,
    nightMode: Boolean? = null,
    rightToLeft: Boolean? = null,
    smallestWidth: Int? = null,
    f: () -> T
): T? = if (AnkoInternals.testConfiguration(
        this, screenSize, density, language, orientation, long,
        fromSdk, sdk, uiMode, nightMode, rightToLeft, smallestWidth
    )
) f() else null


inline fun <T: Any> Activity.configuration(
    screenSize: ScreenSize? = null,
    density: ClosedRange<Int>? = null,
    language: String? = null,
    orientation: Orientation? = null,
    long: Boolean? = null,
    fromSdk: Int? = null,
    sdk: Int? = null,
    uiMode: UiMode? = null,
    nightMode: Boolean? = null,
    rightToLeft: Boolean? = null,
    smallestWidth: Int? = null,
    f: () -> T
): T? = if (AnkoInternals.testConfiguration(
        this, screenSize, density, language, orientation, long,
        fromSdk, sdk, uiMode, nightMode, rightToLeft, smallestWidth
    )
) f() else null


inline fun <T: Any> AnkoContext<*>.configuration(
    screenSize: ScreenSize? = null,
    density: ClosedRange<Int>? = null,
    language: String? = null,
    orientation: Orientation? = null,
    long: Boolean? = null,
    fromSdk: Int? = null,
    sdk: Int? = null,
    uiMode: UiMode? = null,
    nightMode: Boolean? = null,
    rightToLeft: Boolean? = null,
    smallestWidth: Int? = null,
    f: () -> T
): T? = if (AnkoInternals.testConfiguration(
        ctx, screenSize, density, language, orientation, long,
        fromSdk, sdk, uiMode, nightMode, rightToLeft, smallestWidth
    )
) f() else null


@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun <T: Any> Fragment.configuration(
    screenSize: ScreenSize? = null,
    density: ClosedRange<Int>? = null,
    language: String? = null,
    orientation: Orientation? = null,
    long: Boolean? = null,
    fromSdk: Int? = null,
    sdk: Int? = null,
    uiMode: UiMode? = null,
    nightMode: Boolean? = null,
    rightToLeft: Boolean? = null,
    smallestWidth: Int? = null,
    f: () -> T
): T? {
    val act = activity
    return if (act != null) {
        if (AnkoInternals.testConfiguration(
                act, screenSize, density, language, orientation, long,
                fromSdk, sdk, uiMode, nightMode, rightToLeft, smallestWidth
            )
        ) f() else null
    }
    else null
}


inline fun doBeforeSdk(version: Int, f: () -> Unit) {
    if (Build.VERSION.SDK_INT <= version) f()
}


inline fun doFromSdk(version: Int, f: () -> Unit) {
    if (Build.VERSION.SDK_INT >= version) f()
}


inline fun doIfSdk(version: Int, f: () -> Unit) {
    if (Build.VERSION.SDK_INT == version) f()
}

@Keep
data class AttemptResult<out T> @PublishedApi internal constructor(val value: T?, val error: Throwable?) {
    inline fun <R> then(f: (T) -> R): AttemptResult<R> {
        if (isError) {
            @Suppress("UNCHECKED_CAST")
            return this as AttemptResult<R>
        }

        return attempt { f(value as T) }
    }

    inline val isError: Boolean
        get() = error != null

    inline val hasValue: Boolean
        get() = error == null
}


inline fun <T> attempt(f: () -> T): AttemptResult<T> {
    var value: T? = null
    var error: Throwable? = null
    try {
        value = f()
    } catch(t: Throwable) {
        error = t
    }
    return AttemptResult(value, error)
}
