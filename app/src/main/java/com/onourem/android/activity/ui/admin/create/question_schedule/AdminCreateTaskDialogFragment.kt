package com.onourem.android.activity.ui.admin.create.question_schedule

import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogAdminCreateOwnQuestionBinding
import com.onourem.android.activity.databinding.DialogAdminCreateTaskBinding
import com.onourem.android.activity.models.CreateQuestionForOneToManyResponse
import com.onourem.android.activity.models.FutureQuestionDetails
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog.showAlert
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
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import java.util.*
import javax.inject.Inject

class AdminCreateTaskDialogFragment : AbstractBaseDialogBindingFragment<AdminViewModel, DialogAdminCreateTaskBinding>() {
    private val options = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.shape_image_video_placeholder)
        .error(R.drawable.shape_image_video_placeholder)

    @JvmField
    @Inject
    var sharedPreferenceHelper: SharedPreferenceHelper? = null
    private var pickerViewModel: MediaPickerViewModel? = null
    private var uriImagePath = ""
    private var uriVideoPath = ""
    private var bitmap: Bitmap? = null
    private var fileSrc: String? = null
    private var futureQuestionDetails: FutureQuestionDetails? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(MediaPickerViewModel::class.java)
    }

    private fun observeOnActivityResult() {
        pickerViewModel!!.getIntentMutableLiveData()!!.observe(viewLifecycleOwner) { intentIntegerPair ->
            if (intentIntegerPair == null) return@observe
            when (intentIntegerPair.second) {
                111, 222 -> {
                    val data111 = intentIntegerPair.first
                    val uri = data111.data
                    if (uri != null) {
                        uriImagePath = uri.toString()
                    }
                    Glide.with(requireActivity()).asBitmap().load(uri).addListener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
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
                                binding.includeMessageLayout.ivSelectedMedia.setImageBitmap(resource)
                                binding.includeMessageLayout.btnDialogUpImage.visibility = View.INVISIBLE
                                binding.includeMessageLayout.cardSelectedMedia.visibility = View.VISIBLE
                                binding.includeMessageLayout.ibDeleteMedia.visibility = View.VISIBLE
                                binding.includeMessageLayout.ibVideoMedia.visibility = View.INVISIBLE
                            }
                            return true
                        }
                    }).submit()
                }
                333, 444 -> {
                    val data444 = intentIntegerPair.first
                    val uri444 = data444.data
                    if (uri444 != null) {

//                        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//                        fileName = storageDir.getPath() + "/Onourem/videos" + "/temp.mp4";
//                        File video = new File(fileName);
//                        uriVideoPath = video.getPath();


//                        Uri videoURI = FileProvider.getUriForFile(requireActivity(),
//                                requireActivity().getApplicationContext().getPackageName() + ".GenericFileProvider", video);
                        fileSrc = getMediaPath(requireActivity(), uri444)
                        if (Utilities.checkVideoDuration444(requireActivity(), fileSrc, sharedPreferenceHelper!!.getString(Constants.KEY_VIDEO_DURATION))) {
                            bitmap = ThumbnailUtils.createVideoThumbnail(uriVideoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
                            binding.includeMessageLayout.btnDialogUpImage.visibility = View.INVISIBLE
                            binding.includeMessageLayout.cardSelectedMedia.visibility = View.VISIBLE
                            binding.includeMessageLayout.ibDeleteMedia.visibility = View.VISIBLE
                            binding.includeMessageLayout.ibVideoMedia.visibility = View.VISIBLE
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
                                val outputPath = getFilePath(fragmentContext, Common.VIDEO, Common.OPERATION_COMPRESS_VIDEO, getRandomName(6))
                                val fFmpegQueryExtension = FFmpegQueryExtension()
                                val query = fFmpegQueryExtension.compressor(fileSrc!!, width, height, outputPath)
                                val callBackOfQuery = CallBackOfQuery()
                                callBackOfQuery.callQuery((requireActivity() as AppCompatActivity), query, object : FFmpegCallBack {
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
                        }
                        else {
                            uriVideoPath = ""
                            showAlert(Utilities.getMaxVideoDurationText(sharedPreferenceHelper!!.getString(Constants.KEY_VIDEO_DURATION)))
                        }
                        return@observe
                    }
                }
            }
        }
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_admin_create_task
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = AdminCreateOwnQuestionDialogFragmentArgs.fromBundle(requireArguments())
        futureQuestionDetails = args.futureQuestionDetails
        if (futureQuestionDetails != null) {
            binding.tvDialogTitle.text = "Schedule Task"
            val chipDate = Chip(requireActivity())
            chipDate.text = futureQuestionDetails!!.triggerDateAndTime
            chipDate.setChipBackgroundColorResource(R.color.colorAccent)
            chipDate.setTextColor(ContextCompat.getColor(fragmentContext, R.color.white))
            chipDate.setTextAppearance(R.style.ChipTextAppearance)
            val chipQuestionTo = Chip(requireActivity())
            chipQuestionTo.text = futureQuestionDetails!!.questionTo
            chipQuestionTo.setChipBackgroundColorResource(R.color.colorAccent)
            chipQuestionTo.setTextColor(ContextCompat.getColor(fragmentContext, R.color.white))
            chipQuestionTo.setTextAppearance(R.style.ChipTextAppearance)
            //binding.chipGroup.addView(chipDate)
            binding.chipGroup.addView(chipQuestionTo)

            if (futureQuestionDetails!!.taskCategory !=null){
                val chipCategory = Chip(requireActivity())
                chipCategory.text = futureQuestionDetails!!.taskCategory.catCode
                chipCategory.setChipBackgroundColorResource(R.color.colorAccent)
                chipCategory.setTextColor(ContextCompat.getColor(fragmentContext, R.color.white))
                chipCategory.setTextAppearance(R.style.ChipTextAppearance)
                binding.chipGroup.addView(chipCategory)
            }
        }

        binding.includeMessageLayout.btnDialogCancel.setOnClickListener(ViewClickListener { view1: View? ->
            pickerViewModel!!.setIntentMutableLiveData(null)
            navController.popBackStack()
        })
        binding.includeMessageLayout.tvDialogMessage.filters = arrayOf<InputFilter>(LengthFilter(1000))
        binding.includeMessageLayout.tvDialogMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= 800) {
                    binding.includeMessageLayout.tvRemainingText.text = String.format(Locale.getDefault(), "%d remaining", 1000 - s.length)
                }
                else {
                    binding.includeMessageLayout.tvRemainingText.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length >= 800) {
                    binding.includeMessageLayout.tvRemainingText.visibility = View.VISIBLE
                }
                else {
                    binding.includeMessageLayout.tvRemainingText.visibility = View.GONE
                }
            }
        })
        binding.includeMessageLayout.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? ->
            if (futureQuestionDetails != null) {
                //createSystemQuestion()
                Toast.makeText(fragmentContext, "Task Create Api Soon", Toast.LENGTH_SHORT).show()
            }
        })
        binding.includeMessageLayout.btnDialogUpImage.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(
                    true
                )
            )
        })
        val mediaClick = ViewClickListener { v: View? ->
            if (!uriImagePath.equals("", ignoreCase = true)) {
                navController.navigate(MobileNavigationDirections.actionGlobalNavMediaView(1, uriImagePath))
            }
            else if (!uriVideoPath.equals("", ignoreCase = true)) {
                navController.navigate(MobileNavigationDirections.actionGlobalNavMediaView(2, uriVideoPath))
            }
        }
        binding.includeMessageLayout.ivSelectedMedia.setOnClickListener(mediaClick)
        binding.includeMessageLayout.ibVideoMedia.setOnClickListener(mediaClick)
        binding.includeMessageLayout.ibDeleteMedia.setOnClickListener(ViewClickListener { v: View? ->
            showAlert(
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
        binding.includeMessageLayout.txtUserIntimation.visibility = View.GONE
        observeOnActivityResult()
    }

    private fun createSystemQuestion() {
        if (TextUtils.isEmpty(binding.includeMessageLayout.tvDialogMessage.text.toString().trim { it <= ' ' })) {
            showAlert("Please enter text")
            return
        }
        var bitmap: Bitmap? = null
        if (!TextUtils.isEmpty(uriImagePath)) {
            bitmap = this.bitmap
        }
        else if (!TextUtils.isEmpty(uriVideoPath)) {
            bitmap = ThumbnailUtils.createVideoThumbnail(uriVideoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
        }
        var text: String? = binding.includeMessageLayout.tvDialogMessage.text.toString()
        text = Base64Utility.encodeToString(text)
        val onouremProgressDialog = OnouremProgressDialog(fragmentContext)

        val progressCallback = object : ProgressCallback {
            override fun onProgressUpdate(percentage: Int) {
                onouremProgressDialog.setProgress(
                    percentage
                )
            }
        }
        viewModel.createSystemQuestion(text, bitmap, uriVideoPath, futureQuestionDetails, progressCallback).observe(viewLifecycleOwner) { apiResponse ->
            if (apiResponse.loading) {
                showProgress()
            }
            else if (apiResponse.body != null && apiResponse.isSuccess) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000")) {
                    pickerViewModel!!.setIntentMutableLiveData(null)
                    navController.popBackStack(R.id.nav_schedule_future_questions, false)
                    showAlert(apiResponse.body.errorMessage)
                }
                else {
                    showAlert(apiResponse.body.errorMessage)
                }
            }
            else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "createQuestion")
                    }
                    if (fragmentContext != null) (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "createQuestion",
                        java.lang.String.valueOf(apiResponse.code)
                    )
                }
            }
        }
    }


    override fun isCancelable(): Boolean {
        return false
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pickerViewModel = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
//        viewModel.setRefreshShowBadges(true)
    }
}