package com.example.easy.helper

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Parcelable
import android.util.Log

class UsbDeviceHelper private constructor(){
    companion object{
        const val TAG = "UsbDeviceHelper";

        const val UsbRequestCode = 9527
        const val UsbIntentAction = "com.android.USB_PERMISSION"

        private var mIsStart:Boolean = false

        @SuppressLint("StaticFieldLeak")
        private var mUsbDeviceHelper: UsbDeviceHelper?=null

        const val USB_DEVICE_VID:Int = 0x12D1 //SenseEngine M20(s) VID
        const val USB_DEVICE_PID:Int = 0x4321 //SenseEngine M20(s) PID

        @JvmStatic
        val instances: UsbDeviceHelper?
            get() {
                if(null == mUsbDeviceHelper)
                    mUsbDeviceHelper = UsbDeviceHelper()
                return mUsbDeviceHelper
            }
    }

    interface UsbListener{
        fun onDiscover(device:UsbDevice)
        fun onConnected(device: UsbDevice)
        fun onDisconnect(device: UsbDevice)
    }

    private var mContext:Context?=null
    private var mListener:UsbListener?=null
    private var mUsbManager:UsbManager?=null


    private val mUsbPermissionsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device = (intent.getParcelableExtra<Parcelable>("device") as UsbDevice?)!!
            Log.d(TAG, "onReceive: get new device : ${device.deviceName}")
            if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.action) || UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.action)){
                Log.d(TAG,"onReceive: detach or attache")
            }else{
                Log.d(TAG, "onReceive: "+intent.action)
            }
        }
    }

    fun start(context: Context, listener:UsbListener):Boolean{
        mIsStart = true;
        mContext = context;
        mListener = listener;
        mUsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager?
        if(mUsbManager==null){
            Log.d(TAG, "start: Can't get USB SERVICE")
            return false
        }else{
            Log.d(TAG, "start: Get USB SERVICE")
        }
        PendingIntent.getBroadcast(context, UsbRequestCode, Intent(UsbIntentAction),0)
        val filter = IntentFilter(UsbIntentAction)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        context.registerReceiver(mUsbPermissionsReceiver,filter)

        return true
    }

    fun stop(context:Context){
        context.unregisterReceiver(mUsbPermissionsReceiver)
    }
}