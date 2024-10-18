package com.onourem.android.activity.ui.onboarding.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import com.facebook.FacebookSdk.getApplicationContext
import com.google.gson.Gson
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentSignUpBinding
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.SignUpRequest
import com.onourem.android.activity.models.SignUpResponse
import com.onourem.android.activity.models.WebContent
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.AppValidate
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class SignUpFragment :
    AbstractBaseViewModelBindingFragment<OnboardingViewModel, FragmentSignUpBinding>() {
    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var deviceId: String? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var isLinkVerified = "false"
    private var linkUserId: String? = null
    private var adapter: ArrayAdapter<String>? = null
    private var adapterProfession: ArrayAdapter<String>? = null
    private var linkType = ""
    override fun layoutResource(): Int {
        return R.layout.fragment_sign_up
    }

    override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inIt()
        linkUserId = preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
        linkType = preferenceHelper!!.getString(Constants.KEY_LINK_TYPE)
        isLinkVerified = preferenceHelper!!.getString(Constants.KEY_IS_LINK_VERIFIED)
        binding.tvAlreadyHaveAccount.setOnClickListener(ViewClickListener { v: View? ->
            // NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack()
        })
        AppUtilities.disableEmojis(Objects.requireNonNull(binding.tilFirstName.editText))
        AppUtilities.disableEmojis(Objects.requireNonNull(binding.tilLastName.editText))
        binding.btnSignUp.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidData) {
                val signUpRequest = SignUpRequest()
                signUpRequest.firstName =
                    binding.tilFirstName.editText!!.text.toString().trim { it <= ' ' }
                signUpRequest.lastName =
                    binding.tilLastName.editText!!.text.toString().trim { it <= ' ' }
                signUpRequest.emailAddress =
                    binding.tilEmail.editText!!.text.toString()
                signUpRequest.password =
                    binding.tilPassword.editText!!.text.toString()
                signUpRequest.gender = binding.tilSpinner.text.toString()
                signUpRequest.profession = binding.tilSpinnerProf.text.toString()
                signUpRequest.screenId = "2"
                signUpRequest.serviceName = "register"
                signUpRequest.languageId = "1"
                signUpRequest.deviceId = deviceId ?: ""
                signUpRequest.linkUserId = linkUserId ?: ""
                signUpRequest.timeZone = AppUtilities.getTimeZone()
                val countryCode = AppUtilities.getCountryCode(getApplicationContext())
                signUpRequest.countryCode = countryCode
                viewModel.signUp(signUpRequest)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<SignUpResponse> ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                preferenceHelper!!.putValue(
                                    Constants.KEY_AUTH_TOKEN,
                                    apiResponse.body.token
                                )
                                preferenceHelper!!.putValue(
                                    Constants.KEY_LOGGED_IN_USER_ID,
                                    apiResponse.body.userId
                                )
                                preferenceHelper!!.putValue(Constants.KEY_IS_LOGGED_IN, true)
                                preferenceHelper!!.putValue(
                                    Constants.KEY_USER_OBJECT,
                                    Gson().toJson(apiResponse.body)
                                )
                                navController.navigate(
                                    SignUpFragmentDirections.actionNavSignUpToNavVerification(
                                        apiResponse.body.emailAddress
                                    )
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
        })

//        viewModel.checkUserVerification.observe(this) {
//            if (it != "goToVerification"){
//                val gson = Gson()
//                val loginResponse = gson.fromJson(
//                    preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
//                    LoginResponse::class.java
//                )
//
//                if (loginResponse != null && !loginResponse.isRegistrationStaus) {
//                    navController.navigate(
//                        SignUpFragmentDirections.actionNavSignUpToNavVerification(
//                            loginResponse.emailAddress
//                        )
//                    )
//                }
//            }
//        }
    }

    private fun inIt() {
        setTermsConditionsSpan()
        setLoginContainerSpan()
        viewModel.setLinkLiveData(null)
        if (adapter == null) {
            adapter = ArrayAdapter(
                requireActivity(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.gender)
            )
        }

        if (adapterProfession == null) {
            adapterProfession = ArrayAdapter(
                requireActivity(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.profession)
            )
        }
        binding.tilSpinner.setAdapter(adapter)
        binding.tilSpinnerProf.setAdapter(adapterProfession)
    }

    private val isValidData: Boolean
        get() {
            val fName = binding.tilFirstName.editText!!.text.toString()
                .trim { it <= ' ' }
            if (TextUtils.isEmpty(fName) || AppUtilities.hasSpecialChars(fName)) {
                showAlert("Please enter valid First Name.")
                return false
            } else {
                if (binding.tilFirstName.editText!!
                        .text.isEmpty() || binding.tilFirstName.editText!!.text.length > 30
                ) {
                    showAlert("Please enter valid First Name of minimum 1 and Max 30 Characters")
                    return false
                }
            }
            val lName = binding.tilLastName.editText!!.text.toString()
                .trim { it <= ' ' }
            if (TextUtils.isEmpty(lName) || AppUtilities.hasSpecialChars(fName)) {
                showAlert("Please enter valid Last Name.")
                return false
            } else {
                if (binding.tilLastName.editText!!
                        .text.toString()
                        .trim { it <= ' ' }.isEmpty() || binding.tilLastName.editText!!
                        .text.length > 30
                ) {
                    showAlert("Please enter valid Last Name of minimum 1 and Max 30 Characters")
                    return false
                }
            }
            if (binding.tilSpinner.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlert("Please select gender.")
                return false
            }
//            if (binding.tilSpinnerProf.text.toString().trim { it <= ' ' }.isEmpty()) {
//                showAlert("Please select profession.")
//                return false
//            }
            if (TextUtils.isEmpty(
                    binding.tilEmail.editText!!.text.toString()
                        .trim { it <= ' ' })
            ) {
                showAlert("Please enter email Id.")
                return false
            } else if (!AppValidate.isValidEmail(
                    binding.tilEmail.editText!!.text.toString().lowercase(
                        Locale.getDefault()
                    ).trim { it <= ' ' })
            ) {
                showAlert("Please enter valid email Id.")
                return false
            }
            if (TextUtils.isEmpty(
                    binding.tilPassword.editText!!.text.toString()
                        .trim { it <= ' ' })
            ) {
                showAlert("Please enter password.")
                return false
            }
            if (binding.tilPassword.editText!!.text.toString()
                    .trim { it <= ' ' } != binding.tilRePassword.editText!!.text.toString()
                    .trim { it <= ' ' }
            ) {
                showAlert("Re-Password does not match with Password.")
                return false
            }
            return true
        }

    private fun setTermsConditionsSpan() {
        val termsConditions = getString(R.string.label_terms_and_conditions)
        val text = getString(R.string.label_by_signing_up_you_agree_to) + " " + termsConditions
        val spannableStringBuilder = SpannableStringBuilder(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) { // override updateDrawState
                ds.isUnderlineText = false // set to false to remove underline
            }

            override fun onClick(view: View) {

                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_sign_up) {
                    val webContent = WebContent()
                    webContent.property = ""
                    webContent.screenId = "38"
                    webContent.tnc = "TCPP"
                    webContent.title = getString(R.string.label_terms_and_conditions)
                    navController.navigate(
                        SignUpFragmentDirections.actionNavSignUpToNavWebContent(
                            webContent
                        )
                    )
                }


            }
        }
        val end = text.indexOf(termsConditions) + termsConditions.length
        spannableStringBuilder.setSpan(
            clickableSpan,  // Span to add
            text.indexOf(termsConditions),  // Start of the span (inclusive)
            end,  // End of the span (exclusive)
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        )
        val font = TypefaceCompat.create(
            requireActivity(), ResourcesCompat.getFont(
                requireActivity(), R.font.montserrat_semibold
            ), Typeface.BOLD
        )
        val typefaceSpan: MetricAffectingSpan = object : MetricAffectingSpan() {
            override fun updateDrawState(tp: TextPaint) {
                tp.typeface = font
            }

            override fun updateMeasureState(textPaint: TextPaint) {
                textPaint.typeface = font
            }
        }
        spannableStringBuilder.setSpan(
            typefaceSpan,  // Span to add
            text.indexOf(termsConditions),  // Start of the span (inclusive)
            end,  // End of the span (exclusive)
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        )
        val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.colorAccent))
        spannableStringBuilder.setSpan(
            foregroundColorSpan,  // Span to add
            text.indexOf(termsConditions),  // Start of the span (inclusive)
            end,  // End of the span (exclusive)
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        )
        binding.suTvTermsAndConditionsLabel.movementMethod = LinkMovementMethod.getInstance()
        binding.suTvTermsAndConditionsLabel.text = spannableStringBuilder
    }

    private fun setLoginContainerSpan() {
        // Initialize a new SpannableStringBuilder instance
        val login = getString(R.string.label_log_in)
        val text = getString(R.string.label_already_have_an_account) + " " + login
        val spannableStringBuilder = SpannableStringBuilder(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) { // override updateDrawState
                ds.isUnderlineText = false // set to false to remove underline
            }

            override fun onClick(view: View) {

                //login
            }
        }
        val end = text.indexOf(login) + login.length
        spannableStringBuilder.setSpan(
            clickableSpan,  // Span to add
            text.indexOf(login),  // Start of the span (inclusive)
            end,  // End of the span (exclusive)
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        )
        val font = TypefaceCompat.create(
            requireActivity(), ResourcesCompat.getFont(
                requireActivity(), R.font.montserrat_semibold
            ), Typeface.BOLD
        )
        if (font != null) {
            val typefaceSpan: MetricAffectingSpan = object : MetricAffectingSpan() {
                override fun updateDrawState(tp: TextPaint) {
                    tp.typeface = font
                }

                override fun updateMeasureState(textPaint: TextPaint) {
                    textPaint.typeface = font
                }
            }
            spannableStringBuilder.setSpan(
                typefaceSpan,  // Span to add
                text.indexOf(login),  // Start of the span (inclusive)
                end,  // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
            )
        }
        val foregroundColorSpan = ForegroundColorSpan(resources.getColor(R.color.colorAccent))
        spannableStringBuilder.setSpan(
            foregroundColorSpan,  // Span to add
            text.indexOf(login),  // Start of the span (inclusive)
            end,  // End of the span (exclusive)
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        )
        binding.tvAlreadyHaveAccount.movementMethod = LinkMovementMethod.getInstance()
        binding.tvAlreadyHaveAccount.text = spannableStringBuilder
    }
}