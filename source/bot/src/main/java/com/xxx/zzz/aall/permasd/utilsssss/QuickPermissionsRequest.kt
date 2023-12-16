package com.xxx.zzz.aall.permasd.utilsssss

import androidx.annotation.Keep

@Keep
data class QuickPermissionsRequest(
    private var target: PermissionCheckerFragment,
    var permissions: Array<String> = emptyArray(),
    var handleRationale: Boolean = true,
    var rationaleMessage: String = "",
    var handlePermanentlyDenied: Boolean = true,
    var permanentlyDeniedMessage: String = "",
    internal var rationaleMethod: ((QuickPermissionsRequest) -> Unit)? = null,
    internal var permanentDeniedMethod: ((QuickPermissionsRequest) -> Unit)? = null,
    internal var permissionsDeniedMethod: ((QuickPermissionsRequest) -> Unit)? = null,
    var deniedPermissions: Array<String> = emptyArray(),
    var permanentlyDeniedPermissions: Array<String> = emptyArray()
) {
    
    fun proceed() = target.requestPermissionsFromUser()

    
    fun cancel() = target.clean()

    
    fun openAppSettings() = target.openAppSettings()
}