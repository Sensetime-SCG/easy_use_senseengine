package com.example.easy.ui

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.TextureView
import android.view.WindowManager

class CameraTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle:Int = 0
):TextureView(context,attrs,defStyle){
    private var mRequestedAspect = -1.0

    companion object {
        fun getDeviceScreenRealHeight(context: Context): Int {
            val wm = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            val display = wm.defaultDisplay
            var screenHeight = 0
            run {
                val dm = DisplayMetrics()
                display.getRealMetrics(dm)
                screenHeight = dm.heightPixels
            }
            return screenHeight
        }

        fun getDeviceScreenRealWidth(context: Context): Int {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            assert(wm != null)
            wm.defaultDisplay.getRealMetrics(dm)
            return dm.widthPixels
        }
    }


    fun setAspectRatio(aspectRatio: Double) {
        require(aspectRatio >= 0)
        if (mRequestedAspect != aspectRatio) {
            mRequestedAspect = aspectRatio
            requestLayout()
        }
    }
    fun setAspectRatio(width: Int, height: Int) {
        setAspectRatio(width / height.toDouble())
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val screenWidth = getDeviceScreenRealWidth(context).toFloat()
        val screenHeight = getDeviceScreenRealHeight(context).toFloat()

        //uniform scale
        setMeasuredDimension((screenHeight*mRequestedAspect).toInt(),screenHeight.toInt())
    }
}