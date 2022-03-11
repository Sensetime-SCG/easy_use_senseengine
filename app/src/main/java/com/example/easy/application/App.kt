package com.example.easy.application

import android.app.Application
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        //do something.

        Log.d(TAG, "onCreate:" + System.currentTimeMillis())
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "App finish")
        exitProcess(0) //clean
    }

    companion object {
        private const val TAG = "Easy"
        public const val DefaultUvcHeight = 1280
        public const val DefaultUvcWidth = 720
        fun setWindowsFullScreen(app: AppCompatActivity) {
            app.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            app.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            app.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            app.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
}