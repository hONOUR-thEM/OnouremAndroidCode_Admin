package com.onourem.android.activity.ui.games.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.google.android.material.chip.Chip
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogComposeMessageBinding
import com.onourem.android.activity.databinding.ItemUserSearchBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.appreciation.viewmodel.AppreciationViewModel
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog.showAlert
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import com.onourem.android.activity.ui.games.viewmodel.SelectPrivacyViewModel
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
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class TaskAndMessageComposeDialogFragment :
    AbstractBaseBindingDialogFragment<AppreciationViewModel, DialogComposeMessageBinding>() {
    private val options: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)
    private val selectedUsers: ArrayList<UserList>? = ArrayList()
    private val desPath: String = ""

    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var uriImagePath: String? = ""
    private var uriVideoPath: String = ""
    private var bitmap: Bitmap? = null
    private var isOutsideOnourem: Boolean = false
    private var isOnOnourem: Boolean = false
    private var pickerViewModel: MediaPickerViewModel? = null
    private var searchFriendAdapter //    private List<UserList> blockedUserIds;
            : SearchFriendAdapter? = null
    private var privacyViewModel: SelectPrivacyViewModel? = null
    @SuppressLint("NotifyDataSetChanged")
    private val chipOnClickListener: View.OnClickListener = View.OnClickListener { v: View ->
        val userList: UserList = v.tag as UserList
        userList.isSelected = false
        selectedUsers!!.remove(userList)
        if (searchFriendAdapter != null) searchFriendAdapter!!.notifyDataSetChanged()
        configureChipLayout()
    }
    private var loginDayActivityInfoList: LoginDayActivityInfoList? = null
    private var onPostCreateOkActionListener: OnItemClickListener<Pair<LoginDayActivityInfoList?, UploadPostResponse?>>? =
        null
    private val fileName: String? = null
    private var fileSrc: String? = null
    private val mProgressDialog: ProgressDialog? = null
    override fun viewModelType(): Class<AppreciationViewModel> {
        return AppreciationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_compose_message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            MediaPickerViewModel::class.java
        )
        privacyViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            SelectPrivacyViewModel::class.java
        )
        //        viewModel.getLocalBlockUserList().observe(this, listResource -> {
//            if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null && !listResource.getData().isEmpty()) {
//                blockedUserIds = listResource.getData();
//            }
//        });
    }

    private fun setPrivacy(s: String) {
        var name: String = s
        if (name.equals("Receivers Only", ignoreCase = true)) {
            name = "Private"
        }
        if (!TextUtils.isEmpty(loginDayActivityInfoList?.playgroupId)) {
            name = "O-Club"
            binding.includeMessageLayout.button1.tvTitle.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.gray_color
                )
            )
        }
        else {
            binding.includeMessageLayout.button1.tvTitle.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.color_black
                )
            )
        }

        binding.includeMessageLayout.button1.tvTitle.text = name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        observeOnActivityResult()
    }

    private fun observeOnActivityResult() {
        pickerViewModel!!.getIntentMutableLiveData()!!
            .observe(viewLifecycleOwner) { intentIntegerPair ->
                if (intentIntegerPair == null) return@observe
                when (intentIntegerPair.second) {
                    111, 222 -> {
                        val data111: Intent = intentIntegerPair.first
                        val uri: Uri? = data111.data
                        if (uri != null) {
                            showAlert(
                                requireActivity(),
                                getString(R.string.label_confirm),
                                "Would you like to crop the Image?",
                                "",
                                "No",
                                "Yes",
                                OnItemClickListener { item1: Pair<Int?, String?>? ->
                                    if ((item1 != null) && (item1.first != null) && (item1.first == TwoActionAlertDialog.ACTION_RIGHT)) {
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
                                                    requireActivity().runOnUiThread(Runnable {
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
                                                    })
                                                    return true
                                                }
                                            }).submit()
                                    }
                                }
                            )
                        }
                    }
                    333, 444 -> {
                        val data444: Intent = intentIntegerPair.first
                        val uri444: Uri? = data444.data
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
                                binding.includeMessageLayout.btnDialogUpImage.visibility =
                                    View.INVISIBLE
                                binding.includeMessageLayout.cardSelectedMedia.visibility =
                                    View.VISIBLE
                                binding.includeMessageLayout.ibDeleteMedia.visibility =
                                    View.VISIBLE
                                binding.includeMessageLayout.ibVideoMedia.visibility =
                                    View.VISIBLE
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
                                    val retriever: MediaMetadataRetriever = MediaMetadataRetriever()
                                    retriever.setDataSource(fileSrc)
                                    val width: Int =
                                        retriever.frameAtTime!!.width.toString().toInt()
                                    val height: Int =
                                        retriever.frameAtTime!!.height.toString().toInt()
                                    retriever.release()
                                    val outputPath: String = getFilePath(
                                        fragmentContext,
                                        Common.VIDEO,
                                        Common.OPERATION_COMPRESS_VIDEO,
                                        getRandomName(6)
                                    )
                                    val fFmpegQueryExtension: FFmpegQueryExtension =
                                        FFmpegQueryExtension()
                                    val query: Array<String> = fFmpegQueryExtension.compressor(
                                        fileSrc!!, width, height, outputPath
                                    )
                                    val callBackOfQuery: CallBackOfQuery = CallBackOfQuery()
                                    callBackOfQuery.callQuery(
                                        (requireActivity() as AppCompatActivity?)!!,
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
        binding.tvDialogTitle.text = getString(R.string.title_compose_message)
        binding.includeTheme.tvTheme.text = String.format(
            "Category: %s",
            loginDayActivityInfoList?.postCategory
        )
        if (loginDayActivityInfoList?.receiverRequired != null && loginDayActivityInfoList?.receiverRequired.equals(
                "N",
                ignoreCase = true
            )
        ) {
            binding.includeSearchButtonLayout.root.visibility = View.GONE
            binding.includeSearchLayout.root.visibility = View.GONE
            binding.includeMessageLayout.button3.root.visibility = View.INVISIBLE
            binding.includeMessageLayout.root.visibility = View.VISIBLE
            binding.includeChipLayout.root.visibility = View.GONE
            binding.tvSearchLabel.visibility = View.GONE
            binding.btnAddMoreFriends.visibility = View.GONE
        } else {
            binding.includeSearchLayout.root.visibility = View.GONE
            if (selectedUsers != null && selectedUsers.isNotEmpty()) {
                binding.includeSearchButtonLayout.root.visibility = View.GONE
                binding.btnAddMoreFriends.visibility = View.VISIBLE
                binding.includeChipLayout.root.visibility = View.VISIBLE
            } else {
                binding.includeSearchButtonLayout.root.visibility = View.VISIBLE
                binding.includeChipLayout.root.visibility = View.GONE
                binding.btnAddMoreFriends.visibility = View.GONE
            }
            isOnOnourem = false
            binding.includeMessageLayout.button3.root.visibility = View.VISIBLE
            binding.includeMessageLayout.root.visibility = View.VISIBLE
            binding.tvSearchLabel.visibility = View.VISIBLE
        }
        binding.includeMessageLayout.button2.root.visibility = View.GONE
//        var imageUrl: String? = null
//        if (!TextUtils.isEmpty(loginDayActivityInfoList?.activityImageLargeUrl)) {
//            imageUrl = loginDayActivityInfoList?.activityImageLargeUrl
//        }
//        if ((imageUrl != null) && imageUrl.isNotEmpty() && (loginDayActivityInfoList?.receiverRequired != null) && loginDayActivityInfoList?.receiverRequired.equals(
//                "Y",
//                ignoreCase = true
//            )
//        ) {
//            Glide.with(requireActivity())
//                .asBitmap()
//                .load(imageUrl)
//                .apply(options)
//                .addListener(object : RequestListener<Bitmap?> {
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any,
//                        target: Target<Bitmap?>,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Bitmap?,
//                        model: Any,
//                        target: Target<Bitmap?>,
//                        dataSource: DataSource,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        requireActivity().runOnUiThread {
//                            bitmap = resource
//                            uriImagePath = loginDayActivityInfoList?.activityImageLargeUrl
//                            binding.includeMessageLayout.ivSelectedMedia.setImageBitmap(bitmap)
//                            binding.includeMessageLayout.btnDialogUpImage.visibility =
//                                View.INVISIBLE
//                            binding.includeMessageLayout.cardSelectedMedia.visibility =
//                                View.VISIBLE
//                            binding.includeMessageLayout.ibDeleteMedia.visibility = View.VISIBLE
//                            binding.includeMessageLayout.ibVideoMedia.visibility = View.INVISIBLE
//                        }
//                        return true
//                    }
//                })
//                .submit()
//        }
        binding.includeTheme.tvTheme.setOnClickListener(ViewClickListener { v: View? ->
            showAlert(
                "You can't change Message Category for this message."
            )
        })
        setPrivacy("Public")
        binding.includeMessageLayout.button1.checkIcon.isChecked = true
        binding.includeMessageLayout.button1.tvHint.text = "Privacy"
        var drawable: Drawable? =
            AppCompatResources.getDrawable(fragmentContext, R.drawable.ic_privacy_icon)
        assert(drawable != null)
        DrawableCompat.setTint(
            (drawable)!!,
            ContextCompat.getColor(fragmentContext, R.color.colorAccent)
        )
        binding.includeMessageLayout.button1.checkIcon.setBackgroundDrawable(drawable)
        binding.includeMessageLayout.button1.container.setOnClickListener(
            ViewClickListener { v: View? ->

                if (!TextUtils.isEmpty(loginDayActivityInfoList?.playgroupId)){
                    if (loginDayActivityInfoList?.activityType == "Task"){
                        val message: String = "You are sharing this task in O-Club. You can’t change the privacy setting."
                        showAlert(message)
                    }else{
                        val message: String = "You are sharing this message in O-Club. You can’t change the privacy setting."
                        showAlert(message)
                    }
                }else{
                    val bundle: Bundle = Bundle()
                    bundle.putSerializable("groupPointsMap", null)
                    navController.navigate(R.id.action_global_nav_select_privacy, bundle)
                }

            }
        )
        binding.includeMessageLayout.button2.root.visibility = View.GONE
        binding.includeMessageLayout.button3.tvTitle.text = "OFF"
        binding.includeMessageLayout.button3.tvHint.text = "Anonymous"
        binding.includeMessageLayout.tvDialogMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.length >= 2800) {
                    binding.includeMessageLayout.tvRemainingText.text = String.format(
                        Locale.getDefault(),
                        "%d remaining",
                        3000 - s.length
                    )
                } else {
                    binding.includeMessageLayout.tvRemainingText.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length >= 2800) {
                    binding.includeMessageLayout.tvRemainingText.visibility = View.VISIBLE
                } else {
                    binding.includeMessageLayout.tvRemainingText.visibility = View.GONE
                }
            }
        })
        drawable =
            AppCompatResources.getDrawable(fragmentContext, R.drawable.ic_annonymus_shield)
        assert(drawable != null)
        DrawableCompat.setTint(
            (drawable)!!,
            ContextCompat.getColor(fragmentContext, R.color.colorAccent)
        )
        binding.includeMessageLayout.button3.checkIcon.setBackgroundDrawable(drawable)
        binding.includeMessageLayout.button3.container.setOnClickListener(
            ViewClickListener { v: View? ->
                if (isOutsideOnourem) {
                    binding.includeMessageLayout.button3.checkIcon.isChecked = false
                    binding.includeMessageLayout.button3.tvTitle.text = "OFF"
                    binding.includeMessageLayout.button3.tvTitle.setTextColor(
                        ContextCompat.getColor(
                            fragmentContext,
                            R.color.color_black
                        )
                    )
                    showAlert(
                        "This feature is available only for friends who are already on Onourem. You are sending this message to a friend Outside Onourem",
                        null,
                        getString(R.string.label_ok)
                    )
                } else {
                    if (binding.includeMessageLayout.button3.checkIcon.isChecked) {
                        binding.includeMessageLayout.button3.checkIcon.isChecked =
                            !binding.includeMessageLayout.button3.checkIcon.isChecked
                        binding.includeMessageLayout.button3.tvTitle.text = "OFF"
                        binding.includeMessageLayout.button3.tvTitle.setTextColor(
                            ContextCompat.getColor(
                                fragmentContext,
                                R.color.color_black
                            )
                        )
                    } else {
                        binding.includeMessageLayout.button3.checkIcon.isChecked =
                            !binding.includeMessageLayout.button3.checkIcon.isChecked
                        showAlert(
                            "Turn this ON if you wish to keep yourself anonymous to the friend you are sending this message to for the first 2 days. Your name will be revealed after 2 days.",
                            { v1: View? ->
                                binding.includeMessageLayout.button3.checkIcon.isChecked =
                                    true
                                binding.includeMessageLayout.button3.tvTitle.text = "ON"
                                binding.includeMessageLayout.button3.tvTitle.setTextColor(
                                    ContextCompat.getColor(
                                        fragmentContext,
                                        R.color.color_green
                                    )
                                )
                            },
                            getString(R.string.label_continue)
                        )
                    }
                }
            }
        )
        binding.includeMessageLayout.btnDialogOk.setOnClickListener(
            ViewClickListener(View.OnClickListener { v: View? ->
                if (TextUtils.isEmpty(
                        binding.includeMessageLayout.tvDialogMessage.text.toString()
                            .trim { it <= ' ' }
                    ) && TextUtils.isEmpty(uriImagePath)
                ) {
                    showAlert("Please enter text or attach image")
                    return@OnClickListener
                } else if (!isOutsideOnourem && selectedUsers!!.isEmpty() && (loginDayActivityInfoList?.receiverRequired != null) && loginDayActivityInfoList?.receiverRequired.equals(
                        "Y",
                        ignoreCase = true
                    )
                ) {
                    showAlert("You need to select at least one friend")
                    return@OnClickListener
                } else if (isOutsideOnourem && TextUtils.isEmpty(
                        binding.includeSearchButtonLayout.tilFriendName.text
                            .toString()
                            .trim { it <= ' ' }
                    ) && (loginDayActivityInfoList?.receiverRequired != null) && loginDayActivityInfoList?.receiverRequired.equals(
                        "Y",
                        ignoreCase = true
                    )
                ) {
                    showAlert("Please enter friend's name")
                    return@OnClickListener
                }
                val post = UploadPostRequest()
                post.screenId = "18"
                post.deviceId = uniqueDeviceId!!
                post.autoDeleteOnOff = "N"
                post.anonymousOnOff =
                    if (binding.includeMessageLayout.button3.checkIcon.isChecked) "Y" else "N"
                post.activityId = loginDayActivityInfoList?.activityId!!
                post.activityGameResId = loginDayActivityInfoList?.activityGameResponseId?:""
                post.playgroupId = loginDayActivityInfoList?.playgroupId?:""
                post.oClubActivityId = loginDayActivityInfoList?.oClubActivityId?:""
                post.activityTypeId = loginDayActivityInfoList?.activityTypeId?:""
                if (!selectedUsers!!.isEmpty()) {
                    val builder: StringBuilder = StringBuilder()
                    var i: Int = 0
                    while (i < selectedUsers.size) {
                        val userList: UserList = selectedUsers.get(i)
                        builder.append(userList.userId)
                        if (i < (selectedUsers.size - 1)) builder.append(",")
                        i++
                    }
                    post.receiverList = builder.toString()
                }
                if (isOutsideOnourem) {
                    post.anonymousUserList =
                        binding.includeSearchLayout.tilFriendName.text.toString()
                            .trim { it <= ' ' }
                }
                post.text = Objects.requireNonNull(
                    Base64Utility.encodeToString(
                        binding.includeMessageLayout.tvDialogMessage.text
                            .toString()
                    )
                )
                post.pushToDiscover = "N"
                val privacies: List<PrivacyGroup?>? =
                    privacyViewModel!!.selectedPrivacyMutableLiveData!!.value
                if (privacies != null) {
                    post.visibleToList = Utilities.getTokenSeparatedString(privacies, ",")
                } else {
                    //default is public
                    post.visibleToList = "1"
                }
                val outsideOnouremFriendName: String =
                    binding.includeSearchButtonLayout.tilFriendName.text.toString()
                if (!TextUtils.isEmpty(outsideOnouremFriendName)) post.anonymousUserList =
                    outsideOnouremFriendName
                val action: String? = loginDayActivityInfoList?.activityType
                post.userAction = action!!
                post.categoryId = loginDayActivityInfoList?.postCategoryId!!
                var bitmap: Bitmap? = null
                if (!TextUtils.isEmpty(uriImagePath)) {
                    bitmap = this.bitmap
                } else if (!TextUtils.isEmpty(uriVideoPath)) {
                    bitmap = this.bitmap
                }
                post.image = AppUtilities.getBase64String(bitmap, 500)
                post.smallPostImage = AppUtilities.getBase64String(bitmap, 100)
                post.videoData = ""
//                val pDialog: ProgressDialog = ProgressDialog(requireActivity())
//                pDialog.setMessage("Uploading...")
//                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
//                pDialog.max = 100
//                pDialog.progress = 0
//                pDialog.setCancelable(false)
//                pDialog.setIcon(R.drawable.ic_logo)
//                pDialog.isIndeterminate = false
                val onouremProgressDialog = OnouremProgressDialog(fragmentContext)
                val progressCallback = object : ProgressCallback {
                    override fun onProgressUpdate(percentage: Int) {
                        onouremProgressDialog.setProgress(percentage)
                    }
                }
                viewModel.uploadPost(
                    post,
                    (uriImagePath)!!,
                    uriVideoPath,
                    progressCallback
                )
                    .observe(
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
                            if (apiResponse.body.errorCode
                                    .equals("000", ignoreCase = true)
                            ) {
                                if (isOutsideOnourem) {
                                    showAlert(
                                        String.format(
                                            "Your message has been created successfully. You can now share this message with %s",
                                            post.anonymousUserList
                                        ), { v12: View? ->
                                            val share: Intent =
                                                Intent(Intent.ACTION_SEND)
                                            share.type = "text/plain"
                                            if (!TextUtils.isEmpty(apiResponse.body.outsideOnouremEmailMessage)) share.putExtra(
                                                Intent.EXTRA_TEXT,
                                                apiResponse.body.outsideOnouremEmailMessage
                                            )
                                            if (!TextUtils.isEmpty(apiResponse.body.outsideOnouremEmailSubject)) share.putExtra(
                                                Intent.EXTRA_SUBJECT,
                                                apiResponse.body.outsideOnouremEmailSubject
                                            )
                                            //                                if (!TextUtils.isEmpty(uriImagePath)) {
//                                    share.setType("image/*");
//                                    share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriImagePath));
//                                }
                                            startActivityForResult(
                                                Intent.createChooser(
                                                    share,
                                                    "Share via"
                                                ), 1212
                                            )
                                            pickerViewModel!!.setIntentMutableLiveData(
                                                null
                                            )
                                            privacyViewModel!!.setSelected(null)
                                            dismiss()
                                        }, "Share"
                                    )
                                } else {
                                    showAlert(
                                        "Your message has been sent successfully. It can be found on your profile screen."
                                    ) { v12: View? ->
                                        if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) loginDayActivityInfoList?.activityTag =
                                            apiResponse.body.activityTagStatus
                                        if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) loginDayActivityInfoList?.userParticipationStatus =
                                            apiResponse.body.participationStatus
                                        pickerViewModel!!.setIntentMutableLiveData(
                                            null
                                        )
                                        privacyViewModel!!.setSelected(null)
                                        if (onPostCreateOkActionListener != null) {
                                            onPostCreateOkActionListener!!.onItemClick(
                                                Pair.create(
                                                    loginDayActivityInfoList,
                                                    apiResponse.body
                                                )
                                            )
                                        }
                                        dismiss()
                                    }
                                }
                            } else {
                                showAlert(apiResponse.body.errorMessage)
                            }
                        } else {
                            onouremProgressDialog.hideDialog()
                            hideProgress()
                            showAlert(apiResponse.errorMessage)
                            if ((apiResponse.errorMessage != null
                                        && (apiResponse.errorMessage.contains(
                                    getString(
                                        R.string.unable_to_connect_host_message1
                                    )
                                )
                                        || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
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
            }
            )
        )
        binding.includeMessageLayout.btnDialogCancel.setOnClickListener(
            ViewClickListener { v: View? ->
                pickerViewModel!!.setIntentMutableLiveData(null)
                privacyViewModel!!.setSelected(null)
                dismiss()
            }
        )
        binding.includeMessageLayout.btnDialogUpImage.setOnClickListener(
            ViewClickListener { v: View? ->
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(
                        true
                    )
                )
            }
        )
        val mediaClick = ViewClickListener { v: View? ->
            if (!uriImagePath.equals("", ignoreCase = true) || !TextUtils.isEmpty(
                    loginDayActivityInfoList?.activityImageLargeUrl
                )
            ) {
                val path: String? =
                    if (!TextUtils.isEmpty(uriImagePath)) uriImagePath else loginDayActivityInfoList?.activityImageLargeUrl
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavMediaView(
                        1,
                        (path)!!
                    )
                )
            } else if (!uriVideoPath.equals("", ignoreCase = true) || !TextUtils.isEmpty(
                    loginDayActivityInfoList?.activityImageLargeUrl
                )
            ) {
                val path: String? =
                    if (!TextUtils.isEmpty(uriVideoPath)) uriImagePath else loginDayActivityInfoList?.activityImageLargeUrl
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavMediaView(
                        2,
                        (path)!!
                    )
                )
            }
        }
        binding.includeMessageLayout.ivSelectedMedia.setOnClickListener(mediaClick)
        binding.includeMessageLayout.ibVideoMedia.setOnClickListener(mediaClick)
        binding.includeMessageLayout.ibDeleteMedia.setOnClickListener(
            ViewClickListener { v: View? ->
                showAlert(
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
                            binding.includeMessageLayout.ivSelectedMedia.setImageDrawable(
                                null
                            )
                            uriVideoPath = ""
                            uriImagePath = ""
                        }
                    }
                )
            }
        )
        binding.includeMessageLayout.root.visibility = View.VISIBLE
        val searchLayoutShowViewClick: View.OnClickListener = View.OnClickListener { v: View? ->
            AppUtilities.hideKeyboard(requireActivity())
            setupSearchLayout()
        }
        binding.includeSearchButtonLayout.btnOnOnourem.setOnClickListener(
            searchLayoutShowViewClick
        )
        binding.btnAddMoreFriends.setOnClickListener(searchLayoutShowViewClick)
        binding.includeSearchLayout.btnGoBack.setOnClickListener(searchLayoutShowViewClick)
        binding.includeSearchButtonLayout.btnOutsideOnourem.setOnClickListener { v: View? ->
            AppUtilities.hideKeyboard(requireActivity())
            setupSearchFriendLayout()
        }
        binding.includeMessageLayout.root.visibility = View.VISIBLE
        privacyViewModel!!.selectedPrivacyMutableLiveData!!.observe(
            viewLifecycleOwner
        ) { privacyGroups: List<PrivacyGroup?>? ->
            if (privacyGroups == null) return@observe
            if (privacyGroups.size > 1) {
                setPrivacy("Custom")
            } else {
                val privacyGroup: PrivacyGroup? = privacyGroups.get(0)
                setPrivacy(privacyGroup!!.groupName!!)
            }
        }
        Objects.requireNonNull(binding.includeSearchLayout.tilFriendName)
            .setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) performUserSearch(
                    binding.includeSearchLayout.tilFriendName.text.toString()
                )
                true
            }
        binding.includeSearchButtonLayout.ibEndIcon.setOnClickListener(
            ViewClickListener { v: View? ->
                binding.includeSearchButtonLayout.tilFriendName.setText("")
                AppUtilities.hideKeyboard(requireActivity())
                setupSearchFriendLayout()
            }
        )
        binding.includeSearchLayout.ibEndIcon.setOnClickListener(
            ViewClickListener { v: View? ->
                binding.includeSearchLayout.tilFriendName.setText("")
                AppUtilities.hideKeyboard(requireActivity())
                if (searchFriendAdapter != null) searchFriendAdapter!!.reset()
            }
        )
        binding.includeSearchLayout.tilFriendName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!TextUtils.isEmpty(s)) binding.includeSearchLayout.ibEndIcon.visibility = View.VISIBLE else {
                    binding.includeSearchLayout.ibEndIcon.visibility = View.INVISIBLE
                    if (searchFriendAdapter != null) searchFriendAdapter!!.reset()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupSearchFriendLayout() {
        isOutsideOnourem = !isOutsideOnourem
        if (isOutsideOnourem) {
//            binding.includeSearchButtonLayout.tilFriendName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
//            binding.includeSearchButtonLayout.tilFriendName.requestFocus();
            binding.includeSearchButtonLayout.btnOutsideOnourem.visibility = View.GONE
            binding.includeSearchButtonLayout.tvOr.visibility = View.GONE
            binding.includeSearchButtonLayout.btnOnOnourem.visibility = View.GONE
            binding.includeSearchButtonLayout.tilFriendName.visibility = View.VISIBLE
            binding.includeSearchButtonLayout.ibEndIcon.visibility = View.VISIBLE
        } else {
            binding.includeSearchButtonLayout.tilFriendName.visibility = View.GONE
            binding.includeSearchButtonLayout.ibEndIcon.visibility = View.GONE
            binding.includeSearchButtonLayout.tilFriendName.setText("")
            binding.includeSearchButtonLayout.btnOutsideOnourem.visibility = View.VISIBLE
            binding.includeSearchButtonLayout.tvOr.visibility = View.VISIBLE
            binding.includeSearchButtonLayout.btnOnOnourem.visibility = View.VISIBLE
            binding.btnAddMoreFriends.visibility = if (selectedUsers!!.isEmpty()) View.GONE else View.VISIBLE
        }
        binding.invalidateAll()
    }

    private fun setupSearchLayout() {
        isOnOnourem = !isOnOnourem
        if (isOnOnourem) {
            binding.includeSearchLayout.root.visibility = View.VISIBLE
            binding.includeSearchButtonLayout.root.visibility = View.GONE
            binding.includeMessageLayout.root.visibility = View.GONE
            binding.includeChipLayout.root.visibility = View.VISIBLE
            binding.btnAddMoreFriends.visibility = View.GONE
        } else {
            binding.includeSearchButtonLayout.tilFriendName.visibility = View.GONE
            binding.includeSearchLayout.root.visibility = View.GONE
            if (selectedUsers!!.isEmpty()) {
                binding.includeSearchButtonLayout.root.visibility = View.VISIBLE
                binding.includeChipLayout.root.visibility = View.GONE
                binding.btnAddMoreFriends.visibility = View.GONE
            } else {
                binding.includeChipLayout.root.visibility = View.VISIBLE
                binding.includeSearchButtonLayout.root.visibility = View.GONE
                binding.btnAddMoreFriends.visibility = View.VISIBLE
            }
            binding.includeMessageLayout.root.visibility = View.VISIBLE
        }
        binding.invalidateAll()
    }

    private fun performUserSearch(searchText: String) {
        viewModel.getGlobalSearchResult(searchText).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserListResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && (apiResponse.body != null) && (apiResponse.body.userList != null) && apiResponse.body.userList.isNotEmpty()
            ) {
                hideProgress()
                setUserSearchAdapter(apiResponse.body)
            } else {
                hideProgress()
                showAlert("No search result found")
                if ((apiResponse.errorMessage != null
                            && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getGlobalSearchResult")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getGlobalSearchResult",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun setUserSearchAdapter(response: UserListResponse?) {
        val userLists: ArrayList<UserList> = ArrayList()
        for (user: UserList in response!!.userList) {
            if (!user.userId.equals(
                    preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID),
                    ignoreCase = true
                )
            ) {
                userLists.add(user)
            }
        }

        //BlockedUsersUtility.filterBlockedUsers(blockedUserIds, userLists);
        searchFriendAdapter = SearchFriendAdapter(
            userLists
        ) { item: UserList -> addOrRemoveChipGroupsItem(item) }
        binding.includeSearchLayout.rvSearchResult.adapter = searchFriendAdapter
        binding.includeSearchLayout.rvSearchResult.layoutManager = LinearLayoutManager(
            requireActivity(),
            RecyclerView.VERTICAL,
            false
        )
    }

    private fun addOrRemoveChipGroupsItem(item: UserList) {
        if (selectedUsers!!.contains(item)) {
            selectedUsers.remove(item)
        } else {
            selectedUsers.add(item)
        }
        configureChipLayout()
    }

    private fun configureChipLayout() {
        binding.includeChipLayout.chipGroup.removeAllViews()
        if (!selectedUsers!!.isEmpty()) {
            for (userList: UserList in selectedUsers) {
                val chip: Chip = Chip(requireActivity())
                chip.text = String.format(
                    "%s %s",
                    userList.firstName,
                    userList.lastName
                )
                chip.tag = userList
                chip.textSize = 12f
                chip.chipIconSize = 50f
                chip.chipEndPadding = 2f
                chip.setTextColor(ContextCompat.getColor(fragmentContext, R.color.color_white))
                chip.chipBackgroundColor = AppCompatResources.getColorStateList(
                    requireActivity(),
                    R.color.color_dark_gray
                )
                chip.setChipIconResource(R.drawable.ic_close_black_24dp)
                chip.chipIconTint = AppCompatResources.getColorStateList(
                    requireActivity(),
                    R.color.color_white
                )
                chip.setOnClickListener(chipOnClickListener)
                binding.includeChipLayout.chipGroup.addView(chip)
            }
        } else if (!isOnOnourem) {
            binding.btnAddMoreFriends.visibility = View.GONE
            binding.includeSearchButtonLayout.root.visibility = View.VISIBLE
            binding.includeChipLayout.root.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setSelectedCategory(null)
        pickerViewModel = null
        privacyViewModel = null
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
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
//                                binding.includeMessageLayout.btnDialogUpImage.visibility = View.INVISIBLE
//                                binding.includeMessageLayout.cardSelectedMedia.visibility = View.VISIBLE
//                                binding.includeMessageLayout.ibDeleteMedia.visibility = View.VISIBLE
//                                binding.includeMessageLayout.ibVideoMedia.visibility = View.INVISIBLE
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
            result is CropImage.CancelledResult -> showAlert("cropping image was cancelled by the user")
            else -> showAlert("cropping image failed")
        }
    }

    private class SearchFriendAdapter constructor(
        private val userList: MutableList<UserList>,
        private val onItemClickListener: OnItemClickListener<UserList>?
    ) : RecyclerView.Adapter<SearchFriendAdapter.SearchViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
            return SearchViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_user_search, parent, false)
            )
        }

        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
            holder.bind(userList.get(position))
        }

        override fun getItemCount(): Int {
            return userList.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun reset() {
            userList.clear()
            notifyDataSetChanged()
        }

        inner class SearchViewHolder constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            private val binding: ItemUserSearchBinding?
            var options: RequestOptions = RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.default_user_profile_image)
                .error(R.drawable.default_user_profile_image)

            fun bind(userList: UserList) {
                Glide.with(binding!!.rivProfile)
                    .load(userList.profilePicture)
                    .apply(options)
                    .into(binding.rivProfile)
                binding.ivCheck.visibility = if (userList.isSelected) View.VISIBLE else View.GONE
                binding.rivProfileIcon.visibility = if (userList.status
                        .equals("Friends", ignoreCase = true)
                ) View.VISIBLE else View.GONE
                binding.tvName.text = String.format(
                    "%s %s",
                    userList.firstName,
                    userList.lastName
                )
            }

            init {
                binding = DataBindingUtil.bind(itemView)
                itemView.setOnClickListener(ViewClickListener { v: View? ->
                    val user: UserList = userList[bindingAdapterPosition]
                    user.isSelected = !user.isSelected
                    onItemClickListener?.onItemClick(user)
                    notifyItemChanged(bindingAdapterPosition)
                })
            }
        }
    }

    companion object {
        fun getInstance(
            loginDayActivityInfoList: LoginDayActivityInfoList?,
            onPostCreateOkActionListener: OnItemClickListener<Pair<LoginDayActivityInfoList?, UploadPostResponse?>>?
        ): TaskAndMessageComposeDialogFragment {
            val fragment = TaskAndMessageComposeDialogFragment()
            fragment.loginDayActivityInfoList = loginDayActivityInfoList
            fragment.onPostCreateOkActionListener = onPostCreateOkActionListener
            return fragment
        }
    }
}