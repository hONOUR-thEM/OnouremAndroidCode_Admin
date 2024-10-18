package com.onourem.android.activity.ui.admin.create.external_posts

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentExternalContentBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class ExternalContentFragment  //    private int counter = 0;
    : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentExternalContentBinding>() {

    private var bitmap: Bitmap? = null
    private var pickerViewModel: MediaPickerViewModel? = null
    private var adapter: ArrayAdapter<String>? = null
    private var uriImagePath = ""
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15)) //15
        .placeholder(R.drawable.shape_image_video_placeholder)
        .error(R.drawable.shape_image_video_placeholder)


    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper


    override fun layoutResource(): Int {
        return R.layout.fragment_external_content
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

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                addExternalContentByAdmin()
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

//        Toast.makeText(
//            fragmentContext,
//            ExternalContentFragmentArgs.fromBundle(requireArguments()).formattedDateTime,
//            Toast.LENGTH_LONG
//        ).show()


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


    private fun addExternalContentByAdmin() {
        var youtubeLink = ""
        var externalLink = ""
        if (binding.tilSpinner.text.toString() == "Yes") {
            youtubeLink = binding.tilExternalContentUrl.editText!!.text.toString()
        } else {
            externalLink = binding.tilExternalContentUrl.editText!!.text.toString()
        }
        viewModel.addExternalContentByAdmin(
            binding.tilSummary.editText!!.text.toString(),
            youtubeLink,
            externalLink,
            binding.tilSource.editText!!.text.toString(),
            if (binding.tilSpinner.text.toString() == "Yes") {
                "Y"
            } else {
                "N"
            },
            AppUtilities.getBase64String(bitmap, 100),
            AppUtilities.getBase64String(bitmap, 500)
        )
            .observe(viewLifecycleOwner) { apiResponse1: ApiResponse<StandardResponse> ->
                if (apiResponse1.loading) {
                    showProgress()
                } else if (apiResponse1.isSuccess && apiResponse1.body != null) {
                    hideProgress()
                    if (apiResponse1.body.errorCode.equals("000")) {

                        binding.tilExternalContentUrl.editText!!.setText("")
                        binding.tilSummary.editText!!.setText("")
                        binding.tilSpinner.setText("")
                        binding.tilSource.editText!!.setText("")
                        binding.btnSourceImage.text = "Source Image"
                        bitmap = null
                        uriImagePath = ""

                        showAlert("Content as been uploaded."){
                            pickerViewModel!!.setIntentMutableLiveData(null)
                            navController.popBackStack(R.id.nav_create_activities_main, false)
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