package com.onourem.android.activity.ui.audio.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentAudioEditorBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.*
import com.onourem.android.activity.ui.utils.media.Common.getFilePath
import com.onourem.android.activity.ui.utils.media.Common.getRandomName
import com.onourem.android.activity.ui.utils.range_seekbar.OnRangeSeekBarListener
import com.onourem.android.activity.ui.utils.range_seekbar.RangeSeekBar
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AudioEditorFragment :
    AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentAudioEditorBinding>(),
    OnRangeSeekBarListener {

    private var endTime: Int = 0
    private var startTime: Int = 0
    private var mediaTotalDuration = 0L
    private var mediaTotalDurationInSeconds = 0L
    private var mediaTotalTrimmedDurationInSeconds = 0L

    private var mediaPlayer: MediaPlayer? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    var actionToPerform = ""
    private var mFilename: String? = null
    override fun layoutResource(): Int {
        return R.layout.fragment_audio_editor
    }

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFilename = ""
        if (arguments != null) {
            val audioEditorFragmentArgs = AudioEditorFragmentArgs.fromBundle(requireArguments())
            mFilename = audioEditorFragmentArgs.recordedAudioFilePath
            mediaTotalDuration = audioEditorFragmentArgs.mediaTotalDuration

            mediaTotalDurationInSeconds = TimeUnit.MILLISECONDS.toSeconds(mediaTotalDuration)

            startTime = 0
            endTime = mediaTotalDurationInSeconds.toInt()
            mediaTotalTrimmedDurationInSeconds = (endTime - startTime).toLong()
            binding.txtStartTime.text = formatTimeUnitForSeekbar(0)
            binding.txtEndTime.text =
                formatTimeUnitForSeekbar(TimeUnit.SECONDS.toMillis(mediaTotalDurationInSeconds))
            binding.txtTrimmedTime.text =
                formatTimeUnitForSeekbar(
                    TimeUnit.SECONDS.toMillis(
                        mediaTotalTrimmedDurationInSeconds
                    )
                )
            //binding.endtext.setText(mediaTotalDurationInSeconds.toString())

        }
//        viewModel.trimSelection.observe(
//            viewLifecycleOwner) { trimSelection: Pair<Int, Int> ->
//            binding.txtStartTime.text = formatTimeUnitForSeekbar(trimSelection.first.toLong())
//            binding.txtEndTime.text =
//                formatTimeUnitForSeekbar(TimeUnit.SECONDS.toMillis(trimSelection.second.toLong()))
//
//            binding.rangeSeekBar.startProgress = trimSelection.first
//            binding.rangeSeekBar.setMax(mediaTotalDurationInSeconds.toInt())
//            binding.rangeSeekBar.endProgress = trimSelection.second
//            binding.rangeSeekBar.setMinDifference(1)
//        }
        loadGui()


    }

    /**
     * Called from both onCreate and onConfigurationChanged
     * (if the user switched layouts)
     */
    private fun loadGui() {
        // Inflate our UI from its XML layout description.
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

        binding.btnAudioFromStorage.setOnClickListener(ViewClickListener {
            audioConfirmation()
        })

        setupViews()
    }

    private fun setupViews() {
        binding.rangeSeekBar.startProgress = 0 // default is 0
        binding.rangeSeekBar.setMax(mediaTotalDurationInSeconds.toInt())  // default is 50
        binding.rangeSeekBar.endProgress = mediaTotalDurationInSeconds.toInt() // default is 50
        binding.rangeSeekBar.setMinDifference(1) // default is 20
//        startText.setText(String.valueOf(binding.rangeSeekBar.startProgress))
//        endText.setText(String.valueOf(binding.rangeSeekBar.endProgress))
        binding.rangeSeekBar.setOnRangeSeekBarListener(this)
    }


    private fun audioConfirmation() {

//        if (mediaTotalTrimmedDurationInSeconds > (preferenceHelper!!.getInt(Constants.KEY_AUDIO_LIMIT))) {
//            showAlert("Audio length is greater than 3 minutes, Please trim it to continue.")
//        } else {
            TwoActionAlertDialog.showAlert(
                requireActivity(),
                getString(R.string.label_confirm),
                "Section selected would be the final Audio. Would you like to continue?",
                "",
                "No",
                "Yes"
            ) { item1: androidx.core.util.Pair<Int?, String> ->
                if (item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {

//                saveRingtone();
                    showProgress()
                    val outputPath = getFilePath(
                        fragmentContext,
                        Common.MP3,
                        Common.OPERATION_COMPRESS_AUDIO,
                        getRandomName(6)
                    )
                    val fFmpegQueryExtension = FFmpegQueryExtension()

                    val query = fFmpegQueryExtension.cutAudio(
                        mFilename!!,
                        startTime.toString(),
                        endTime.toString(),
                        outputPath
                    )
                    val callBackOfQuery = CallBackOfQuery()
                    callBackOfQuery.callQuery(
                        (requireActivity() as DashboardActivity),
                        query,
                        object : FFmpegCallBack {
                            override fun process(logMessage: LogMessage) {}
                            override fun statisticsProcess(statistics: Statistics) {}
                            override fun success() {

                                afterSavingRingtone(outputPath, endTime.toInt())

                            }

                            override fun cancel() {}
                            override fun failed() {}
                        })


                }
            }
//        }
    }

    private fun enableDisableButtons(mIsPlaying: Boolean) {
        if (mIsPlaying) {
            binding.play.setImageResource(R.drawable.ic_recording_pause)
            binding.play.contentDescription = resources.getText(R.string.stop)
        } else {
            binding.play.setImageResource(R.drawable.ic_recording_play)
            binding.play.contentDescription = resources.getText(R.string.play)
        }
    }

    private fun afterSavingRingtone(finalOutPath: String, duration: Int) {
        hideProgress()
        navController.navigate(
            AudioEditorFragmentDirections.actionNavAudioEditorToNavRecorderSettings(
                finalOutPath,
                duration.toLong(),
                false,
                startTime,
                endTime
            )
        )
    }

    override fun onRangeValues(rangeSeekBar: RangeSeekBar?, start: Int, end: Int) {

        startTime = start
        endTime = end

        mediaTotalTrimmedDurationInSeconds = (endTime - startTime).toLong()

        binding.txtStartTime.text =
            formatTimeUnitForSeekbar(TimeUnit.SECONDS.toMillis(start.toLong()))
        binding.txtEndTime.text = formatTimeUnitForSeekbar(TimeUnit.SECONDS.toMillis(end.toLong()))
        binding.txtTrimmedTime.text =
            formatTimeUnitForSeekbar(TimeUnit.SECONDS.toMillis(mediaTotalTrimmedDurationInSeconds))

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
}