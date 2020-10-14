package com.airhockey.android.util

import android.opengl.GLES20
import android.util.Log


/**
 * 着色器辅助类
 *
 * @author wangzhichao
 * @date 20-10-9
 */
object ShaderHelper {
    private const val TAG = "ShaderHelper"

    fun compileVertexShader(shaderCode: String): Int {
        // GL_VERTEX_SHADER 代表顶点着色器
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode)
    }

    fun compileFragmentShader(shaderCode: String): Int {
        // GL_FRAGMENT_SHADER 代表片段着色器
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode)
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        // glCreateShader 创建一个新的着色器对象，返回的是着色器对象的 id，就是着色器对象的引用
        val shaderObjectId = GLES20.glCreateShader(type)
        if (shaderObjectId == 0) {
            // 返回 0，表示创建着色器对象失败
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader.")
            }
            return 0
        }
        // 告诉 OpenGL 读入 shaderCode 定义的源代码，并把它与 shaderObjectId 所引用的着色器对象关联起来
        GLES20.glShaderSource(shaderObjectId, shaderCode)
        // 编译着色器
        GLES20.glCompileShader(shaderObjectId)
        // 检查 OpenGL 是否能够成功地编译这个着色器
        val compileStatus: IntArray = intArrayOf(0)
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        if (LoggerConfig.ON) {
            Log.w(
                TAG,
                "Result of compiling source: \n $shaderCode \n: ${
                    GLES20.glGetShaderInfoLog(shaderObjectId)
                }"
            )
        }

        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderObjectId)

            if (LoggerConfig.ON) {
                Log.w(TAG, "Compilation of shader failed.")
            }

            return 0
        }
        return shaderObjectId
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        // 创建一个程序对象，返回的是程序对象的引用，如果创建失败就会返回 0。
        val programObjectId = GLES20.glCreateProgram()

        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new program")
            }
            return 0
        }
        // 附上着色器: 把顶点着色器和片段着色器都附加到程序对象上。
        GLES20.glAttachShader(programObjectId, vertexShaderId)
        GLES20.glAttachShader(programObjectId, fragmentShaderId)
        // 链接程序
        GLES20.glLinkProgram(programObjectId)
        // 检查链接是成功还是失败
        val linkStatus: IntArray = intArrayOf(0)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (LoggerConfig.ON) {
            Log.w(
                TAG,
                "Results of linking program:\n${GLES20.glGetProgramInfoLog(programObjectId)}"
            )
        }
        if (linkStatus[0] == 0) {
            // 链接失败了，删除程序
            GLES20.glDeleteProgram(programObjectId)
            if (LoggerConfig.ON) {
                Log.w(TAG, "Linking of program failed.")
            }
            return 0
        }
        return programObjectId
    }

    fun validateProgram(programObjectId: Int): Boolean {
        GLES20.glValidateProgram(programObjectId)

        val validateStatus: IntArray = intArrayOf(0)
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)
        Log.w(
            TAG,
            "Results of validating program: ${validateStatus[0]}\nLog:${
                GLES20.glGetProgramInfoLog(programObjectId)
            }"
        )
        return validateStatus[0] != 0
    }
}