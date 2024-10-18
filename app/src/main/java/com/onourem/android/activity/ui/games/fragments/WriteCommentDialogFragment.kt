package com.onourem.android.activity.ui.games.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
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
import android.util.Pair
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.onourem.android.activity.arch.fragment.AbstractBaseBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogAnswerQuestionBinding
import com.onourem.android.activity.models.UploadPostRequest
import com.onourem.android.activity.models.UploadPostResponse
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
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
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class WriteCommentDialogFragment :
    AbstractBaseBindingDialogFragment<CommentsViewModel, DialogAnswerQuestionBinding>() {
    private val desPath = ""

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
    private var gameId: String? = null
    private var participantId: String? = null
    private var activityGameResId: String? = null
    private var postId: String? = null
    private var playGroupId: String? = null
    private var itemClickListener: OnItemClickListener<Void?>? = null
    private val fileName: String? = null
    private var fileSrc: String? = null
    override fun viewModelType(): Class<CommentsViewModel> {
        return CommentsViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            MediaPickerViewModel::class.java
        )
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_answer_question
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
        pickerViewModel!!.intentMutableLiveData!!.observe(viewLifecycleOwner) { intentIntegerPair: Pair<Intent, Int?>? ->
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
                                                binding.ivSelectedMedia.setImageBitmap(resource)
                                                binding.btnDialogUpImage.visibility =
                                                    View.INVISIBLE
                                                binding.cardSelectedMedia.visibility =
                                                    View.VISIBLE
                                                binding.ibDeleteMedia.visibility = View.VISIBLE
                                                binding.ibVideoMedia.visibility = View.INVISIBLE
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
                        fileSrc = getMediaPath(requireActivity(), uri444)
                        //                        Uri videoURI = FileProvider.getUriForFile(requireActivity(),
//                                requireActivity().getApplicationContext().getPackageName() + ".GenericFileProvider", video);
                        if (Common.checkMediaDurationValid(
                                fragmentContext, fileSrc, preferenceHelper!!.getString(
                                    Constants.KEY_VIDEO_DURATION
                                )
                            )
                        ) {
                            bitmap = Common.retrieveVideoFrameFromVideo(fileSrc)
                            binding.btnDialogUpImage.visibility = View.INVISIBLE
                            binding.cardSelectedMedia.visibility = View.VISIBLE
                            binding.ibDeleteMedia.visibility = View.VISIBLE
                            binding.ibVideoMedia.visibility = View.VISIBLE
                            Glide.with(requireActivity())
                                .load(bitmap)
                                .apply(options!!)
                                .into(binding.ivSelectedMedia)

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
        binding.btnDialogOk.setOnClickListener(ViewClickListener { v: View? ->
            if (TextUtils.isEmpty(
                    binding.tvDialogMessage.text.toString()
                        .trim { it <= ' ' }) && TextUtils.isEmpty(uriImagePath)
            ) {
                showAlert("Please enter text or attach image")
                return@ViewClickListener
            }
            val post = UploadPostRequest()
            post.screenId = "18"
            post.deviceId = uniqueDeviceId?:""
            post.autoDeleteOnOff = "N"
            post.text = Objects.requireNonNull(
                Base64Utility.encodeToString(
                    binding.tvDialogMessage.text.toString()
                )
            )
            post.pushToDiscover = "N"
            val action = "WriteComment"
            post.gameId = gameId?:""
            post.participantId = participantId?:""
            if (!TextUtils.isEmpty(postId)) {
                post.postId = postId?:""
            }
            if (!TextUtils.isEmpty(activityGameResId)) {
                post.activityGameResId = activityGameResId?:""
            }
            if (!TextUtils.isEmpty(playGroupId)) {
                post.playgroupId = playGroupId?:""
            }
            post.userAction = action
            var bitmap: Bitmap? = null
            if (!TextUtils.isEmpty(uriImagePath)) {
                bitmap = this.bitmap
            } else if (!TextUtils.isEmpty(uriVideoPath)) {
                bitmap = this.bitmap
            }
            post.image = AppUtilities.getBase64String(bitmap, 500)
            post.smallPostImage = AppUtilities.getBase64String(bitmap, 100)
            post.videoData = "dummyField"
//            val pDialog = ProgressDialog(requireActivity())
//            pDialog.setMessage("Uploading...")
//            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
//            pDialog.max = 100
//            pDialog.progress = 0
//            pDialog.setCancelable(false)
//            pDialog.setIcon(R.drawable.ic_logo)
//            pDialog.isIndeterminate = false
            val onouremProgressDialog = OnouremProgressDialog(fragmentContext)
            val progressCallback = object : ProgressCallback {
                override fun onProgressUpdate(percentage: Int) {
                    onouremProgressDialog.setProgress(percentage)
                }
            }
            viewModel.uploadPost(post, uriImagePath, uriVideoPath, progressCallback).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<UploadPostResponse> ->
                if (apiResponse.loading) {
                    if (!uriVideoPath.equals("", ignoreCase = true)) {
                        onouremProgressDialog.showDialogWithText("Uploading Video", true)
                    } else {
                        showProgress()
                    }
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    onouremProgressDialog.hideDialog()
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        pickerViewModel!!.setIntentMutableLiveData(null)
                        dismiss()
                        if (itemClickListener != null) {
                            itemClickListener!!.onItemClick(null)
                        }
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    onouremProgressDialog.hideDialog()
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "uploadPost")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "uploadPost",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
        })
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
                OnItemClickListener { item1: androidx.core.util.Pair<Int?, Any?> ->
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
//                                binding.ivSelectedMedia.setImageBitmap(resource)
//                                binding.btnDialogUpImage.visibility = View.INVISIBLE
//                                binding.cardSelectedMedia.visibility = View.VISIBLE
//                                binding.ibDeleteMedia.visibility = View.VISIBLE
//                                binding.ibVideoMedia.visibility = View.INVISIBLE
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
    companion object {
        fun getInstance(
            gameId: String?,
            participantId: String?,
            activityGameResId: String?,
            postId: String?,
            playGroupId: String?, itemClickListener: OnItemClickListener<Void?>?
        ): WriteCommentDialogFragment {
            val fragment = WriteCommentDialogFragment()
            fragment.gameId = gameId
            fragment.participantId = participantId
            fragment.activityGameResId = activityGameResId
            fragment.postId = postId
            fragment.playGroupId = playGroupId
            fragment.itemClickListener = itemClickListener
            return fragment
        }
    }
}