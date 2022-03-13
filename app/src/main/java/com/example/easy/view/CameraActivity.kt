package com.example.easy.view

import android.graphics.SurfaceTexture
import android.hardware.usb.UsbDevice
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.easy.R
import com.example.easy.application.App
import com.example.easy.ui.CameraTextureView
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usb.UVCCamera

class CameraActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "CameraActivity"
        private const val USB_DEVICE_VID = 0x12D1 //SenseEngine M20(s) VID
        private const val USB_DEVICE_PID = 0x4321 //SenseEngine M20(s) PID

        private var mUsbMonitor: USBMonitor? = null
        private var mUvcCamera: UVCCamera? = null
        private var mCameraTextureView: CameraTextureView? = null
    }

    private fun requestUsbDevicePermissions() {
        if (mUsbMonitor != null) {
            val usbDeviceList: List<UsbDevice> = mUsbMonitor!!.deviceList
            for (device in usbDeviceList) { //filter
                if (USB_DEVICE_VID == device.vendorId && USB_DEVICE_PID == device.productId) {
                    Log.d(
                        TAG,
                        "requestPermissions: found SenseEngine device ," + System.currentTimeMillis()
                    )
                    mUsbMonitor?.requestPermission(device)
                }
            }
        }
    }

    private val mOnDeviceConnectListener = object :
        USBMonitor.OnDeviceConnectListener {
        override fun onAttach(device: UsbDevice) {
            Log.d(TAG, "onAttach: " + device.productName + " ," + System.currentTimeMillis())
            requestUsbDevicePermissions()
        }

        override fun onCancel(device: UsbDevice) {
            Log.d(TAG, "onCancel: " + device.deviceName + " ," + System.currentTimeMillis())
        }

        override fun onConnect(
            device: UsbDevice,
            ctrlBlock: USBMonitor.UsbControlBlock,
            createNew: Boolean
        ) {
            Log.d(TAG, "onConnect: " + System.currentTimeMillis())
            if (false == mCameraTextureView?.isAvailable) {
                Log.e(TAG, "onConnect: camera texture view unavailable")
                return
            }
            mUvcCamera?.stopPreview()
            mUvcCamera?.destroy()
            val camera = object : UVCCamera() {}
            Log.d(TAG, "onConnect: " + System.currentTimeMillis() + ", constructor camera ")
            try {
                camera.open(ctrlBlock)
                camera.setPreviewSize(
                    App.DefaultUvcWidth,
                    App.DefaultUvcHeight,
                    UVCCamera.FRAME_FORMAT_MJPEG
                )
            } catch (e: UnsupportedOperationException) {
                Log.e(TAG, e.toString())
                camera.destroy()
                return
            }
            mUvcCamera = camera
            val st: SurfaceTexture = mCameraTextureView?.surfaceTexture ?: return
            mUvcCamera!!.setPreviewTexture(st)
            mUvcCamera!!.setFrameCallback(
                { frame_type, frame, verifyResultBuffer ->
                    if (verifyResultBuffer != null && verifyResultBuffer.capacity() > 0) {
                        Log.d(TAG, "onVerifyFrame: $verifyResultBuffer")
                    }
                },
                UVCCamera.PIXEL_FORMAT_NV21,
                false
            )
            mUvcCamera!!.startPreview()
            Log.d(TAG, "onConnect: " + System.currentTimeMillis() + ", start preview")
        }

        override fun onDettach(device: UsbDevice) {
            Log.d(TAG, "onDettach: " + " ," + System.currentTimeMillis())
        }

        override fun onDisconnect(device: UsbDevice, ctrlBlock: USBMonitor.UsbControlBlock) {
            Log.d(TAG, "onDisconnect: " + " ," + System.currentTimeMillis())
            mUvcCamera?.stopPreview()
            mUvcCamera?.destroy()
            mUvcCamera = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: " + System.currentTimeMillis())
        setContentView(R.layout.activity_camera)
        App.setWindowsFullScreen(this@CameraActivity)
        mCameraTextureView = findViewById(R.id.camera_act_camera_textureview);
        mCameraTextureView?.setAspectRatio(9, 16)
        mCameraTextureView?.scaleX = -1f
        mUsbMonitor = USBMonitor(this, mOnDeviceConnectListener)
        mUsbMonitor!!.register()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: " + System.currentTimeMillis())
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: " + System.currentTimeMillis())

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: " + System.currentTimeMillis())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: " + System.currentTimeMillis())
        mUsbMonitor?.unregister()
        mUsbMonitor?.destroy()
        mUsbMonitor = null
    }
}