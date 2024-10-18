package com.onourem.android.activity.ui.audio.fragments

import android.app.Activity
import com.onourem.android.activity.ui.utils.compression.LightCompressionUtils.getMediaPath
import com.onourem.android.activity.ui.utils.media.Common.getFilePath
import com.onourem.android.activity.ui.utils.media.Common.getRandomName
import com.onourem.android.activity.arch.fragment.AbstractBaseBindingDialogFragment
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import javax.inject.Inject
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.bumptech.glide.request.RequestOptions
import android.graphics.Bitmap
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.onourem.android.activity.ui.audio.fragments.AudioCommentDialogFragmentArgs
import com.onourem.android.activity.R
import android.content.Intent
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.engine.GlideException
import com.onourem.android.activity.ui.utils.compression.LightCompressionUtils
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.text.TextUtils
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.text.Editable
import android.text.InputFilter
import android.view.View
import androidx.core.util.Pair
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.databinding.DialogAudioCommentBinding
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.media.*
import java.util.*
import javax.inject.Named

class AudioCommentDialogFragment :
    AbstractBaseBindingDialogFragment<CommentsViewModel, DialogAudioCommentBinding>() {

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null
    private var options: RequestOptions? = null
    private var uriImagePath = ""
    private var uriVideoPath = ""
    private var bitmap: Bitmap? = null
    private var pickerViewModel: MediaPickerViewModel? = null
    private var audioId: String? = null
    private var fileSrc: String? = null
    override fun viewModelType(): Class<CommentsViewModel> {
        return CommentsViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            MediaPickerViewModel::class.java
        )
        val args = AudioCommentDialogFragmentArgs.fromBundle(requireArguments())
        audioId = args.audioId
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_audio_comment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder)
        setupView()
        observeOnActivityResult()
    }

    private fun observeOnActivityResult() {
        pickerViewModel!!.getIntentMutableLiveData()!!
            .observe(viewLifecycleOwner) { intentIntegerPair ->
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
                            ) { item1: Pair<Int?, String?>? ->
                                if (item1 != null && item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                                    openCropActivity(uri)
                                } else {
                                    uriImagePath = uri.toString()
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
                                                    bitmap = resource
                                                    binding.ivSelectedMedia.setImageBitmap(
                                                        resource
                                                    )
                                                    binding.btnDialogUpImage.visibility =
                                                        View.INVISIBLE
                                                    binding.cardSelectedMedia.visibility =
                                                        View.VISIBLE
                                                    binding.ibDeleteMedia.visibility =
                                                        View.VISIBLE
                                                    binding.ibVideoMedia.visibility =
                                                        View.INVISIBLE
                                                }
                                                return true
                                            }
                                        }).submit()
                                }
                            }
                        }
                    }
                    333, 444 -> {
                        val data444 = intentIntegerPair.first
                        val uri444 = data444.data
                        if (uri444 != null) {
                            fileSrc = getMediaPath(requireActivity(), uri444)
                            if (Utilities.checkVideoDuration444(
                                    requireActivity(), fileSrc, preferenceHelper!!.getString(
                                        Constants.KEY_VIDEO_DURATION
                                    )
                                )
                            ) {
                                bitmap = Common.retrieveVideoFrameFromVideo(fileSrc)
                                //
//                            binding.btnDialogUpImage.setVisibility(View.INVISIBLE);
//                            binding.cardSelectedMedia.setVisibility(View.VISIBLE);
//                            binding.ibDeleteMedia.setVisibility(View.VISIBLE);
//                            binding.ibVideoMedia.setVisibility(View.VISIBLE);
                                Glide.with(requireActivity())
                                    .load(bitmap)
                                    .apply(options!!)
                                    .into(binding.ivSelectedMedia)
                                if (!TextUtils.isEmpty(fileSrc)) {
                                    showProgressWithText("Compressing Video", false)
                                    val retriever = MediaMetadataRetriever()
                                    retriever.setDataSource(fileSrc)
                                    val width = retriever.frameAtTime!!.width.toString().toInt()
                                    val height = retriever.frameAtTime!!.height.toString().toInt()
                                    retriever.release()
                                    val outputPath = getFilePath(
                                        fragmentContext,
                                        Common.VIDEO,
                                        Common.OPERATION_COMPRESS_VIDEO,
                                        getRandomName(6)
                                    )
                                    val fFmpegQueryExtension = FFmpegQueryExtension()
                                    val query = fFmpegQueryExtension.compressor(
                                        fileSrc!!, width, height, outputPath
                                    )
                                    val callBackOfQuery = CallBackOfQuery()
                                    callBackOfQuery.callQuery(
                                        (requireActivity() as AppCompatActivity),
                                        query,
                                        object : FFmpegCallBack {
                                            override fun process(logMessage: LogMessage) {
                                                //onouremProgressDialog.setProgress(logMessage.getLevel().getValue(), 100);
                                            }

                                            override fun statisticsProcess(statistics: Statistics) {}
                                            override fun success() {
                                                uriVideoPath = outputPath
                                                hideProgress()
                                            }

                                            override fun cancel() {
                                                hideProgress()
                                            }

                                            override fun failed() {
                                                hideProgress()
                                            }
                                        })
                                }
                            } else {
                                uriVideoPath = ""
                                showAlert(
                                    Utilities.getMaxVideoDurationText(
                                        preferenceHelper!!.getString(Constants.KEY_VIDEO_DURATION)
                                    )
                                )
                            }
                            return@observe
                        }
                    }
                }
            }
    }

    private fun setupView() {
        binding.bubble.root.visibility = View.GONE
        binding.button1.root.visibility = View.GONE
        binding.button2.root.visibility = View.GONE
        binding.button3.root.visibility = View.GONE
        binding.clImageButton.visibility = View.GONE
        binding.tvDialogTitle.text = getString(R.string.label_comment)
        binding.tvDialogMessage.filters = arrayOf<InputFilter>(LengthFilter(1000))
        binding.tvDialogMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= 800) {
                    binding.tvRemainingText.text =
                        String.format(Locale.getDefault(), "%d remaining", 1000 - s.length)
                } else {
                    binding.tvRemainingText.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length >= 800) {
                    binding.tvRemainingText.visibility = View.VISIBLE
                } else {
                    binding.tvRemainingText.visibility = View.GONE
                }
            }
        })
        binding.btnDialogOk.setOnClickListener(ViewClickListener(View.OnClickListener { v: View? ->
            if (TextUtils.isEmpty(
                    Objects.requireNonNull(
                        binding.tvDialogMessage.text
                    ).toString().trim { it <= ' ' })
            ) {
                showAlert("Please enter text")
                return@OnClickListener
            }
            viewModel.writeAudioComments(
                audioId!!, Objects.requireNonNull(
                    Base64Utility.encodeToString(
                        binding.tvDialogMessage.text.toString()
                    )
                )
            ).observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        pickerViewModel!!.setIntentMutableLiveData(null)
                        viewModel.publishCommentCount(audioId, null, 1, null)
                        dismiss()
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "writeAudioComments")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "writeAudioComments",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
        }))
        binding.btnDialogCancel.setOnClickListener(ViewClickListener { v: View? ->
            pickerViewModel!!.setIntentMutableLiveData(null)
            dismiss()
        })
        binding.btnDialogUpImage.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(true)
            )
        })
        val clickListener = ViewClickListener { v: View? ->
            if (!uriImagePath.equals("", ignoreCase = true)) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavMediaView(
                        1,
                        uriImagePath
                    )
                )
            } else if (!uriVideoPath.equals("", ignoreCase = true)) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavMediaView(
                        2,
                        uriVideoPath
                    )
                )
            }
        }
        binding.ivSelectedMedia.setOnClickListener(clickListener)
        binding.ibVideoMedia.setOnClickListener(clickListener)
        binding.ibDeleteMedia.setOnClickListener(ViewClickListener { v: View? ->
            TwoActionAlertDialog.showAlert(
                requireActivity(),
                getString(R.string.label_confirm),
                "Delete Image/Video?",
                null,
                "Cancel",
                "Yes",
                OnItemClickListener { item1: Pair<Int?, Any?> ->
                    if (TwoActionAlertDialog.ACTION_RIGHT == item1.first) {
                        binding.btnDialogUpImage.visibility = View.VISIBLE
                        binding.cardSelectedMedia.visibility = View.INVISIBLE
                        binding.ibDeleteMedia.visibility = View.INVISIBLE
                        binding.ibVideoMedia.visibility = View.INVISIBLE
                        binding.ivSelectedMedia.setImageDrawable(null)
                        uriVideoPath = ""
                        uriImagePath = ""
                    }
                })
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pickerViewModel = null
        viewModel.setReloadUi("reload")
    }

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
                uriImagePath = result.uriContent.toString()

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
                                bitmap = resource
                                binding.ivSelectedMedia.setImageBitmap(resource)
                                binding.btnDialogUpImage.visibility = View.INVISIBLE
                                binding.cardSelectedMedia.visibility = View.VISIBLE
                                binding.ibDeleteMedia.visibility = View.VISIBLE
                                binding.ibVideoMedia.visibility = View.INVISIBLE
                            }
                            return true
                        }
                    }).submit()

            }
            result is CropImage.CancelledResult -> showAlert("cropping image was cancelled by the user")
            else -> showAlert("cropping image failed")
        }
    }

}