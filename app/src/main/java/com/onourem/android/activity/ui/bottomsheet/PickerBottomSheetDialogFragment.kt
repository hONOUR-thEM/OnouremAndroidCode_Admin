package com.onourem.android.activity.ui.bottomsheet

import android.Manifest.permission.*
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.onourem.android.activity.ui.utils.permissions.PermissionManager
import com.onourem.android.activity.ui.utils.permissions.PermissionResult
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetDialogFragment
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.fragments.openAppSystemSettings
import com.onourem.android.activity.ui.games.viewmodel.MediaPickerViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


class PickerBottomSheetDialogFragment :
    AbstractBaseBottomSheetDialogFragment<MediaPickerViewModel>(),
    OnItemClickListener<Action<Any?>> {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var photoURI: Uri? = null
    private var videoURI: Uri? = null

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    override fun layoutResource(): Int {
        return R.layout.dialog_bottom_sheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val rvActions: RecyclerView = requireView().findViewById(R.id.rvActions)
        val appCompatTextView: AppCompatTextView = requireView().findViewById(R.id.tvTitle)
        appCompatTextView.text = "Options\n\nWhat do you want to do?"
        rvActions.layoutManager = LinearLayoutManager(fragmentContext)
        rvActions.addItemDecoration(
            DividerItemDecoration(
                fragmentContext,
                DividerItemDecoration.VERTICAL
            )
        )
        val actions: MutableList<Action<*>> = ArrayList()
        actions.add(
            Action<Any?>(
                getString(R.string.label_photo_library),
                R.color.color_black,
                ActionType.PHOTO_LIBRARY,
                null
            )
        )
        actions.add(
            Action<Any?>(
                getString(R.string.label_photo_camera),
                R.color.color_black,
                ActionType.PHOTO_CAMERA,
                null
            )
        )
        if (PickerBottomSheetDialogFragmentArgs.fromBundle(
                requireArguments()
            ).isVideoAllowed
        ) {
            val videoDurationText = Utilities.getMaxVideoDurationTextForPickerBottomSheet(
                preferenceHelper!!.getString(Constants.KEY_VIDEO_DURATION)
            )
            actions.add(
                Action<Any?>(
                    getString(R.string.label_video_library) + " " + videoDurationText,
                    R.color.color_black,
                    ActionType.VIDEO_LIBRARY,
                    null
                )
            )
            actions.add(
                Action<Any?>(
                    getString(R.string.label_video_camera) + " " + videoDurationText,
                    R.color.color_black,
                    ActionType.VIDEO_CAMERA,
                    null
                )
            )
        }
        rvActions.adapter = ActionBottomSheetDialogAdapter(actions as MutableList<Action<Any?>>, this)
    }

    override fun viewModelType(): Class<MediaPickerViewModel> {
        return MediaPickerViewModel::class.java
    }

//    private fun pickCamera() {
//        val values = ContentValues()
//        values.put(MediaStore.Images.Media.TITLE, "New Picture")
//        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
//        val cam_uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri)
//
//        //startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE); // OLD WAY
//        resultImageLauncher.launch(cameraIntent) // VERY NEW WAY
//    }

    //Creates the ActivityResultLauncher
    private val resultImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            val intent = Intent()
            intent.data = photoURI
            viewModel.setIntentMutableLiveData(Pair.create(intent, 111))
            navController.popBackStack()
        }
    }

//    private val resultImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        when (result.resultCode) {
//            RESULT_OK -> {
//
//                if (result.data != null) {
//                    result.data?.data = photoURI
//                    viewModel.setIntentMutableLiveData(Pair.create(result.data, 222))
//                    navController.popBackStack()
//                }
//            }
//            else -> {
//                // logic
//            }
//        }
//    }

    private val getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        if (uri != null) {
            val intent = Intent()
            intent.data = uri
            viewModel.setIntentMutableLiveData(Pair.create(intent, 111))
            navController.popBackStack()
        }
    }

    private val getVideoContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        if (uri != null) {
            val intent = Intent()
            intent.data = uri
            viewModel.setIntentMutableLiveData(Pair.create(intent, 333))
            navController.popBackStack()
        }
    }

    //
    private val resultVideoLauncher = registerForActivityResult(ActivityResultContracts.CaptureVideo()) {
        if (it) {
            val intent = Intent()
            intent.data = videoURI
            viewModel.setIntentMutableLiveData(Pair.create(intent, 444))
            navController.popBackStack()
        }
    }

    override fun onItemClick(action: Action<Any?>) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            coroutineScope.launch(Dispatchers.Main) {
                handlePermissionResult(
                    PermissionManager.requestPermissions(
                        this@PickerBottomSheetDialogFragment,
                        4,
                        CAMERA,
                        READ_MEDIA_VIDEO,
                        READ_MEDIA_IMAGES,
                    ), action
                )
            }
        } else {
            coroutineScope.launch(Dispatchers.Main) {
                handlePermissionResult(
                    PermissionManager.requestPermissions(
                        this@PickerBottomSheetDialogFragment,
                        4,
                        READ_EXTERNAL_STORAGE,
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                    ), action
                )
            }
        }


        //navController.popBackStack();
    }

//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        var data = data
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == 222) {
//                data = Intent()
//                data.data = photoURI
//            } else if (requestCode == 444) {
//                data = Intent()
//                data.data = videoURI
//            }
//            viewModel.setIntentMutableLiveData(Pair.create(data, requestCode))
//        }
//        navController.popBackStack()
//    }

    private fun handlePermissionResult(permissionResult: PermissionResult, action: Action<*>) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                when (action.actionType) {
                    ActionType.PHOTO_LIBRARY -> {
                        val actionPickImage =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        if (actionPickImage.resolveActivity(fragmentContext.packageManager) != null) {
//                            startActivityForResult(actionPickImage, 111)
                            getImageContent.launch("image/*")
                        } else {
                            //Log no activity found
                            //Inform user via toast/snackbar etc
                            Toast.makeText(fragmentContext, "No App Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    ActionType.PHOTO_CAMERA -> try {

//                        pickCamera()
//                        val actionCaptureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        // actionCaptureImage.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        val outputDir =
                            fragmentContext.externalCacheDir // context being the Activity pointer
                        val image = File.createTempFile("temp", ".jpg", outputDir)

//                    File storageDir = fragmentContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                    File image = new File(
//                            storageDir.getPath() + "/temp" + System.currentTimeMillis() + ".jpg"
//                    );
                        photoURI = FileProvider.getUriForFile(
                            fragmentContext,
                            fragmentContext.applicationContext.packageName + ".GenericFileProvider",
                            image
                        )
                        if (photoURI != null) {
                            resultImageLauncher.launch(photoURI)
                        }
//                        actionCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                        //pic = f;
//                        if (actionCaptureImage.resolveActivity(fragmentContext.packageManager) != null) {
////                            startActivityForResult(actionCaptureImage, 222)
//                            resultImageLauncher.launch(photoURI)
//                        } else {
//                            //Log no activity found
//                            //Inform user via toast/snackbar etc
//                            Toast.makeText(fragmentContext, "No App Found", Toast.LENGTH_SHORT)
//                                .show()
//                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    ActionType.VIDEO_LIBRARY -> {

                        val intent = Intent()
                        intent.type = "video/*"
                        intent.action = Intent.ACTION_GET_CONTENT

//                        val intent = Intent(
//                            Intent.ACTION_PICK,
//                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//                        )
//                        intent.setDataAndType( MediaStore.Video.Media.INTERNAL_CONTENT_URI,"video/*")
//                        val dir =
//                            fragmentContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
//                        val lvideo = File(
//                            dir!!.path + "/temp.mp4"
//                        )
//                        videoURI = FileProvider.getUriForFile(
//                            fragmentContext,
//                            fragmentContext.applicationContext.packageName + ".GenericFileProvider",
//                            lvideo
//                        )
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
//                         intent.setAction(Intent.ACTION_GET_CONTENT);
                        if (intent.resolveActivity(fragmentContext.packageManager) != null) {
//                            startActivityForResult(
//                                Intent.createChooser(intent, "Select a video"),
//                                333
//                            )
//                            startActivityForResult(Intent.createChooser(intent, "Select Video"), 333)
                            getVideoContent.launch(intent.type)
                        } else {
                            //Log no activity found
                            //Inform user via toast/snackbar etc
                            Toast.makeText(fragmentContext, "No App Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    ActionType.VIDEO_CAMERA -> {
//                        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        val storageDir =
                            fragmentContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                        val video = File(
                            storageDir!!.path + "/temp.mp4"
                        )
                        videoURI = FileProvider.getUriForFile(
                            fragmentContext,
                            fragmentContext.applicationContext.packageName + ".GenericFileProvider",
                            video
                        )
                        if (videoURI != null) {
                            resultVideoLauncher.launch(videoURI)
                        }
//                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
//                        val chooserIntent = Intent.createChooser(takeVideoIntent, "Capture Video")
//                        if (chooserIntent.resolveActivity(fragmentContext.packageManager) != null) {
////                            startActivityForResult(chooserIntent, 444)
//                            resultVideoLauncher.launch(chooserIntent)
//                        } else {
//                            //Log no activity found
//                            //Inform user via toast/snackbar etc
//                            Toast.makeText(fragmentContext, "No App Found", Toast.LENGTH_SHORT)
//                                .show()
//                        }
                    }
                    else -> {

                    }
                }
            }
            is PermissionResult.PermissionDenied -> {
                Toast.makeText(fragmentContext, "Denied", Toast.LENGTH_SHORT).show()
            }
            is PermissionResult.ShowRational -> {
                showAlert(
                    "We need permission to work functionalities like capture images/video.",
                    ViewClickListener {
                        when (permissionResult.requestCode) {

                            4 -> {
                                coroutineScope.launch(Dispatchers.Main) {
                                    handlePermissionResult(
                                        PermissionManager.requestPermissions(
                                            this@PickerBottomSheetDialogFragment,
                                            4,
                                            READ_EXTERNAL_STORAGE,
                                            CAMERA,
                                            WRITE_EXTERNAL_STORAGE,
                                        ),
                                        action
                                    )
                                }
                            }
                        }
                    })

            }
            is PermissionResult.PermissionDeniedPermanently -> {
                showAlert(
                    "You have denied app permissions permanently, We need permissions to work functionalities like images/video capture & save to local storage. Please go to Settings and give Onourem App required permissions to use the app.",
                    ViewClickListener {
                        fragmentContext.openAppSystemSettings()
                    })
            }
        }
    }

}