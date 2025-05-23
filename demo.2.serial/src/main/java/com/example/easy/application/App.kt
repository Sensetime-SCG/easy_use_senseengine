package com.example.easy.application

import android.app.Application
import android.hardware.usb.UsbDevice
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.example.easy.helper.UsbDeviceHelper
import kotlin.system.exitProcess

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate:" + System.currentTimeMillis())
        mUsbDeviceHelper!!.start(applicationContext, mUsbListener)
        Log.d(TAG, "onCreate: start over")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "App finish")
        mUsbDeviceHelper?.stop(applicationContext)
        exitProcess(0) //clean
    }

    private var mUsbListener: UsbDeviceHelper.UsbListener = object : UsbDeviceHelper.UsbListener {
        override fun onConnected(device: UsbDevice) {
            Log.d(TAG, "onConnected: " + System.currentTimeMillis())
        }

        override fun onDisconnect(device: UsbDevice) {
            Log.d(TAG, "onDisconnect: " + System.currentTimeMillis())
        }

        override fun onDiscover(device: UsbDevice) {
            Log.d(TAG, "onDiscover: " + System.currentTimeMillis())
        }
    }

    var mUsbDeviceHelper: UsbDeviceHelper? = UsbDeviceHelper.instances

    companion object {
        private const val TAG = "Easy"
    }
}