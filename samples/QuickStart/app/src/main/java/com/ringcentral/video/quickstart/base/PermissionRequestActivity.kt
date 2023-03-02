package com.ringcentral.video.quickstart.base

import android.Manifest
import android.content.pm.PackageManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.ringcentral.video.quickstart.R

open class PermissionRequestActivity : AppCompatActivity() {
    var permissionHostLayout: View? = null
    private fun requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
            /*
             * Provide an additional rationale to the user if the permission was not granted
             * and the user would benefit from additional context for the use of the permission.
             * Display a SnackBar with cda button to request the missing permission.
             */
            Snackbar.make(permissionHostLayout!!, R.string.camera_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok) {
                // Request the permission
                ActivityCompat.requestPermissions(this@PermissionRequestActivity, arrayOf(Manifest.permission.CAMERA),
                        PERMISSION_REQUEST_CAMERA
                )
            }.show()
        } else {
            Snackbar.make(permissionHostLayout!!, R.string.camera_unavailable, Snackbar.LENGTH_SHORT).show()
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    private fun requestAudioPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
            /*
             * Provide an additional rationale to the user if the permission was not granted
             * and the user would benefit from additional context for the use of the permission.
             * Display a SnackBar with cda button to request the missing permission.
             */
            Snackbar.make(permissionHostLayout!!, R.string.audio_record_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok) {
                // Request the permission
                ActivityCompat.requestPermissions(this@PermissionRequestActivity, arrayOf(Manifest.permission.RECORD_AUDIO),
                        PERMISSION_REQUEST_AUDIO
                )
            }.show()
        } else {
            Snackbar.make(permissionHostLayout!!, R.string.audio_record_unavailable, Snackbar.LENGTH_SHORT).show()
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_AUDIO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                Snackbar.make(permissionHostLayout!!, R.string.camera_permission_granted,
                        Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                // Permission request for camera is denied.
                Snackbar.make(permissionHostLayout!!, R.string.camera_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show()
            }
        } else if (requestCode == PERMISSION_REQUEST_AUDIO) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                Snackbar.make(permissionHostLayout!!, R.string.audio_record_permission_granted,
                        Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                // Permission request for audio is denied.
                Snackbar.make(permissionHostLayout!!, R.string.audio_record_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestAudioPermission()
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CAMERA = 0
        private const val PERMISSION_REQUEST_AUDIO = 1
    }
}