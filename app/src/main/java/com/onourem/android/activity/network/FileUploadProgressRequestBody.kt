package com.onourem.android.activity.network

import android.os.Handler
import okhttp3.RequestBody
import kotlin.Throws
import okio.BufferedSink
import com.onourem.android.activity.network.FileUploadProgressRequestBody
import android.os.Looper
import okhttp3.MediaType
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class FileUploadProgressRequestBody(
    private val mFile: File,
    private val content_type: MediaType?,
    private val mListener: ProgressCallback
) : RequestBody() {
    private var uploaded: Long = 0
    override fun contentType(): MediaType? {
        return content_type
    }

    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        FileInputStream(mFile).use { `in` ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (`in`.read(buffer).also { read = it } != -1) {

                // update progress on UI thread
                handler.post {
                    val progress = (100 * uploaded / fileLength).toInt()
                    if (progress < 100) {
                        mListener.onProgressUpdate(progress)
                    }
                }
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        }
    }

    interface ProgressCallback {
        fun onProgressUpdate(percentage: Int)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}