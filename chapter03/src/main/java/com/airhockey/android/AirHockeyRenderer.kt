package com.airhockey.android

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.airhockey.android.util.LoggerConfig
import com.airhockey.android.util.ShaderHelper
import com.airhockey.android.util.TextResourceReader
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
class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var program: Int = 0
    private var uColorLocation: Int = 0
    private var aPositionLocation: Int = 0

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
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,
        // 三角形 2，以逆时针的顺序排列顶点
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,
        // 中间线
        -0.5f, 0f,
        0.5f, 0f,
        // 表示木槌的两个点
        0f, -0.25f,
        0f, 0.25f
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
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        // 读取着色器的代码
        val vertexShaderSource =
            TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource =
            TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader)
        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program)
        }

        GLES20.glUseProgram(program)

        // 获取 uniform 的位置，并把这个位置存入 uColorLocation
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR)
        // 获取属性的位置
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION)
        // 确保 OpenGL 从缓冲区开头处开始读取数据
        vertexData.position(0)
        // 在缓冲区 vertexData 中找到 a_Position 对应的数据
        // 这个函数非常重要
        GLES20.glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexData
        )
        // 使能顶点数组
        GLES20.glEnableVertexAttribArray(aPositionLocation)

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
        // 绘制两个三角形
        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
        // 绘制中心分隔线
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)
        // 绘制木槌的点
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)

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

        private const val U_COLOR = "u_Color"

        private const val A_POSITION = "a_Position"
    }

}