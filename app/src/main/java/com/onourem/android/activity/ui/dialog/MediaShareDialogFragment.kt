package com.onourem.android.activity.ui.dialog

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.DialogMediaShareBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.media.Common
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import java.io.File
import javax.inject.Inject


class MediaShareDialogFragment :
    AbstractBaseViewModelBindingFragment<QuestionGamesViewModel, DialogMediaShareBinding>() {

    private lateinit var progressDialog: OnouremProgressDialog

    private var hasShown = false

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    override fun layoutResource(): Int {
        return R.layout.dialog_media_share
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: MediaShareDialogFragmentArgs =
            MediaShareDialogFragmentArgs.fromBundle(requireArguments())

        // Initialize PRDownloader with read and connection timeout
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(fragmentContext, config)

        download(args)

        progressDialog =
            OnouremProgressDialog(
                fragmentContext
            )

        showProgressWithText("Downloading Media To Share", false)
    }

    private fun download(args: MediaShareDialogFragmentArgs) {

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
        val shareMessage: String = args.linkMsg
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

        var extension = ""
        var fileOperation = ""

        when (args.mediaType) {
            "1" -> {
                shareIntent.type = "image/png"
                extension = Common.IMAGE
                fileOperation = Common.OPERATION_DOWNLOAD_IMAGE
            }
            "2" -> {
                shareIntent.type = "video/mp4"
                extension = Common.VIDEO
                fileOperation = Common.OPERATION_DOWNLOAD_VIDEO
            }
            "3" -> {
                shareIntent.type = "audio/*"
                extension = Common.MP3
                fileOperation = Common.OPERATION_DOWNLOAD_AUDIO
            }
        }

        val fileName =
            "Onourem_File_" + Common.getRandomName(6) + "." + extension

        val outputPath = Common.getDownloadedFilePath(fragmentContext, fileOperation)

        PRDownloader.download(
            args.mediaUrl,
            outputPath,
            fileName
        )
            .build()
            .setOnProgressListener {
                // Update the progress

                binding.progressBar.max = it.totalBytes.toInt()
                binding.progressBar.progress = it.currentBytes.toInt()

                binding.txtCurrentSize.text = AppUtilities.getBytesToMBString(it.currentBytes)
                binding.txtTotalSize.text = AppUtilities.getBytesToMBString(it.totalBytes)

                progressDialog.setProgress(it.currentBytes.toInt())

            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    // Update the progress bar to show the completeness
                    binding.progressBar.max = 100
                    binding.progressBar.progress = 100

                    val file = File("$outputPath/$fileName")

                    val fileUri = FileProvider.getUriForFile(
                        fragmentContext,
                        fragmentContext.applicationContext.packageName + ".GenericFileProvider",
                        file
                    )

                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                    shareIntent.setDataAndType(
                        fileUri,
                        fragmentContext.contentResolver.getType(fileUri)
                    )
                    try {
                        fragmentContext.startActivity(Intent.createChooser(shareIntent, args.title))
                    } catch (ex : ActivityNotFoundException) {
                        Toast.makeText(fragmentContext,"NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
                    }
                    hideProgress()
                    navController.popBackStack()

                }

                override fun onError(error: Error?) {
                    Toast.makeText(
                        fragmentContext,
                        "Failed to download the ${args.mediaUrl}",
                        Toast.LENGTH_SHORT
                    ).show()

                    hideProgress()
                }


            })

    }

    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }
}