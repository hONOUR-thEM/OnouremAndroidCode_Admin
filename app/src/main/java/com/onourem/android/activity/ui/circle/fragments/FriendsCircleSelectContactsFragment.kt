package com.onourem.android.activity.ui.circle.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentFriendsCircleSelectContactsBinding
import com.onourem.android.activity.models.GetQualityQuestionsResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.circle.adapters.MobileNumberAdapter
import com.onourem.android.activity.ui.circle.models.ContactItem
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random


class FriendsCircleSelectContactsFragment :
    AbstractBaseViewModelBindingFragment<FriendCircleGameViewModel, FragmentFriendsCircleSelectContactsBinding>() {

    private var response: GetQualityQuestionsResponse? = null
    private lateinit var arrayList: ArrayList<String>
    private lateinit var adapter: MobileNumberAdapter

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null


    override fun viewModelType(): Class<FriendCircleGameViewModel> {
        return FriendCircleGameViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_friends_circle_select_contacts
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (response == null) {
            getQualityQuestions()
        }

        binding.btnNext.isEnabled = false
        binding.btnNext.setBackgroundColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.gray_color_light
            )
        )

        binding.btnSelectFriends.setOnClickListener(ViewClickListener {
            navController.navigate(
                FriendsCircleSelectContactsFragmentDirections.actionNavSelectContactsToNavContactsList(
                    response!!,
                    true
                )
            )
        })

        binding.btnNext.setOnClickListener(ViewClickListener {

            MainScope().launch {
                withContext(Dispatchers.Default) {
                    //TODO("Background processing...")
                    val savedContactList =
                        (fragmentContext as DashboardActivity).getContactList()

                    if (savedContactList != null && savedContactList.size > 0) {

                        if (!FriendsCircleSelectContactsFragmentArgs.fromBundle(requireArguments()).isFromQuestionViewPger) {

                            val filteredList: ArrayList<ContactItem> =
                                savedContactList.filter { it.selected } as ArrayList<ContactItem>

                            viewModel.reset()
                            response!!.qualityQuestionList!!.forEachIndexed { index, questionItem ->
                                filteredList.forEach {
                                    viewModel.insert(
                                        QuestionForContacts(
                                            Random.nextInt().toString(),
                                            it.displayName!!,
                                            it.selectedMobileNumber,
                                            questionItem.questionId,
                                            false,
                                            index
                                        )
                                    )
                                }
                            }
                        }

                    }
                }
                //TODO("Update UI here!")
                if (FriendsCircleSelectContactsFragmentArgs.fromBundle(requireArguments()).isFromQuestionViewPger) {
                    navController.popBackStack()
                } else {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavFriendsCircleQuestionViewPager(
                            response!!,
                            false
                        )
                    )
                }


            }


        })

        binding.btnPrev.setOnClickListener(ViewClickListener {
            //Common.showCenterToast(fragmentContext, "Previous")
            if (FriendsCircleSelectContactsFragmentArgs.fromBundle(requireArguments()).isFromBubbleScreen) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavFriendsCircleAddNumber(true),
                    NavOptions.Builder()
                        .setPopUpTo(navController.graph.startDestinationId, false).build()
                )
            } else {
                navController.popBackStack()
            }

        })

        binding.btnSkip.setOnClickListener(ViewClickListener {
            navController.popBackStack(R.id.nav_home, false)
        })

        viewModel.getSelectedContacts().observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.toInt() > 0) {
                    binding.btnNext.isEnabled = true
                    binding.btnNext.setBackgroundColor(
                        ContextCompat.getColor(
                            fragmentContext,
                            R.color.good_red
                        )
                    )
                }
                binding.txtIntroSubTitle.text = "$it Selected"
            }
        }

    }

    private fun getQualityQuestions() {

        viewModel.getQualityQuestions().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetQualityQuestionsResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    response = apiResponse.body
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }


    private fun setAdapter() {
//        val layoutManager = LinearLayoutManager(activity)
//        binding.rvMobileNumbers.layoutManager = layoutManager
//        arrayList = ArrayList<String>()
//        adapter = MobileNumberAdapter(arrayList) {
//            arrayList.remove(it)
//            adapter.notifyDataSetChanged()
//            if (arrayList.size == 0){
//                binding.btnNext.isEnabled = false
//                binding.btnNext.setBackgroundColor(ContextCompat.getColor(fragmentContext, R.color.gray))
//            }
//        }
//
//        binding.rvMobileNumbers.adapter = adapter

    }


}