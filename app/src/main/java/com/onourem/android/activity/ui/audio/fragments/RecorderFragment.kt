package com.onourem.android.activity.ui.audio.fragments

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

import com.onourem.android.activity.ui.utils.permissions.PermissionManager
import com.onourem.android.activity.ui.utils.permissions.PermissionResult
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveConfig
import com.github.squti.androidwaverecorder.WaveRecorder
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentRecorderBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Constants.KEY_AUDIO_LIMIT
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.*
import com.onourem.android.activity.ui.utils.media.Common.getFilePath
import com.onourem.android.activity.ui.utils.media.Common.getRandomName
import com.onourem.android.activity.viewmodel.DashboardViewModel
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class RecorderFragment :
    AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentRecorderBinding>(),
    MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private lateinit var userActionViewModel: UserActionViewModel
    private var isFromStorage: Boolean = false
    private val PERMISSIONS_REQUEST_RECORD_AUDIO = 77
    private val FILE_REQUEST_CODE = 1

    private var waveRecorder: WaveRecorder? = null
    private var isRecording = false
    private var isPaused = false

    private var mediaPlayer: MediaPlayer? = null
    private var timer: Timer? = null
    private var prepared = false
    private var pausedAt = 0
    private var mediaTotalDuration = 0L
    private val pitchValue = 1f

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

//    private val mInterval = 1000 // 5 seconds by default, can be changed later
//
//    private var mHandler: Handler? = null
//
//    private var mStatusChecker: Runnable = object : Runnable {
//        override fun run() {
//            try {
//                requireActivity().runOnUiThread {
//
//                    val file = File(recordedMediaFilePath)
//                    if (file.exists()) {
//                        stopRepeatingTask()
//                        binding.ibDelete.visibility = View.VISIBLE
//                        binding.ibPlay.visibility = View.VISIBLE
//                    } else {
//                        mHandler!!.postDelayed(this, mInterval.toLong())
//                    }
//
//                }
//            } finally {
//                // 100% guarantee that this always happens, even if
//                // your update method throws an exception
//
//            }
//        }
//    }
//
//    private fun startRepeatingTask() {
//        mStatusChecker.run()
//    }
//
//    private fun stopRepeatingTask() {
//        mHandler!!.removeCallbacks(mStatusChecker)
//    }

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var dashboardViewModel: DashboardViewModel? = null
    private var recordedMediaFilePath = ""
    private var recordedMediaFilePathVolumeAutomation = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(this, viewModelFactory).get(
            DashboardViewModel::class.java
        )

        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )


    }

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_recorder
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideProgress()
        binding.parent.keepScreenOn = true
        // your code here
        // mHandler = Handler(Looper.getMainLooper())
        prepared = false

        prepareAudioRecordingSettings()

        binding.btnStartStopRecording.setOnClickListener(ViewClickListener {

            if ((fragmentContext as DashboardActivity).isGuestUser()) {

                (fragmentContext as DashboardActivity).showGuestPopup(
                    "Record Vocals",
                    "Here you can record vocals and share with your friends"
                )

            } else {


                if (!isRecording) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        coroutineScope.launch(Dispatchers.Main) {
                            handleRecordButtonResult(
                                PermissionManager.requestPermissions(
                                    this@RecorderFragment,
                                    4,
                                    Manifest.permission.READ_MEDIA_AUDIO,
                                )
                            )
                        }
                    } else {
                        coroutineScope.launch(Dispatchers.Main) {
                            handleRecordButtonResult(
                                PermissionManager.requestPermissions(
                                    this@RecorderFragment,
                                    4,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                )
                            )
                        }
                    }
//                if (ContextCompat.checkSelfPermission(
//                        requireActivity(),
//                        Manifest.permission.RECORD_AUDIO
//                    )
//                    != PackageManager.PERMISSION_GRANTED
//                ) {
//                    ActivityCompat.requestPermissions(
//                        requireActivity(),
//                        arrayOf(Manifest.permission.RECORD_AUDIO),
//                        PERMISSIONS_REQUEST_RECORD_AUDIO
//                    )
//                } else {
//
//                }
                } else {
                    waveRecorder!!.stopRecording()
                }

            }


        })

        binding.txtPauseResume.setOnClickListener(ViewClickListener {
            if (!isPaused) {
                waveRecorder!!.pauseRecording()
            } else {
                waveRecorder!!.resumeRecording()
            }
        })

        binding.ibPlay.setOnClickListener(ViewClickListener {
            onPlayButtonClicked()
        })

        binding.ibDelete.setOnClickListener(ViewClickListener {
            deleteAudio()
        })

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer!!.seekTo(progress)
                }
            }
        })

        binding.btnAudioFromStorage.setOnClickListener(ViewClickListener {

            if ((fragmentContext as DashboardActivity).isGuestUser()) {

                (fragmentContext as DashboardActivity).showGuestPopup(
                    "Upload Audios",
                    "Here you can upload audios from files and share with your friends"
                )

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    coroutineScope.launch(Dispatchers.Main) {
                        handleSubmitButtonResult(
                            PermissionManager.requestPermissions(
                                this@RecorderFragment,
                                4,
                                Manifest.permission.READ_MEDIA_AUDIO,
                            )
                        )
                    }
                } else {
                    coroutineScope.launch(Dispatchers.Main) {
                        handleSubmitButtonResult(
                            PermissionManager.requestPermissions(
                                this@RecorderFragment,
                                4,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            )
                        )
                    }
                }
            }
        })


        viewModel.filePath.observe(viewLifecycleOwner) {

            if (it != null) {
                isFromStorage = true
                recordedMediaFilePath = it.audioUrl
                binding.clPlayRecording.visibility = View.VISIBLE
                binding.clRecording.visibility = View.GONE
                binding.btnStartStopRecording.visibility = View.INVISIBLE

                binding.btnAudioFromStorage.visibility = View.VISIBLE
                binding.btnAudioFromStorage.text = "Continue"

                binding.btnAudioFromStorage.strokeColor =
                    ContextCompat.getColorStateList(fragmentContext, R.color.color_black)
                binding.btnAudioFromStorage.setTextColor(
                    ContextCompat.getColor(
                        fragmentContext,
                        R.color.color_black
                    )
                )
                binding.btnAudioFromStorage.setBackgroundColor(
                    ContextCompat.getColor(
                        fragmentContext,
                        R.color.white
                    )
                )

                binding.ibDelete.visibility = View.VISIBLE
                binding.ibPlay.visibility = View.VISIBLE

                binding.seekBarCurrentHint.text = "00:00"
                binding.seekBarHint.text = formatTimeUnitForSeekbar(it.duration.toLong())
                binding.txtSeconds.text = formatTimeUnit(it.duration.toLong())
                mediaTotalDuration = it.duration.toLong()

            }

        }

        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                when (action.actionType) {
                    ActionType.AUDIO_EDIT_YES -> {
                        navController.navigate(
                            RecorderFragmentDirections.actionNavRecorderToNavAudioEditor(
                                recordedMediaFilePath,
                                mediaTotalDuration,
                                isFromStorage
                            )
                        )
                    }
                    ActionType.AUDIO_EDIT_NO -> {

                        if (TimeUnit.MILLISECONDS.toSeconds(mediaTotalDuration) > (preferenceHelper!!.getInt(
                                KEY_AUDIO_LIMIT
                            ))
                        ) {
                            showAlert("Audio length is greater than 3 minutes, Please trim it to continue.")
                        } else {
                            navController.navigate(
                                RecorderFragmentDirections.actionNavRecorderToNavRecorderSettings(
                                    recordedMediaFilePath,
                                    mediaTotalDuration,
                                    isFromStorage, 0, 0
                                )
                            )
                        }
                    }
                    ActionType.DELETE_AUDIO_CONFIRMATION -> {
                        deleteRecording()
                    }
                    else -> {

                    }
                }

            }
        }

    }

    private fun handleSubmitButtonResult(permissionResult: PermissionResult) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
//                Toast.makeText(requireContext(), "Granted", Toast.LENGTH_SHORT).show()
                clearMediaPlayer()
                if (binding.btnAudioFromStorage.text == "Upload Audios") {
                    navController.navigate(MobileNavigationDirections.actionGlobalAudioPickerFragment())
                } else if (binding.btnAudioFromStorage.text == "Continue") {

                    addFilters()

                }
            }
            is PermissionResult.PermissionDenied -> {
                Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT).show()
            }
            is PermissionResult.ShowRational -> {
                showAlert(
                    "Permissions Needed",
                    "We need permissions to access local audio files on this device.",
                    ViewClickListener {
                        when (permissionResult.requestCode) {

                            4 -> {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    coroutineScope.launch(Dispatchers.Main) {
                                        handleSubmitButtonResult(
                                            PermissionManager.requestPermissions(
                                                this@RecorderFragment,
                                                4,
                                                Manifest.permission.READ_MEDIA_AUDIO,
                                            )
                                        )
                                    }
                                } else {
                                    coroutineScope.launch(Dispatchers.Main) {
                                        handleSubmitButtonResult(
                                            PermissionManager.requestPermissions(
                                                this@RecorderFragment,
                                                4,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            )
                                        )
                                    }
                                }

                            }
                        }
                    })

            }
            is PermissionResult.PermissionDeniedPermanently -> {
                showAlert(
                    "You have denied app permissions permanently, We need permissions to access local audio files on this device. Please go to Settings and give Onourem App required permissions to use the app.",
                    ViewClickListener {
                        requireActivity().openAppSystemSettings()
                    })
            }
        }
    }

    private fun handleRecordButtonResult(permissionResult: PermissionResult) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                waveRecorder!!.startRecording()
            }
            is PermissionResult.PermissionDenied -> {
                Toast.makeText(requireContext(), "Denied", Toast.LENGTH_SHORT).show()
            }
            is PermissionResult.ShowRational -> {
                showAlert(
                    "Permissions Needed",
                    "We need permission to work record and save audio functionality.",
                    ViewClickListener {
                        when (permissionResult.requestCode) {

                            4 -> {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    coroutineScope.launch(Dispatchers.Main) {
                                        handleRecordButtonResult(
                                            PermissionManager.requestPermissions(
                                                this@RecorderFragment,
                                                4,
                                                Manifest.permission.READ_MEDIA_AUDIO,
                                                Manifest.permission.RECORD_AUDIO

                                            )
                                        )
                                    }
                                } else {
                                    coroutineScope.launch(Dispatchers.Main) {
                                        handleRecordButtonResult(
                                            PermissionManager.requestPermissions(
                                                this@RecorderFragment,
                                                4,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.RECORD_AUDIO

                                            )
                                        )
                                    }
                                }

                            }
                        }
                    })

            }
            is PermissionResult.PermissionDeniedPermanently -> {
                showAlert(
                    "You have denied app permissions permanently, We need permission to work record and save audio functionality. Please go to Settings and give Onourem App required permissions to use the app.",
                    ViewClickListener {
                        requireActivity().openAppSystemSettings()
                    })
            }
        }
    }

    private fun addFilters() {

        val titleText = "Would you like to trim your audio?"
        val actions = ArrayList<Action<*>>()
        actions.add(
            Action(
                getString(R.string.action_label_edit_audio_yes),
                R.color.color_black,
                ActionType.AUDIO_EDIT_YES,
                null
            )
        )
        actions.add(
            Action(
                getString(R.string.action_label_edit_audio_no),
                R.color.color_black,
                ActionType.AUDIO_EDIT_NO,
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


    private fun prepareAudioRecordingSettings() {

        recordedMediaFilePath = getFilePath(
            fragmentContext,
            Common.MP3,
            Common.OPERATION_RECORDING_AUDIO,
            getRandomName(6)
        )

        waveRecorder = WaveRecorder(recordedMediaFilePath)
        waveRecorder!!.waveConfig =
            WaveConfig(16000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        waveRecorder!!.onAmplitudeListener = null
        waveRecorder!!.noiseSuppressorActive = true

        waveRecorder!!.onStateChangeListener = {
            when (it) {
                RecorderState.RECORDING -> startRecording()
                RecorderState.STOP -> stopRecording()
                RecorderState.PAUSE -> pauseRecording()
            }
        }

        waveRecorder!!.onTimeElapsed = {
            //Log.e(TAG, "onCreate: time elapsed $it")
            mediaTotalDuration = it * 1000
            binding.seekBarHint.text = formatTimeUnitForSeekbar(mediaTotalDuration)
            binding.txtSeconds.text = formatTimeUnit(mediaTotalDuration)
            if (TimeUnit.MILLISECONDS.toSeconds(mediaTotalDuration) >= (preferenceHelper!!.getInt(
                    KEY_AUDIO_LIMIT
                ) - 1 )
            ) {
                waveRecorder!!.stopRecording()
                stopRecording()
            }

        }


    }


    companion object {
        private const val TAG = "Recording"
    }

    private fun startRecording() {

        Log.d(TAG, waveRecorder!!.audioSessionId.toString())
        isRecording = true
        isPaused = false
        binding.btnStartStopRecording.text = "Stop Recording"
        binding.txtPauseResume.text = "Pause Recording"
        binding.txtPauseResume.visibility = View.GONE
        binding.btnStartStopRecording.strokeColor =
            ContextCompat.getColorStateList(fragmentContext, R.color.color_6)
        binding.btnStartStopRecording.setTextColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.white
            )
        )
        binding.btnStartStopRecording.setBackgroundColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.color_6
            )
        )
        binding.ivRecording.setImageDrawable(
            AppCompatResources.getDrawable(
                fragmentContext,
                R.drawable.ic_red_mic
            )
        )

        binding.ibDelete.visibility = View.GONE
        binding.ibPlay.visibility = View.GONE
        timer = Timer()

        binding.btnAudioFromStorage.visibility = View.GONE
    }

    private fun stopRecording() {
        binding.btnAudioFromStorage.visibility = View.VISIBLE
        isRecording = false
        isPaused = false
        binding.txtPauseResume.visibility = View.GONE
        //Toast.makeText(requireActivity(), "File saved at : $filePath", Toast.LENGTH_LONG).show()
        binding.btnStartStopRecording.text = "Start Recording"

        binding.btnStartStopRecording.strokeColor =
            ContextCompat.getColorStateList(fragmentContext, R.color.color_black)
        binding.btnStartStopRecording.setTextColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.color_black
            )
        )
        binding.btnStartStopRecording.setBackgroundColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.white
            )
        )
        binding.ivRecording.setImageDrawable(
            AppCompatResources.getDrawable(
                fragmentContext,
                R.drawable.ic_black_mic
            )
        )

        binding.clPlayRecording.visibility = View.VISIBLE
        binding.clRecording.visibility = View.GONE
        binding.btnStartStopRecording.visibility = View.INVISIBLE

        binding.btnAudioFromStorage.visibility = View.VISIBLE
        binding.btnAudioFromStorage.text = "Continue"

        binding.btnAudioFromStorage.strokeColor =
            ContextCompat.getColorStateList(fragmentContext, R.color.color_black)
        binding.btnAudioFromStorage.setTextColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.color_black
            )
        )
        binding.btnAudioFromStorage.setBackgroundColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.white
            )
        )

        binding.seekBarCurrentHint.text = "00:00"
        // binding.seekBarHint.text = binding.txtSeconds.text

        binding.ibDelete.visibility = View.VISIBLE
        binding.ibPlay.visibility = View.VISIBLE

        viewModel.actionConsumed()
    }

    private fun pauseRecording() {
        binding.txtPauseResume.text = "Resume Recording"
        isPaused = true
    }

    private fun deleteRecording() {
        if (mediaPlayer != null) {
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
            binding.seekBar.progress = 0
            binding.seekBarCurrentHint.text = "00:00"
            binding.seekBarHint.text = "00:00"
        }

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

        binding.clPlayRecording.visibility = View.GONE
        binding.clRecording.visibility = View.VISIBLE
        binding.btnStartStopRecording.visibility = View.VISIBLE
        binding.btnAudioFromStorage.text = "Upload Audios"
        binding.txtSeconds.text = ""
        binding.btnAudioFromStorage.strokeColor =
            ContextCompat.getColorStateList(fragmentContext, R.color.color_dark_gray)
        binding.btnAudioFromStorage.setTextColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.color_dark_gray
            )
        )
        binding.btnAudioFromStorage.setBackgroundColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.white
            )
        )

//        if (!isFromStorage) {
//            delete(requireActivity() as AppCompatActivity, recordedMediaFilePath)
//        }

        binding.ibDelete.visibility = View.GONE
        binding.ibPlay.visibility = View.GONE

        viewModel.actionConsumed()

        prepareAudioRecordingSettings()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray,
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    waveRecorder!!.startRecording()
                }
                return
            }

            else -> {
            }
        }
    }

    private fun audioVolumeAutomation(file: String) {
        showProgressWithText("Volume Automation in progress", false)
        val outputPath = Common.getFilePath(
            fragmentContext,
            Common.MP3,
            Common.OPERATION_VOLUME_AUDIO,
            Common.getRandomName(6)
        )
        val ffmpegQueryExtension = FFmpegQueryExtension()

        val query = ffmpegQueryExtension.audioVolumeUpdate(
            inputFile = file,
            volume = 1.0f,
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

                }

                override fun cancel() {

                }

                override fun failed() {

                }
            })
    }

    private fun formatTimeUnit(timeInMilliseconds: Long): String {

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
    fun onPlayButtonClicked() {

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()

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
                val file = File(recordedMediaFilePath)
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
        parentJob.cancel()
//        stopRepeatingTask()
        if (mediaPlayer != null) {
            mediaPlayer = null
        }
        viewModel.actionConsumed()
        waveRecorder!!.stopRecording()
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

}