package com.onourem.android.activity.ui.utils

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.IOException

object VideoThumbnailHelper {
    fun createVideoThumbnail(videoPath: String?): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(videoPath)
            retriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                retriever.release()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    fun createVideoThumbnail(videoUri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(videoUri.toString())
            retriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                retriever.release()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}
