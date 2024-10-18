package com.onourem.android.activity.ui.admin.vocals

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentRatingBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject
import kotlin.math.roundToInt

class RatingFragment() :
    AbstractBaseBottomSheetBindingDialogFragment<MediaOperationViewModel, FragmentRatingBinding>() {


    private var audioRatings: Int = 0

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_rating
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
        val args = RatingFragmentArgs.fromBundle(requireArguments())


        binding.tvDialogTitle.text = "Update audio ratings"

        binding.mRating.setOnRatingChangeListener { ratingBar, rating, fromUser ->
            audioRatings = rating.roundToInt()
        }

        if (args.fromAction == "3Dots"){
            binding.mRating.rating = args.song.audioRatings.toFloat()
        }

        binding.cvSubmit.setOnClickListener(ViewClickListener {
            if (audioRatings > 0) {
                updateRatings(args)
            } else {
                Toast.makeText(fragmentContext, "Please update rating first", Toast.LENGTH_SHORT)
                    .show()
            }

        })

    }


    private fun updateRatings(args: RatingFragmentArgs) {

        when (args.fromAction) {
            "3Dots" -> {
                val song = args.song
                song.audioRatings = audioRatings.toString()
                updateAudioRating(song)
            }

            "Approve" -> {
                val song = args.song
                song.audioRatings = audioRatings.toString()
                approveAudio(song)
            }

            "Reject" -> {
                val song = args.song
                rejectAudio(song)
            }
        }
    }

    private fun approveAudio(audio: Song) {
        viewModel.approveAudioRequest(audio.audioId, audio.audioRatings).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    viewModel.setFromAction(Pair("Approve", audio))
                    navController.popBackStack()
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun rejectAudio(audio: Song) {
        viewModel.rejectAudioRequest(audio.audioId, audio.audioRatings)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress();
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress();
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        viewModel.setFromAction(Pair("Reject", audio))
                        navController.popBackStack()
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress();
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

    private fun updateAudioRating(audio: Song) {
        viewModel.updateAudioRating(audio.audioId, audio.audioRatings).observe(viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress();
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress();
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    navController.popBackStack()
                    viewModel.setFromAction(Pair("Update", audio))
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress();
                showAlert(apiResponse.errorMessage)
            }
        }
    }

}