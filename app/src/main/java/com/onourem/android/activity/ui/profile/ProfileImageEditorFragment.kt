package com.onourem.android.activity.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentProfileImageEditorBinding
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.UpdateUserCoverAndProfileImageResponse
import com.onourem.android.activity.models.UserProfileData
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class ProfileImageEditorFragment :
    AbstractBaseViewModelBindingFragment<ProfileViewModel, FragmentProfileImageEditorBinding>() {
    private val requestOptions: BaseRequestOptions<*> = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var type = 0
    private var pickerViewModel: MediaPickerViewModel? = null
    private var profile: UserProfileData? = null
    override fun layoutResource(): Int {
        return R.layout.fragment_profile_image_editor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            MediaPickerViewModel::class.java
        )
        pickerViewModel!!.intentMutableLiveData!!.observe(this) { intentIntegerPair: Pair<Intent, Int?>? ->
            if (intentIntegerPair == null) return@observe
            when (intentIntegerPair.second) {
                111, 222 -> {
                    val data111 = intentIntegerPair.first
                    val uri = data111.data
                    if (uri != null) {
                        TwoActionAlertDialog.showAlert(
                            requireActivity(),
                            getString(R.string.label_confirm),
                            "Would you like to crop the Image?",
                            "",
                            "No",
                            "Yes"
                        ) { item1: androidx.core.util.Pair<Int?, String?>? ->
                            if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                                openCropActivity(uri)
                            } else {
                                val uriImagePath = uri.toString()
                                Glide.with(requireActivity()).asBitmap().load(uriImagePath)
                                    .addListener(object : RequestListener<Bitmap?> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: Target<Bitmap?>,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Bitmap,
                                            model: Any,
                                            target: Target<Bitmap?>?,
                                            dataSource: DataSource,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            requireActivity().runOnUiThread { changeImage(resource) }
                                            return true
                                        }
                                    }).submit()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun changeImage(resource: Bitmap?) {
        showProgress()
        val largeImage = AppUtilities.getBase64String(resource, 500)
        val smallPostImage = AppUtilities.getBase64String(resource, 100)
        viewModel.updateUserCoverAndProfileImage(
            smallPostImage,
            largeImage,
            if (type == TYPE_COVER_IMAGE) "cover" else "profile"
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UpdateUserCoverAndProfileImageResponse> ->
            if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                pickerViewModel!!.setIntentMutableLiveData(null)
                //                viewModel.setProfileImageUpdated(true);
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    binding.ivPicture.setImageBitmap(resource)
                    val loginResponse = Gson().fromJson(
                        preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                        LoginResponse::class.java
                    )
                    if (type == TYPE_COVER_IMAGE) {
                        profile!!.coverLargePicture = apiResponse.body.largeCoverPicture
                        loginResponse.largeCoverPicture = apiResponse.body.largeCoverPicture?:""
                        loginResponse.coverPicture = apiResponse.body.coverPicture?:""
                    } else {
                        profile!!.userProfilePicture = apiResponse.body.largeProfilePicture
                        loginResponse.largeProfilePicture = apiResponse.body.largeProfilePicture?:""
                        loginResponse.profilePicture = apiResponse.body.profilePicture?:""
                    }
                    preferenceHelper!!.putValue(
                        Constants.KEY_USER_OBJECT,
                        Gson().toJson(loginResponse)
                    )
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else if (!apiResponse.loading) {
                pickerViewModel!!.setIntentMutableLiveData(null)
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "updateUserCoverAndProfileImage")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "updateUserCoverAndProfileImage",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    override fun viewModelType(): Class<ProfileViewModel> {
        return ProfileViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type = ProfileImageEditorFragmentArgs.fromBundle(
            requireArguments()
        ).type
        profile = ProfileImageEditorFragmentArgs.fromBundle(
            requireArguments()
        ).profile
        Glide.with(requireActivity())
            .load(if (type == TYPE_PROFILE_IMAGE) profile!!.userProfilePicture else profile!!.coverLargePicture)
            .apply(requestOptions)
            .into(binding.ivPicture)
        val deviceUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
        if (profile!!.loginUserId.equals(deviceUserId, ignoreCase = true)) {
            binding.btnChangePicture.visibility = View.VISIBLE
            binding.btnChangePicture.setOnClickListener(ViewClickListener { v: View? ->
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(false)
                )
            })
        } else {
            binding.btnChangePicture.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pickerViewModel = null
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (result != null) {
//                val uriImagePath = result.uri.toString()
//                Glide.with(requireActivity()).asBitmap().load(uriImagePath)
//                    .addListener(object : RequestListener<Bitmap?> {
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any,
//                            target: Target<Bitmap?>,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            showAlert(getString(R.string.error_while_processing_image_request))
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: Bitmap?,
//                            model: Any,
//                            target: Target<Bitmap?>,
//                            dataSource: DataSource,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            requireActivity().runOnUiThread { changeImage(resource) }
//                            return true
//                        }
//                    }).submit()
//            }
//        }
//    }

    private fun openCropActivity(uriImagePath: Uri) {
        // for fragment (DO NOT use `getActivity()`)
//        CropImage.activity(uriImagePath)
//            .start(fragmentContext, this)
        cropImage.launch(
            CropImageContractOptions(
                uri = uriImagePath,
                cropImageOptions = CropImageOptions(),
            ),
        )
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        when {
            result.isSuccessful -> {
                val uriImagePath = result.uriContent.toString()
                Glide.with(requireActivity()).asBitmap().load(uriImagePath)
                    .addListener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            model: Any,
                            target: Target<Bitmap?>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            requireActivity().runOnUiThread {
                                changeImage(resource)
                            }
                            return true
                        }
                    }).submit()

            }
            result is CropImage.CancelledResult -> showAlert("cropping image was cancelled by the user")
            else -> showAlert("cropping image failed")
        }
    }

    companion object {
        const val TYPE_PROFILE_IMAGE = 0
        const val TYPE_COVER_IMAGE = 1
    }
}