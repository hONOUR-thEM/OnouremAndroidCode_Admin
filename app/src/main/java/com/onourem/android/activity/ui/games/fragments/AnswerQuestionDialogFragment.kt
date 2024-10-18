package com.onourem.android.activity.ui.games.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.*
import android.text.method.ScrollingMovementMethod
import android.text.style.RelativeSizeSpan
import android.util.Pair
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
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
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.viewmodel.*
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
import com.onourem.android.activity.ui.utils.media.Statistics
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class AnswerQuestionDialogFragment :
    AbstractBaseBindingDialogFragment<AnswerQuestionViewModel, DialogAnswerQuestionBinding>() {
    private val options = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)
    private val desPath = ""

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null
    private var uriImagePath = ""
    private var uriVideoPath = ""
    private val videoUri: Uri? = null
    private var bitmap: Bitmap? = null
    private var pickerViewModel: MediaPickerViewModel? = null
    private var privacyViewModel: SelectPrivacyViewModel? = null
    private var loginDayActivityInfoList: LoginDayActivityInfoList? = null
    private var playGroup: PlayGroup? = null
    private var itemClickListener: OnItemClickListener<UploadPostResponse?>? = null
    private val fileName: String? = null
    private var fileSrc: String? = null
    private var oneToManyViewModel: OneToManyViewModel? = null
    private val mProgressDialog: ProgressDialog? = null
    private var questionGamesViewModel: QuestionGamesViewModel? = null
    override fun viewModelType(): Class<AnswerQuestionViewModel> {
        return AnswerQuestionViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            MediaPickerViewModel::class.java
        )
        privacyViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            SelectPrivacyViewModel::class.java
        )
        oneToManyViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            OneToManyViewModel::class.java
        )
        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            QuestionGamesViewModel::class.java
        )
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_answer_question
    }

    private fun setPrivacy(name: String?) {
        var name = name
        if (name.equals("Receivers Only", ignoreCase = true)) {
            name = "Private"
        }
        if (!(playGroup!!.playGroupId.equals("AAA", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("0", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("1", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("2", ignoreCase = true))
        ) {
            name = "O-Club"
            binding.button1.tvTitle.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.gray_color
                )
            )
        }
        else {
            binding.button1.tvTitle.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.color_black
                )
            )
        }
        if (playGroup!!.playGroupId.equals("AAA", ignoreCase = true)
            || playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
            || playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)
            || playGroup!!.playGroupId.equals("0", ignoreCase = true)
            || playGroup!!.playGroupId.equals("1", ignoreCase = true)
            || (playGroup!!.playGroupId.equals("2", ignoreCase = true)
                    && ActivityType.getActivityType(loginDayActivityInfoList!!.activityType) == ActivityType.ONE_TO_MANY)
        ) {
            binding.button1.tvTitle.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.color_black
                )
            )
        }
        else {
            binding.button1.tvTitle.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.gray_color
                )
            )
        }
        binding.button1.tvTitle.text = name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeOnActivityResult()
        //if (getFragmentContext() != null)
        //  ((DashboardActivity) getFragmentContext()).addNetworkErrorUserInfo("createGameActivity", String.valueOf(500));
        if (loginDayActivityInfoList != null) {
            setupView()
        }
    }

    private fun observeOnActivityResult() {
        pickerViewModel!!.getIntentMutableLiveData()!!.observe(
            viewLifecycleOwner
        ) { intentIntegerPair: Pair<Intent, Int?>? ->
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
                            }
                            else {
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

//                        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
//                        fileName = storageDir.getPath() + "/Onourem/videos" + "/temp.mp4";
//                        File video = new File(fileName);
//                        uriVideoPath = video.getPath();
//
//                        Uri videoURI = FileProvider.getUriForFile(requireActivity(),
//                                requireActivity().getApplicationContext().getPackageName() + ".GenericFileProvider", video);
                        fileSrc = getMediaPath(requireActivity(), uri444)
                        if (Utilities.checkVideoDuration444(
                                requireActivity(), fileSrc, preferenceHelper!!.getString(
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
                                .apply(options)
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
                        }
                        else {
                            uriVideoPath = ""
                            showAlert(
                                Utilities.getMaxVideoDurationText(
                                    preferenceHelper!!.getString(Constants.KEY_VIDEO_DURATION)
                                )
                            )
                        }
                    }
                }
            }
        }

    }

    private fun setupView() {
        binding.tvDialogMessage.hint = "Write your answer here..."
        if (loginDayActivityInfoList!!.activityText != null) {
            binding.tvDialogTitle.text = loginDayActivityInfoList!!.activityText
            binding.tvDialogTitle.movementMethod = ScrollingMovementMethod()
            TextViewCompat.setAutoSizeTextTypeWithDefaults(
                binding.tvDialogTitle,
                TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
            )
        }
        setPrivacyBubbleCount(
            viewModel!!.getPointsForGroup(loginDayActivityInfoList!!.privacyId!!),
            R.color.color_blue
        )
        setPrivacy(loginDayActivityInfoList!!.privacyName)
        binding.button1.checkIcon.isChecked = true
        binding.button1.tvHint.text = "Privacy"
        var drawable = AppCompatResources.getDrawable(fragmentContext, R.drawable.ic_privacy_icon)!!
        DrawableCompat.setTint(
            drawable,
            ContextCompat.getColor(fragmentContext, R.color.colorAccent)
        )
        binding.button1.checkIcon.setBackgroundDrawable(drawable)
        binding.button1.container.setOnClickListener(ViewClickListener { v: View? ->
            if ((playGroup!!.playGroupId.equals("AAA", ignoreCase = true)
                        || playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
                        || playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true))
                && ActivityType.getActivityType(loginDayActivityInfoList!!.activityType) == ActivityType.ONE_TO_MANY
            ) {
                setPrivacyBubbleCount("0", R.color.color_transparent)
                val bundle = Bundle()
                bundle.putSerializable("groupPointsMap", viewModel!!.groupPointsMap)
                bundle.putBoolean("showPrivateOption", false)
                navController.navigate(R.id.action_global_nav_select_privacy, bundle)
            }
            else {
                val message: String
                message =
                    if (ActivityType.getActivityType(loginDayActivityInfoList!!.activityType) == ActivityType.ONE_TO_ONE || ActivityType.getActivityType(
                            loginDayActivityInfoList!!.activityType
                        ) == ActivityType.D_TO_ONE
                    ) {
                        "You can't change privacy for this message."
                    }
                    else {
                        "You are answering this question in an O-Club. Club members can see your response. You can't change privacy."
                    }
                showAlert(message)
            }
        })


//        binding.button2.tvTitle.setText("Public");
//        binding.button2.tvHint.setText("Privacy");
//        binding.button2.getRoot().setOnClickListener(v -> {
//            binding.button2.checkIcon.setChecked(!binding.button2.checkIcon.isChecked());
//
//        });
        binding.button2.root.visibility = View.GONE
        binding.button3.tvTitle.text = getString(R.string.label_off)
        binding.button3.tvHint.text = getString(R.string.label_auto_delete)
        drawable = AppCompatResources.getDrawable(fragmentContext, R.drawable.ic_custom_delete)!!
        DrawableCompat.setTint(
            drawable,
            ContextCompat.getColor(fragmentContext, R.color.colorAccent)
        )
        binding.button3.checkIcon.setBackgroundDrawable(drawable)
        binding.button3.container.setOnClickListener(ViewClickListener { v: View? ->
            if (binding.button3.checkIcon.isChecked) {
                binding.button3.checkIcon.isChecked = !binding.button3.checkIcon.isChecked
                binding.button3.tvTitle.text = getString(R.string.label_off)
                binding.button3.tvTitle.setTextColor(
                    ContextCompat.getColor(
                        fragmentContext,
                        R.color.color_black
                    )
                )
            }
            else {
                binding.button3.checkIcon.isChecked = !binding.button3.checkIcon.isChecked
                showAlert(
                    "Turn this ON if you want your response to get automatically deleted after 15 days.",
                    { v1: View? ->
                        binding.button3.checkIcon.isChecked = true
                        binding.button3.tvTitle.text = getString(R.string.label_on)
                        binding.button3.tvTitle.setTextColor(
                            ContextCompat.getColor(
                                fragmentContext,
                                R.color.color_green
                            )
                        )
                    },
                    getString(R.string.label_continue)
                )
            }
        })
        binding.tvDialogMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= 2800) {
                    binding.tvRemainingText.text =
                        String.format(Locale.getDefault(), "%d remaining", 3000 - s.length)
                }
                else {
                    binding.tvRemainingText.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length >= 2800) {
                    binding.tvRemainingText.visibility = View.VISIBLE
                }
                else {
                    binding.tvRemainingText.visibility = View.GONE
                }
            }
        })
        binding.btnDialogOk.setOnClickListener(ViewClickListener(View.OnClickListener { v: View? ->
            if (TextUtils.isEmpty(
                    binding.tvDialogMessage.text.toString()
                        .trim { it <= ' ' }) && TextUtils.isEmpty(uriImagePath)
            ) {
                showAlert("Please enter text or attach image")
                return@OnClickListener
            }
            val post = UploadPostRequest()
            post.screenId = "18"
            post.activityText =
                Base64Utility.encodeToString(loginDayActivityInfoList!!.activityText)
            post.playgroupId = loginDayActivityInfoList!!.playgroupId ?: ""
            post.oClubActivityId = loginDayActivityInfoList!!.oClubActivityId ?: ""
            post.gameId = loginDayActivityInfoList!!.gameId ?: ""
            post.deviceId = uniqueDeviceId ?: ""
            post.loginDay = loginDayActivityInfoList!!.loginDay ?: ""
            post.autoDeleteOnOff = if (binding.button3.checkIcon.isChecked) "Y" else "N"
            post.activityId = loginDayActivityInfoList!!.activityId ?: ""
            post.activityTypeId = loginDayActivityInfoList!!.activityTypeId ?: ""
            post.text = Objects.requireNonNull(
                Base64Utility.encodeToString(
                    binding.tvDialogMessage.text.toString()
                )
            )
            post.pushToDiscover = "N"
            val privacies = privacyViewModel!!.selectedPrivacyMutableLiveData!!.value
            if (privacies != null) {
                post.visibleToList = Utilities.getTokenSeparatedString(privacies, ",")
            }
            else {
                //default is public
                post.visibleToList = "1"
            }
            var action = ""
            when (loginDayActivityInfoList!!.activityType) {
                "1to1" -> action = "GameResponseFor1to1"
                "Dto1" -> action = "GameResponseForDto1"
                "1toM" -> {
                    action = "GameResponseFor1toM"
                    if (loginDayActivityInfoList!!.userParticipationStatus.equals(
                            "None",
                            ignoreCase = true
                        )
                    ) {
                        action = "CreateGameFor1toM"
                    }
                    else if (loginDayActivityInfoList!!.userParticipationStatus.equals(
                            "Pending",
                            ignoreCase = true
                        )
                    ) {
                        post.activityGameResId = loginDayActivityInfoList!!.activityGameResponseId ?: ""
                        action = "GameResponseFor1toM"
                    }
                }
            }
            post.activityGameResId = loginDayActivityInfoList!!.activityGameResponseId ?: ""
            post.userAction = action
            var bitmap: Bitmap? = null
            if (!TextUtils.isEmpty(uriImagePath)) {
                bitmap = this.bitmap
            }
            else if (!TextUtils.isEmpty(uriVideoPath)) {
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
            viewModel!!.uploadPost(post, uriImagePath, uriVideoPath, progressCallback).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<UploadPostResponse> ->
                if (apiResponse.loading) {
                    if (!uriVideoPath.equals("", ignoreCase = true)) {
                        onouremProgressDialog.showDialogWithText("Uploading Video", true)
                    }
                    else {
                        showProgress()
                    }
                }
                else if (apiResponse.isSuccess && apiResponse.body != null) {
                    onouremProgressDialog.hideDialog()
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (loginDayActivityInfoList!!.activityType.equals(
                                "1toM",
                                ignoreCase = true
                            )
                        ) {
                            val todayAnswerCount =
                                preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_TODAY)
                            preferenceHelper!!.putValue(
                                Constants.KEY_TOTAL_QUES_ANSWERED_TODAY,
                                todayAnswerCount + 1
                            )
                            if (apiResponse.body.message != null && !apiResponse.body.message.equals(
                                    "",
                                    ignoreCase = true
                                )
                            ) {
                                showAlert(apiResponse.body.message) { v12: View? ->
                                    if (apiResponse.body.activityText.equals(
                                            "Deleted",
                                            ignoreCase = true
                                        )
                                    ) {
                                        questionGamesViewModel!!.setUpdateItem(
                                            loginDayActivityInfoList!!
                                        )
                                        dismiss()
                                    }
                                    else {
                                        if (apiResponse.body.activityText != null) {
                                            binding.tvDialogTitle.text =
                                                apiResponse.body.activityText
                                            loginDayActivityInfoList!!.activityText =
                                                apiResponse.body.activityText
                                            questionGamesViewModel!!.setRefreshEditedItem(
                                                loginDayActivityInfoList!!
                                            )
                                            oneToManyViewModel!!.setReloadRequired(apiResponse.body.activityText ?: "")
                                        }
                                    }
                                }
                            }
                            else {
                                pickerViewModel!!.setIntentMutableLiveData(null)
                                privacyViewModel!!.setSelected(null)
                                if (itemClickListener != null) {
                                    itemClickListener!!.onItemClick(apiResponse.body)
                                }
                                dismiss()
                            }
                        }
                        else {
                            pickerViewModel!!.setIntentMutableLiveData(null)
                            privacyViewModel!!.setSelected(null)
                            if (itemClickListener != null) {
                                itemClickListener!!.onItemClick(apiResponse.body)
                            }
                            dismiss()
                        }
                    }
                    else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                }
                else {
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
        }))
        binding.btnDialogCancel.setOnClickListener(ViewClickListener { v: View? ->
            pickerViewModel!!.setIntentMutableLiveData(null)
            privacyViewModel!!.setSelected(null)
            dismiss()
        })
        binding.btnDialogUpImage.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(true)
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
            }
            else if (!uriVideoPath.equals("", ignoreCase = true)) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavMediaView(
                        2,
                        uriVideoPath
                    )
                )
            }
        }
        binding.ivSelectedMedia.setOnClickListener(mediaClick)
        binding.ibVideoMedia.setOnClickListener(mediaClick)
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

                        //deleteFile(fileSrc);
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
        privacyViewModel!!.selectedPrivacyMutableLiveData!!.observe(viewLifecycleOwner) { privacyGroups: List<PrivacyGroup?>? ->
            if (privacyGroups == null) return@observe
            if (privacyGroups.size > 1) {
                setPrivacy("Custom")
            }
            else {
                val privacyGroup = privacyGroups[0]
                setPrivacy(privacyGroup!!.groupName)
                var tintColor = R.color.color_transparent
                val groupId = privacyGroup.groupId
                when (groupId) {
                    1 -> tintColor = R.color.color_blue
                    2 -> tintColor = R.color.color_pink
                    4 -> tintColor = R.color.color_green
                }
                setPrivacyBubbleCount(
                    viewModel!!.getPointsForGroup(privacyGroup.groupId.toString()),
                    tintColor
                )
            }
        }
    }

    private fun deleteFile(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun activityPlayGroupId(): String? {
        var activityPlayGroupId: String? = null
        val playGroupId = playGroup!!.playGroupId
        activityPlayGroupId = if (playGroupId.equals("AAA", ignoreCase = true)) {
            "0"
        }
        else if (playGroupId.equals("BBB", ignoreCase = true)) {
            playGroupId
        }
        else if (playGroupId.equals("YYY", ignoreCase = true)) {
            "1"
        }
        else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
            "2"
        }
        else {
            playGroupId
        }
        return activityPlayGroupId
    }

    @SuppressLint("RestrictedApi")
    private fun setPrivacyBubbleCount(points: String?, tintColor: Int) {
        if (playGroup!!.playGroupId.equals("AAA", ignoreCase = true)
            || playGroup!!.playGroupId.equals("FFF", ignoreCase = true)
            || playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
            || playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)
        ) {
            if (TextUtils.isEmpty(points) || points.equals("0", ignoreCase = true)) {
                binding.bubble.tvQuestionPoints.visibility = View.GONE
            }
            else {
                val builder = SpannableStringBuilder().append(points)
                    .append("\n")
                    .append("Points")
                builder.setSpan(
                    RelativeSizeSpan(1.2f),
                    0, points!!.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.setSpan(
                    RelativeSizeSpan(0.7f),
                    points.length, builder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.bubble.tvQuestionPoints.text = builder
                binding.bubble.tvQuestionPoints.visibility = View.VISIBLE
                binding.bubble.tvQuestionPoints.supportBackgroundTintList =
                    AppCompatResources.getColorStateList(requireActivity(), tintColor)
            }
        }
        else {
            binding.bubble.tvQuestionPoints.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pickerViewModel = null
        privacyViewModel = null
    }

    //@Deprecated("Deprecated in Java")
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
                            target: Target<Bitmap?>,
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
        @JvmStatic
        fun getInstance(
            loginDayActivityInfoList: LoginDayActivityInfoList?,
            playGroup: PlayGroup?,
            itemClickListener: OnItemClickListener<UploadPostResponse?>?
        ): AnswerQuestionDialogFragment {
            val answerQuestionDialogFragment = AnswerQuestionDialogFragment()
            answerQuestionDialogFragment.loginDayActivityInfoList = loginDayActivityInfoList
            answerQuestionDialogFragment.playGroup = playGroup
            answerQuestionDialogFragment.itemClickListener = itemClickListener
            return answerQuestionDialogFragment
        }

        fun delete(context: Context, file: File): Boolean {
            val where = MediaStore.MediaColumns.DATA + "=?"
            val selectionArgs = arrayOf(
                file.absolutePath
            )
            val contentResolver = context.contentResolver
            val filesUri = MediaStore.Files.getContentUri("external")
            contentResolver.delete(filesUri, where, selectionArgs)
            if (file.exists()) {
                contentResolver.delete(filesUri, where, selectionArgs)
            }
            return !file.exists()
        }
    }
}