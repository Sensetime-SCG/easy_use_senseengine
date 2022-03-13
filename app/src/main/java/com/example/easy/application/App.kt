package com.example.easy.application

import android.app.Application
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate:" + System.currentTimeMillis())
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "App finish")
        exitProcess(0) //clean
    }

    companion object {
        private const val TAG = "Easy"
        const val DefaultUvcHeight = 1280
        const val DefaultUvcWidth = 720
        var isPreviewScaleHeight = true
        fun setWindowsFullScreen(app: AppCompatActivity) {
            @Suppress("DEPRECATION")
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                app.window.insetsController?.hide(WindowInsets.Type.statusBars())
            }else{
                app.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                app.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
            app.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            app.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        }
    }
}