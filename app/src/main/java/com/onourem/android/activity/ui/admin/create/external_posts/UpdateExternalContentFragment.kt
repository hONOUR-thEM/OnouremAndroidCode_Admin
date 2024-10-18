package com.onourem.android.activity.ui.admin.create.external_posts

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentUpdateExternalContentBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UpdateExternalContent
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class UpdateExternalContentFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentUpdateExternalContentBinding>() {

    private var status: String = ""
    private lateinit var externalContent: ExternalActivityData
    private var bitmap: Bitmap? = null
    private var pickerViewModel: MediaPickerViewModel? = null
    private var adapter: ArrayAdapter<String>? = null
    private var uriImagePath = ""
    private var isUpdatedImage = false
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15)) //15
        .placeholder(R.drawable.shape_image_video_placeholder)
        .error(R.drawable.shape_image_video_placeholder)

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper


    override fun layoutResource(): Int {
        return R.layout.fragment_update_external_content
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            MediaPickerViewModel::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        externalContent =
            UpdateExternalContentFragmentArgs.fromBundle(requireArguments()).externalContent

        binding.tilExternalContentUrl.editText!!.setText(externalContent.videoUrl)
        binding.tilSummary.editText!!.setText(externalContent.summary)
        binding.tilSpinner.setText(
            if (externalContent.youtubeLink == "Y") {
                "Yes"
            } else {
                "No"
            }
        )

        binding.tilSource.editText!!.setText(externalContent.sourceName)

        Glide.with(fragmentContext)
            .load(externalContent.imageUrl)
            .apply(options)
            .into(binding.ivSourceImage)

        if (externalContent.imageUrl != "") {
            uriImagePath = externalContent.imageUrl.toString()
            Glide.with(requireActivity()).asBitmap().load(externalContent.imageUrl)
                .addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        model: Any,
                        target: Target<Bitmap>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        requireActivity().runOnUiThread {
                            bitmap = resource
                            binding.btnSourceImage.text = "Source Image Selected"
                            Glide.with(fragmentContext)
                                .load(bitmap)
                                .apply(options)
                                .into(binding.ivSourceImage)
                        }
                        return true
                    }
                }).submit()
        } else {
            binding.btnSourceImage.text = "Source Image"
        }

        if (externalContent.status == "Y") {
            status = "Y"
            binding.switchCompat.isChecked = true
        } else {
            status = "N"
            binding.switchCompat.isChecked = false
        }

        binding.switchCompat.setOnCheckedChangeListener { buttonView, isChecked ->
            status = if (isChecked) {
                AppUtilities.showSnackbar(
                    binding.linearLayout, "Post will be Published", View.GONE,
                    Snackbar.LENGTH_LONG, "", null
                )
                "Y"
            } else {
                AppUtilities.showSnackbar(
                    binding.linearLayout, "Post will be Unpublished", View.GONE,
                    Snackbar.LENGTH_LONG, "", null
                )
                "N"
            }
        }


        if (adapter == null) {
            adapter = ArrayAdapter(
                requireActivity(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.isYoutube)
            )
        }
        binding.tilSpinner.setAdapter(adapter)

        binding.btnSignUp.setOnClickListener(ViewClickListener {
            if (isValidData) {
                updateExternalContentByAdmin()
            }
        })

        binding.btnSourceImage.setOnClickListener(ViewClickListener {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(
                    false
                )
            )
        })

        observeOnActivityResult()

    }

    private val isValidData: Boolean
        get() {
//            val fName = binding.tilExternalContentUrl.editText!!.text.toString()
//                .trim { it <= ' ' }
//            if (TextUtils.isEmpty(fName)) {
//                showAlert("Please enter valid link.")
//                return false
//            }
            val summary = binding.tilSummary.editText!!.text.toString()
                .trim { it <= ' ' }
            if (TextUtils.isEmpty(summary)) {
                showAlert("Please enter valid summary.")
                return false
            }
            if (binding.tilSpinner.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlert("Please select is this Youtube Url or not.")
                return false
            }
            if (TextUtils.isEmpty(
                    binding.tilSource.editText!!.text.toString()
                        .trim { it <= ' ' })
            ) {
                showAlert("Please enter source name.")
                return false
            }
            if (TextUtils.isEmpty(uriImagePath) || bitmap == null) {
                showAlert("Please attach source image")
                return false
            }

            return true
        }


    private fun updateExternalContentByAdmin() {
        var youtubeLink = ""
        var externalLink = ""
        if (binding.tilSpinner.text.toString() == "Yes") {
            youtubeLink = binding.tilExternalContentUrl.editText!!.text.toString()
        } else {
            externalLink = binding.tilExternalContentUrl.editText!!.text.toString()
        }

        val updateExternalContent = UpdateExternalContent()
        updateExternalContent.externalId = externalContent.id
        updateExternalContent.status = status
        updateExternalContent.externalLink = externalLink
        updateExternalContent.isYouTubeLink = if (binding.tilSpinner.text.toString() == "Yes") {
            "Y"
        } else {
            "N"
        }
        updateExternalContent.sourceName = binding.tilSource.editText!!.text.toString()
        updateExternalContent.summary = binding.tilSummary.editText!!.text.toString()
        updateExternalContent.videoLink = youtubeLink
        if (isUpdatedImage) {
            updateExternalContent.image = AppUtilities.getBase64String(bitmap, 500)
            updateExternalContent.smallPostImage = AppUtilities.getBase64String(bitmap, 100)
        }

        viewModel.updateExternalContentByAdmin(updateExternalContent)
            .observe(viewLifecycleOwner) { apiResponse1: ApiResponse<StandardResponse> ->
                if (apiResponse1.loading) {
                    showProgress()
                } else if (apiResponse1.isSuccess && apiResponse1.body != null) {
                    hideProgress()
                    if (apiResponse1.body.errorCode.equals("000")) {
                        showAlert("Content as been updated.") {
                            navController.popBackStack()
                        }
                    } else {
                        showAlert(apiResponse1.errorMessage)
                    }

                } else {
                    showAlert(apiResponse1.errorMessage)
                    hideProgress()
                }
            }
    }

    private fun observeOnActivityResult() {
        pickerViewModel!!.intentMutableLiveData?.observe(
            viewLifecycleOwner
        ) { intentIntegerPair: Pair<Intent, Int?>? ->
            if (intentIntegerPair == null) return@observe
            when (intentIntegerPair.second) {
                111, 222 -> {
                    val data111 = intentIntegerPair.first
                    val uri = data111.data
                    if (uri != null) {
                        uriImagePath = uri.toString()
                        Glide.with(requireActivity()).asBitmap().load(uriImagePath)
                            .addListener(object : RequestListener<Bitmap> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap>,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Bitmap,
                                    model: Any,
                                    target: Target<Bitmap>,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    requireActivity().runOnUiThread {
                                        isUpdatedImage = true
                                        bitmap = resource
                                        binding.btnSourceImage.text = "Source Image Selected"
                                        Glide.with(fragmentContext)
                                            .load(bitmap)
                                            .apply(options)
                                            .into(binding.ivSourceImage)
                                    }
                                    return true
                                }
                            }).submit()
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pickerViewModel!!.setIntentMutableLiveData(null)
        pickerViewModel = null

    }


}