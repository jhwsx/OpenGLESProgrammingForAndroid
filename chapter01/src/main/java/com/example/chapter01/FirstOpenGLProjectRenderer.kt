package com.example.chapter01

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 渲染器
 *
 * @author wangzhichao
 * @date 20-9-30
 */
class FirstOpenGLProjectRenderer : GLSurfaceView.Renderer{

    // 当 surface 被创建或者重建时，GLSurfaceView 会调用这个方法
    // 这个方法可能会被调用多次
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated: ${Thread.currentThread().name}") // GLThread 6244
        // 设置清空屏幕时用的颜色
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
    }

    // 在 surface 被创建以后，每次 surface 尺寸发生变化，这个方法都会被 GLSurfaceView 调用
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged: ${Thread.currentThread().name}") // GLThread 6244
        // 设置视口尺寸，充满整个 surface
        GLES20.glViewport(0, 0, width, height)
    }

    // 当绘制一帧时，这个方法会被 GLSurfaceView 调用
    override fun onDrawFrame(gl: GL10?) {
        Log.d(TAG, "onDrawFrame: ${Thread.currentThread().name}") // GLThread 6244
        // 清空屏幕，擦除屏幕上的所有颜色，并用之前 glClearColor() 调用定义的颜色填充整个屏幕
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    companion object {
        private const val TAG = "FirstOpenGLProjectRende"
    }

}