package com.onourem.android.activity.ui.admin.create.question_schedule

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.BottomSheetScheduleQuestionBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.appreciation.viewmodel.AppreciationViewModel
import com.onourem.android.activity.ui.games.adapters.MultiChoiceShareWithFriendsAdapter
import com.onourem.android.activity.ui.games.adapters.MultiChoiceShareWithGroupAdapter
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject

class ScheduleOnouremActivityFragment :
    AbstractBaseViewModelBindingFragment<GamesReceiverViewModel, BottomSheetScheduleQuestionBinding>(),
    View.OnClickListener {
    private var mResponse: ApiResponse<PlayGroupsResponse>? = null
    private var selectedGroupsAdapter: SelectedGroupsAdapter? = null
    private var selectedPlayGroupsArrayList: ArrayList<PlayGroup>? = null
    private lateinit var args: ScheduleOnouremActivityFragmentArgs

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private val groupPointsMap: HashMap<String, String>? = null
    private var friendsAdapter: MultiChoiceShareWithFriendsAdapter? = null
    private var groupsAdapter: MultiChoiceShareWithGroupAdapter? = null
    private val isActivityForNewQuestion = false
    private val blockedUserIds: List<UserList>? = null
    private var questionViewModel: QuestionGamesViewModel? = null
    private var appreciationViewModel: AppreciationViewModel? = null
    private val playGroupId: String? = null
    private var formattedDateTime = ""
    private var activityType = ""
    private var activityId = ""
    private var friendsList: MutableList<UserList>? = null
    private var mainPlayGroupsArrayList: MutableList<PlayGroup>? = null
    private var dashboardViewModel: DashboardViewModel? = null
    private var adminViewModel: AdminViewModel? = null
    private var categoryId = "-2"
    private var genderValue = "All"
    private var categoryName = ""
    override fun viewModelType(): Class<GamesReceiverViewModel> {
        return GamesReceiverViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.bottom_sheet_schedule_question
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]
        args = ScheduleOnouremActivityFragmentArgs.fromBundle(requireArguments())
        formattedDateTime = args.formattedDateTime
        activityType = args.activityType
        activityId = args.activityId
        Toast.makeText(
            fragmentContext,
            formattedDateTime,
            Toast.LENGTH_LONG
        ).show()
        questionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(QuestionGamesViewModel::class.java)
        adminViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(AdminViewModel::class.java)
        appreciationViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(AppreciationViewModel::class.java)

    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups) {
                if (groupsAdapter != null) groupsAdapter!!.filter.filter(s)
            } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends) {
                if (friendsAdapter != null) friendsAdapter!!.filter.filter(s)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        isCancelable = false
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
        init()
    }

    private fun loadSelectedGroupData() {

        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        layoutManager.reverseLayout = true;
        layoutManager.stackFromEnd = true;
// Set the layout manager to your recyclerview
        binding.rvSelectedGroups.layoutManager = layoutManager
        selectedPlayGroupsArrayList = ArrayList()
        setAdapter(selectedPlayGroupsArrayList!!)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(groupItemList: ArrayList<PlayGroup>) {

        selectedGroupsAdapter = SelectedGroupsAdapter(groupItemList) { item ->
            TwoActionAlertDialog.showAlert(
                requireActivity(),
                getString(R.string.label_confirm),
                "Would you like to remove this O-Club?",
                item.second,
                "Cancel",
                "Yes"
            ) { item1: Pair<Int?, PlayGroup?>? ->
                if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {

                    mainPlayGroupsArrayList?.forEach {
                        if (it.playGroupId == item.second.playGroupId){
                            it.isSelected = false
                        }
                    }

                    selectedPlayGroupsArrayList?.remove(item.second)

                    groupsAdapter?.notifyDataSetChanged()
                    selectedGroupsAdapter?.notifyDataSetChanged()

                }
            }
        }

        binding.rvSelectedGroups.adapter = selectedGroupsAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        // if (getFragmentContext() != null)
        //              ((DashboardActivity) getFragmentContext()).addNetworkErrorUserInfo("createGameActivity", String.valueOf(500));
        binding.btnFriends.setOnClickListener(this)
        binding.btnGroups.setOnClickListener(this)
        friendsList = ArrayList()
        this.mainPlayGroupsArrayList = ArrayList()
        binding.btnGroups.performClick()
        loadSelectedGroupData()

        binding.tvReset.setOnClickListener { v: View? ->
            loadData()
        }
        binding.tvSubmit.setOnClickListener(ViewClickListener { v: View? -> submit() })
        binding.rvResult.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        Objects.requireNonNull(binding.tilSearch.editText!!).addTextChangedListener(textWatcher)
        binding.cbSelectAll.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            onCheckedChanged(
                buttonView, isChecked
            )
        }

        if (mResponse == null) {
            loadData()
        }

    }

    private fun loadData() {
        viewModel.getUserActivityPlayGroupsNames("").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<PlayGroupsResponse> ->

            mResponse = apiResponse
            handleGroupResponse(mResponse!!)
        }
    }

    private fun submit() {
        binding.etSearch.setText("")
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends && (friendsAdapter == null || friendsAdapter!!.selected.isEmpty())) {
            showAlert("You need to select at least one friend")
            return
        }
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups && (groupsAdapter == null || groupsAdapter!!.selected.isEmpty())) {
            showAlert("You need to select at least one group")
            return
        }

        val createGameActivityRequest = CreateGameActivityRequest()
        createGameActivityRequest.serviceName = "createGameActivity"
        createGameActivityRequest.screenId = "48"
        createGameActivityRequest.isActivityForNewQuestion = (if (isActivityForNewQuestion) "Y" else "N")
        createGameActivityRequest.playGroupIds = ""
        createGameActivityRequest.userIds = ""
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends) {
            if (binding.tilSpinner.text.toString().equals("", ignoreCase = true)) {
                createGameActivityRequest.genderType = "All"
            } else {
                createGameActivityRequest.genderType = binding.tilSpinner.text.toString()
            }
        }
        var userIds: String? = ""

        if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups && groupsAdapter != null && groupsAdapter!!.selected.isNotEmpty() || selectedGroupsAdapter != null && selectedGroupsAdapter!!.selected.isNotEmpty()) {
            userIds = ""
            if (categoryId == "-2") {
                createGameActivityRequest.playGroupIds = Utilities.getTokenSeparatedString(groupsAdapter?.selected, ",")
                createGameActivityRequest.userIds = Utilities.getTokenSeparatedString(groupsAdapter?.selected, ",")
            } else {
                createGameActivityRequest.playGroupIds = Utilities.getTokenSeparatedString(selectedGroupsAdapter?.selected, ",")
                createGameActivityRequest.userIds = Utilities.getTokenSeparatedString(selectedGroupsAdapter?.selected, ",")
            }
        }
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends && friendsAdapter!!.selected.isNotEmpty()) {
            userIds = "All"
            createGameActivityRequest.userIds = Utilities.getTokenSeparatedString(friendsAdapter!!.selected, ",")
        }

        var playGroupIds: String? = ""
//        if (!TextUtils.isEmpty(categoryName) && categoryName.equals("All", ignoreCase = true)) {
//            playGroupIds = "All"
//            categoryId = "All"
//        } else {
//            playGroupIds = createGameActivityRequest.playGroupIds
//        }

        playGroupIds = if (groupsAdapter!!.itemCount == groupsAdapter!!.selected.size) {
            categoryName
        } else {
            createGameActivityRequest.playGroupIds
        }
        scheduleOnouremActivityByAdmin(
            AppUtilities.getTimeZone(),
            activityId,
            playGroupIds,
            formattedDateTime,
            activityType,
            userIds
        )

    }

    private fun scheduleOnouremActivityByAdmin(
        timeZone: String,
        activityId: String,
        playGroupIds: String?,
        formattedDateTime: String,
        activityType: String,
        userIds: String?
    ) {

        adminViewModel!!.scheduleOnouremActivityByAdmin(
            timeZone,
            activityId,
            playGroupIds!!,
            formattedDateTime,
            activityType,
            userIds!!
        )
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        showAlert(apiResponse.body.message) {
                            navController.popBackStack()
                        }
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnGroups -> {
                binding.btnFriends.isActivated = true
                binding.cbSelectAll.visibility = View.VISIBLE
                showCategorySpinner(true)
            }

            R.id.btnFriends -> {
                binding.btnGroups.isActivated = true
                binding.cbSelectAll.visibility = View.GONE
                showGenderSpinner(false)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun handleGroupResponse(apiResponse: ApiResponse<PlayGroupsResponse>) {
        if (apiResponse.loading) {
            showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000")) {
                if (apiResponse.body.playGroup.isNotEmpty()) {
                    this.mainPlayGroupsArrayList!!.clear()
                    this.mainPlayGroupsArrayList!!.addAll(apiResponse.body.playGroup)
                    setPlayGroupsAdapter(false)
                } else {
                    val errorMessage =
                        "You don't have any O-Clubs to post question on. Create one under Question Games by pressing Plus button and start posting questions"
                    binding.tvMessage.text = errorMessage
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.rvResult.adapter = null
                    binding.cbSelectAll.visibility = View.GONE
                }
            } else {
                hideProgress()
                showAlert(apiResponse.body.errorMessage)
            }
        } else {
            hideProgress()
            if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                    getString(R.string.unable_to_connect_host_message3)
                ))
            ) {
                if (BuildConfig.DEBUG) {
                    AppUtilities.showLog("Network Error", "getUserActivityPlayGroupsNames")
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    "getUserActivityPlayGroupsNames", apiResponse.code.toString()
                )
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun handleFriendsListResponse(apiResponse: ApiResponse<UserListResponse>) {
        if (apiResponse.loading) {
            showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000")) {
                if (apiResponse.body.userList.isNotEmpty()) {
                    val list = apiResponse.body.userList
                    friendsList!!.clear()
                    friendsList!!.addAll(apiResponse.body.userList)
                    //BlockedUsersUtility.filterBlockedUsers(blockedUserIds, list);
                    setMyFriendsAdapter(list)
                    binding.cbSelectAll.text = String.format("Select All Friends (%d)", apiResponse.body.userList.size)
                } else {
                    val errorMessage =
                        "You have not made any friends on Onourem yet. Click on the search icon to start adding friends."
                    binding.tvMessage.text = errorMessage
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.rvResult.adapter = null
                    binding.tilSearch.visibility = View.GONE
                    binding.cbSelectAll.visibility = View.GONE
                }
            } else {
                showAlert(apiResponse.body.errorMessage)
            }
        } else {
            hideProgress()
            showAlert(apiResponse.errorMessage)
            if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                    getString(R.string.unable_to_connect_host_message3)
                ))
            ) {
                if (BuildConfig.DEBUG) {
                    AppUtilities.showLog("Network Error", "getFriendList")
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo("getFriendList", apiResponse.code.toString())
            }
        }
    }

    @SuppressLint("DefaultLocale", "NotifyDataSetChanged")
    private fun setPlayGroupsAdapter(checkBoxSelectAll: Boolean) {

        binding.tvMessage.visibility = View.GONE
        binding.rvResult.visibility = View.VISIBLE
        binding.rvResult.adapter = null
        groupsAdapter = null
        //        if (categoryId.equalsIgnoreCase("-2")) {
//            if (playGroups.size() > 0) {
//                binding.tilSearch.setVisibility(View.GONE);
//            } else {
//                binding.tilSearch.setVisibility(View.GONE);
//                binding.tilSearch.setVisibility(View.GONE);
//                binding.tvMessage.setVisibility(View.VISIBLE);
//                binding.tvMessage.setText("No groups associated with selected playgroup category.");
//            }
//            groupsAdapter = new MultiChoiceShareWithGroupAdapter(playGroups);
//            binding.rvResult.setAdapter(groupsAdapter);
////        }
        if (categoryId.equals("-2", ignoreCase = true)) {
            //this is All Category Selected -2
            binding.cbSelectAll.visibility = View.VISIBLE
            binding.clSelectedGroups.visibility = View.GONE
            val newGroups = ArrayList<PlayGroup>()
            for (playGroup in mainPlayGroupsArrayList!!) {
                playGroup.isSelected = checkBoxSelectAll

//                groupItemArrayList?.forEach {
//                    if (playGroup.playGroupId == it.playGroupId) {
//                        playGroup.isSelected = it.isSelected
//                        playGroup.isDisable = true
//                    }
//                }

                newGroups.add(playGroup)
            }

//            if (allGroupsSelected) {
//                selectedPlayGroupsArrayList?.addAll(newGroups)
//            }

            if (newGroups.size > 0) {
                binding.tilSearch.visibility = View.VISIBLE
                Objects.requireNonNull(binding.tilSearch.editText!!).setText("")
            } else {
//                binding.tilSearch.visibility = View.GONE
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = "No groups associated with selected playgroup category."
            }

//            newGroups.forEach { item ->
//                groupItemArrayList?.forEach { selecteditem ->
//                    item.isSelected = selecteditem.isSelected
//                }
//            }
            groupsAdapter = MultiChoiceShareWithGroupAdapter(newGroups) {
                val position = it.first
                val clickedItem = it.second
                clickedItem.isSelected = !clickedItem.isSelected
                groupsAdapter?.notifyItemChanged(position, clickedItem)


//                if (clickedItem.isSelected) {
//                    groupItemArrayList?.add(clickedItem)
//                } else {
//                    groupItemArrayList?.remove(clickedItem)
//                }
//
//                selectedGroupsAdapter?.notifyDataSetChanged()
//                binding.tvSelectedCount.text = selectedGroupsAdapter?.selected?.size.toString()
            }


            binding.cbSelectAll.text = String.format("Select All (%d) Groups", newGroups.size)
            binding.rvResult.adapter = groupsAdapter

        } else if (!categoryId.equals("-2", ignoreCase = true)) {

            binding.cbSelectAll.visibility = View.GONE

            val newGroups = ArrayList<PlayGroup>()
            for (playGroup in mainPlayGroupsArrayList!!) {
                if (playGroup.playGroupCategoryId.equals(categoryId, ignoreCase = true)) {
                    newGroups.add(playGroup)
                }
            }

            if (newGroups.size > 0) {
                binding.tilSearch.visibility = View.VISIBLE
                Objects.requireNonNull(binding.tilSearch.editText!!).setText("")
                binding.cbSelectAll.text = String.format("Select All (%d) Groups", newGroups.size)
            } else {
//                binding.tilSearch.visibility = View.GONE
                binding.tvMessage.visibility = View.VISIBLE
                binding.tvMessage.text = "No groups associated with selected playgroup category."
            }
//            newGroups.forEach { item ->
//                groupItemArrayList?.forEach { selecteditem ->
//                    item.isSelected = selecteditem.isSelected
//                }
//            }
            groupsAdapter = MultiChoiceShareWithGroupAdapter(newGroups) { pair ->

                binding.clSelectedGroups.visibility = View.VISIBLE
                val position = pair.first
                val clickedItem = pair.second
                clickedItem.isSelected = !clickedItem.isSelected
                groupsAdapter?.notifyItemChanged(position, clickedItem)
                mainPlayGroupsArrayList?.find { it.playGroupId == clickedItem.playGroupId }?.isSelected = clickedItem.isSelected
                if (clickedItem.isSelected) {
                    selectedPlayGroupsArrayList?.add(clickedItem)
                    binding.rvSelectedGroups.scrollToPosition(selectedPlayGroupsArrayList!!.size - 1)
                } else {
                    selectedPlayGroupsArrayList?.remove(clickedItem)
//                    if (selectedPlayGroupsArrayList!!.size > 0) {
//                        binding.rvSelectedGroups.scrollToPosition(selectedPlayGroupsArrayList!!.size - 1)
//                    }
                }

                selectedGroupsAdapter?.notifyDataSetChanged()
                binding.tvSelectedCount.text = selectedGroupsAdapter?.selected?.size.toString()

            }
            binding.rvResult.adapter = groupsAdapter
        }
    }

    private fun setMyFriendsAdapter(userList: List<UserList>?) {
        binding.clSelectedGroups.visibility = View.GONE
        binding.tvMessage.visibility = View.GONE
        binding.rvResult.visibility = View.VISIBLE
        binding.rvResult.adapter = null
        friendsAdapter = null
        friendsAdapter = MultiChoiceShareWithFriendsAdapter(userList!!)
        binding.rvResult.adapter = friendsAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        questionViewModel!!.setRefreshShowBadges(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1212) {
            navController.popBackStack()
        }
    }

//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//    }

    private fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            when (binding.rgTabs.checkedRadioButtonId) {
                R.id.btnGroups -> {
                    binding.btnFriends.isActivated = true
                    for (playGroup in this.mainPlayGroupsArrayList!!) {
                        playGroup.isSelected = true
                    }
                    setPlayGroupsAdapter(true)
                }

                R.id.btnFriends -> {
                    binding.btnGroups.isActivated = true
                    for (userList in friendsList!!) {
                        userList.isSelected = true
                    }
                    setMyFriendsAdapter(friendsList)
                }
            }
        } else {
            if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups) {
                for (playGroup in this.mainPlayGroupsArrayList!!) {
                    playGroup.isSelected = false
                }
                setPlayGroupsAdapter(false)
            } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends) {
                for (userList in friendsList!!) {
                    userList.isSelected = false
                }
                setMyFriendsAdapter(friendsList)
            }
        }
    }

    private fun showCategorySpinner(show: Boolean) {
        categoryName = ""
        binding.tilSearch.hint = getString(R.string.hint_enter_group_name_to_search)
        binding.cbSelectAll.isChecked = false
//        binding.tilSearch.visibility = View.VISIBLE
        Objects.requireNonNull(binding.tilSearch.editText!!).setText("")
        binding.tilSearch.clearFocus()
        if (false) {
            binding.btnGroups.isActivated = false
            binding.btnFriends.isChecked = true
            showAlert("This question is only for individual friends and not for O-Clubs.")
        } else {
            binding.btnFriends.isActivated = true
            viewModel.getUserActivityPlayGroupsNames("")
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<PlayGroupsResponse> -> handleGroupResponse(apiResponse) }
        }
        if (show) {
            binding.tilSpinner.setText("")
            binding.tilSpinner.clearFocus()
            binding.tilSpinnerCategory.visibility = View.VISIBLE
            binding.tilSpinnerCategory.hint = getString(R.string.label_category)
            //            binding.cbSelectAll.setVisibility(View.GONE);
            binding.tilSpinner.visibility = View.VISIBLE
            val playGroupCategoryList = ArrayList<PlayGroupCategoryList>()
            questionViewModel!!.getPlayGroupCategory().observe(viewLifecycleOwner) { standardResponseApiResponse ->
                if (standardResponseApiResponse.loading) {
                    showProgress()
                } else if (standardResponseApiResponse.isSuccess && standardResponseApiResponse.body != null) {
                    hideProgress()
                    val playGroupCategories: GetPlayGroupCategories = standardResponseApiResponse.body
                    if (playGroupCategories.errorCode.equals("000")) {
                        playGroupCategoryList.clear()
                        playGroupCategoryList.add(PlayGroupCategoryList(-2, "All", "Y"))
                        playGroupCategoryList.addAll(standardResponseApiResponse.body.playGroupCategoryList!!)
                        binding.tilSpinner.setAdapter(null)
                        val adapterCountry =
                            ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, playGroupCategoryList)
                        binding.tilSpinner.setAdapter(adapterCountry)
                        binding.tilSpinner.onItemClickListener =
                            AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                                val category = playGroupCategoryList[position]
                                categoryId = category.getId().toString()
                                categoryName = category.getCategoryName()
//                                viewModel.getUserActivityPlayGroupsNames("").observe(
//                                    viewLifecycleOwner
//                                ) { apiResponse: ApiResponse<PlayGroupsResponse> ->
//                                    handleGroupResponse(
//                                        apiResponse
//                                    )
//                                }
                                handleGroupResponse(mResponse!!)
                            }
                        binding.tilSpinner.setText(adapterCountry.getItem(0).toString(), false)
                        categoryId = "-2"
                        categoryName = "All"
                    } else {
                        showAlert(playGroupCategories.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(standardResponseApiResponse.errorMessage)
                }
            }
        } else {
            binding.tilSpinnerCategory.visibility = View.GONE
        }
    }

    private fun showGenderSpinner(show: Boolean) {
        categoryName = ""
        binding.cbSelectAll.isChecked = false
        binding.tilSearch.clearFocus()
        binding.tilSearch.hint = getString(R.string.hint_enter_name_to_search)
        binding.tilSearch.visibility = View.GONE
        binding.btnGroups.isActivated = true
        viewModel.friendList.observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserListResponse> -> handleFriendsListResponse(apiResponse) }
        if (show) {
            binding.tilSpinner.setText("")
            binding.tilSpinner.clearFocus()
            binding.tilSpinnerCategory.visibility = View.GONE
            binding.tilSpinnerCategory.hint = "If question is gender specific"
            binding.cbSelectAll.visibility = View.GONE
            val genderList = ArrayList<String>()
            genderList.add("All")
            genderList.add("Male")
            genderList.add("Female")
            genderList.add("Trans")
            genderList.add("Unspecified")
            val adapterCountry = ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, genderList)
            binding.tilSpinner.setAdapter(adapterCountry)
            binding.tilSpinner.onItemClickListener =
                AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                    genderValue = genderList[position]
                    viewModel.friendList.observe(
                        viewLifecycleOwner
                    ) { apiResponse: ApiResponse<UserListResponse> -> handleFriendsListResponse(apiResponse) }
                }
        } else {
            binding.tilSpinnerCategory.visibility = View.GONE
        }
    }

    private fun showCard(text: String) {
        binding.btnGroups.isActivated = true
        binding.btnFriends.isActivated = true
        binding.rvResult.adapter = null
        binding.tvMessage.visibility = View.VISIBLE
        binding.tvMessage.text = text
        categoryName = ""
        binding.tilSpinner.setText("")
        binding.tilSpinner.clearFocus()
        binding.cbSelectAll.isChecked = false
        binding.tilSearch.clearFocus()
        binding.tilSearch.hint = getString(R.string.hint_enter_name_to_search)
        binding.tilSearch.visibility = View.GONE
        binding.rvResult.adapter = null
        binding.tilSpinnerCategory.visibility = View.GONE
    }
}