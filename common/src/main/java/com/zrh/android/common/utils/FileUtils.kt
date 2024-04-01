package com.zrh.android.common.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.*

/**
 *
 * @author zrh
 * @date 2024/1/11
 *
 */
object FileUtils {

    fun write(input:InputStream, output:OutputStream){
        try {
            val buffer = ByteArray(1024)
            var len = input.read(buffer)
            while (len != -1){
                output.write(buffer, 0, len)
                len = input.read(buffer)
            }
            output.flush()
        }finally {
            input.close()
            output.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getDownloadUri(context: Context, relativeDirPath: String, fileName: String, mimeType: String): Uri? {
        val contentValues = ContentValues()

        contentValues.put(MediaStore.DownloadColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.DownloadColumns.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.DownloadColumns.RELATIVE_PATH, relativeDirPath)
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        return uri
    }

    private fun getDownloadOutput(context: Context, file: File, mimeType: String, outputDir:String):OutputStream?{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val path = Environment.DIRECTORY_DOWNLOADS+"/$outputDir"
            val uri = getDownloadUri(context, path, file.name, mimeType) ?: return null
            val out: OutputStream? = context.contentResolver.openOutputStream(uri)
            return out
        }
        val sdcard = Environment.getExternalStorageDirectory()
        val downloadDir = File(sdcard, "/Download/$outputDir")
        if (!downloadDir.exists()){
            downloadDir.mkdirs()
        }
        val outputFile = File(downloadDir, file.name)
        return FileOutputStream(outputFile)
    }

    fun saveFileToDownload(context: Context, file: File, mimeType: String, outputDir:String) {
        try {
            val output = getDownloadOutput(context, file, mimeType, outputDir) ?: return
            val input = FileInputStream(file)
            write(input, output)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getFileName(url:String):String{
        try {
            val paths = url.split("?")
            val path = paths[0]
            val index = path.lastIndexOf("/")
            if (index != -1 && index < path.length){
                return path.substring(index+1, path.length)
            }
        } catch (_: Exception) { }
        return ""
    }

    fun getImageMimeType(fileName: String):String{
        val array = fileName.split(".")
        if (array.isEmpty()) return "image/jpeg"
        return when (array.last()) {
            "webp", "png" -> "image/png"
            "bmp" -> "image/bmp"
            "gif" -> "image/gif"
            else -> "image/jpeg"
        }
    }
}