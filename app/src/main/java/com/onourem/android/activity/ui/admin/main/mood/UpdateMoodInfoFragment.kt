package com.onourem.android.activity.ui.admin.main.mood

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.onourem.android.activity.databinding.FragmentUpdateMoodInfoBinding
import com.onourem.android.activity.models.MoodInfoData
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.network.FileUploadProgressRequestBody
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.main.other.CustomAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.LocalMoods
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.VideoThumbnailHelper.createVideoThumbnail
import com.onourem.android.activity.ui.utils.compression.LightCompressionUtils
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.CallBackOfQuery
import com.onourem.android.activity.ui.utils.media.Common
import com.onourem.android.activity.ui.utils.media.FFmpegCallBack
import com.onourem.android.activity.ui.utils.media.FFmpegQueryExtension
import com.onourem.android.activity.ui.utils.media.LogMessage
import com.onourem.android.activity.ui.utils.media.Statistics
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import java.util.HashMap
import javax.inject.Inject


class UpdateMoodInfoFragment  //    private int counter = 0;
    : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentUpdateMoodInfoBinding>() {

    private var args: MoodInfoData? = null
    private var selectedMood: UserExpressionList? = null
    private var bitmap: Bitmap? = null
    private var pickerViewModel: MediaPickerViewModel? = null
    private var adapter: ArrayAdapter<String>? = null
    private var uriImagePath = ""
    private var uriVideoPath = ""
    private var hasUploadVideoPresent = false
    private var hasUploadImagePresent = false
    private var youtubeVideoLink = ""
    private var fileSrc: String? = null
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15)) //15
        .placeholder(R.drawable.shape_image_video_placeholder)
        .error(R.drawable.shape_image_video_placeholder)


    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper


    override fun layoutResource(): Int {
        return R.layout.fragment_update_mood_info
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MediaPickerViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

    }

    private fun calculatePopupWindowHeight(maxItemsToShow: Int): Int {
        val itemHeight = resources.getDimensionPixelSize(R.dimen.select_dialog_item_height)
        val dividerHeight = resources.getDimensionPixelSize(R.dimen.select_dialog_divider_height)
        return (maxItemsToShow * itemHeight + (maxItemsToShow - 1) * dividerHeight)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = UpdateMoodInfoFragmentArgs.fromBundle(requireArguments()).moodInfo


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

                showAlert("Alert", "Some fields are empty do you want to proceed?", "Proceed") {
                    addMoodInfoUpdateByAdmin(selectedMood!!)
                }

            }
        })

        binding.btnSourceImage.setOnClickListener(ViewClickListener {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavPickerBottomsheetDialogFragment(
                    true
                )
            )
        })

        observeOnActivityResult()

        val userExpressionList: List<UserExpressionList> = LocalMoods.getAllMoods(
            preferenceHelper
        )

        val adapter: CustomAdapter<UserExpressionList> =
            CustomAdapter(fragmentContext, R.layout.simple_dropdown_item_onourem, userExpressionList)

        binding.autoCompleteTextView.setAdapter(adapter)
//        binding.autoCompleteTextView.threshold = 4
        

        binding.autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
                selectedMood = parent.getItemAtPosition(position) as UserExpressionList
                Toast.makeText(fragmentContext, "Selected: ${selectedMood?.id}", Toast.LENGTH_SHORT).show()
            }


        if (args != null) {

            val localMoods: HashMap<String, UserExpressionList>?
            localMoods = LocalMoods.getAllMoodsMap()

            selectedMood = localMoods[args?.expressionId]

            binding.autoCompleteTextView.isEnabled = false
            binding.autoCompleteTextView.setText(selectedMood?.expressionText)

            Glide.with(requireActivity()).asBitmap().load(args?.imageUrl)
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


                            if (!TextUtils.isEmpty(args?.imageUrl)) {
                                binding.ivSourceImage.visibility = View.VISIBLE
                            } else {
                                binding.ivSourceImage.visibility = View.GONE
                            }

                            if (!TextUtils.isEmpty(args?.videoUrl) && args?.youtubeLink == "Y") {
                                binding.ivPlayVideo.visibility  = View.VISIBLE
                                hasUploadVideoPresent = true
                                hasUploadImagePresent = false
                            } else {
                                binding.ivPlayVideo.visibility  = View.GONE
                                hasUploadVideoPresent = false
                                hasUploadImagePresent = true

                            }
                            
                            if (!TextUtils.isEmpty(args?.imageUrl)) {
                                binding.ivSourceImage.visibility = View.VISIBLE
                                Glide.with(fragmentContext)
                                    .load(bitmap)
                                    .apply(options)
                                    .into(binding.ivSourceImage)

                                if (args?.youtubeLink == "Y" || args?.youtubeLink == "V") {
                                    binding.ivPlayVideo.visibility  = View.VISIBLE
                                } else {
                                    binding.ivPlayVideo.visibility  = View.GONE
                                }
                            } else {
                                binding.ivSourceImage.visibility = View.GONE
                            }
                            
                            bitmap = resource
                            binding.btnSourceImage.text = "Source Selected"

                            binding.tilExternalContentUrl.visibility = View.VISIBLE
                            binding.tilSpinnerInput.visibility = View.VISIBLE
                            binding.tilSource.visibility = View.VISIBLE


                        }
                        return true
                    }
                }).submit()



            binding.tilSpinner.setText(
                if (args?.youtubeLink == "Y") {
                    "Yes"
                } else {
                    "No"
                }
            )


            binding.tilExternalContentUrl.editText?.setText(args?.videoUrl?:"")
            binding.tilSource.editText?.setText(args?.sourceName?:"")
            binding.tilSummary.editText?.setText(args?.summary?:"")

            
            

        }


    }

    private val isValidData: Boolean
        get() {


            val mood = binding.autoCompleteTextView.text.toString()
                .trim { it <= ' ' }
            if (TextUtils.isEmpty(mood)) {
                showAlert("Please select mood first.")
                return false
            }


//            if (hasUploadVideoPresent) {
//                val summary = binding.tilSummary.editText!!.text.toString()
//                    .trim { it <= ' ' }
//                if (TextUtils.isEmpty(summary)) {
//                    showAlert("Please enter valid text.")
//                    return false
//                }
//            }

//            if (hasUploadImagePresent) {

//                val summary = binding.tilSummary.editText!!.text.toString()
//                    .trim { it <= ' ' }
//                if (TextUtils.isEmpty(summary)) {
//                    showAlert("Please enter valid text.")
//                    return false
//                }
//
//                val fName = binding.tilExternalContentUrl.editText!!.text.toString()
//                    .trim { it <= ' ' }
//                if (TextUtils.isEmpty(fName)) {
//                    showAlert("Please enter valid link.")
//                    return false
//                }
//
//                if (binding.tilSpinner.text.toString().trim { it <= ' ' }.isEmpty()) {
//                    showAlert("Please select is this Youtube Url or not.")
//                    return false
//                }
//                if (TextUtils.isEmpty(
//                        binding.tilSource.editText!!.text.toString()
//                            .trim { it <= ' ' })
//                ) {
//                    showAlert("Please enter source name.")
//                    return false
//                }

//            }

            if (!hasUploadImagePresent && !hasUploadVideoPresent) {
                showAlert("Please attach source")
                return false
            }

            return true
        }


    private fun addMoodInfoUpdateByAdmin(mood: UserExpressionList) {
//        var youtubeLink = ""
        var externalLink = ""
//        if (binding.tilSpinner.text.toString() == "Yes") {
//            youtubeLink = binding.tilExternalContentUrl.editText!!.text.toString()
//        } else {
//
//        }
        externalLink = binding.tilExternalContentUrl.editText!!.text.toString()
        val onouremProgressDialog = OnouremProgressDialog(fragmentContext)
        val progressCallback =
            object : FileUploadProgressRequestBody.ProgressCallback {
                override fun onProgressUpdate(percentage: Int) {
                    onouremProgressDialog.setProgress(percentage)
                }
            }

        viewModel.addMoodResponseCounsellingCardByAdmin(
            mood.id,
            binding.tilSummary.editText!!.text.toString(),
            "",
            externalLink,
            binding.tilSource.editText!!.text.toString(),
            if (binding.tilSpinner.text.toString() == "Yes") {
                "Y"
            } else {
                "N"
            },
            bitmap,
            uriVideoPath,
            progressCallback
        ).observe(viewLifecycleOwner) { apiResponse1: ApiResponse<StandardResponse> ->
            if (apiResponse1.loading) {
                showProgress()
            } else if (apiResponse1.isSuccess && apiResponse1.body != null) {
                hideProgress()
                if (apiResponse1.body.errorCode.equals("000")) {

                    binding.tilExternalContentUrl.editText!!.setText("")
                    binding.tilSummary.editText!!.setText("")
                    binding.tilSpinner.setText("")
                    binding.tilSource.editText!!.setText("")
                    binding.btnSourceImage.text = "Source Image/Video"
                    bitmap = null
                    uriImagePath = ""
                    uriVideoPath = ""

                    showAlert("Mood Info has been updated.") {
                        pickerViewModel!!.setIntentMutableLiveData(null)
                        navController.popBackStack(R.id.nav_admin_menu, false)
                    }

                } else {
                    showAlert(apiResponse1.body.errorMessage)
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
                                        binding.ivPlayVideo.visibility = View.GONE
                                        binding.ivSourceImage.visibility = View.VISIBLE
                                        bitmap = resource
                                        binding.btnSourceImage.text = "Source Selected"
                                        Glide.with(fragmentContext)
                                            .load(bitmap)
                                            .apply(options)
                                            .into(binding.ivSourceImage)

                                        hasUploadImagePresent = true
                                        hasUploadVideoPresent = false

                                        binding.tilExternalContentUrl.visibility = View.VISIBLE
                                        binding.tilSpinnerInput.visibility = View.VISIBLE
                                        binding.tilSource.visibility = View.VISIBLE


                                    }
                                    return true
                                }
                            }).submit()

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


//                        Uri videoURI = FileProvider.getUriForFile(requireActivity(),
//                                requireActivity().getApplicationContext().getPackageName() + ".GenericFileProvider", video);
                        fileSrc = LightCompressionUtils.getMediaPath(requireActivity(), uri444)
                        if (Utilities.checkVideoDuration444(
                                requireActivity(), fileSrc, preferenceHelper.getString(
                                    Constants.KEY_VIDEO_DURATION
                                )
                            )
                        ) {
//                            bitmap =
//                                ThumbnailUtils.createVideoThumbnail(uriVideoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
                            bitmap = createVideoThumbnail(fileSrc)


                            // File destinationFile = LightCompressionUtils.saveVideoFile(fileSrc, requireActivity());
                            if (!TextUtils.isEmpty(fileSrc)) {


                                Glide.with(requireActivity())
                                    .load(bitmap)
                                    .apply(options)
                                    .into(binding.ivSourceImage)

                                //desPath = destinationFile.getPath();
                                //OnouremProgressDialog onouremProgressDialog = new OnouremProgressDialog(getFragmentContext());
                                // Compression start
                                showProgressWithText("Compressing Video", false)
                                val retriever = MediaMetadataRetriever()
                                retriever.setDataSource(fileSrc)
                                val width = retriever.frameAtTime!!.width.toString().toInt()
                                val height = retriever.frameAtTime!!.height.toString().toInt()
                                retriever.release()
                                val outputPath = Common.getFilePath(
                                    fragmentContext,
                                    Common.VIDEO,
                                    Common.OPERATION_COMPRESS_VIDEO,
                                    Common.getRandomName(6)
                                )
                                val fFmpegQueryExtension = FFmpegQueryExtension()
                                val query = fFmpegQueryExtension.compressor(fileSrc!!, width, height, outputPath)
                                val callBackOfQuery = CallBackOfQuery()
                                callBackOfQuery.callQuery((requireActivity() as AppCompatActivity), query, object :
                                    FFmpegCallBack {
                                    override fun process(logMessage: LogMessage) {
                                        //onouremProgressDialog.setProgress(logMessage.getLevel().getValue(), 100);
                                    }

                                    override fun statisticsProcess(statistics: Statistics) {}
                                    override fun success() {
                                        binding.btnSourceImage.text = "Source Selected"
                                        uriVideoPath = outputPath
                                        binding.ivPlayVideo.visibility = View.VISIBLE
                                        binding.ivSourceImage.visibility = View.VISIBLE

                                        hasUploadImagePresent = false
                                        hasUploadVideoPresent = true

                                        binding.tilExternalContentUrl.visibility = View.GONE
                                        binding.tilSpinnerInput.visibility = View.GONE
                                        binding.tilSource.visibility = View.GONE

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
                            bitmap = null
                            showAlert(Utilities.getMaxVideoDurationText(preferenceHelper.getString(Constants.KEY_VIDEO_DURATION)))
                        }
                        return@observe
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