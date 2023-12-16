package com.xxx.zzz.aall.permasd.utilsssss


import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri.fromParts
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.core.app.ActivityCompat
import com.xxx.zzz.aall.orgsss.jetbrains.ankos.jetbrains.dialogs.alert


class PermissionCheckerFragment : Fragment() {

    private var quickPermissionsRequest: QuickPermissionsRequest? = null

    interface QuickPermissionsCallback {
        fun shouldShowRequestPermissionsRationale(quickPermissionsRequest: QuickPermissionsRequest?)
        fun onPermissionsGranted(quickPermissionsRequest: QuickPermissionsRequest?)
        fun onPermissionsPermanentlyDenied(quickPermissionsRequest: QuickPermissionsRequest?)
        fun onPermissionsDenied(quickPermissionsRequest: QuickPermissionsRequest?)
    }

    companion object {
        private const val TAG = "QuickPermissionsKotlin"
        private const val PERMISSIONS_REQUEST_CODE = 199
        fun newInstance(): PermissionCheckerFragment = PermissionCheckerFragment()
    }

    private var mListener: QuickPermissionsCallback? = null

    fun setListener(listener: QuickPermissionsCallback) {
        mListener = listener
        Log.d(TAG, "onCreate: listeners set")
    }

    private fun removeListener() {
        mListener = null
    }

    @Deprecated("Deprecated in Java")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate: permission fragment created")
    }

    fun setRequestPermissionsRequest(quickPermissionsRequest: QuickPermissionsRequest?) {
        this.quickPermissionsRequest = quickPermissionsRequest
    }

    private fun removeRequestPermissionsRequest() {
        quickPermissionsRequest = null
    }

    fun clean() {
        if (quickPermissionsRequest != null) {


            if (quickPermissionsRequest?.deniedPermissions?.size ?: 0 > 0)
                mListener?.onPermissionsDenied(quickPermissionsRequest)

            removeRequestPermissionsRequest()
            removeListener()
        } else {
            Log.w(
                TAG, "clean: QuickPermissionsRequest has already completed its flow. " +
                        "No further callbacks will be called for the current flow."
            )
        }
    }

    fun requestPermissionsFromUser() {
        if (quickPermissionsRequest != null) {
            Log.d(TAG, "requestPermissionsFromUser: requesting permissions")
            requestPermissions(quickPermissionsRequest?.permissions.orEmpty(), PERMISSIONS_REQUEST_CODE)
        } else {
            Log.w(
                TAG, "requestPermissionsFromUser: QuickPermissionsRequest has already completed its flow. " +
                        "Cannot request permissions again from the request received from the callback. " +
                        "You can start the new flow by calling runWithPermissions() { } again."
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "passing callback")


        handlePermissionResult(permissions, grantResults)
    }


    private fun handlePermissionResult(permissions: Array<String>, grantResults: IntArray) {





        if (permissions.isEmpty()) {
            Log.w(TAG, "handlePermissionResult: Permissions result discarded. You might have called multiple permissions request simultaneously")
            return
        }

        if (PermissionsUtil.hasSelfPermission(context, permissions)) {

            quickPermissionsRequest?.deniedPermissions = emptyArray()


            mListener?.onPermissionsGranted(quickPermissionsRequest)


            clean()
        } else {

            val deniedPermissions = PermissionsUtil.getDeniedPermissions(permissions, grantResults)
            quickPermissionsRequest?.deniedPermissions = deniedPermissions


            var shouldShowRationale = true
            var isPermanentlyDenied = false
            for (i in 0 until deniedPermissions.size) {
                val deniedPermission = deniedPermissions[i]
                val rationale = shouldShowRequestPermissionRationale(deniedPermission)
                if (!rationale) {
                    shouldShowRationale = false
                    isPermanentlyDenied = true
                    break
                }
            }

            if (quickPermissionsRequest?.handlePermanentlyDenied == true && isPermanentlyDenied) {

                quickPermissionsRequest?.permanentDeniedMethod?.let {

                    quickPermissionsRequest?.permanentlyDeniedPermissions =
                        PermissionsUtil.getPermanentlyDeniedPermissions(
                            this,
                            permissions,
                            grantResults
                        )
                    mListener?.onPermissionsPermanentlyDenied(quickPermissionsRequest)
                    return
                }

                activity?.alert {
                    message = quickPermissionsRequest?.permanentlyDeniedMessage.orEmpty()
                    positiveButton("!!!SETTINGS") {
                        openAppSettings()
                    }
                    negativeButton("!!!CANCEL") {
                        clean()
                    }
                }?.apply { isCancelable = false }?.show()
                return
            }


            if (quickPermissionsRequest?.handleRationale == true && shouldShowRationale) {

                quickPermissionsRequest?.rationaleMethod?.let {
                    mListener?.shouldShowRequestPermissionsRationale(quickPermissionsRequest)
                    return
                }

                activity?.alert {
                    message = quickPermissionsRequest?.rationaleMessage.orEmpty()
                    positiveButton("TRY AGAIN") {
                        requestPermissionsFromUser()
                    }
                    negativeButton("CANCEL") {
                        clean()
                    }
                }?.apply { isCancelable = false }?.show()
                return
            }



            clean()
        }
    }

    fun openAppSettings() {
        if (quickPermissionsRequest != null) {
            val intent = Intent(
                ACTION_APPLICATION_DETAILS_SETTINGS,
                fromParts("package", activity?.packageName, null)
            )

            startActivityForResult(intent, PERMISSIONS_REQUEST_CODE)
        } else {
            Log.w(TAG, "openAppSettings: QuickPermissionsRequest has already completed its flow. Cannot open app settings")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val permissions = quickPermissionsRequest?.permissions ?: emptyArray()
            val grantResults = IntArray(permissions.size)
            permissions.forEachIndexed { index, s ->
                grantResults[index] = context?.let { ActivityCompat.checkSelfPermission(it, s) } ?: PackageManager.PERMISSION_DENIED
            }

            handlePermissionResult(permissions, grantResults)
        }
    }
}
