package com.onourem.android.activity.ui.audio.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.databinding.FragmentAudioCategoryBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.adapters.AudioStatsAdapter
import com.onourem.android.activity.ui.audio.models.AudioStats
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class AudioStatsFragment :
    AbstractBaseBottomSheetBindingDialogFragment<MediaOperationViewModel, FragmentAudioCategoryBinding>() {

    private lateinit var audioCategoryAdapter: AudioStatsAdapter

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_audio_category
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dialog != null) dialog!!.setCanceledOnTouchOutside(false)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
        binding.tvDialogTitle.text = "Audio Stats"

        binding.cvClose.setOnClickListener(ViewClickListener {
            navController.popBackStack()
        })

        setAdapter(
            AudioStatsFragmentArgs.fromBundle(
                requireArguments()
            ).song
        )

    }


    private fun setAdapter(song: Song) {

        val audioStatsList = ArrayList<AudioStats>()
        audioStatsList.add(
            AudioStats(
                "Number Of Likes : ",
                song.numberOfLike
            )
        )
        audioStatsList.add(
            AudioStats(
                "Number Of Listen : ",
                song.numberOfViews
            )
        )

        val layoutManager = LinearLayoutManager(activity)
        binding.rvAudioCategory.addItemDecoration(
            DividerItemDecoration(
                activity, DividerItemDecoration.VERTICAL
            )
        )
        binding.rvAudioCategory.layoutManager = layoutManager
        audioCategoryAdapter = AudioStatsAdapter(audioStatsList) {

        }
        binding.rvAudioCategory.adapter = audioCategoryAdapter
    }


}