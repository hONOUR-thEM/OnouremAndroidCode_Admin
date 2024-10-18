package com.onourem.android.activity.ui.audio.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.databinding.FragmentAudioPickerBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.adapters.SongsPickerAdapter
import com.onourem.android.activity.ui.audio.playback.SongProvider
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.FFmpegQueryExtension
import javax.inject.Inject

class AudioPickerFragment :
    AbstractBaseBottomSheetBindingDialogFragment<MediaOperationViewModel, FragmentAudioPickerBinding>() {

    private lateinit var adapter: SongsPickerAdapter

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null


    val ffmpegQueryExtension = FFmpegQueryExtension()

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_audio_picker
    }


    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.isNotEmpty()) {
                adapter.filter!!.filter(s)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dialog != null) dialog!!.setCanceledOnTouchOutside(true)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
        binding.tvDialogTitle.text = "Please select audio"
        binding.tvDialogSubTitle.text = "(Max 3 mins)"
        binding.tvDialogSubTitle.visibility = View.VISIBLE
        setAdapter()
        binding.cvClose.setOnClickListener(ViewClickListener {
            navController.popBackStack()
        })

        binding.edtSearchQuery.addTextChangedListener(textWatcher)
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(activity)
        binding.rvAudioCategory.addItemDecoration(
            DividerItemDecoration(
                activity, DividerItemDecoration.VERTICAL
            )
        )

        binding.rvAudioCategory.layoutManager = layoutManager

        adapter = SongsPickerAdapter {
            viewModel.setFilePath(it.second)
            navController.popBackStack()
        }

        adapter.addSongs(SongProvider.getAllDeviceSongs(requireActivity()))

        binding.rvAudioCategory.adapter = adapter

    }


}