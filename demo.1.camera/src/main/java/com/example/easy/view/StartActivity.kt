package com.example.easy.view

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.easy.R
import com.example.easy.application.App


class StartActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "StartActivity"
        private const val REQUEST_CODE: Int = 1
        private var mMissPermission: MutableList<String> = mutableListOf()

    }

    private val REQUIRED_PERMISSION_LIST: List<String> = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
    )

    private fun isVersionGreaterM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun checkAndRequestPermission() {
        mMissPermission.clear()
        for (permission in REQUIRED_PERMISSION_LIST) {
            val result = ContextCompat.checkSelfPermission(this@StartActivity, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                mMissPermission.add(permission)
            }
        }
        if (mMissPermission.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this@StartActivity,
                mMissPermission.toTypedArray(),
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: " + System.currentTimeMillis())
        if (REQUEST_CODE == requestCode) {
            for (i in grantResults.indices.reversed()) {
                if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                    mMissPermission.remove(permissions[i])
                    Log.d(TAG, "onRequestPermissionsResult: " + permissions[i])
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: $mMissPermission")
        if (mMissPermission.isNotEmpty()) {
            Toast.makeText(
                this@StartActivity,
                "Access permission failure,exit...",
                Toast.LENGTH_SHORT
            ).show()
            this@StartActivity.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate:" + System.currentTimeMillis())
        setContentView(R.layout.activity_start)
        if (isVersionGreaterM()) {
            checkAndRequestPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "resume.")
        //do something.
    }

    fun onClickStartCameraActivity(view: View) {
        Log.d(TAG, "onClickStartCameraActivity: view: $view")
        startActivity(Intent(this@StartActivity, CameraActivity::class.java))
    }

    fun onClickChoosePreviewScale(view: View) {
        Log.d(TAG, "onClickChoosePreviewScale: view: $view")
        val scaleList = arrayOf("Scale Height", "Scale Weigth")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.act_btn_preview_scale)
        builder.setItems(scaleList) { dialogInterface, i ->
            dialogInterface.dismiss()
            App.isPreviewScaleHeight = (i == 0)
            Toast.makeText(this, scaleList[i], Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("cancel", null).show()
    }
}