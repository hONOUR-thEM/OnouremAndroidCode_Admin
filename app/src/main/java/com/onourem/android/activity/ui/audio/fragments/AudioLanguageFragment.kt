package com.onourem.android.activity.ui.audio.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.databinding.FragmentAudioLanguageBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.adapters.AudioLanguageAdapter
import com.onourem.android.activity.ui.audio.models.Language
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.FFmpegQueryExtension
import javax.inject.Inject

class AudioLanguageFragment :
    AbstractBaseBottomSheetBindingDialogFragment<MediaOperationViewModel, FragmentAudioLanguageBinding>() {

    private lateinit var audioCategoryAdapter: AudioLanguageAdapter

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null


    val ffmpegQueryExtension = FFmpegQueryExtension()

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_audio_language
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dialog != null) dialog!!.setCanceledOnTouchOutside(false)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
        binding.tvDialogTitle.text = "Please select audio language"
        binding.cvClose.setOnClickListener(ViewClickListener {
            navController.popBackStack()
        })
        setAdapter(
            AudioLanguageFragmentArgs.fromBundle(
                requireArguments()
            ).responseDataLanguage.languageList as ArrayList<Language>
        )
    }

    private fun setAdapter(languageList: ArrayList<Language>) {
        val layoutManager = LinearLayoutManager(activity)
        binding.rvAudioCategory.addItemDecoration(
            DividerItemDecoration(
                activity, DividerItemDecoration.VERTICAL
            )
        )

        binding.rvAudioCategory.layoutManager = layoutManager

        audioCategoryAdapter = AudioLanguageAdapter(languageList) {
            viewModel.setAudioLanguageObject(it)
            navController.popBackStack()
        }

        binding.rvAudioCategory.adapter = audioCategoryAdapter

    }


}