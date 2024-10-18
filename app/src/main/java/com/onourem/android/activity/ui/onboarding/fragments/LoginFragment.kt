package com.onourem.android.activity.ui.onboarding.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
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
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.facebook.*
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.Profile.Companion.getCurrentProfile
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogSimpleFacebookBinding
import com.onourem.android.activity.databinding.FragmentLoginBinding
import com.onourem.android.activity.models.LinkData
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.SendPushNotificationToLinkUserResponse
import com.onourem.android.activity.models.SignUpRequest
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio.fragments.runOnUiThread
import com.onourem.android.activity.ui.onboarding.OnboardingActivity
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.AppValidate
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class LoginFragment :
    AbstractBaseViewModelBindingFragment<OnboardingViewModel, FragmentLoginBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    @JvmField
    @Inject
    @Named("uniqueDeviceId")
    var uniqueDeviceId: String? = null
    private var strEmail: String? = null
    private var strPassword = ""
    private var linkUserId: String? = null
    private var linkType: String? = null
    private var isLinkVerified = "false"
    private var canAppInstalledDirectly: String? = null
    private var callbackManager: CallbackManager? = null
    private var bitmap: Bitmap? = null
    private var socialId = ""
    private var counter = 0

    override fun layoutResource(): Int {
        return R.layout.fragment_login
    }

    override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        viewModel.checkUserVerification.observe(this) {
//            if (it == "goToVerification"){
//                val gson = Gson()
//                val loginResponse = gson.fromJson(
//                    preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
//                    LoginResponse::class.java
//                )
//
//                if (loginResponse != null && !loginResponse.isRegistrationStaus) {
//                    navController.navigate(
//                        LoginFragmentDirections.actionNavLoginToNavVerification(
//                            loginResponse.emailAddress
//                        )
//                    )
//                }
//            }
//        }

        val loginFragmentArgs = LoginFragmentArgs.fromBundle(requireArguments())
        binding.tilEmail.editText!!.setText(loginFragmentArgs.emailId)
        setSignUpContainerSpan()
        viewModel.linkLiveData.observe(viewLifecycleOwner) { linkData: LinkData? ->
            if (linkData != null) {
                linkUserId = preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
                linkType = preferenceHelper!!.getString(Constants.KEY_LINK_TYPE)
                isLinkVerified = preferenceHelper!!.getString(Constants.KEY_IS_LINK_VERIFIED)
                if (AppValidate.isNotEmpty(linkUserId) && !java.lang.Boolean.parseBoolean(
                        isLinkVerified
                    )
                ) {
                    binding.tvSignUp.visibility = View.INVISIBLE
                    binding.tvInvitationLink.visibility = View.VISIBLE
                    //AlertMessage.showApplicationAlertMessage(message: "The link is not valid", typeOfAlert: "Info", controller: self)
                    setFacebookAlert(getString(R.string.invitation_link_not_valid))
                } else if (AppValidate.isNotEmpty(linkUserId) && java.lang.Boolean.parseBoolean(
                        isLinkVerified
                    ) && counter == 0
                ) {
                    binding.tvSignUp.visibility = View.VISIBLE
                    binding.tvInvitationLink.visibility = View.INVISIBLE
//                    val action = LoginFragmentDirections.actionNavLoginToNavSignUp()
                    counter += 1
                    val gson = Gson()
                    val loginResponse = gson.fromJson(
                        preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                        LoginResponse::class.java
                    )

                    if (loginResponse != null && !loginResponse.isRegistrationStaus) {
                        if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_login) {
                            val actionToVerification =
                                LoginFragmentDirections.actionNavLoginToNavVerification(loginResponse.emailAddress)
                            navController.navigate(actionToVerification)
                        }
                    }
                } else {
                    binding.tvInvitationLink.visibility = View.VISIBLE
                }
                canAppInstalledDirectly =
                    preferenceHelper!!.getString(Constants.KEY_CAN_APP_INSTALLED_DIRECTLY)
                if (!TextUtils.isEmpty(canAppInstalledDirectly)) {
                    if (canAppInstalledDirectly == "N") {
                        binding.tvSignUp.visibility = View.INVISIBLE
                        binding.tvInvitationLink.visibility = View.VISIBLE
                    } else if (canAppInstalledDirectly == "Y") {
                        binding.tvSignUp.visibility = View.VISIBLE
                        binding.tvInvitationLink.visibility = View.INVISIBLE
                    }
                }
            }
        }
        linkUserId = preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
        linkType = preferenceHelper!!.getString(Constants.KEY_LINK_TYPE)
        isLinkVerified = preferenceHelper!!.getString(Constants.KEY_IS_LINK_VERIFIED)
        if (AppValidate.isNotEmpty(linkUserId) && !java.lang.Boolean.parseBoolean(isLinkVerified)) {
            binding.tvSignUp.visibility = View.INVISIBLE
            binding.tvInvitationLink.visibility = View.VISIBLE
            //AlertMessage.showApplicationAlertMessage(message: "The link is not valid", typeOfAlert: "Info", controller: self)
            setFacebookAlert(getString(R.string.invitation_link_not_valid))
        } else if (AppValidate.isNotEmpty(linkUserId) && java.lang.Boolean.parseBoolean(
                isLinkVerified
            ) && counter == 0
        ) {
            binding.tvSignUp.visibility = View.VISIBLE
            binding.tvInvitationLink.visibility = View.INVISIBLE
            counter += 1

            val gson = Gson()
            val loginResponse = gson.fromJson(
                preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                LoginResponse::class.java
            )

            if (loginResponse != null && !loginResponse.isRegistrationStaus) {
                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_login) {
                    val actionToVerification = LoginFragmentDirections.actionNavLoginToNavVerification(loginResponse.emailAddress)
                    navController.navigate(actionToVerification)
                }
            }
//            val action = LoginFragmentDirections.actionNavLoginToNavSignUp()

        } else {
            binding.tvInvitationLink.visibility = View.VISIBLE
        }
        canAppInstalledDirectly =
            preferenceHelper!!.getString(Constants.KEY_CAN_APP_INSTALLED_DIRECTLY)
        if (!TextUtils.isEmpty(canAppInstalledDirectly)) {
            if (canAppInstalledDirectly == "N") {
                binding.tvSignUp.visibility = View.INVISIBLE
                binding.tvInvitationLink.visibility = View.VISIBLE
            } else if (canAppInstalledDirectly == "Y") {
                binding.tvSignUp.visibility = View.VISIBLE
                binding.tvInvitationLink.visibility = View.INVISIBLE
            }
        }
        binding.btnLoginGuest.setOnClickListener(ViewClickListener { v: View? ->
            preferenceHelper!!.putValue(Constants.KEY_IS_LOGGED_IN_AS_GUEST, true)
            startActivity(
                Intent(
                    requireActivity(),
                    DashboardActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            (fragmentContext as OnboardingActivity).finishAffinity()
            requireActivity().finish()
        })
        binding.btnLogin.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidData) {
                viewModel.login(strEmail!!, strPassword)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<LoginResponse> ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                hideProgress()

                                handleAdminFlow(apiResponse)

                            } else {
                                hideProgress()
                                showAlert(apiResponse.body.errorMessage)
                            }
                        } else {
                            hideProgress()
                            showAlert(apiResponse.errorMessage)
                        }
                    }
            }
        })
        binding.btnFacebook.setOnClickListener(ViewClickListener { v: View? ->
            showAlert(
                "Onourem Alert",
                "Email-id is mandatory to log into Onourem. Please use a social account which is linked with your email-id or register on Onourem with you email id."
            ) { view1: View? ->
                callbackManager = create()
                LoginManager.getInstance()
                    .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                        private var mProfileTracker: ProfileTracker? = null
                        override fun onSuccess(result: LoginResult) {
                            val request = GraphRequest.newMeRequest(
                                result.accessToken
                            ) { `object`: JSONObject?, response: GraphResponse? ->
                                try {
                                    if (response?.error != null) {
                                        setFacebookAlert("Facebook Error : " + response.error?.errorMessage)
                                        LoginManager.getInstance().logOut()
                                    } else if (getCurrentProfile() == null) {
                                        mProfileTracker = object : ProfileTracker() {
                                            override fun onCurrentProfileChanged(
                                                oldProfile: Profile?,
                                                currentProfile: Profile?
                                            ) {
                                                // profile2 is the new profile
                                                handleFacebookUserProfile(`object`, currentProfile)
                                                mProfileTracker!!.stopTracking()
                                            }
                                        }
                                        // no need to call startTracking() on mProfileTracker
                                        // because it is called by its constructor, internally.
                                        (mProfileTracker as ProfileTracker).startTracking()
                                    } else {
                                        handleFacebookUserProfile(
                                            `object`,
                                            getCurrentProfile()
                                        )
                                    }
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                    setFacebookAlert("Unexpected Facebook error occurred.")
                                    LoginManager.getInstance().logOut()
                                }
                            }
                            val parameters = Bundle()
                            parameters.putString(
                                "fields",
                                "id,name,first_name,last_name,link,gender,picture,email,verified"
                            )
                            request.parameters = parameters
                            request.executeAsync()
                        }

                        override fun onCancel() {
                            setFacebookAlert("Facebook login cancelled.")
                            LoginManager.getInstance().logOut()
                        }

                        override fun onError(error: FacebookException) {
                            setFacebookAlert("Facebook login error : " + error.localizedMessage)
                            LoginManager.getInstance().logOut()
                        }

                    })
                LoginManager.getInstance().logInWithReadPermissions(
                    this@LoginFragment,
                    callbackManager!!,
                    listOf("email", "public_profile" /*, "user_gender", "user_location"*/)
                )
            }
        })
        binding.tvForgotPassword.setOnClickListener(ViewClickListener { v: View? ->
            // NavController navController = Navigation.findNavController(requireView());
            val action = LoginFragmentDirections.actionNavLoginToNavForgot()
            if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_login) {
                navController.navigate(action)
            }
        })
    }

    private fun handleAdminFlow(apiResponse: ApiResponse<LoginResponse>) {
        if (BuildConfig.IS_ADMIN) {
            //2023 nt1 jain dev admin
            //4264 info@Onourem prod admin

            var userId = ""

            when (BuildConfig.FLAVOR) {
                "AdminDev" -> userId = "2023"
                "AdminProd" -> userId = "4264"
            }

            if (apiResponse.body?.userId == userId) {
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
                if (apiResponse.body.isRegistrationStaus) {
                    if (TextUtils.isEmpty(apiResponse.body.dateOfBirth)) {
                        navController.navigate(LoginFragmentDirections.actionNavLoginToNavDatePicker())
                    } else {
                        startActivity(
                            Intent(
                                requireActivity(),
                                DashboardActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        requireActivity().finish()
                    }
                } else {
                    navController.navigate(
                        LoginFragmentDirections.actionNavLoginToNavVerification(
                            apiResponse.body.emailAddress
                        )
                    )
                }
            } else {
                showAlert("Please login with Admin Credentials") {
                    logoutListener.onLogout()
                }
            }

        } else {
            showAlert("Please login with Admin Credentials") {
                logoutListener.onLogout()
            }
        }
    }

    private fun handleFacebookUserProfile(`object`: JSONObject?, profile: Profile?) {
        if (profile == null) {
            setFacebookAlert("Error while fetching Facebook profile. Please try again.")
            LoginManager.getInstance().logOut()
            return
        }
        if (!TextUtils.isEmpty(linkUserId) && !java.lang.Boolean.parseBoolean(isLinkVerified)) {
            linkUserId = ""
            isLinkVerified = "false"
            setFacebookAlert(getString(R.string.invitation_link_not_valid))
        }
        //        else if (canAppInstalledDirectly.equals("N") && isEmpty(linkUserId) && !Boolean.parseBoolean(isLinkVerified)) {
//        }
        showProgress()
        if (profile.getProfilePictureUri(500, 500).toString() != "") {
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.default_place_holder)
                .error(R.drawable.default_place_holder)
            Glide.with(requireActivity())
                .asBitmap()
                .load(profile.getProfilePictureUri(500, 500).toString())
                .apply(options)
                .listener(object : RequestListener<Bitmap?> {
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
                        runOnUiThread {
                            bitmap = resource
                            getFacebookLogin(`object`)
                        }
                        return true
                    }
                }).submit()
        } else {
            getFacebookLogin(`object`)
        }

    }

    private fun getFacebookLogin(`object`: JSONObject?) {
        if (!TextUtils.isEmpty(
                `object`!!.optString("email")
            ) && !TextUtils.isEmpty(`object`.optString("id"))
        ) {
            viewModel.socialLogin(
                linkUserId!!,
                "1",
                `object`.optString("email"),
                `object`.optString("id")
            ).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<LoginResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (apiResponse.body.errorMessage.equals(
                                "SocialId does not exist",
                                ignoreCase = true
                            )
                        ) {

                            // Toast.makeText(requireActivity(), "SocialId does not exist", Toast.LENGTH_SHORT).show();
                            val signUpRequest = SignUpRequest()
                            signUpRequest.firstName = `object`.optString("first_name")
                            signUpRequest.lastName = `object`.optString("last_name")
                            signUpRequest.emailAddress = `object`.optString("email")
                            signUpRequest.socialId = `object`.optString("id")
                            signUpRequest.serviceName = "registerSocial"
                            signUpRequest.screenId = "2"
                            signUpRequest.timeZone = AppUtilities.getTimeZone()
                            linkUserId =
                                preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
                            signUpRequest.linkUserId = linkUserId as String
                            val countryCode =
                                AppUtilities.getCountryCode(getApplicationContext())
                            signUpRequest.countryCode = countryCode
                            if ("male".equals(
                                    `object`.optString("gender"),
                                    ignoreCase = true
                                )
                            ) {
                                signUpRequest.gender = "Male"
                            } else if ("female".equals(
                                    `object`.optString("gender"),
                                    ignoreCase = true
                                )
                            ) {
                                signUpRequest.gender = "Female"
                            } else if ("trans".equals(
                                    `object`.optString("gender"),
                                    ignoreCase = true
                                )
                            ) {
                                signUpRequest.gender = "Trans"
                            }


                            //if (signUpRequestCO.emailAddress.length()>0 && signUpRequestCO.gender.length()>0 && signUpRequestCO.socialId.length() >0)
                            if (signUpRequest.emailAddress.isNotEmpty() && signUpRequest.socialId.isNotEmpty()) {
                                signUpRequest.profilePicture =
                                    AppUtilities.getBase64String(bitmap, 100)
                                signUpRequest.largeProfilePicture =
                                    AppUtilities.getBase64String(bitmap, 500)
                                signUpRequest.password = signUpRequest.socialId
                                signUpRequest.socialSource = "Facebook"
                                signUpRequest.deviceId = uniqueDeviceId!!
                                signUpRequest.languageId = "1"
                                if (canAppInstalledDirectly == "N") {
                                    if (!TextUtils.isEmpty(linkUserId) && java.lang.Boolean.parseBoolean(
                                            isLinkVerified
                                        )
                                    ) {
                                        registerSocial(signUpRequest)
                                    } else if (!TextUtils.isEmpty(linkUserId) && !java.lang.Boolean.parseBoolean(
                                            isLinkVerified
                                        )
                                    ) {
                                        linkUserId = ""
                                        isLinkVerified = "false"
                                        preferenceHelper!!.putValue(
                                            Constants.KEY_LINK_USER_ID,
                                            ""
                                        )
                                        preferenceHelper!!.putValue(
                                            Constants.KEY_IS_LINK_VERIFIED,
                                            "false"
                                        )
                                        setFacebookAlert(getString(R.string.invitation_link_not_valid))
                                    } else if (canAppInstalledDirectly == "N" && TextUtils.isEmpty(
                                            linkUserId
                                        ) && isLinkVerified.equals("false", ignoreCase = true)
                                    ) {
                                        setFacebookAlert("Looks like you are new here. Sorry, but currently we are an invite only community. We hope you get an invitation from an existing Onourem-er soon. Thanks for stopping by.")
                                    } else {
                                        setFacebookAlert("Looks like you are new here. Sorry, but currently we are an invite only community. We hope you get an invitation from an existing Onourem-er soon. Thanks for stopping by.")
                                    }
                                } else {
                                    registerSocial(signUpRequest)
                                }
                            }
                        } else {
                            LoginManager.getInstance().logOut()
                            socialId = `object`.optString("id")
                            apiResponse.body.socialId = socialId
                            //  Toast.makeText(requireActivity(), "socialLogin SuccessFul", Toast.LENGTH_SHORT).show();
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
                            if (apiResponse.body.isRegistrationStaus) {
                                if (TextUtils.isEmpty(apiResponse.body.dateOfBirth)) {
                                    navController.navigate(LoginFragmentDirections.actionNavLoginToNavDatePicker())
                                } else {
                                    startActivity(
                                        Intent(
                                            requireActivity(),
                                            DashboardActivity::class.java
                                        ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    )
                                    //TODO Need to remove this after Signup UI Complete.
                                    //preferenceHelper.putValue(KEY_LINK_USER_ID, "");
                                    requireActivity().finish()
                                }
                            } else {
                                navController.navigate(
                                    LoginFragmentDirections.actionNavLoginToNavVerification(
                                        apiResponse.body.emailAddress
                                    )
                                )
                            }
                        }
                    } else {
                        setFacebookAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    setFacebookAlert(apiResponse.errorMessage)
                }
            }
        } else {
            showAlert(
                "Onourem Alert",
                "Email-id is mandatory to log into Onourem. You can also use a social account with a registered email-id to register on Onourem."
            ) { view: View? -> }
        }
    }

    private fun sendPushNotificationToLinkUser(apiResponseLoginResponse: LoginResponse?) {
        viewModel.sendPushNotificationToLinkUser(linkUserId!!, "2")
            .observe(viewLifecycleOwner) { apiResponse1: ApiResponse<SendPushNotificationToLinkUserResponse> ->
                if (apiResponse1.loading) {
                    showProgress()
                } else if (apiResponse1.isSuccess && apiResponse1.body != null) {
                    hideProgress()
                    if (apiResponse1.body.errorCode.equals("000", ignoreCase = true)) {
                        linkUserId = ""
                        isLinkVerified = "false"
                        preferenceHelper!!.putValue(Constants.KEY_LINK_USER_ID, linkUserId)
                        preferenceHelper!!.putValue(Constants.KEY_LINK_TYPE, linkType)
                        preferenceHelper!!.putValue(Constants.KEY_IS_LINK_VERIFIED, isLinkVerified)
                        if (apiResponseLoginResponse!!.isRegistrationStaus) {
                            if (TextUtils.isEmpty(apiResponseLoginResponse.dateOfBirth)) {
                                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_login) {
                                    navController.navigate(LoginFragmentDirections.actionNavLoginToNavDatePicker())
                                }
                            } else {
                                startActivity(
                                    Intent(
                                        requireActivity(),
                                        DashboardActivity::class.java
                                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                                //TODO Need to remove this after Signup UI Complete.
                                //preferenceHelper.putValue(KEY_LINK_USER_ID, "");
                                requireActivity().finish()
                            }
                        } else {
                            navController.navigate(
                                LoginFragmentDirections.actionNavLoginToNavVerification(
                                    apiResponseLoginResponse.emailAddress
                                )
                            )
                        }

//                    navController.popBackStack();
                        //showAlert(message);
                    } else {
                        setFacebookAlert(apiResponse1.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    setFacebookAlert(apiResponse1.errorMessage)
                }
            }
    }

    private fun registerSocial(signUpRequest: SignUpRequest) {
        // Toast.makeText(requireActivity(), "Register Service", Toast.LENGTH_SHORT).show();
        viewModel.registerSocial(signUpRequest)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<LoginResponse> ->
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
                        sendPushNotificationToLinkUser(apiResponse.body)
                    } else {
                        setFacebookAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    setFacebookAlert(apiResponse.errorMessage)
                }
            }
    }

//    fun openActivityForResult() {
//        val intent = Intent(this, SomeActivity::class.java)
//        resultLauncher.launch(intent)
//    }
//
//    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // There are no request codes
//            val data: Intent? = result.data
//
//        }
//        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
//            callbackManager?.onActivityResult(result.resultCode, result.resultCode, result.data)
//        }
//    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode != Activity.RESULT_CANCELED && data != null) {
//            callbackManager!!.onActivityResult(requestCode, resultCode, data)
//        }
//        //        super.onActivityResult(requestCode, resultCode, data);
//    }

    private val isValidData: Boolean
        get() {
            var isValidData = true
            strEmail = binding.tilEmail.editText!!.text.toString()
                .trim { it <= ' ' }
            strPassword = binding.tilPassword.editText!!.text.toString()
                .trim { it <= ' ' }
            if (TextUtils.isEmpty(binding.tilEmail.editText!!.text)) {
                setFacebookAlert("Please enter valid email Id.")
                return false
            } else if (!AppValidate.isValidEmail(strEmail)) {
                setFacebookAlert("Please enter valid email Id.")
                isValidData = false
            } else if (TextUtils.isEmpty(
                    binding.tilPassword.editText!!.text.toString().trim { it <= ' ' })
            ) {
                setFacebookAlert("Please enter valid password.")
                isValidData = false
            } else if (strPassword.length < 5 || strPassword.length > 20) {
                setFacebookAlert("Password must be between 5-20 characters")
                isValidData = false
            }
            return isValidData
        }

    private fun setSignUpContainerSpan() {
        // Initialize a new SpannableStringBuilder instance
        val signup = getString(R.string.label_sign_up)
        val text = getString(R.string.label_dont_have_account) + " " + signup
        val spannableStringBuilder = SpannableStringBuilder(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) { // override updateDrawState
                ds.isUnderlineText = false // set to false to remove underline
            }

            override fun onClick(view: View) {
                val action = LoginFragmentDirections.actionNavLoginToNavSignUp()
                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_login) {
                    navController.navigate(action)
                }
            }
        }
        val end = text.indexOf(signup) + signup.length
        spannableStringBuilder.setSpan(
            clickableSpan,  // Span to add
            text.indexOf(signup),  // Start of the span (inclusive)
            end,  // End of the span (exclusive)
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        )
        val font = TypefaceCompat.create(
            requireActivity(),
            ResourcesCompat.getFont(requireActivity(), R.font.montserrat_semibold),
            Typeface.BOLD
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
            text.indexOf(signup),  // Start of the span (inclusive)
            end,  // End of the span (exclusive)
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        )
        val foregroundColorSpan = ForegroundColorSpan(Color.rgb(243, 135, 21))
        spannableStringBuilder.setSpan(
            foregroundColorSpan,  // Span to add
            text.indexOf(signup),  // Start of the span (inclusive)
            end,  // End of the span (exclusive)
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        )
        binding.tvSignUp.movementMethod = LinkMovementMethod.getInstance()
        binding.tvSignUp.text = spannableStringBuilder
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setFacebookAlert(message: String?) {
        val builder = AlertDialog.Builder(fragmentContext)
        val binding = DialogSimpleFacebookBinding.inflate(LayoutInflater.from(fragmentContext))
        builder.setView(binding.root)
        builder.setCancelable(true)
        val alertDialog = builder.create()
        binding.tvDialogMessage.text = message
        binding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? -> alertDialog.dismiss() })
        alertDialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }
}