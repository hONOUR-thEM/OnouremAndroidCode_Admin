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
import com.onourem.android.activity.ui.audio.adapters.AudioCategoryAdapter
import com.onourem.android.activity.ui.audio.models.AudioCategory
import com.onourem.android.activity.ui.audio.models.GetAudioCategoryResponse
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class AudioCategoryFragment :
    AbstractBaseBottomSheetBindingDialogFragment<MediaOperationViewModel, FragmentAudioCategoryBinding>() {

    private lateinit var response: GetAudioCategoryResponse
    private lateinit var audioCategoryAdapter: AudioCategoryAdapter

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
        binding.tvDialogTitle.text = "Please select audio category"

        setAdapter(AudioCategoryFragmentArgs.fromBundle(requireArguments()).responseDataCategory.audioCategory as ArrayList<AudioCategory>)

        binding.cvClose.setOnClickListener(ViewClickListener {
            navController.popBackStack()
        })
    }


    private fun setAdapter(categories: ArrayList<AudioCategory>) {
//        Handler(Looper.myLooper()!!).postDelayed({
//
//        }, 100)
        val layoutManager = LinearLayoutManager(activity)
        binding.rvAudioCategory.addItemDecoration(
            DividerItemDecoration(
                activity, DividerItemDecoration.VERTICAL
            )
        )
        binding.rvAudioCategory.layoutManager = layoutManager
        audioCategoryAdapter = AudioCategoryAdapter(categories) {
            viewModel.setAudioCategoryObject(it)
            navController.popBackStack()
        }
        binding.rvAudioCategory.adapter = audioCategoryAdapter
    }


}