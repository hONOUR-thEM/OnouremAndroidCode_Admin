package com.onourem.android.activity.ui.audio.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.databinding.FragmentAudioCategoryBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.adapters.AudioBackgroundAdapter
import com.onourem.android.activity.ui.audio.models.AudioCategory
import com.onourem.android.activity.ui.audio.models.BackgroundAudio
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.FFmpegQueryExtension
import javax.inject.Inject

class AudioBackgroundFragment :
    AbstractBaseBottomSheetBindingDialogFragment<MediaOperationViewModel, FragmentAudioCategoryBinding>() {

    private lateinit var audioCategoryBackgroundAdapter: AudioBackgroundAdapter

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null


    val ffmpegQueryExtension = FFmpegQueryExtension()

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_audio_category
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dialog != null) dialog!!.setCanceledOnTouchOutside(false)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
        val audioCategory = AudioBackgroundFragmentArgs.fromBundle(
            requireArguments()
        ).audioCategory
        binding.tvDialogTitle.text =
            "Please select background audio of category ${audioCategory.name}"
        setAdapter(audioCategory)
        binding.cvClose.setOnClickListener(ViewClickListener {
            navController.popBackStack()
        })

    }

    private fun setAdapter(categories: AudioCategory) {
        val bgAudioList = categories.backgroundAudioList as ArrayList<BackgroundAudio>
        val layoutManager = LinearLayoutManager(activity)
        binding.rvAudioCategory.addItemDecoration(
            DividerItemDecoration(
                activity, DividerItemDecoration.VERTICAL
            )
        )
        binding.rvAudioCategory.layoutManager = layoutManager
        audioCategoryBackgroundAdapter =
            AudioBackgroundAdapter(bgAudioList) {
                viewModel.setAudioBackgroundObject(it)
                navController.popBackStack()
            }
        binding.rvAudioCategory.adapter = audioCategoryBackgroundAdapter
    }


}