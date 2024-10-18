package com.onourem.android.activity.ui.audio.fragments

import android.content.ContentUris
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAudioRecordSheetBinding
import com.onourem.android.activity.models.GetAllGroupsResponse
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio.models.AudioCategory
import com.onourem.android.activity.ui.audio.models.BackgroundAudio
import com.onourem.android.activity.ui.audio.models.GetAudioCategoryResponse
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.games.viewmodel.SelectPrivacyViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.*
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class RecorderSettingsFragment :
    AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentAudioRecordSheetBinding>(),
    MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private var backPressedWithoutUpload: Boolean = false
    private var isFromStorage: Boolean = false
    private lateinit var userActionViewModel: UserActionViewModel
    private var mediaTotalDuration: Long = 0
    private var audioCategoryId: String = ""
    private var selectedAudioCategory: AudioCategory =
        AudioCategory()
    private var privacyIds: String = ""
    private var languageId: String = ""
    private var categoryResponse: GetAudioCategoryResponse =
        GetAudioCategoryResponse()

    var privacyGroupsDefault: ArrayList<PrivacyGroup> = ArrayList()
    var privacyGroups: ArrayList<PrivacyGroup> = ArrayList()

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var dashboardViewModel: DashboardViewModel? = null
    private var selectPrivacyViewModel: SelectPrivacyViewModel? = null
    private var mediaPlayer: MediaPlayer? = null
    private var recordedMediaFilePath = ""
    private var backgroundMusicPath = ""
    private var backgroundMusicVolumeDownPath = ""
    private val pitchValue = 1f
    private val speedValue = 1f
    private var timer: Timer? = null
    private var prepared = false
    private var pausedAt = 0
    private var mergedAudioPath = ""
    private var clickedFromButton = false

    private var mStartPos = 0
    private var mEndPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(this, viewModelFactory).get(
            DashboardViewModel::class.java
        )
        selectPrivacyViewModel = ViewModelProvider(this, viewModelFactory).get(
            SelectPrivacyViewModel::class.java
        )

        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )

    }

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_audio_record_sheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCategories()

        recordedMediaFilePath =
            RecorderSettingsFragmentArgs.fromBundle(requireArguments()).recordedAudioFilePath

        mStartPos = RecorderSettingsFragmentArgs.fromBundle(requireArguments()).trimSelectionStart
        mEndPos = RecorderSettingsFragmentArgs.fromBundle(requireArguments()).trimSelectionEnd

        val retriever = MediaMetadataRetriever()
        // There are other variations of setDataSource(), if you have a different input
        retriever.setDataSource(context, Uri.fromFile(File(recordedMediaFilePath)))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()

        isFromStorage =
            RecorderSettingsFragmentArgs.fromBundle(
                requireArguments()
            ).isFromStorage


        prepared = false
        timer = Timer()

        binding.btnDialogSelectCategory.text = "--"
        binding.btnLanguage.text = "--"
        binding.btnDialogSelectBgAudio.text = "--"

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer!!.seekTo(progress)
                }
            }
        })

        binding.ibPlay.setOnClickListener(ViewClickListener {
            try {
                var path = ""
                path = if (mergedAudioPath.isNotEmpty()) {
                    mergedAudioPath
                } else {
                    recordedMediaFilePath
                }
                onPlayButtonClicked(path)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        })

        binding.ibDelete.setOnClickListener(ViewClickListener {
            deleteAudio()
        })

        userActionViewModel!!.actionMutableLiveData.observe(
            viewLifecycleOwner
        ) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()

                if (action.actionType == ActionType.DELETE_AUDIO_CONFIRMATION) {
                    deleteRecording(false)
                }

            }
        }


        binding.btnSubmit.setOnClickListener(ViewClickListener {
            if (!TextUtils.isEmpty(recordedMediaFilePath)) {

                if (!TextUtils.isEmpty(binding.edtAudioTitle.text.toString())) {
                    if (binding.btnDialogSelectCategory.text == "--" || binding.btnLanguage.text == "--") {
                        showAlert("Please select audio language and category")
                    } else {
                        uploadAudioInfo(binding.edtAudioTitle.text.toString())
                    }
                } else {
                    showAlert("Please enter audio title")
                }
            }
        })

        binding.btnDialogSelectCategory.setOnClickListener(ViewClickListener {
            if (categoryResponse.audioCategory != null) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavAudioCategory(
                        categoryResponse
                    )
                )
            }
        })

        binding.btnDialogSelectBgAudio.setOnClickListener(ViewClickListener {
            if (selectedAudioCategory.id != null) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavAudioBackground(
                        selectedAudioCategory
                    )
                )
            } else {
                showAlert("Please select audio category first")
            }

        })

        binding.btnLanguage.setOnClickListener(ViewClickListener {
            if (categoryResponse.languageList != null) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavAudioLanguage(
                        categoryResponse
                    )
                )
            }

        })

        binding.btnPrivacy.setOnClickListener(ViewClickListener {
            clickedFromButton = true
            getPrivacy()
        })

        viewModel.audioCategoryObject.observe(viewLifecycleOwner) {
            if (it != null && it.id != null) {
                binding.btnDialogSelectCategory.text = it.name
                selectedAudioCategory = it
                val noAudio = BackgroundAudio(-1, -1, "No Background", "0")
                selectedAudioCategory.backgroundAudioList.add(noAudio)
                audioCategoryId = it.id.toString()
            }
        }

        viewModel.audioLanguageObject.observe(viewLifecycleOwner) {
            if (it != null && it.id != null) {
                binding.btnLanguage.text = it.languageName
                languageId = it.id.toString()
            }
        }

        viewModel.audioBackgroundObject.observe(viewLifecycleOwner) {
            if (it != null && it.id != null) {

                if (it.id == -1) {
                    binding.btnDialogSelectBgAudio.text = "--"
                    mergedAudioPath = ""
                    clearMediaPlayer()

                    Toast.makeText(fragmentContext, "Background Audio Removed.", Toast.LENGTH_LONG)
                        .show()
                } else {
                    binding.btnDialogSelectBgAudio.text =
                        it.fileName.substring(
                            it.fileName.lastIndexOf('/') + 1,
                            it.fileName.lastIndexOf('.')
                        )

                    download(it.fileName, binding.btnDialogSelectBgAudio.text.toString())
                }

            }
        }

        viewModel.selectedPrivacyGroups.observe(viewLifecycleOwner) {
            if (it != null && it.size > 0) {

                if (it.size > 1) {
                    binding.btnPrivacy.text = "Custom"
                } else if (it.size == 1) {
                    binding.btnPrivacy.text = it[0].groupName
                }

                val ids: ArrayList<Int> = ArrayList()
                it.forEach { item ->
                    ids.add(item.groupId)
                }

                privacyIds = Utilities.getTokenSeparatedString(ids, ",")

                if (privacyIds != "") {
                    val selectedPrivacyIdsList: List<String> =
                        listOf(*privacyIds.split(",").toTypedArray())

                    val oldSelected = ArrayList<PrivacyGroup>()
                    selectedPrivacyIdsList.forEach(action = {
                        val privacyGroup = PrivacyGroup()
                        privacyGroup.groupId = it.toInt()
                        oldSelected.add(privacyGroup)
                    })

                    selectPrivacyViewModel!!.setSelected(oldSelected)

                }
            }
        }

        binding.seekBarCurrentHint.text = "00:00"
        binding.seekBarHint.text = formatTimeUnitForSeekbar(time!!.toLong())
        mediaTotalDuration = time.toLong()

        getPrivacy()

    }

    private fun getPrivacy() {
        selectPrivacyViewModel!!.getAllGroups("18").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetAllGroupsResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    val privacyGroups =
                        apiResponse.body.privacyGroups
                    val iterator =
                        privacyGroups?.listIterator()
                    if (iterator != null) {
                        while (iterator.hasNext()) {
                            val privacyGroup = iterator.next()
                            if (privacyGroup.groupName
                                    .equals("Receivers Only", ignoreCase = true)
                            ) {
                                privacyGroup.groupName = "Private"
                                break
                            }
                        }
                    }
                    if (selectPrivacyViewModel!!.selectedPrivacyMutableLiveData!!
                            .value != null && !selectPrivacyViewModel!!.selectedPrivacyMutableLiveData!!
                            .value?.isEmpty()!!
                    ) {
                        val defaultSelected: MutableList<PrivacyGroup> =
                            ArrayList()
                        val oldSelected: MutableList<PrivacyGroup> =
                            selectPrivacyViewModel!!.selectedPrivacyMutableLiveData!!
                                .value as MutableList<PrivacyGroup>
                        for (selectedGroup in oldSelected) {
                            if (privacyGroups != null) {
                                for (group in privacyGroups) {
                                    if (selectedGroup.groupId == group.groupId) {
                                        defaultSelected.add(group)
                                    }
                                }
                            }
                        }
                        oldSelected.clear()
                        oldSelected.addAll(defaultSelected)
                        this@RecorderSettingsFragment.privacyGroupsDefault.clear()
                        this@RecorderSettingsFragment.privacyGroupsDefault.addAll(
                            defaultSelected
                        )
                        this@RecorderSettingsFragment.privacyGroups.clear()
                        if (privacyGroups != null) {
                            this@RecorderSettingsFragment.privacyGroups.addAll(privacyGroups)
                        }
                        viewModel.setSelectedPrivacyGroup(privacyGroupsDefault)
                    } else {
                        val defaultSelected: MutableList<PrivacyGroup> =
                            ArrayList()
                        if (privacyGroups != null) {
                            for (i in privacyGroups.indices) {
                                val privacyGroup = privacyGroups[i]
                                if (privacyGroup.groupId == 1) {
                                    defaultSelected.add(privacyGroup)
                                } else if (privacyGroup.groupName
                                        .equals("Receivers Only", ignoreCase = true)
                                ) {
                                    privacyGroup.groupName = "Private"
                                }
                            }
                        }
                        this@RecorderSettingsFragment.privacyGroupsDefault.clear()
                        this@RecorderSettingsFragment.privacyGroupsDefault.addAll(
                            defaultSelected
                        )
                        this@RecorderSettingsFragment.privacyGroups.clear()
                        if (privacyGroups != null) {
                            this@RecorderSettingsFragment.privacyGroups.addAll(privacyGroups)
                        }
                        viewModel.setSelectedPrivacyGroup(privacyGroupsDefault)
                    }

                    if (clickedFromButton) {
                        clickedFromButton = false
                        val song = Song()
                        song.privacyId = privacyIds
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavAudioPrivacy(
                                this@RecorderSettingsFragment.privacyGroups.toTypedArray(),
                                this@RecorderSettingsFragment.privacyGroupsDefault.toTypedArray(),
                                song
                            )
                        )
                    }

                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else if (!apiResponse.loading) {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getAllGroups")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getAllGroups",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun deleteAudio() {
        val titleText = "Do you want to delete this recording?"
        val actions = ArrayList<Action<*>>()
        actions.add(
            Action(
                getString(R.string.action_label_delete_confirm),
                R.color.color_black,
                ActionType.DELETE_AUDIO_CONFIRMATION,
                null
            )
        )
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText,
                bundle, ""
            )
        )
    }

    private fun deleteRecording(inclusive: Boolean) {

//        if (!isFromStorage) {
//
//            if (mergedAudioPath.isNotEmpty()) {
//                val mergedAudioPathFile = File(mergedAudioPath)
//                if (mergedAudioPathFile.exists()) {
//                    delete(requireActivity() as AppCompatActivity, mergedAudioPath)
//                }
//            }
//
//            if (backgroundMusicVolumeDownPath.isNotEmpty()) {
//                val backgroundMusicVolumeDownPathFile = File(backgroundMusicVolumeDownPath)
//                if (backgroundMusicVolumeDownPathFile.exists()) {
//                    delete(requireActivity() as AppCompatActivity, backgroundMusicVolumeDownPath)
//                }
//            }
//
//        }

        mergedAudioPath = ""
        backgroundMusicVolumeDownPath = ""
        backgroundMusicPath = ""

        binding.btnDialogSelectBgAudio.text = "--"
        recordedMediaFilePath =
            RecorderSettingsFragmentArgs.fromBundle(
                requireArguments()
            ).recordedAudioFilePath

        mediaTotalDuration =
            RecorderSettingsFragmentArgs.fromBundle(
                requireArguments()
            ).mediaTotalDuration

        binding.seekBarCurrentHint.text = "00:00"
        binding.seekBarHint.text = "00:00"

        clearMediaPlayer()

        viewModel.actionConsumed()

        if (inclusive) {
            navController.popBackStack(R.id.nav_vocals_main, false)
        } else {
            navController.popBackStack()
        }

    }

    private fun uploadAudioInfo(title: String) {

//        val retriever = MediaMetadataRetriever()
//        // There are other variations of setDataSource(), if you have a different input
//        retriever.setDataSource(context, Uri.fromFile(File(recordedMediaFilePath)))
//        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//        retriever.release()
        backPressedWithoutUpload = true

        val onouremProgressDialog = OnouremProgressDialog(fragmentContext)

        val progressCallback = object : ProgressCallback {
            override fun onProgressUpdate(percentage: Int) {
                onouremProgressDialog.setProgress(
                    percentage
                )
            }
        }

        val path: String = mergedAudioPath.ifEmpty {
            recordedMediaFilePath
        }

        viewModel.uploadAudioInfo(
            Base64Utility.encodeToString(title),
            path,
            preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID),
            audioCategoryId,
            TimeUnit.MILLISECONDS.toSeconds(mediaTotalDuration).toString(),
            languageId,
            privacyIds,
            progressCallback
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    showAlert(apiResponse.body.message) {
                        deleteRecording(true)
                        val navBuilder = NavOptions.Builder()
                        val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavVocalsMain(
                                "",
                                "",
                                "",
                                ""
                            ), navOptions
                        )
                    }
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "uploadAudioInfo")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "uploadAudioInfo",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun download(mediaUrl: String, selectedBgFileName: String) {

        if (mergedAudioPath.isNotEmpty()) {
            val mergedAudioPathFile = File(mergedAudioPath)
            if (mergedAudioPathFile.exists()) {
                //  delete(requireActivity() as AppCompatActivity, mergedAudioPath)
            }
        }

        if (backgroundMusicVolumeDownPath.isNotEmpty()) {
            val backgroundMusicVolumeDownPathFile = File(backgroundMusicVolumeDownPath)
            if (backgroundMusicVolumeDownPathFile.exists()) {
                // delete(requireActivity() as AppCompatActivity, backgroundMusicVolumeDownPath)
            }
        }

        val extension = Common.MP3
        val fileOperation = Common.OPERATION_DOWNLOAD_AUDIO

        val fileName = "Onourem_File_$selectedBgFileName.$extension"

        val outputPath = Common.getDownloadedFilePath(fragmentContext, fileOperation)

        val file = File("$outputPath/$fileName")
        if (file.exists()) {
            backgroundMusicPath = "$outputPath/$fileName"
            audioVolumeProcess(backgroundMusicPath)
        } else {
            showProgressWithText("Updating Background Audio", false)

            PRDownloader.download(
                mediaUrl,
                outputPath,
                fileName
            )
                .build()
                .setOnProgressListener {

                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        hideProgress()
                        backgroundMusicPath = "$outputPath/$fileName"
                        audioVolumeProcess("$outputPath/$fileName")
                    }

                    override fun onError(error: Error?) {
                        hideProgress()
                        Toast.makeText(
                            fragmentContext,
                            "Failed to download the $mediaUrl",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }


                })
        }


    }


    private fun audioVolumeProcess(file: String) {
        showProgressWithText("Adding Background Music", false)
        val outputPath = Common.getFilePath(
            fragmentContext,
            Common.MP3,
            Common.OPERATION_VOLUME_AUDIO,
            Common.getRandomName(6)
        )
        val ffmpegQueryExtension = FFmpegQueryExtension()

        val query = ffmpegQueryExtension.audioVolumeUpdate(
            inputFile = file,
            volume = 0.1f,
            output = outputPath
        )
        CallBackOfQuery().callQuery(
            requireActivity() as AppCompatActivity,
            query,
            object : FFmpegCallBack {
                override fun process(logMessage: LogMessage) {
                }

                override fun success() {
                    hideProgress()
                    val fileOutput = File(outputPath)
                    backgroundMusicVolumeDownPath = outputPath
                    mergeAudioProcess(recordedMediaFilePath, outputPath)
                }

                override fun cancel() {

                }

                override fun failed() {

                }
            })
    }

    private fun mergeAudioProcess(recordedAudioPath: String, backgroundMusicPath: String) {

        showProgressWithText("Adding Background Music", false)

        val outputPath = Common.getFilePath(
            fragmentContext,
            Common.MP3,
            Common.OPERATION_MERGED_AUDIO,
            Common.getRandomName(6)
        )
        val pathsList = ArrayList<Paths>()

        val paths = Paths()
        paths.filePath = recordedAudioPath
        paths.isImageFile = false
        pathsList.add(paths)

        val backGroundAudioPath = Paths()
        backGroundAudioPath.filePath = backgroundMusicPath
        backGroundAudioPath.isImageFile = false
        pathsList.add(backGroundAudioPath)
        val ffmpegQueryExtension = FFmpegQueryExtension()

        val query = ffmpegQueryExtension.mergeAudios(
            inputAudioList = pathsList,
            duration = Common.DURATION_FIRST,
            output = outputPath
        )

        CallBackOfQuery().callQuery(
            requireActivity() as AppCompatActivity,
            query,
            object : FFmpegCallBack {
                override fun process(logMessage: LogMessage) {

                }

                override fun success() {
                    hideProgress()
                    mergedAudioPath = outputPath
                    clearMediaPlayer()
                    onPlayButtonClicked(mergedAudioPath)

                }

                override fun cancel() {
                    hideProgress()
                }

                override fun failed() {
                    hideProgress()
                }
            })
    }

    fun formatTimeUnit(timeInMilliseconds: Long): String {

        var value = ""
        try {
            if (TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds) > 0) {
                value = String.format(
                    Locale.getDefault(),
                    "%2d min %2d sec",
                    TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
                    TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
                    )
                )
            } else {
                value = String.format(
                    Locale.getDefault(),
                    "%2d sec",
                    TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
                    )
                )
            }

        } catch (e: Exception) {
            value = "00 min 00 sec"
        }
        return value
    }


    private fun formatTimeUnitForSeekbar(timeInMilliseconds: Long): String {

        return String.format(
            Locale.getDefault(),
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
            TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
            )
        )
    }

    @Throws(IOException::class)
    fun onPlayButtonClicked(mediaPath: String) {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
//            Log.d("Record", "setAudioAttributes()")
            val builder: AudioAttributes.Builder = AudioAttributes.Builder()
            builder.setUsage(AudioAttributes.USAGE_MEDIA)
            builder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            val attributes = builder.build()
            mediaPlayer!!.setAudioAttributes(attributes)

//            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setVolume(1f, 1f)
            mediaPlayer!!.isLooping = false
            mediaPlayer!!.setOnPreparedListener(this)
            mediaPlayer!!.setOnCompletionListener(this)
        }
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            pausedAt = mediaPlayer!!.currentPosition
            binding.ibPlay.setImageDrawable(null)
            binding.ibPlay.setImageDrawable(
                AppCompatResources.getDrawable(
                    fragmentContext,
                    R.drawable.ic_recording_play
                )
            )

            if (timer != null) {
                timer!!.cancel()
            }
            timer = Timer()
        } else {
            timer = Timer()
            if (pausedAt <= 0) {
                clearMediaPlayer()
                val file = File(mediaPath)
                val inputStream = FileInputStream(file)
                mediaPlayer!!.setDataSource(inputStream.fd)
                inputStream.close()
//                mediaPlayer!!.setDataSource(mediaPath)
                mediaPlayer!!.prepare()
            }
            mediaPlayer!!.start()
            //binding.seekbar.max = mediaPlayer!!.duration
            binding.ibPlay.setImageDrawable(null)
            binding.ibPlay.setImageDrawable(
                AppCompatResources.getDrawable(
                    fragmentContext,
                    R.drawable.ic_recording_pause
                )
            )

            timer!!.schedule(ProgressUpdate(), 0, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
//        stopRepeatingTask()
        if (mediaPlayer != null) {
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        viewModel.setSelectedPrivacyGroup(null)
        viewModel.setPrivacyUpdatedItem(null)
        if (!backPressedWithoutUpload) {
            // viewModel.setTrimSelection(Pair(mStartPos, mEndPos))
        }
    }

    private fun clearMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            pausedAt = 0
            //            mediaPlayer = null;
        }
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        prepared = true
//        var params: PlaybackParams? = null
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            params = PlaybackParams()
//            params.pitch = pitchValue
//            //params.setSpeed(speedValue);
//            params.audioFallbackMode = PlaybackParams.AUDIO_FALLBACK_MODE_DEFAULT
//            mediaPlayer.playbackParams = params
//            //new Thread(AudioRecordFragment.this).start();
//        }
        binding.seekBar.max = mediaPlayer.duration
        binding.seekBarCurrentHint.text = "00:00"
        binding.seekBarHint.text = formatTimeUnitForSeekbar(mediaPlayer.duration.toLong())
    }

    override fun onCompletion(mp: MediaPlayer) {
        if (mediaPlayer != null) {
            clearMediaPlayer()
            binding.ibPlay.setImageDrawable(null)
            binding.ibPlay.setImageDrawable(
                AppCompatResources.getDrawable(
                    fragmentContext,
                    R.drawable.ic_recording_play
                )
            )
            binding.seekBar.progress = 0

            binding.seekBarCurrentHint.text = formatTimeUnitForSeekbar(0)
            binding.seekBarHint.text = formatTimeUnitForSeekbar(mediaTotalDuration)

            if (timer != null) {
                timer!!.cancel()
            }
        }
    }

    private inner class ProgressUpdate : TimerTask() {
        override fun run() {
            if (mediaPlayer != null) {
                val currentPosition = mediaPlayer!!.currentPosition
                val totalDuration = mediaPlayer!!.duration
//                val msg = Message()
//                msg.arg1 = currentPosition
                //                Bundle bundle = new Bundle();
//                bundle.putString("current", String.valueOf(currentPosition));
//                bundle.putString("total", String.valueOf(totalDuration));
//                msg.setData(bundle);
                // mHandler.dispatchMessage(msg)

                requireActivity().runOnUiThread {
                    binding.seekBar.progress = currentPosition
                    if (TimeUnit.MILLISECONDS.toSeconds(currentPosition.toLong()) > 0) {
                        binding.seekBarCurrentHint.text =
                            formatTimeUnitForSeekbar(currentPosition.toLong())
                        binding.seekBarHint.text = formatTimeUnitForSeekbar(totalDuration.toLong())

                    }
                }
            }
        }
    }

    fun delete(activity: AppCompatActivity, absolutePath: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            // Set up the projection (we only need the ID)
            val projection = arrayOf(MediaStore.Audio.Media._ID)

            // Match on the file path
            val selection = MediaStore.Audio.Media.DATA + " = ?"
            val selectionArgs = arrayOf<String>(absolutePath)

            // Query for the ID of the media matching the file path
            val queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val contentResolver = activity.contentResolver
            val c = contentResolver.query(queryUri, projection, selection, selectionArgs, null)
            if (c!!.moveToFirst()) {
                // We found the ID. Deleting the item via the content provider will also remove the file
                val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val deleteUri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                contentResolver.delete(deleteUri, null, null)

            } else {
                Log.w("Media ", "Media not found!!")

            }
            c.close()
        }, 70)
    }

    private fun getCategories() {
        viewModel.audioCategory().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetAudioCategoryResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    categoryResponse = apiResponse.body
//                        viewModel.setAudioCategoryObject(categoryResponse.audioCategory[0])
//                        viewModel.setAudioLanguageObject(categoryResponse.languageList[0])
//                        viewModel.setAudioBackgroundObject(categoryResponse.audioCategory[0].backgroundAudioList[0])
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }

    }

    companion object {
        val RequestPermissionCode = 1
    }


}