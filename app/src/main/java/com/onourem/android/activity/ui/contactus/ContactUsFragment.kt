package com.onourem.android.activity.ui.contactus

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentContactUsBinding
import com.onourem.android.activity.models.SendMessageResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.message.ConversationsViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Events.getConversationAdminUser
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import javax.inject.Inject

class ContactUsFragment :
    AbstractBaseViewModelBindingFragment<ContactUsViewModel, FragmentContactUsBinding>() {

    private lateinit var conversationsViewModel: ConversationsViewModel

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    private var loginUserId: String? = null
    var actionToPerform = ""
    override fun layoutResource(): Int {
        return R.layout.fragment_contact_us
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        conversationsViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[ConversationsViewModel::class.java]

    }
    override fun viewModelType(): Class<ContactUsViewModel> {
        return ContactUsViewModel::class.java
    }


    private fun sendMessage(text: String?) {
        val mConversation = getConversationAdminUser(loginUserId)

//        var otherUserId: String? = ""
//        if (!Objects.requireNonNull(mConversation.userTwo)
//                .equals(loginUserId, ignoreCase = true)
//        ) {
//            otherUserId = mConversation.userTwo
//        }
//        else if (!Objects.requireNonNull(mConversation.userOne)
//                .equals(loginUserId, ignoreCase = true)
//        ) {
//            otherUserId = mConversation.userOne
//        }

        conversationsViewModel.postMessage(mConversation.id!!, mConversation.userOne!!, Base64Utility.encodeToString(text)!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<SendMessageResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                }else if (apiResponse.body != null && apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    showAlert("Your message has been sent successfully") { v1: View? -> navController.popBackStack() }
                    hideProgress()
                } else {
                    hideProgress()
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contactUsFragmentArgs = ContactUsFragmentArgs.fromBundle(
            requireArguments()
        )
        loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)

        actionToPerform = contactUsFragmentArgs.actionToPerform
        if (actionToPerform.equals("ContactUs", ignoreCase = true)) {
            binding.editText.hint = "Enter your message here"
        } else if (actionToPerform.equals("ReportProblem", ignoreCase = true)) {
            binding.editText.hint = "Describe the issue you are facing here"
        }
        binding.btnSubmit.setOnClickListener(ViewClickListener { v: View? ->
            if (!TextUtils.isEmpty(
                    binding.editText.editText!!.text.toString().trim { it <= ' ' })
            ) {
                viewModel.contactUs(
                    binding.editText.editText!!.text.toString(),
                    actionToPerform
                ).observe(
                    viewLifecycleOwner
                ) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                            sendMessage(binding.editText.editText!!.text.toString())
                            showAlert("Your message has been sent successfully") { v1: View? -> navController.popBackStack() }
                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                        if (apiResponse.errorMessage != null
                            && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "contactUs")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "contactUs",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
            } else {
                showAlert("Please tell us more about the problem.")
            }
        })
    }
}