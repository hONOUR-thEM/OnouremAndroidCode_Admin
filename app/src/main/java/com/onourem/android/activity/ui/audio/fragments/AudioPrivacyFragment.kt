package com.onourem.android.activity.ui.audio.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.databinding.FragmentAudioPrivacyBinding
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.adapters.SelectAudioPrivacyAdapter
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class AudioPrivacyFragment :
    AbstractBaseBottomSheetBindingDialogFragment<MediaOperationViewModel, FragmentAudioPrivacyBinding>() {

    private var song: Song? = null
    private lateinit var oldSelected: ArrayList<PrivacyGroup>
    private lateinit var audioPrivacyAdapter: SelectAudioPrivacyAdapter

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_audio_privacy
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (dialog != null) dialog!!.setCanceledOnTouchOutside(true)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDialogTitle.text = "Please select audio privacy"

        val privacyGroups = AudioPrivacyFragmentArgs.fromBundle(
            requireArguments()
        ).privacyGroups
        val defaultGroups = AudioPrivacyFragmentArgs.fromBundle(
            requireArguments()
        ).defaultGroups

        song = AudioPrivacyFragmentArgs.fromBundle(requireArguments()).song

        setAdapter(privacyGroups.toList(), defaultGroups.toList())
        binding.cvSubmit.visibility = View.GONE
        binding.cvSubmit.setOnClickListener(ViewClickListener {
            viewModel.setSelectedPrivacyGroup(audioPrivacyAdapter.selected)
            if (song != null) {
                val ids: ArrayList<Int> = ArrayList()
                audioPrivacyAdapter.selected.forEach { item ->
                    ids.add(item.groupId)
                }
                song!!.privacyId = Utilities.getTokenSeparatedString(ids, ",")
                viewModel.setPrivacyUpdatedItem(song)
            }
            navController.popBackStack()
        })
    }

    private fun setAdapter(
        privacyGroups: List<PrivacyGroup>,
        defaultSelected: List<PrivacyGroup>,
    ) {
        val layoutManager = LinearLayoutManager(activity)
        binding.rvAudioPrivacy.addItemDecoration(
            DividerItemDecoration(
                activity, DividerItemDecoration.VERTICAL
            )
        )
        binding.rvAudioPrivacy.layoutManager = layoutManager


//        if (selectedPrivacyIds != "") {
//
//            val selectedPrivacyIdsList: List<String> =
//                listOf(*selectedPrivacyIds.split(",").toTypedArray())
//
//            oldSelected = ArrayList()
//            selectedPrivacyIdsList.forEach(action = {
//                val privacyGroup = PrivacyGroup()
//                privacyGroup.groupId = it.toInt()
//                oldSelected.add(privacyGroup)
//            })
//        }


        audioPrivacyAdapter = SelectAudioPrivacyAdapter(
            privacyGroups,
            defaultSelected,
        ) { item: List<PrivacyGroup> ->
            val ids: ArrayList<Int> = ArrayList()
            item.forEach { itemX ->
                ids.add(itemX.groupId)
            }

            if (song != null) {
                if (Utilities.getTokenSeparatedString(ids, ",") != song!!.privacyId) {
                    binding.cvSubmit.visibility = View.VISIBLE
                } else {
                    binding.cvSubmit.visibility = View.GONE
                }
            }

//            if (Utilities.getTokenSeparatedString(ids, ",") != song!!.privacyId) {
//                binding.cvSubmit.visibility = View.VISIBLE
//            } else {
//                binding.cvSubmit.visibility = View.GONE
//            }

        }
        binding.rvAudioPrivacy.adapter = audioPrivacyAdapter
    }


}