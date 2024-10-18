package com.onourem.android.activity.ui.utils.media

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by Kedar Labde on 12-July-2021
 * Onourem Social Games
 */
object Common {
    const val PERM = 111
    const val VIDEO_FILE_REQUEST_CODE = 112
    const val IMAGE_FILE_REQUEST_CODE = 113
    const val AUDIO_FILE_REQUEST_CODE = 114
    const val TIME_FORMAT = "HH:mm:ss"
    const val OUT_PUT_DIR: String = "Output"
    private val format: DecimalFormat = DecimalFormat("#.##")
    private const val MB = (1024 * 1024).toLong()
    private const val KB: Long = 1024

    //Output Files
    const val IMAGE: String = "png"
    const val VIDEO: String = "mp4"
    const val GIF: String = "gif"
    const val MP3: String = "mp3"
    const val M4A: String = "m4a"

    const val OPERATION_DOWNLOAD_IMAGE: String = "OPERATION_DOWNLOAD_IMAGE"
    const val OPERATION_DOWNLOAD_VIDEO: String = "OPERATION_DOWNLOAD_VIDEO"
    const val OPERATION_COMPRESS_VIDEO: String = "OPERATION_COMPRESS_VIDEO"
    const val OPERATION_COMPRESS_AUDIO: String = "OPERATION_COMPRESS_AUDIO"
    const val OPERATION_DOWNLOAD_AUDIO: String = "OPERATION_DOWNLOAD_AUDIO"
    const val OPERATION_RECORDING_AUDIO: String = "OPERATION_RECORDING_AUDIO"
    const val OPERATION_VIDEO: String = "OPERATION_VIDEO"
    const val OPERATION_IMAGE: String = "OPERATION_IMAGE"
    const val OPERATION_MERGED_AUDIO: String = "OPERATION_MERGED_AUDIO"
    const val OPERATION_VOLUME_AUDIO: String = "OPERATION_VOLUME_AUDIO"


    // Standard Ratio
    const val RATIO_1: String = "16:9"
    const val RATIO_2: String = "4:3"
    const val RATIO_3: String = "16:10"
    const val RATIO_4: String = "5:4"
    const val RATIO_5: String = "2:21:1"
    const val RATIO_6: String = "2:35:1"
    const val RATIO_7: String = "2:39:1"

    // Standard  Bitrate
    const val BITRATE_96: String = "96" //kbps
    const val BITRATE_112: String = "112" //kbps
    const val BITRATE_128: String = "128" //kbps
    const val BITRATE_160: String = "160" //kbps
    const val BITRATE_192: String = "192" //kbps
    const val BITRATE_256: String = "256" //kbps
    const val BITRATE_320: String = "320" //kbps

    const val DURATION_LONGEST: String = "longest"
    const val DURATION_SHORTEST: String = "shortest"
    const val DURATION_FIRST: String = "first"

    fun stringForTime(timeMs: Long?): String {
        val mFormatBuilder = StringBuilder()
        val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
        val totalSeconds = timeMs?.div(1000)
        val seconds = (totalSeconds?.rem(60))?.toInt()
        val minutes = ((totalSeconds?.div(60))?.rem(60))?.toInt()
        val hours = (totalSeconds?.div(3600))?.toInt()
        mFormatBuilder.setLength(0)
        return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString()
    }

    fun getFrameRate(fileString: String) {
        val extractor = MediaExtractor()
        val file = File(fileString)
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val fd = fis.fd
            extractor.setDataSource(fd)
            val numTracks = extractor.trackCount
            for (i in 0 until numTracks) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime?.startsWith("video/") == true) {
                    if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        FFmpegQueryExtension().FRAME_RATE =
                            format.getInteger(MediaFormat.KEY_FRAME_RATE)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            extractor.release()
            try {
                fis?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getFileSize(file: File): String {
        require(file.isFile) { "Expected a file" }
        val length = file.length().toDouble()
        if (length > MB) {
            return format.format(length / MB).toString() + " MB"
        }
        return if (length > KB) {
            format.format(length / KB).toString() + " KB"
        } else {
            format.format(length).toString() + " GB"
        }
    }

//    fun selectFile(activity: AppCompatActivity, maxSelection: Int, isImageSelection: Boolean, isAudioSelection: Boolean) {
//        val intent = Intent(activity, FilePickerActivity::class.java)
//        when {
//            isImageSelection -> {
//                intent.putExtra(
//                    FilePickerActivity.CONFIGS, Configurations.Builder()
//                        .setCheckPermission(true)
//                        .setShowImages(true)
//                        .setShowVideos(false)
//                        .enableImageCapture(true)
//                        .enableVideoCapture(false)
//                        .setMaxSelection(maxSelection)
//                        .setSkipZeroSizeFiles(true)
//                        .build()
//                )
//                activity.startActivityForResult(intent, IMAGE_FILE_REQUEST_CODE)
//            }
//            isAudioSelection -> {
//                intent.putExtra(
//                    FilePickerActivity.CONFIGS, Configurations.Builder()
//                        .setCheckPermission(true)
//                        .setShowImages(false)
//                        .setShowVideos(false)
//                        .setShowAudios(true)
//                        .enableImageCapture(false)
//                        .enableVideoCapture(false)
//                        .setMaxSelection(maxSelection)
//                        .setSkipZeroSizeFiles(true)
//                        .build()
//                )
//                activity.startActivityForResult(intent, AUDIO_FILE_REQUEST_CODE)
//            }
//            else -> {
//                intent.putExtra(
//                    FilePickerActivity.CONFIGS, Configurations.Builder()
//                        .setCheckPermission(true)
//                        .setShowImages(false)
//                        .setShowVideos(true)
//                        .enableImageCapture(false)
//                        .enableVideoCapture(true)
//                        .setMaxSelection(maxSelection)
//                        .setSkipZeroSizeFiles(true)
//                        .build()
//                )
//                activity.startActivityForResult(intent, VIDEO_FILE_REQUEST_CODE)
//            }
//        }
//    }

    @JvmStatic
    fun getFilePath(
        context: Context,
        fileExtension: String,
        operation: String,
        fileName: String,
    ): String {
        val cw = ContextWrapper(context)
        var directory: File? = null
        var folder = ""
        when (operation) {
            OPERATION_COMPRESS_VIDEO -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                folder = "CompressVideo"
            }
            OPERATION_DOWNLOAD_VIDEO -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                folder = "DownloadVideo"
            }
            OPERATION_VIDEO -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                folder = "Video"
            }
            OPERATION_COMPRESS_AUDIO -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                folder = "CompressAudio"
            }
            OPERATION_RECORDING_AUDIO -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                folder = "Recordings"
            }
            OPERATION_MERGED_AUDIO -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                folder = "MergeAudio"
            }
            OPERATION_VOLUME_AUDIO -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                folder = "VolumeAudio"
            }
            OPERATION_DOWNLOAD_IMAGE -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                folder = "DownloadImage"
            }
            OPERATION_IMAGE -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                folder = "Image"
            }
        }

        val storagePath = File(directory!!.absolutePath)
        if (!storagePath.exists()) {
            storagePath.mkdir()
        }

        val dir = File(storagePath.absolutePath + File.separator + folder)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val dest =
            File(dir.path + File.separator + folder + "_Onourem_File_" + fileName + "." + fileExtension)
        return dest.absolutePath
    }

    @JvmStatic
    fun getDownloadedFilePath(
        context: Context,
        operation: String,
    ): String {
        val cw = ContextWrapper(context)
        var directory: File? = null
        var folder = ""
        when (operation) {

            OPERATION_DOWNLOAD_VIDEO -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                folder = "DownloadVideo"
            }

            OPERATION_DOWNLOAD_IMAGE -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                folder = "DownloadImage"
            }

            OPERATION_DOWNLOAD_AUDIO -> {
                directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                folder = "DownloadAudio"
            }

        }

        val storagePath = File(directory!!.absolutePath)
        if (!storagePath.exists()) {
            storagePath.mkdir()
        }

        val dir = File(storagePath.absolutePath + File.separator + folder)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val dest =
            File(dir.path + File.separator)
        return dest.absolutePath
    }

    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File =
        File(context.cacheDir, fileName).also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(fileName).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }

    @JvmStatic
    fun getRandomName(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return List(length) { charset.random() }
            .joinToString("")
    }

    @JvmStatic
    fun showCenterToast(context: Context, text: String) {
        val toast = Toast.makeText(
            context,
            text, Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    @JvmStatic
    fun extractVideoId(ytUrl: String?): String? {
        var videoId: String? = null
        val regex =
            "^((?:https?:)?//)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be|youtube-nocookie.com))(/(?:[\\w\\-]+\\?v=|feature=|watch\\?|e/|embed/|v/)?)([\\w\\-]+)(\\S+)?\$"
        val pattern: Pattern = Pattern.compile(
            regex,
            Pattern.CASE_INSENSITIVE
        )
        val matcher: Matcher? = ytUrl?.let { pattern.matcher(it) }
        if (matcher!!.matches()) {
            videoId = matcher.group(5)
        }
        return videoId
    }

    @JvmStatic
    fun <T> isPresent(arr: ArrayList<T>, target: T): Boolean {
        return arr.any { x: T -> x == target }
    }


    @JvmStatic
    fun addHash(parseColor: String): String {
        return "#$parseColor"
    }

    @Throws(Throwable::class)
    fun retrieveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        val mediaMetadataRetriever = MediaMetadataRetriever()
        try {
            mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.frameAtTime
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            //throw Throwable("Exception in retrieveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever.release()
        }
        return bitmap
    }

    fun checkMediaDurationValid(mContext: Context, uri: String?, minutes: String): Boolean {
        var seconds = 0L
        var limit = 0L
        try {
            MediaPlayer.create(mContext, Uri.parse(uri)).also {
                val millis = it.duration.toLong()
                seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
                limit = TimeUnit.MILLISECONDS.toSeconds(
                    TimeUnit.MILLISECONDS.convert(
                        minutes.toInt()
                            .toLong(), TimeUnit.MINUTES
                    )
                )
                it.reset()
                it.release()
            }

            return seconds < limit
        } catch (e: Exception) {
            return false
        }
    }
}