package com.airhockey.android

import android.app.ActivityManager
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class AirHockeyActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private var rendererSet: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)
        // 检查系统是否支持 OpenGL ES 2.0
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val deviceConfigurationInfo = activityManager.deviceConfigurationInfo
        val supportsES2 = deviceConfigurationInfo.reqGlEsVersion >= 0x20000
        Log.d(TAG, "onCreate: supportES2=$supportsES2")
        if (supportsES2) {
            // 设置 OpenGL ES 2.0 环境
            glSurfaceView.setEGLContextClientVersion(2)
            // 设置渲染器
            glSurfaceView.setRenderer(AirHockeyRenderer())
            rendererSet = true
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_SHORT)
                .show()
        }
        setContentView(glSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            // 暂停后台渲染线程并暂停 Open GL 上下文
            glSurfaceView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            // 暂停后台渲染线程续用 Open GL 上下文
            glSurfaceView.onResume()
        }
    }

    companion object {
        private const val TAG = "FirstOpenGLProjectActiv"
    }
}
