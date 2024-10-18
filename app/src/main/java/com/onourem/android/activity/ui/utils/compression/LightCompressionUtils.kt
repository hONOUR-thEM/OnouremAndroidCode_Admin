package com.onourem.android.activity.ui.utils.compression

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.*
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

object LightCompressionUtils {
    @JvmStatic
    @Suppress("DEPRECATION")
    fun saveVideoFile(filePath: String?, context: Activity): File? {
        filePath?.let {
            val videoFile = File(filePath)
            val videoFileName = "${System.currentTimeMillis()}_${videoFile.name}"
            val folderName = Environment.DIRECTORY_MOVIES
            if (Build.VERSION.SDK_INT >= 30) {

                val values = ContentValues().apply {

                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        videoFileName
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Images.Media.RELATIVE_PATH, folderName)
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val collection =
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val fileUri = context.contentResolver.insert(collection, values)

                fileUri?.let {
                    context.contentResolver.openFileDescriptor(fileUri, "rw")
                        .use { descriptor ->
                            descriptor?.let {
                                FileOutputStream(descriptor.fileDescriptor).use { out ->
                                    FileInputStream(videoFile).use { inputStream ->
                                        val buf = ByteArray(4096)
                                        while (true) {
                                            val sz = inputStream.read(buf)
                                            if (sz <= 0) break
                                            out.write(buf, 0, sz)
                                        }
                                    }
                                }
                            }
                        }

                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    context.contentResolver.update(fileUri, values, null, null)

                    return File(getMediaPath(context, fileUri))
                }
            } else {
                val downloadsPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val desFile = File(downloadsPath, videoFileName)

                if (desFile.exists())
                    desFile.delete()

                try {
                    desFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                return desFile
            }
        }
        return null
    }


    @JvmStatic
    fun getMediaPath(context: Context, uri: Uri): String {

        val resolver = context.contentResolver
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = resolver.query(uri, projection, null, null, null)
            return if (cursor != null) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                cursor.moveToFirst()
                cursor.getString(columnIndex)

            } else ""

        } catch (e: Exception) {
            resolver.let {
                val filePath = (context.applicationInfo.dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                resolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        val buf = ByteArray(4096)
                        var len: Int
                        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(
                            buf,
                            0,
                            len
                        )
                    }
                }
                return file.absolutePath
            }
        } finally {
            cursor?.close()
        }
    }

    @JvmStatic
    fun getFileSize(size: Long): String {
        if (size <= 0)
            return "0"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

        return DecimalFormat("#,##0.#").format(
            size / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }

    //The following methods can be alternative to [getMediaPath].
// todo(abed): remove [getPathFromUri], [getVideoExtension], and [copy]
    @JvmStatic
    fun getPathFromUri(context: Context, uri: Uri): String {
        var file: File? = null
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var success = false
        try {
            val extension: String = getVideoExtension(uri)
            inputStream = context.contentResolver.openInputStream(uri)
            file = File.createTempFile("compressor", extension, context.cacheDir)
            file.deleteOnExit()
            outputStream = FileOutputStream(file)
            if (inputStream != null) {
                copy(inputStream, outputStream)
                success = true
            }
        } catch (ignored: IOException) {
        } finally {
            try {
                inputStream?.close()
            } catch (ignored: IOException) {
            }
            try {
                outputStream?.close()
            } catch (ignored: IOException) {
                // If closing the output stream fails, we cannot be sure that the
                // target file was written in full. Flushing the stream merely moves
                // the bytes into the OS, not necessarily to the file.
                success = false
            }
        }
        return if (success) file!!.path else ""
    }

    /** @return extension of video with dot, or default .mp4 if it none.
     */
    @JvmStatic
    private fun getVideoExtension(uriVideo: Uri): String {
        var extension: String? = null
        try {
            val imagePath = uriVideo.path
            if (imagePath != null && imagePath.lastIndexOf(".") != -1) {
                extension = imagePath.substring(imagePath.lastIndexOf(".") + 1)
            }
        } catch (e: Exception) {
            extension = null
        }
        if (extension == null || extension.isEmpty()) {
            //default extension for matches the previous behavior of the plugin
            extension = "mp4"
        }
        return ".$extension"
    }

    @JvmStatic
    private fun copy(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(4 * 1024)
        var bytesRead: Int
        while (`in`.read(buffer).also { bytesRead = it } != -1) {
            out.write(buffer, 0, bytesRead)
        }
        out.flush()
    }


}

