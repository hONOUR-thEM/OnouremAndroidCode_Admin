package com.onourem.android.activity.ui.games.dialogs

import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogCreateOwnQuestionBinding
import com.onourem.android.activity.models.CreateQuestionForOneToManyResponse
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.compression.LightCompressionUtils.getMediaPath
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.*
import com.onourem.android.activity.ui.utils.media.Common.getFilePath
import com.onourem.android.activity.ui.utils.media.Common.getRandomName
import com.onourem.android.activity.ui.utils.media.Common.retrieveVideoFrameFromVideo
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import java.util.*
import javax.inject.Inject


class CreateOwnQuestionDialogFragment :
    AbstractBaseDialogBindingFragment<QuestionGamesViewModel, DialogCreateOwnQuestionBinding>() {
    private val options = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)
    private val desPath = ""

    @JvmField
    @Inject
    var sharedPreferenceHelper: SharedPreferenceHelper? = null
    private var pickerViewModel: MediaPickerViewModel? = null
    private var uriImagePath = ""
    private var uriVideoPath = ""
    private var bitmap: Bitmap? = null
    private var fileSrc: String? = null
    private var questionViewModel: QuestionGamesViewModel? = null
    private val mProgressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            MediaPickerViewModel::class.java
        )
        questionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            QuestionGamesViewModel::class.java
        )
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
                                if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
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
                                                    binding.includeMessageLayout.ivSelectedMedia.setImageBitmap(
                                                        resource
                                                    )
                                                    binding.includeMessageLayout.btnDialogUpImage.visibility =
                                                        View.INVISIBLE
                                                    binding.includeMessageLayout.cardSelectedMedia.visibility =
                                                        View.VISIBLE
                                                    binding.includeMessageLayout.ibDeleteMedia.visibility =
                                                        View.VISIBLE
                                                    binding.includeMessageLayout.ibVideoMedia.visibility =
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
                                    requireActivity(), fileSrc, sharedPreferenceHelper!!.getString(
                                        Constants.KEY_VIDEO_DURATION
                                    )
                                )
                            ) {

                                bitmap = retrieveVideoFrameFromVideo(fileSrc)

                                binding.includeMessageLayout.btnDialogUpImage.visibility =
                                    View.INVISIBLE
                                binding.includeMessageLayout.cardSelectedMedia.visibility =
                                    View.VISIBLE
                                binding.includeMessageLayout.ibDeleteMedia.visibility =
                                    View.VISIBLE
                                binding.includeMessageLayout.ibVideoMedia.visibility =
                                    View.VISIBLE
                                Glide.with(requireActivity())
                                    .asBitmap()
                                    .load(bitmap)
                                    .apply(options)
                                    .into(binding.includeMessageLayout.ivSelectedMedia)


                                // File destinationFile = LightCompressionUtils.saveVideoFile(fileSrc, requireActivity());
                                if (!TextUtils.isEmpty(fileSrc)) {
                                    //desPath = destinationFile.getPath();
                                    //OnouremProgressDialog onouremProgressDialog = new OnouremProgressDialog(getFragmentContext());
                                    // Compression start
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
                                        sharedPreferenceHelper!!.getString(Constants.KEY_VIDEO_DURATION)
                                    )
                                )
                            }
                            return@observe
                        }
                    }
                }
            }
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_create_own_question
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDialogTitle.setText(R.string.create_your_own_ques)
        binding.includeMessageLayout.btnDialogCancel.setOnClickListener(ViewClickListener { view1: View? ->
            pickerViewModel!!.setIntentMutableLiveData(null)
            navController.popBackStack()
        })
        binding.includeMessageLayout.tvDialogMessage.filters =
            arrayOf<InputFilter>(LengthFilter(1000))
        binding.includeMessageLayout.tvDialogMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= 800) {
                    binding.includeMessageLayout.tvRemainingText.text =
                        String.format(Locale.getDefault(), "%d remaining", 1000 - s.length)
                } else {
                    binding.includeMessageLayout.tvRemainingText.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length >= 800) {
                    binding.includeMessageLayout.tvRemainingText.visibility = View.VISIBLE
                } else {
                    binding.includeMessageLayout.tvRemainingText.visibility = View.GONE
                }
            }
        })
        binding.includeMessageLayout.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? -> createQuestion() })
        binding.includeMessageLayout.btnDialogUpImage.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(true)
            )
        })
        val mediaClick = ViewClickListener { v: View? ->
            if (!uriImagePath.equals("", ignoreCase = true)) {
                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_create_own_question) {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavMediaView(
                            1,
                            uriImagePath
                        )
                    )
                }
            } else if (!uriVideoPath.equals("", ignoreCase = true)) {
                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_create_own_question) {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavMediaView(
                            2,
                            uriVideoPath
                        )
                    )
                }
            }
        }
        binding.includeMessageLayout.ivSelectedMedia.setOnClickListener(mediaClick)
        binding.includeMessageLayout.ibVideoMedia.setOnClickListener(mediaClick)
        binding.includeMessageLayout.ibDeleteMedia.setOnClickListener(ViewClickListener { v: View? ->
            TwoActionAlertDialog.showAlert(
                requireActivity(),
                getString(R.string.label_confirm),
                "Delete Image/Video?",
                null,
                "Cancel",
                "Yes",
                OnItemClickListener { item1: Pair<Int?, Any?> ->
                    if (TwoActionAlertDialog.ACTION_RIGHT == item1.first) {
                        binding.includeMessageLayout.btnDialogUpImage.visibility = View.VISIBLE
                        binding.includeMessageLayout.cardSelectedMedia.visibility = View.INVISIBLE
                        binding.includeMessageLayout.ibDeleteMedia.visibility = View.INVISIBLE
                        binding.includeMessageLayout.ibVideoMedia.visibility = View.INVISIBLE
                        binding.includeMessageLayout.ivSelectedMedia.setImageDrawable(null)
                        uriVideoPath = ""
                        uriImagePath = ""
                    }
                })
        })
        binding.includeMessageLayout.button1.root.visibility = View.GONE
        binding.includeMessageLayout.button2.root.visibility = View.GONE
        binding.includeMessageLayout.button3.root.visibility = View.GONE
        binding.includeMessageLayout.txtUserIntimation.visibility = View.VISIBLE
        observeOnActivityResult()
    }

    private fun createQuestion() {
        if (TextUtils.isEmpty(
                binding.includeMessageLayout.tvDialogMessage.text.toString().trim { it <= ' ' })
        ) {
            showAlert("Please enter text")
            return
        }
        var bitmap: Bitmap? = null
        if (!TextUtils.isEmpty(uriImagePath)) {
            bitmap = this.bitmap
        } else if (!TextUtils.isEmpty(uriVideoPath)) {
            bitmap = this.bitmap
        }
        var text = binding.includeMessageLayout.tvDialogMessage.text.toString()
        text = Base64Utility.encodeToString(text.trim { it <= ' ' })

        val onouremProgressDialog = OnouremProgressDialog(fragmentContext)
        val progressCallback =
            object : ProgressCallback {
                override fun onProgressUpdate(percentage: Int) {
                    onouremProgressDialog.setProgress(percentage)
                }
            }
        viewModel.createQuestion(text, bitmap, uriVideoPath, progressCallback).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<CreateQuestionForOneToManyResponse> ->
            if (apiResponse.loading) {
                if (!uriVideoPath.equals("", ignoreCase = true)) {
                    onouremProgressDialog.showDialogWithText("Uploading Video", true)
                } else {
                    showProgress()
                }
            } else if (apiResponse.body != null && apiResponse.isSuccess) {
                hideProgress()
                onouremProgressDialog.hideDialog()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    pickerViewModel!!.setIntentMutableLiveData(null)
                    if (apiResponse.body.loginDayActivityInfoList != null && apiResponse.body.loginDayActivityInfoList!!.isNotEmpty()) {
                        navController.popBackStack()
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavAskQuestionsSheet(
                                apiResponse.body.loginDayActivityInfoList!![0],
                                true,
                                CreateOwnQuestionDialogFragmentArgs.fromBundle(
                                    requireArguments()
                                ).selectedGroup.playGroupId!!
                            )
                        )
                    } else {
                        navController.popBackStack()
                    }
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                onouremProgressDialog.hideDialog()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "createQuestion")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "createQuestion",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    override fun isCancelable(): Boolean {
        return false
    }

    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pickerViewModel = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        questionViewModel!!.setRefreshShowBadges(true)
    }

    private fun openCropActivity(uriImagePath: Uri) {
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
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            requireActivity().runOnUiThread {
                                bitmap = resource
                                binding.includeMessageLayout.ivSelectedMedia.setImageBitmap(
                                    resource
                                )
                                binding.includeMessageLayout.btnDialogUpImage.visibility =
                                    View.INVISIBLE
                                binding.includeMessageLayout.cardSelectedMedia.visibility =
                                    View.VISIBLE
                                binding.includeMessageLayout.ibDeleteMedia.visibility =
                                    View.VISIBLE
                                binding.includeMessageLayout.ibVideoMedia.visibility =
                                    View.INVISIBLE
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