package com.airhockey.android.util

import android.content.Context
import android.content.res.Resources
import java.io.*

/**
 * 从资源中读取文本的类
 *
 * @author wangzhichao
 * @date 20-10-9
 */
object TextResourceReader {

    fun readTextFileFromResource(context: Context, resourceId: Int): String {
        val sb = StringBuilder()
        var inputStream: InputStream? = null
        try {
            inputStream = context.resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var nextLine = bufferedReader.readLine()
            while (nextLine != null) {
                sb.append(nextLine)
                sb.append('\n')
                nextLine = bufferedReader.readLine()
            }
        } catch (e: IOException) {
            throw RuntimeException("Could not open resource: $resourceId", e)
        } catch (e: Resources.NotFoundException) {
            throw RuntimeException("Resource not found: $resourceId", e)
        }finally {
            inputStream?.close()
        }
        return sb.toString()
    }
}