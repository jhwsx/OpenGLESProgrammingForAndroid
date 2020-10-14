package com.airhockey.android

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 渲染器
 *
 * @author wangzhichao
 * @date 20-9-30
 */
class AirHockeyRenderer : GLSurfaceView.Renderer {


    // 桌子顶点数据，按左上右下的顺序来存储
//        val tableVertices: FloatArray = floatArrayOf(
//            0f, 0f,
//            0f, 14f,
//            9f, 14f,
//            9f, 0f,
//        )
    // 使用两个三角形而不是一个长方形，这是因为在 OpenGL 里，只能绘制点，直线以及三角形。
    val tableVerticesWithTriangles: FloatArray = floatArrayOf(
        // 三角形 1，以逆时针的顺序排列顶点
        0f, 0f,
        9f, 14f,
        0f, 14f,
        // 三角形 2，以逆时针的顺序排列顶点
        0f, 0f,
        9f, 0f,
        9f, 14f,
        // 中间线
        0f, 7f,
        9f, 7f,
        // 表示木槌的两个点
        4.5f, 2f,
        4.5f, 12f
    )

    /**
     * 用于在本地内存中存储数据
     */
    private val vertexData: FloatBuffer = ByteBuffer
        // 分配一块本地内存，这块内存不会被垃圾回收器管理
        .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
        // 字节缓冲区按照本地字节序组织它的内存块
        .order(ByteOrder.nativeOrder())
        // 得到一个可以反映顶层字节的 FloatBuffer 类实例
        .asFloatBuffer()

    init {
        // 把数据从 Dalvik 的内存复制到本地内存。
        vertexData.put(tableVerticesWithTriangles)
    }

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
        /**
         * 表示一个顶点有两个分量
         */
        private const val POSITION_COMPONENT_COUNT = 2

        /**
         * 表示每个浮点数都包含 4 个字节
         */
        private const val BYTES_PER_FLOAT = 4

        private const val TAG = "FirstOpenGLProjectRende"
    }

}