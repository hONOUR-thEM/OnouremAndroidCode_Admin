package com.onourem.android.activity.ui.circle.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavOptions
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogWellDoneBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener


class WellDoneDialogFragment :
    AbstractBaseDialogBindingFragment<FriendCircleGameViewModel, DialogWellDoneBinding>() {

    private var hasPlayedGame: Boolean = false

    override fun layoutResource(): Int {
        return R.layout.dialog_well_done
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);

        setHasOptionsMenu(true)
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

        val args = WellDoneDialogFragmentArgs.fromBundle(requireArguments())

//        getQualityQuestions()

        binding.btnContinue.setOnClickListener(ViewClickListener {
            createQualityQuestionGame()
        })

        binding.btnCancel.setOnClickListener(ViewClickListener {
            navController.popBackStack()
        })

//        binding.materialCardView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(args.backgroundColor))
        binding.btnContinue.setBackgroundColor(Color.parseColor(args.waveColor))
//        binding.btnContinue.setTextColor(Color.parseColor(args.backgroundColor))

    }

//    private fun getQualityQuestions() {
//        viewModel.getQualityQuestions().observe(
//            viewLifecycleOwner
//        ) { apiResponse: ApiResponse<GetQualityQuestionsResponse> ->
//            if (apiResponse.loading) {
//                showProgress()
//            } else if (apiResponse.isSuccess && apiResponse.body != null) {
//                hideProgress()
//
//                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                    hasPlayedGame = apiResponse.body.qualityQuestionList!!.isEmpty()
//                } else {
//                    showAlert(apiResponse.body.errorMessage)
//                }
//            } else {
//                hideProgress()
//                showAlert(apiResponse.errorMessage)
//            }
//        }
//    }

    private fun createQualityQuestionGame() {

        val listOfContacts = viewModel.getSelectedFinalContactsForQuestions(true)

        val stringArray = ArrayList<String>()
        if (listOfContacts.isNotEmpty()) {
            listOfContacts.forEachIndexed { index, questionForContacts ->

                if (index == listOfContacts.size - 1) {
                    stringArray.add("${questionForContacts.questionId},${questionForContacts.userName},${questionForContacts.mobileNumber}")
                } else {
                    stringArray.add("${questionForContacts.questionId},${questionForContacts.userName},${questionForContacts.mobileNumber}!!!")
                }
//                val utils = PhoneNumberUtil.getInstance()
//                try {
//                    for (region in utils.supportedRegions) {
//                        // Check whether it's a valid number.
//                        var isValid =
//                            utils.isPossibleNumber(questionForContacts.mobileNumber, region)
//                        if (isValid) {
//                            val number: PhoneNumber =
//                                utils.parse(questionForContacts.mobileNumber, region)
//                            // Check whether it's a valid number for the given region.
//                            isValid = utils.isValidNumberForRegion(number, region)
//                            if (isValid) {
//                                Log.d("####Region:", region) // IN
//                                Log.d("####CCode:", number.countryCode.toString()) // 91
//                                Log.d("####No:", number.nationalNumber.toString()) // 99xxxxxxxxxx
//
//
//                            }
//                        }
//                    }
//                } catch (e: NumberParseException) {
//                    e.printStackTrace()
//                }

            }

            viewModel.createQualityQuestionGame(stringArray.joinToString("")).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()

                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                        (fragmentContext as DashboardActivity).setHasUserPlayedGame(1)

                        //navController.popBackStack(R.id.home, false)
                        //navController.navigate(R.id.nav_friends_thoughts)


                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavFriendsThoughts(false),
                            NavOptions.Builder()
                                .setPopUpTo(navController.graph.startDestinationId, false).build()
                        )


                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
        }


    }

    override fun viewModelType(): Class<FriendCircleGameViewModel> {
        return FriendCircleGameViewModel::class.java
    }

}