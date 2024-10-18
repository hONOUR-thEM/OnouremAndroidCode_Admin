package com.onourem.android.activity.ui.admin.bottomsheets

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetDialogFragment
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.fragments.openAppSystemSettings
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionBottomSheetDialogAdapter
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.bottomsheet.PickerBottomSheetDialogFragmentArgs
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


class AdminPickerBottomSheetDialogFragment :
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
        appCompatTextView.text = getString(R.string.options_what_do_you_want_to_do)
        rvActions.layoutManager = LinearLayoutManager(requireActivity())
        rvActions.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
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
                    getString(R.string.label_youtube_video),
                    R.color.color_black,
                    ActionType.YOUTUBE_VIDEO,
                    null
                )
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

//    private val resultImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        when (result.resultCode) {
//            Activity.RESULT_OK -> {
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
//
//    private val resultVideoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        when (result.resultCode) {
//            Activity.RESULT_OK -> {
//
//                if (result.data != null) {
//                    result.data?.data = videoURI
//                    viewModel.setIntentMutableLiveData(Pair.create(result.data, 444))
//                    navController.popBackStack()
//                }
//            }
//            else -> {
//                // logic
//            }
//        }
//    }

    override fun onItemClick(action: Action<Any?>) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            coroutineScope.launch(Dispatchers.Main) {
                handlePermissionResult(
                    PermissionManager.requestPermissions(
                        this@AdminPickerBottomSheetDialogFragment,
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
                        this@AdminPickerBottomSheetDialogFragment,
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var data = data
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 222) {
                data = Intent()
                data.data = photoURI
            } else if (requestCode == 444) {
                data = Intent()
                data.data = videoURI
            }
            viewModel.setIntentMutableLiveData(Pair.create(data, requestCode))
        }
        navController.popBackStack()
    }

    private fun handlePermissionResult(permissionResult: PermissionResult, action: Action<*>) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                when (action.actionType) {
                    ActionType.PHOTO_LIBRARY -> {
                        val actionPickImage =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        if (actionPickImage.resolveActivity(requireActivity().packageManager) != null) {
                            startActivityForResult(actionPickImage, 111)
//                            resultImageLauncher.launch(actionPickImage)
                        } else {
                            //Log no activity found
                            //Inform user via toast/snackbar etc
                            Toast.makeText(requireActivity(), "No App Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    ActionType.PHOTO_CAMERA -> try {
                        val actionCaptureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        // actionCaptureImage.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        val outputDir =
                            requireActivity().externalCacheDir // context being the Activity pointer
                        val image = File.createTempFile("temp", ".jpg", outputDir)

//                    File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                    File image = new File(
//                            storageDir.getPath() + "/temp" + System.currentTimeMillis() + ".jpg"
//                    );
                        photoURI = FileProvider.getUriForFile(
                            requireActivity(),
                            requireActivity().applicationContext.packageName + ".GenericFileProvider",
                            image
                        )
                        actionCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        //pic = f;
                        if (actionCaptureImage.resolveActivity(requireActivity().packageManager) != null) {
                            startActivityForResult(actionCaptureImage, 222)
//                            resultImageLauncher.launch(actionCaptureImage)
                        } else {
                            //Log no activity found
                            //Inform user via toast/snackbar etc
                            Toast.makeText(requireActivity(), "No App Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    ActionType.YOUTUBE_VIDEO -> {
                        viewModel.setIntentMutableLiveData(Pair.create(null, 555))
                        navController.popBackStack()
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
//                            requireActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES)
//                        val lvideo = File(
//                            dir!!.path + "/temp.mp4"
//                        )
//                        videoURI = FileProvider.getUriForFile(
//                            requireActivity(),
//                            requireActivity().applicationContext.packageName + ".GenericFileProvider",
//                            lvideo
//                        )
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
//                         intent.setAction(Intent.ACTION_GET_CONTENT);
                        if (intent.resolveActivity(requireActivity().packageManager) != null) {
//                            startActivityForResult(
//                                Intent.createChooser(intent, "Select a video"),
//                                333
//                            )
                            startActivityForResult(Intent.createChooser(intent, "Select Video"), 333)
//                            resultVideoLauncher.launch(intent)
                        } else {
                            //Log no activity found
                            //Inform user via toast/snackbar etc
                            Toast.makeText(requireActivity(), "No App Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    ActionType.VIDEO_CAMERA -> {
                        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        val storageDir =
                            requireActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                        val video = File(
                            storageDir!!.path + "/temp.mp4"
                        )
                        videoURI = FileProvider.getUriForFile(
                            requireActivity(),
                            requireActivity().applicationContext.packageName + ".GenericFileProvider",
                            video
                        )
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                        val chooserIntent = Intent.createChooser(takeVideoIntent, "Capture Video")
                        if (chooserIntent.resolveActivity(requireActivity().packageManager) != null) {
                            startActivityForResult(chooserIntent, 444)
//
//                            resultVideoLauncher.launch(chooserIntent)
                        } else {
                            //Log no activity found
                            //Inform user via toast/snackbar etc
                            Toast.makeText(requireActivity(), "No App Found", Toast.LENGTH_SHORT)
                                .show()
                        }
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
                                            this@AdminPickerBottomSheetDialogFragment,
                                            4,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
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