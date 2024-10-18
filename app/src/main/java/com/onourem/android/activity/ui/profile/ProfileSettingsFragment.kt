package com.onourem.android.activity.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.*
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import javax.inject.Inject

class ProfileSettingsFragment :
    AbstractBaseViewModelBindingFragment<ProfileViewModel, FragmentProfileSettingsBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var countryList: CountryList? = null
    private var stateList: StateList? = null
    private var cityList: CityList? = null
    private var loginResponse: LoginResponse? = null
    private var updateProfileRequest: UpdateProfileRequest? = null
    private var userReferenceLists: MutableList<UserReferenceList>? = null
    private var loginResponseTemp: LoginResponse? = null
    override fun viewModelType(): Class<ProfileViewModel> {
        return ProfileViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_profile_settings
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemDecoration =
            DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(
            AppCompatResources.getDrawable(
                fragmentContext, R.drawable.divider
            )!!
        )
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addItemDecoration(itemDecoration)
        layoutManager.orientation = RecyclerView.VERTICAL
        loginResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
            LoginResponse::class.java
        )
        updateProfileRequest = UpdateProfileRequest()
        userReferenceLists = ArrayList()
        updateProfileRequest!!.firstName = loginResponse!!.firstName
        updateProfileRequest!!.lastName = loginResponse!!.lastName
        updateProfileRequest!!.gender = loginResponse!!.gender
        updateProfileRequest!!.emailAddress = loginResponse!!.emailAddress
        //        updateProfileRequest.setRefCode("");
        updateProfileRequest!!.screenId = "14"
        updateProfileRequest!!.serviceName = "updateProfile"
        updateProfileRequest!!.countryId = ""
        updateProfileRequest!!.stateId = ""
        updateProfileRequest!!.password = ""
        updateProfileRequest!!.oldPassword = ""
        updateProfileRequest!!.cityId = loginResponse!!.cityId
        setUserProfileData()
        val profileData = UserProfileData()
        profileData.userProfilePicture = loginResponse!!.largeProfilePicture
        profileData.coverLargePicture = loginResponse!!.largeCoverPicture
        profileData.loginUserId = loginResponse!!.userId
        binding.cardProfileChange.setOnClickListener(ViewClickListener { v: View? ->
            if (loginResponse != null) navController.navigate(
                ProfileSettingsFragmentDirections.actionNavProfileSettingsToNavProfileImageEditor(
                    ProfileImageEditorFragment.TYPE_PROFILE_IMAGE,
                    profileData
                )
            )
        })
        binding.cardCoverChange.setOnClickListener(ViewClickListener { v: View? ->
            if (loginResponse != null) navController.navigate(
                ProfileSettingsFragmentDirections.actionNavProfileSettingsToNavProfileImageEditor(
                    ProfileImageEditorFragment.TYPE_COVER_IMAGE,
                    profileData
                )
            )
        })
        setAdapter()
    }

    private fun setAdapter() {
        loginResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
            LoginResponse::class.java
        )
        val profileItems = ArrayList<ProfileItem>()
        profileItems.add(
            ProfileItem(
                getString(R.string.label_name),
                loginResponse!!.firstName + " " + loginResponse!!.lastName,
                1
            )
        )
        profileItems.add(
            ProfileItem(
                getString(R.string.label_email),
                loginResponse!!.emailAddress,
                2
            )
        )
        profileItems.add(
            ProfileItem(
                getString(R.string.label_gender),
                loginResponse!!.gender,
                3
            )
        )
//        profileItems.add(
//            ProfileItem(
//                getString(R.string.label_profession),
//                loginResponse!!.profession,
//                4
//            )
//        )
        profileItems.add(ProfileItem(getString(R.string.label_password), "******", 4))
//        profileItems.add(
//            ProfileItem(
//                getString(R.string.label_location),
//                loginResponse!!.geoName,
//                5
//            )
//        )
        profileItems.add(
            ProfileItem(
                getString(R.string.label_about_onourem),
                loginResponse!!.refName,
                6
            )
        )
        val profileSettingsAdapter = ProfileSettingsAdapter(profileItems) { item: ProfileItem ->
            if (item.title.equals(getString(R.string.label_name), ignoreCase = true)) {
                setName(binding.root)
            } else if (item.title.equals(getString(R.string.label_email), ignoreCase = true)) {
                val snackbar = Snackbar.make(
                    binding.root,
                    "You can not change email-id.",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()
            } else if (item.title.equals(getString(R.string.label_gender), ignoreCase = true)) {
                setGender(binding.root)
            }
//            else if (item.title.equals(getString(R.string.label_profession), ignoreCase = true)) {
//                setProfession(binding.root)
//            }
            else if (item.title.equals(getString(R.string.label_password), ignoreCase = true)) {
                if (!TextUtils.isEmpty(loginResponse!!.socialId)) {
                    showAlert("You are logged in through Facebook. Log in through your email to reset your password.")
                } else {
                    setPassword(binding.root)
                }
            } else if (item.title.equals(getString(R.string.label_location), ignoreCase = true)) {
                //setLocation(binding.root)
            } else if (item.title.equals(
                    getString(R.string.label_about_onourem),
                    ignoreCase = true
                )
            ) {
                setAboutOnourem(binding.root)
            }
        }
        binding.recyclerView.adapter = profileSettingsAdapter
    }

    private fun setUserProfileData() {
        val options: BaseRequestOptions<*> = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)
        val coverOptions: BaseRequestOptions<*> = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder)
        Glide.with(requireActivity())
            .load(loginResponse!!.largeProfilePicture)
            .apply(options)
            .into(binding.ivProfileImage)
        Glide.with(requireActivity())
            .load(loginResponse!!.largeCoverPicture)
            .apply(coverOptions)
            .into(binding.ivCoverImage)
        Utilities.verifiedUserType(
            binding.root.context,
            loginResponse!!.userTypeId,
            binding.ivIconVerified
        )
    }

    private fun updateProfile(alertDialog: AlertDialog, from: String) {
        loginResponseTemp = LoginResponse()
        loginResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
            LoginResponse::class.java
        )
        viewModel.updateProfile(updateProfileRequest!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<LoginResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        loginResponseTemp = apiResponse.body
                        when (from) {
                            "name" -> {
                                loginResponse!!.firstName = loginResponseTemp!!.firstName
                                loginResponse!!.lastName = loginResponseTemp!!.lastName
                            }
                            "gender" -> loginResponse!!.gender = loginResponseTemp!!.gender
                            "profession" -> loginResponse!!.profession = loginResponseTemp!!.profession
                            "location" -> {
                                loginResponse!!.city = loginResponseTemp!!.city
                                loginResponse!!.cityId = loginResponseTemp!!.cityId
                                loginResponse!!.state = loginResponseTemp!!.state
                                loginResponse!!.country = loginResponseTemp!!.country
                                loginResponse!!.geoName = loginResponseTemp!!.geoName
                            }
                        }
                        preferenceHelper!!.putValue(
                            Constants.KEY_USER_OBJECT,
                            Gson().toJson(loginResponse)
                        )
                        setAdapter()
                        alertDialog.dismiss()
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
                            AppUtilities.showLog("Network Error", "updateProfile")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "updateProfile",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun validatePassword(alertDialog: AlertDialog) {
        viewModel.validatePassword(updateProfileRequest!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<LoginResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        alertDialog.dismiss()
                        val snackbar = Snackbar.make(
                            binding.root,
                            "Password has been successfully changed.",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.setTextColor(Color.WHITE)
                        snackbar.show()
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
                            AppUtilities.showLog("Network Error", "validatePassword")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "validatePassword",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setName(anchorView: View) {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogBinding =
            DialogProfileSettingsNameBinding.inflate(LayoutInflater.from(anchorView.context))
        builder.setView(dialogBinding.root)
        builder.setCancelable(true)
        val alertDialog = builder.create()


        dialogBinding.tilFirstName.editText!!.setText(
            loginResponse!!.firstName
        )

        dialogBinding.tilLastName.editText!!.setText(
            loginResponse!!.lastName
        )

        AppUtilities.disableEmojis(dialogBinding.tilFirstName.editText)
        AppUtilities.disableEmojis(dialogBinding.tilLastName.editText)
        dialogBinding.tilFirstName.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s) || AppUtilities.hasSpecialChars(s.toString())) {
                    dialogBinding.tilFirstName.error = "Please enter valid First Name."
                } else {
                    dialogBinding.tilFirstName.error = null
                    if (s.length < 1 || s.length > 30) {
                        dialogBinding.tilFirstName.error =
                            "Please enter valid First Name of minimum 1 and Max 30 Characters"
                    } else {
                        dialogBinding.tilFirstName.error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        dialogBinding.tilLastName.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s) || AppUtilities.hasSpecialChars(s.toString())) {
                    dialogBinding.tilLastName.error = "Please enter valid Last Name."
                } else {
                    dialogBinding.tilLastName.error = null
                    if (s.length < 1 || s.length > 30) {
                        dialogBinding.tilLastName.error =
                            "Please enter valid Last Name of minimum 1 and Max 30 Characters"
                    } else {
                        dialogBinding.tilLastName.error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        dialogBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? ->
            if (!TextUtils.isEmpty(
                    dialogBinding.tilFirstName.editText!!.text.toString()
                        .trim { it <= ' ' }) && !TextUtils.isEmpty(
                    dialogBinding.tilLastName.editText!!.text.toString().trim { it <= ' ' })
            ) {
                if (isValidNameData(dialogBinding)) {
                    updateProfileRequest!!.firstName =
                        dialogBinding.tilFirstName.editText!!.text.toString()
                    updateProfileRequest!!.lastName =
                        dialogBinding.tilLastName.editText!!.text.toString()
                    if (dialogBinding.tilFirstName.editText!!.text.toString().trim { it <= ' ' }
                            .equals(
                                loginResponse!!.firstName, ignoreCase = true
                            )
                        && dialogBinding.tilLastName.editText!!.text.toString().trim { it <= ' ' }
                            .equals(
                                loginResponse!!.lastName, ignoreCase = true
                            )
                    ) {
                        alertDialog.dismiss()
                        Snackbar.make(
                            dialogBinding.root,
                            "Seems you tried to change to same First Name and Last Name",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        updateProfile(alertDialog, "name")
                    }
                }
            }
        })
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setGender(anchorView: View) {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogBinding =
            DialogProfileSettingsGenderBinding.inflate(LayoutInflater.from(anchorView.context))
        builder.setView(dialogBinding.root)
        builder.setCancelable(true)
        val alertDialog = builder.create()
        dialogBinding.tilSpinner.setText(loginResponse!!.gender)
        val adapter = ArrayAdapter(
            requireActivity(),
            R.layout.dropdown_menu_popup_item,
            resources.getStringArray(R.array.gender)
        )
        dialogBinding.tilSpinner.setAdapter(adapter)
        dialogBinding.tilSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                updateProfileRequest!!.gender = dialogBinding.tilSpinner.text.toString()
                updateProfile(alertDialog, "gender")
            }
        dialogBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? -> alertDialog.dismiss() })
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setProfession(anchorView: View) {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogBinding =
            DialogProfileSettingsProfessionBinding.inflate(LayoutInflater.from(anchorView.context))
        builder.setView(dialogBinding.root)
        builder.setCancelable(true)
        val alertDialog = builder.create()
        dialogBinding.tilSpinner.setText(loginResponse!!.profession)
        val adapter = ArrayAdapter(
            requireActivity(),
            R.layout.dropdown_menu_popup_item,
            resources.getStringArray(R.array.profession)
        )
        dialogBinding.tilSpinner.setAdapter(adapter)
        dialogBinding.tilSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                updateProfileRequest!!.profession = dialogBinding.tilSpinner.text.toString()
                updateProfile(alertDialog, "profession")
            }
        dialogBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? -> alertDialog.dismiss() })
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPassword(anchorView: View) {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogBinding =
            DialogProfileSettingsPasswordBinding.inflate(LayoutInflater.from(anchorView.context))
        builder.setView(dialogBinding.root)
        builder.setCancelable(true)
        val alertDialog = builder.create()
        dialogBinding.tilOldPassword.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s)) {
                    dialogBinding.tilOldPassword.error = "Please enter old password."
                } else {
                    dialogBinding.tilOldPassword.error = null
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        dialogBinding.tilPassword.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s)) {
                    dialogBinding.tilPassword.error = "Please enter new password."
                } else {
                    dialogBinding.tilPassword.error = null
                    if (s.length < 7) {
                        dialogBinding.tilPassword.error =
                            "Please enter password minimum of 7 Characters"
                    } else {
                        dialogBinding.tilPassword.error = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        dialogBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? ->
            if (isValidPasswordData(dialogBinding)) {
                updateProfileRequest!!.password =
                    dialogBinding.tilPassword.editText!!.text.toString()
                updateProfileRequest!!.oldPassword =
                    dialogBinding.tilOldPassword.editText!!.text.toString()
                validatePassword(alertDialog)
            }
        })
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setLocation(anchorView: View) {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogBinding =
            DialogProfileSettingsLocationBinding.inflate(LayoutInflater.from(anchorView.context))
        builder.setView(dialogBinding.root)
        builder.setCancelable(true)
        val alertDialog = builder.create()
        val countryLists = ArrayList<CountryList>()
        val stateLists = ArrayList<StateList>()
        val cityLists = ArrayList<CityList>()
        dialogBinding.tilSpinnerStateInput.isEnabled = false
        dialogBinding.tilSpinnerCityInput.isEnabled = false
        viewModel.countryList.observe(viewLifecycleOwner) { getGeoListApiResponse: ApiResponse<GetGeoList> ->
            if (getGeoListApiResponse.body != null) {
                countryLists.addAll(getGeoListApiResponse.body.countryList!!)
            }
        }
        val adapterCountry =
            ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, countryLists)
        dialogBinding.tilSpinner.setAdapter(adapterCountry)
        dialogBinding.tilSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
                stateLists.clear()
                cityLists.clear()
                dialogBinding.tilSpinnerState.setText("")
                dialogBinding.tilSpinnerCity.setText("")
                dialogBinding.tilSpinnerInput.isEnabled = false
                countryList = parent.adapter.getItem(position) as CountryList
                viewModel.getStateList(countryList!!.id.toString())
                    .observe(viewLifecycleOwner) { getGeoListApiResponse: ApiResponse<GetGeoList> ->
                        if (getGeoListApiResponse.body != null) {
                            dialogBinding.tilSpinnerStateInput.isEnabled = true
                            stateLists.addAll(getGeoListApiResponse.body.stateList!!)
                        }
                    }
                val adapterState =
                    ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, stateLists)
                dialogBinding.tilSpinnerState.setAdapter(adapterState)
            }
        dialogBinding.tilSpinnerState.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
                cityLists.clear()
                dialogBinding.tilSpinnerCity.setText("")
                dialogBinding.tilSpinnerStateInput.isEnabled = false
                stateList = parent.adapter.getItem(position) as StateList
                viewModel.getCityList(stateList!!.id.toString())
                    .observe(viewLifecycleOwner) { getGeoListApiResponse: ApiResponse<GetGeoList> ->
                        dialogBinding.tilSpinnerCityInput.isEnabled = true
                        if (getGeoListApiResponse.body != null) {
                            cityLists.addAll(getGeoListApiResponse.body.cityList!!)
                        }
                    }
                val adapterCity =
                    ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, cityLists)
                dialogBinding.tilSpinnerCity.setAdapter(adapterCity)
            }
        dialogBinding.tilSpinnerCity.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
                cityList = parent.adapter.getItem(position) as CityList
                dialogBinding.tilSpinnerCityInput.isEnabled = false
            }
        dialogBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? ->
            var countryId = -1
            var stateId = -1
            var cityId = -1
            if (countryList != null) {
                countryId = countryList!!.id!!
                updateProfileRequest!!.countryId = countryList!!.id.toString()
            }
            if (stateList != null) {
                stateId = stateList!!.id!!
                updateProfileRequest!!.stateId = stateList!!.id.toString()
            }
            if (cityList != null) {
                cityId = cityList!!.id!!
                updateProfileRequest!!.cityId = cityList!!.id.toString()
            }
            if (!TextUtils.isEmpty(
                    dialogBinding.tilSpinner.text.toString()
                        .trim { it <= ' ' }) && countryId != -1 && !TextUtils.isEmpty(
                    dialogBinding.tilSpinnerState.text.toString()
                        .trim { it <= ' ' }) && stateId != -1 && !TextUtils.isEmpty(
                    dialogBinding.tilSpinnerCity.text.toString().trim { it <= ' ' }) && cityId != -1
            ) {
                updateProfile(alertDialog, "location")
            } else {
                Toast.makeText(
                    dialogBinding.root.context,
                    "Enter location details to update.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        dialogBinding.ibCountryClear.setOnClickListener(ViewClickListener { view12: View? ->
            dialogBinding.tilSpinner.setText("")
            dialogBinding.tilSpinnerState.setText("")
            dialogBinding.tilSpinnerCity.setText("")
            dialogBinding.tilSpinnerInput.isEnabled = true
            dialogBinding.tilSpinnerStateInput.isEnabled = false
            dialogBinding.tilSpinnerCityInput.isEnabled = false
            stateLists.clear()
            cityLists.clear()
        })
        dialogBinding.ibStateClear.setOnClickListener(ViewClickListener { view12: View? ->
            dialogBinding.tilSpinnerState.setText("")
            dialogBinding.tilSpinnerCity.setText("")
            var isSelected = false
            for (countryList in countryLists) {
                if (countryList.cName.equals(
                        dialogBinding.tilSpinner.text.toString().trim { it <= ' ' },
                        ignoreCase = true
                    )
                ) {
                    isSelected = true
                    break
                }
            }
            if (countryLists.size > 0 && isSelected) {
                dialogBinding.tilSpinnerStateInput.isEnabled = true
            }
            cityLists.clear()
            dialogBinding.tilSpinnerCityInput.isEnabled = false
        })
        dialogBinding.ibCityClear.setOnClickListener(ViewClickListener { view12: View? ->
            dialogBinding.tilSpinnerCity.setText("")
            var isSelected = false
            for (stateList in stateLists) {
                if (stateList.stateName.equals(
                        dialogBinding.tilSpinnerState.text.toString().trim { it <= ' ' },
                        ignoreCase = true
                    )
                ) {
                    isSelected = true
                    break
                }
            }
            if (stateLists.size > 0 && isSelected) {
                dialogBinding.tilSpinnerCityInput.isEnabled = true
            }
        })
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setAboutOnourem(anchorView: View) {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogBinding =
            DialogProfileSettingsAboutOnouremBinding.inflate(LayoutInflater.from(anchorView.context))
        builder.setView(dialogBinding.root)
        builder.setCancelable(true)
        val alertDialog = builder.create()
        dialogBinding.tilSpinner.setText(loginResponse!!.refName)
        val adapter =
            ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, userReferenceLists!!)
        if (userReferenceLists!!.size == 0) {
            viewModel.userReferenceList.observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetUserReferenceList> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        userReferenceLists!!.addAll(apiResponse.body.userReferenceList!!)
                        adapter.notifyDataSetChanged()
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
                            AppUtilities.showLog("Network Error", "getUserReferenceList")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getUserReferenceList",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
        }
        dialogBinding.tilSpinner.setAdapter(adapter)
        dialogBinding.tilSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>, view: View?, position: Int, id: Long ->
                val userReferenceList = parent.adapter.getItem(position) as UserReferenceList
                viewModel.setUserReference(userReferenceList.id)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<SetUserReferenceResponse> ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                loginResponse = Gson().fromJson(
                                    preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                                    LoginResponse::class.java
                                )
                                loginResponse!!.refName = apiResponse.body.refCode!!
                                preferenceHelper!!.putValue(
                                    Constants.KEY_USER_OBJECT,
                                    Gson().toJson(loginResponse)
                                )
                                setAdapter()
                                alertDialog.dismiss()
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
                                    AppUtilities.showLog("Network Error", "setUserReference")
                                }
                                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                    "setUserReference",
                                    apiResponse.code.toString()
                                )
                            }
                        }
                    }
            }
        dialogBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? -> alertDialog.dismiss() })
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun isValidNameData(nameBinding: DialogProfileSettingsNameBinding): Boolean {
        val fName = nameBinding.tilFirstName.editText!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(fName) || AppUtilities.hasSpecialChars(fName)) {
            Objects.requireNonNull(nameBinding.tilFirstName).error =
                "Please enter valid First Name."
            return false
        } else if (nameBinding.tilFirstName.editText!!
                .text.toString()
                .trim { it <= ' ' }.length < 1 || nameBinding.tilFirstName.editText!!
                .text.toString().trim { it <= ' ' }.length > 30
        ) {
            nameBinding.tilFirstName.error =
                "Please enter valid First Name of minimum 1 and Max 30 Characters"
            return false
        }
        val lName = nameBinding.tilLastName.editText!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(lName) || AppUtilities.hasSpecialChars(lName)) {
            Objects.requireNonNull(nameBinding.tilLastName).error = "Please enter valid Last Name."
            return false
        } else if (nameBinding.tilLastName.editText!!
                .text.toString().trim { it <= ' ' }.length < 1 || nameBinding.tilLastName.editText!!
                .text.toString().trim { it <= ' ' }.length > 30
        ) {
            nameBinding.tilLastName.error =
                "Please enter valid Last Name of minimum 1 and Max 30 Characters"
            return false
        }
        return true
    }

    private fun isValidPasswordData(passwordBinding: DialogProfileSettingsPasswordBinding): Boolean {
        var isValidData = true
        if (TextUtils.isEmpty(
                passwordBinding.tilOldPassword.editText!!.text.toString().trim { it <= ' ' })
        ) {
            passwordBinding.tilOldPassword.error = "Please enter old password."
            isValidData = false
            return isValidData
        }
        if (TextUtils.isEmpty(
                passwordBinding.tilPassword.editText!!.text.toString().trim { it <= ' ' })
        ) {
            passwordBinding.tilPassword.error = "Please enter new password."
            isValidData = false
            return isValidData
        }
        if (!Objects.requireNonNull(
                passwordBinding.tilPassword.editText!!.text.toString().trim { it <= ' ' }.equals(
                    Objects.requireNonNull(
                        passwordBinding.tilRePassword.editText!!.text.toString()
                            .trim { it <= ' ' }), ignoreCase = true
                )
            )
        ) {
            val snackbar = Snackbar.make(
                passwordBinding.root,
                "Re-Password does not match with new Password.",
                Snackbar.LENGTH_LONG
            )
            snackbar.setTextColor(Color.WHITE)
            snackbar.show()
            isValidData = false
            return isValidData
        }
        return isValidData
    }
}