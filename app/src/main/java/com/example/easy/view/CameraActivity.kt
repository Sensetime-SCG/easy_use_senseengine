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
        private val TAG = "CameraActivity"
        private val USB_DEVICE_VID = 0x12D1
        private val USB_DEVICE_PID = 0x4321

        private var mUsbMonitor: USBMonitor? = null
        private var mUvcCamera: UVCCamera? = null
        private var mCameraTextureView: CameraTextureView? = null
    }

    private fun requestUsbDevicePermissions() {
        if (mUsbMonitor != null) {
            val usbDeviceList: List<UsbDevice> = mUsbMonitor!!.deviceList
            for (device in usbDeviceList) { //filter
                if (USB_DEVICE_VID == device.vendorId && USB_DEVICE_PID == device.productId) {
                    Log.d(TAG, "requestPermissions: find device")
                    mUsbMonitor?.requestPermission(device)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate:" + System.currentTimeMillis())
        setContentView(R.layout.activity_camera)
        App.setWindowsFullScreen(this@CameraActivity)
        mCameraTextureView = findViewById(R.id.camera_act_camera_textureview);
        mCameraTextureView?.setAspectRatio(9, 16)
        mCameraTextureView?.scaleX = -1f

        mUsbMonitor = USBMonitor(this, object :
            USBMonitor.OnDeviceConnectListener {
            override fun onAttach(device: UsbDevice?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d(TAG, "onAttach: " + device?.productName)
                } else {
                    Log.d(TAG, "onAttach: " + device?.deviceName)
                }
                requestUsbDevicePermissions()
            }

            override fun onCancel(device: UsbDevice?) {
                Log.d(TAG, "onCancel: " + device?.deviceName)
            }

            override fun onConnect(
                device: UsbDevice?,
                ctrlBlock: USBMonitor.UsbControlBlock?,
                createNew: Boolean
            ) {
                Log.d(TAG, "onConnect: ctrlBlock $ctrlBlock")
                if (false == mCameraTextureView?.isAvailable) {
                    Log.e(TAG, "onConnect: cameratextureview unavailable")
                    return
                }
                val camera: UVCCamera = object : UVCCamera() {}
                try {
                    camera.open(ctrlBlock)
                    camera.setPreviewSize(
                        App.DefaultUvcWidth,
                        App.DefaultUvcHeight,
                        UVCCamera.FRAME_FORMAT_MJPEG
                    );
                } catch (e: UnsupportedOperationException) {
                    Log.e(TAG, e.toString())
                    camera.destroy()
                    return
                }

                val st: SurfaceTexture = mCameraTextureView?.surfaceTexture!!
                camera.setPreviewTexture(st)
                camera.setFrameCallback(
                    { frame_type, frame, verifyResultBuffer ->
                        Log.d(
                            TAG,
                            "onVerifyFrame: frame type: $frame_type,  frame len: " + frame?.capacity()
                        )
                        if (verifyResultBuffer != null && verifyResultBuffer.capacity() > 0) {
                            Log.d(TAG, "onVerifyFrame: $verifyResultBuffer")
                        }
                    },
                    UVCCamera.PIXEL_FORMAT_NV21,
                    false
                )
                mUvcCamera = camera
            }

            override fun onDettach(device: UsbDevice?) {
                Log.d(TAG, "onDettach: $device")
                mUvcCamera?.destroy()
            }

            override fun onDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
                Log.d(TAG, "onDisconnect: $device")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mUsbMonitor!!.register() //assert
    }

    override fun onResume() {
        super.onResume()
        requestUsbDevicePermissions()
    }

    override fun onStop() {
        super.onStop()
        mUvcCamera?.newThreadDestroy(null)
        System.gc()
    }

    override fun onDestroy() {
        super.onDestroy()
        mUsbMonitor?.unregister()
        mUsbMonitor?.destroy()
        mUsbMonitor = null
    }
}