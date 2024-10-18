package com.onourem.android.activity.ui.games.dialogs

import android.app.Activity
import com.onourem.android.activity.ui.utils.compression.LightCompressionUtils.getMediaPath
import com.onourem.android.activity.ui.utils.media.Common.getFilePath
import com.onourem.android.activity.ui.utils.media.Common.getRandomName
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.R
import android.os.Bundle
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.games.dialogs.SendNowOrSendLaterDialogFragment
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.bumptech.glide.request.RequestOptions
import javax.inject.Inject
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import android.graphics.Bitmap
import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
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
import androidx.appcompat.app.AppCompatActivity
import com.onourem.android.activity.ui.games.dialogs.EditQuestionDialogFragmentArgs
import com.bumptech.glide.request.target.SimpleTarget
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.text.Editable
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.EditQuestionResponse
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.DashboardActivity
import android.content.DialogInterface
import android.net.Uri
import android.text.InputFilter
import android.view.View
import androidx.core.util.Pair
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.databinding.DialogEditQuestionBinding
import com.onourem.android.activity.ui.games.dialogs.DynamicBannerAlertDialog
import com.onourem.android.activity.ui.games.dialogs.WhatsAppAlertDialog
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.media.*
import java.util.*

class EditQuestionDialogFragment :
    AbstractBaseDialogBindingFragment<QuestionGamesViewModel, DialogEditQuestionBinding>() {
    private val options = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)
    private val desPath = ""

    @JvmField
    @Inject
    var sharedPreferenceHelper: SharedPreferenceHelper? = null
    var question: LoginDayActivityInfoList? = null
    private var pickerViewModel: MediaPickerViewModel? = null
    private var uriImagePath = ""
    private var uriVideoPath = ""
    private var bitmap: Bitmap? = null
    private val fileName: String? = null
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
                            }
                        }
                    }
                    333, 444 -> {
                        val data444 = intentIntegerPair.first
                        val uri444 = data444.data
                        if (uri444 != null) {

//                        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//                        fileName = storageDir.getPath() + "/Onourem/videos" + "/temp.mp4";
//                        File video = new File(fileName);
//                        uriVideoPath = video.getPath();
//
//
//                        Uri videoURI = FileProvider.getUriForFile(requireActivity(),
//                                requireActivity().getApplicationContext().getPackageName() + ".GenericFileProvider", video);
                            fileSrc = getMediaPath(requireActivity(), uri444)
                            if (Utilities.checkVideoDuration444(
                                    requireActivity(), fileSrc, sharedPreferenceHelper!!.getString(
                                        Constants.KEY_VIDEO_DURATION
                                    )
                                )
                            ) {
                                bitmap = Common.retrieveVideoFrameFromVideo(fileSrc)
                                binding.includeMessageLayout.btnDialogUpImage.visibility =
                                    View.INVISIBLE
                                binding.includeMessageLayout.cardSelectedMedia.visibility =
                                    View.VISIBLE
                                binding.includeMessageLayout.ibDeleteMedia.visibility =
                                    View.VISIBLE
                                binding.includeMessageLayout.ibVideoMedia.visibility =
                                    View.VISIBLE

//                            binding.includeMessageLayout.btnDialogUpImage.setEnabled(false);
                                binding.includeMessageLayout.cardSelectedMedia.isEnabled = false
                                binding.includeMessageLayout.ibDeleteMedia.isEnabled = false
                                binding.includeMessageLayout.ibVideoMedia.isEnabled = false
                                Glide.with(requireActivity())
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

//                                VideoCompressor.start(null, // => This is required if srcUri is provided. If not, pass null.
//                                        null, // => Source can be provided as content uri, it requires context.
//                                        fileSrc, desPath, new CompressionListener() {
//                                            @Override
//                                            public void onStart() {
//                                                // Compression start
//                                                mProgressDialog = new ProgressDialog(requireActivity());
//                                                // Set your ProgressBar Title
//                                                //mProgressDialog.setTitle("Compressing Video, Please Wait!");
//                                                mProgressDialog.setIcon(R.drawable.ic_logo);
//                                                // Set your ProgressBar Message
//                                                mProgressDialog.setMessage("Compressing Video, Please Wait!");
//                                                mProgressDialog.setIndeterminate(false);
//                                                mProgressDialog.setMax(100);
//                                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                                                // Show ProgressBar
//                                                mProgressDialog.setCancelable(false);
//                                                //  mProgressDialog.setCanceledOnTouchOutside(false);
//                                                mProgressDialog.show();
//                                            }
//
//                                            @Override
//                                            public void onSuccess() {
//                                                // On Compression success
//                                                uriVideoPath = desPath;
//                                                mProgressDialog.dismiss();
//                                            }
//
//                                            @Override
//                                            public void onFailure(@NotNull String failureMessage) {
//                                                // On Failure
//                                                mProgressDialog.dismiss();
//                                            }
//
//                                            @Override
//                                            public void onProgress(float v) {
//                                                // Update UI with progress value
//                                                requireActivity().runOnUiThread(() -> {
//                                                    mProgressDialog.setProgress((int) v);
////                                            progress.setText(progressPercent + "%");
////                                            progressBar.setProgress((int) progressPercent);
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onCancelled() {
//                                                // On Cancelled
//                                            }
//                                        },  new Configuration(
//                                                VideoQuality.HIGH,
//                                                true,
//                                                false,
//                                                null /*videoHeight: double, or null*/,
//                                                null /*videoWidth: double, or null*/,
//                                                null /*videoBitrate: int, or null*/
//                                        ));
                                }
                            } else {
                                uriVideoPath = ""
                                showAlert(
                                    Utilities.getMaxVideoDurationText(
                                        sharedPreferenceHelper!!.getString(Constants.KEY_VIDEO_DURATION)
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_edit_question
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDialogTitle.setText(R.string.edit_question)
        if (arguments != null) {
            question = EditQuestionDialogFragmentArgs.fromBundle(requireArguments()).question
            binding.includeMessageLayout.tvDialogMessage.setText(question!!.activityText)
            binding.includeMessageLayout.btnDialogOk.text = "Update"

//            binding.includeMessageLayout.btnDialogUpImage.setEnabled(false);
            binding.includeMessageLayout.cardSelectedMedia.isEnabled = false
            binding.includeMessageLayout.ibDeleteMedia.isEnabled = false
            binding.includeMessageLayout.ibVideoMedia.isEnabled = false
            if (!question!!.activityImageLargeUrl.equals("", ignoreCase = true)) {
                Glide.with(this)
                    .asBitmap()
                    .load(question!!.activityImageLargeUrl)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            bitmap = resource
                            binding.includeMessageLayout.ivSelectedMedia.setImageBitmap(resource)
                            binding.includeMessageLayout.btnDialogUpImage.visibility =
                                View.INVISIBLE
                            binding.includeMessageLayout.cardSelectedMedia.visibility =
                                View.VISIBLE
                            binding.includeMessageLayout.ibDeleteMedia.visibility = View.VISIBLE
                            binding.includeMessageLayout.ibVideoMedia.visibility = View.INVISIBLE

//                                binding.includeMessageLayout.btnDialogUpImage.setEnabled(false);
                            binding.includeMessageLayout.cardSelectedMedia.isEnabled = false
                            binding.includeMessageLayout.ibDeleteMedia.isEnabled = false
                            binding.includeMessageLayout.ibVideoMedia.isEnabled = false
                        }
                    })
            }
            binding.includeMessageLayout.btnDialogCancel.setOnClickListener(ViewClickListener { view1: View? ->
                pickerViewModel!!.setIntentMutableLiveData(null)
                navController.popBackStack()
            })
            binding.includeMessageLayout.tvDialogMessage.filters =
                arrayOf<InputFilter>(LengthFilter(1000))
            binding.includeMessageLayout.tvDialogMessage.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

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
            binding.includeMessageLayout.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? -> editQuestion() })

//            binding.includeMessageLayout.btnDialogUpImage.setOnClickListener(new ViewClickListener(v -> navController.navigate(MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(false))));
            binding.includeMessageLayout.btnDialogUpImage.setOnClickListener(ViewClickListener { v: View? ->
                showAlert(
                    "You can only edit the question text"
                )
            })
            val mediaClick = ViewClickListener { v: View? ->
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
                            binding.includeMessageLayout.btnDialogUpImage.visibility =
                                View.VISIBLE
                            binding.includeMessageLayout.cardSelectedMedia.visibility =
                                View.INVISIBLE
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
    }

    private fun editQuestion() {
        if (TextUtils.isEmpty(
                binding.includeMessageLayout.tvDialogMessage.text.toString().trim { it <= ' ' })
        ) {
            showAlert("Please enter text")
            return
        }
        var bitmap: Bitmap? = null
        if (!TextUtils.isEmpty(uriImagePath)) {
            bitmap = this.bitmap
        } /*else if (!TextUtils.isEmpty(uriVideoPath)) {
                bitmap = ThumbnailUtils.createVideoThumbnail(uriVideoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            }*/
        var text: String? = binding.includeMessageLayout.tvDialogMessage.text.toString()
        text = Base64Utility.encodeToString(text)
        viewModel.editQuestion(question!!.activityId, text)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<EditQuestionResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        pickerViewModel!!.setIntentMutableLiveData(null)
                        if (!apiResponse.body.message.equals(
                                "",
                                ignoreCase = true
                            )
                        ) {
                            showAlert(apiResponse.body.message)
                        } else {
                            question!!.activityText =
                                binding.includeMessageLayout.tvDialogMessage.text.toString()
                            questionViewModel!!.setRefreshEditedItem(question!!)
                            navController.popBackStack()
                        }
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
                            AppUtilities.showLog("Network Error", "editQuestion")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "editQuestion",
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (result != null) {
//                uriImagePath = result.uri.toString()
//                Glide.with(requireActivity()).asBitmap().load(uriImagePath)
//                    .addListener(object : RequestListener<Bitmap?> {
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any,
//                            target: Target<Bitmap?>,
//                            isFirstResource: Boolean
//                        ): Boolean {
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
//                            requireActivity().runOnUiThread {
//                                bitmap = resource
//                                binding.includeMessageLayout.ivSelectedMedia.setImageBitmap(
//                                    resource
//                                )
//                                binding.includeMessageLayout.btnDialogUpImage.visibility =
//                                    View.INVISIBLE
//                                binding.includeMessageLayout.cardSelectedMedia.visibility =
//                                    View.VISIBLE
//                                binding.includeMessageLayout.ibDeleteMedia.visibility =
//                                    View.VISIBLE
//                                binding.includeMessageLayout.ibVideoMedia.visibility =
//                                    View.INVISIBLE
//                                binding.includeMessageLayout.btnDialogUpImage.isEnabled = false
//                                binding.includeMessageLayout.cardSelectedMedia.isEnabled = false
//                                binding.includeMessageLayout.ibDeleteMedia.isEnabled = false
//                                binding.includeMessageLayout.ibVideoMedia.isEnabled = false
//                            }
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
                                binding.includeMessageLayout.btnDialogUpImage.isEnabled = false
                                binding.includeMessageLayout.cardSelectedMedia.isEnabled = false
                                binding.includeMessageLayout.ibDeleteMedia.isEnabled = false
                                binding.includeMessageLayout.ibVideoMedia.isEnabled = false
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