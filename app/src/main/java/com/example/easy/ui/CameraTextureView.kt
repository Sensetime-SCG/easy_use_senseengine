package com.example.easy.ui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.TextureView
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.RequiresApi
import com.example.easy.application.App

class CameraTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TextureView(context, attrs, defStyle) {
    private var mRequestAspect = -1.0

    companion object {
        private const val TAG = "CameraTextureView"

        @Suppress("DEPRECATION")
        fun getDeviceScreenRealHeight(context: Context): Int {
            val wm = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            val dm = DisplayMetrics()
            wm.defaultDisplay.getRealMetrics(dm)
            return dm.heightPixels
        }

        @Suppress("DEPRECATION")
        fun getDeviceScreenRealWidth(context: Context): Int {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getRealMetrics(dm)
            return dm.widthPixels
        }

        @RequiresApi(Build.VERSION_CODES.R)
        fun getDeviceScreenRealSize(context: Context): Size {
            val metrics: WindowMetrics =
                context.getSystemService(WindowManager::class.java).currentWindowMetrics
            return Size(metrics.bounds.width(), metrics.bounds.height())
        }
    }

    fun setAspectRatio(width: Int, height: Int) {
        val aspectRatio = width / height.toDouble()
        require(aspectRatio >= 0)
        if (mRequestAspect != aspectRatio) {
            mRequestAspect = aspectRatio
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measureWidth: Int = 0
        var measureHeight: Int = 0
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            val size = getDeviceScreenRealSize(context)
            if (App.isPreviewScaleHeight) {
                measureHeight = size.height
                measureWidth = (size.height.toDouble() * mRequestAspect).toInt()
            } else {
                measureHeight = (size.width.toDouble() / mRequestAspect).toInt()
                measureWidth = size.width
            }
        } else {
            if (App.isPreviewScaleHeight) {
                measureHeight = getDeviceScreenRealHeight(context)
                measureWidth =
                    (getDeviceScreenRealHeight(context).toFloat() * mRequestAspect).toInt()
            } else {
                measureHeight =
                    (getDeviceScreenRealWidth(context).toDouble() / mRequestAspect).toInt()
                measureWidth = getDeviceScreenRealWidth(context)
            }
        }
        Log.d(
            TAG,
            "onMeasure: scale height: ${App.isPreviewScaleHeight},width: $measureWidth, height: $measureHeight, $mRequestAspect, " + System.currentTimeMillis()
        )
        setMeasuredDimension(measureWidth, measureHeight)
    }
}