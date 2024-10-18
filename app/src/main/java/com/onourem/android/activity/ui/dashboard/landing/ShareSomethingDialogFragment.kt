package com.onourem.android.activity.ui.dashboard.landing

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.google.gson.Gson
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.databinding.DialogShareSomethingBinding
import com.onourem.android.activity.models.CategoryList
import com.onourem.android.activity.models.GetUserMoodResponseMsgResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.appreciation.viewmodel.AppreciationViewModel
import com.onourem.android.activity.ui.audio.fragments.openAppSystemSettings
import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerService
import com.onourem.android.activity.ui.dashboard.adapters.ShareSomethingAdapter
import com.onourem.android.activity.ui.dashboard.adapters.ShareSomethingHeaderAdapter
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ShareSomethingDialogFragment :
    AbstractBaseDialogBindingFragment<DashboardViewModel, DialogShareSomethingBinding>() {

    private lateinit var mediaOperationViewModel: MediaOperationViewModel
    private lateinit var appreciationViewModel: AppreciationViewModel

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    override fun layoutResource(): Int {
        return R.layout.dialog_share_something
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        mediaOperationViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            )[MediaOperationViewModel::class.java]

        appreciationViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            )[AppreciationViewModel::class.java]

    }

//    override fun onPrepareOptionsMenu(menu: Menu) {
//        menu.findItem(R.id.search_nav).isVisible = false
//        menu.findItem(R.id.profile_nav).isVisible = false
//        menu.findItem(R.id.phone_nav).isVisible = false
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.search_nav)?.setVisible(false)
                menu.findItem(R.id.profile_nav)?.setVisible(false)
                menu.findItem(R.id.phone_nav)?.setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.parent.setOnClickListener(ViewClickListener {
            dismiss()
        })

        binding.ivClose.setOnClickListener(ViewClickListener {
            dismiss()
        })

        binding.cardRecording.setOnClickListener(ViewClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                coroutineScope.launch {
                    withContext(Dispatchers.Main) {
                        handlePermissionResult(
                            PermissionManager.requestPermissions(
                                this@ShareSomethingDialogFragment, 5,
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.RECORD_AUDIO,
                            )
                        )
                    }
                }
            } else {
                coroutineScope.launch {
                    withContext(Dispatchers.Main) {
                        handlePermissionResult(
                            PermissionManager.requestPermissions(
                                this@ShareSomethingDialogFragment, 5,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
//                            Manifest.permission.READ_CONTACTS,
                            )
                        )
                    }
                }
            }


        })

        binding.cardWrite.setOnClickListener(ViewClickListener {
            val item = CategoryList()
            item.id = "8"
            item.catCode = "Thoughts"
            item.colorCode = "e76875"
            item.description =
                "Under ‘Thoughts’ category, you can note down various memories and experiences worth remembering. You can keep it private or share it with friends or with the world."
            item.imageCode = "miss_you"
            item.postSubCatCOList = emptyList()
            item.receiverRequired = "N"
            item.publicCommentAllowed = "N"
            item.showInAll = "Y"
            item.status = "Y"
            item.visibleInAllCountries = "Y"
            appreciationViewModel.setSelectedCategory(item)
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavComposeMessageDialog(
                    false, null
                )
            )

        })

        setAdapter()

    }

    private fun handlePermissionResult(permissionResult: PermissionResult) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                mediaOperationViewModel.setPlayerOperation(AudioPlayerService.KEY_SHOULD_NOT_PLAY)
                (fragmentContext as DashboardActivity).setMusicPlayerVisibility(false)

                val navBuilder = NavOptions.Builder()
                val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavVocalsMain(
                        "",
                        "",
                        "",
                        ""
                    ), navOptions
                )
                mediaOperationViewModel.setOpenRecording(true)


//                val navBuilder = NavOptions.Builder()
//                val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
//                navController.navigate(
//                    MobileNavigationDirections.actionGlobalNavRecorder(
//                        preferenceHelper.getInt(Constants.KEY_AUDIO_LIMIT).toLong()
//                    ), navOptions
//                )
            }

            is PermissionResult.PermissionDenied -> {
                Toast.makeText(fragmentContext, "Denied", Toast.LENGTH_SHORT).show()
            }

            is PermissionResult.ShowRational -> {
                showAlert(
                    "Permissions Needed",
                    "We need permissions to work record audio functionality.",
                    ViewClickListener {
                        when (permissionResult.requestCode) {

                            5 -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    coroutineScope.launch(Dispatchers.Main) {
                                        handlePermissionResult(
                                            PermissionManager.requestPermissions(
                                                this@ShareSomethingDialogFragment,
                                                5,
                                                Manifest.permission.READ_MEDIA_AUDIO,
                                                Manifest.permission.RECORD_AUDIO,
                                            )
                                        )
                                    }
                                } else {
                                    coroutineScope.launch(Dispatchers.Main) {
                                        handlePermissionResult(
                                            PermissionManager.requestPermissions(
                                                this@ShareSomethingDialogFragment,
                                                5,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.RECORD_AUDIO,
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                )

            }

            is PermissionResult.PermissionDeniedPermanently -> {
                showAlert(
                    "Permissions Needed",
                    "You have denied app permissions permanently, We need permissions to work record audio functionality. Please go to Settings and give Onourem App required permissions to use the app.",
                    ViewClickListener {
                        fragmentContext.openAppSystemSettings()
                    })
            }
        }
    }


    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(activity)

        binding.rvUsers.layoutManager = layoutManager


        val headerAdapter = ShareSomethingHeaderAdapter(
            "Share Your Heart",
            "Onourem is about genuine interactions. Journal privately or share thoughts and life experiences with close friends regularly to keep them close forever. Below are some examples:"
        )

        val itemList = ArrayList<String>()

        val response = Gson().fromJson(
            preferenceHelper.getString(Constants.CHANGED_MOOD),
            GetUserMoodResponseMsgResponse::class.java
        )

        itemList.add("An experience that made you a better person")
        itemList.add("A mistake you made, how it impacted you/others and what did you learn")
        itemList.add("An act or behaviour by someone you know that you appreciated or learnt from")

        if (response.exampleTexts!!.isNotEmpty()) {
            val adapter = ShareSomethingAdapter(response.exampleTexts as ArrayList<String>)
            binding.rvUsers.adapter = ConcatAdapter(headerAdapter, adapter)

            Handler(Looper.getMainLooper()).postDelayed({
                binding.rvUsers.smoothScrollToPosition(response.exampleTexts.size - 1)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.rvUsers.smoothScrollToPosition(0)
                }, 600)
            }, 600)
        } else {
            val adapter = ShareSomethingAdapter(itemList)
            binding.rvUsers.adapter = ConcatAdapter(headerAdapter, adapter)
        }

    }
}