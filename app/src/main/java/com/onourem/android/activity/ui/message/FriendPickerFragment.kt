package com.onourem.android.activity.ui.message

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentFriendPickerBinding
import com.onourem.android.activity.models.Conversation
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.message.adapter.FriendsPickerAdapter
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject

class FriendPickerFragment :
    AbstractBaseBottomSheetBindingDialogFragment<ConversationsViewModel, FragmentFriendPickerBinding>() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var adapter: FriendsPickerAdapter
    private lateinit var questionGamesViewModel: QuestionGamesViewModel
    private lateinit var friendList: List<UserList>

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionGamesViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory!!
        )[QuestionGamesViewModel::class.java]
        dashboardViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]
    }

    override fun viewModelType(): Class<ConversationsViewModel> {
        return ConversationsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_friend_picker
    }


    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.isNotEmpty()) {
                adapter.filter!!.filter(s)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (dialog != null) dialog!!.setCanceledOnTouchOutside(true)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendList = ArrayList()
        binding.tvDialogTitle.text = "Start conversation with a friend"
        binding.tvDialogSubTitle.visibility = View.GONE
        setAdapter()
        binding.cvClose.setOnClickListener(ViewClickListener {
            navController.popBackStack()
        })

        binding.edtSearchQuery.addTextChangedListener(textWatcher)


        // if (friendsSearchListAdapter == null) {
        questionGamesViewModel!!.friendList.observe(
            viewLifecycleOwner
        ) { friendsListResponseApiResponse: ApiResponse<UserListResponse> ->
            if (friendsListResponseApiResponse.isSuccess && friendsListResponseApiResponse.body != null) {
                if (friendsListResponseApiResponse.body.errorCode.equals(
                        "000",
                        ignoreCase = true
                    )
                ) {
                    val list = friendsListResponseApiResponse.body.userList

                    if (list == null || list.isEmpty()) {
                        binding.tvMessage.visibility = View.VISIBLE
                        binding.rvAudioCategory.visibility = View.INVISIBLE
                    } else {
                        (friendList as ArrayList<UserList>).addAll(list)
                        setAdapter()
                    }
                } else {
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.rvAudioCategory.visibility = View.INVISIBLE
                }
            } else if (!friendsListResponseApiResponse.loading) {
                hideProgress()
                showAlert(friendsListResponseApiResponse.errorMessage)
                if (friendsListResponseApiResponse.errorMessage != null
                    && (friendsListResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || friendsListResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getFriendList")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getFriendList",
                        friendsListResponseApiResponse.code.toString()
                    )
                }
            }
        }

        binding.btnInvite.setOnClickListener(ViewClickListener { v: View? ->
            navController!!.navigate(MobileNavigationDirections.actionGlobalNavInviteFriends())
        })
    }


    private fun shortLink(
        title: String,
        linkUserId: String,
        linkMsg: String,
        linkType: String,
        activityImageUrl: String,
        activityText: String
    ) {
        showProgress()
        val description: String
        var imageUrl = ""
        description = if (linkType.equals("Card", ignoreCase = true)) {
            "Fun cards to share with friends"
        } else if (linkType.equals("1toM", ignoreCase = true)) {
            "Fun questions to ask friends"
        } else {
            "A place for good friends"
        }
        if (!TextUtils.isEmpty(activityImageUrl)) {
            imageUrl = activityImageUrl
        }
        val titleSocial: String = activityText
        val builderSocialMeta = DynamicLink.SocialMetaTagParameters.Builder()
            .setTitle(titleSocial)
            .setImageUrl(Uri.parse(imageUrl))
            .setDescription(description)
            .build()
        val navigationInfoParameters = DynamicLink.NavigationInfoParameters.Builder()
        navigationInfoParameters.setForcedRedirectEnabled(true)
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://www.onourem.com/?linkUserId=$linkUserId&linkType=$linkType"))
            .setDomainUriPrefix("https://e859h.app.goo.gl")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("com.onourem.android.activity") //                                .setMinimumVersion(125)
                    .build()
            )
            .setSocialMetaTagParameters(builderSocialMeta)
            .setIosParameters(
                DynamicLink.IosParameters.Builder("com.onourem.onoureminternet")
                    .setAppStoreId("1218240628") //                                .setMinimumVersion("1.0.1")
                    .build()
            ) // Set parameters
            // ...
            .setNavigationInfoParameters(navigationInfoParameters.build())
            .buildShortDynamicLink()
            .addOnCompleteListener(requireActivity()) { task: Task<ShortDynamicLink> ->
                if (task.isSuccessful) {
                    // Short link created
                    val shortLink = Objects.requireNonNull(task.result).shortLink
                    //Log.d("shortLink", shortLink.toString());
                    dashboardViewModel!!.updateAppShortLink(linkUserId, shortLink.toString())
                        .observe(
                            viewLifecycleOwner
                        ) { response: ApiResponse<StandardResponse> ->
                            if (response.loading) {
                                showProgress()
                            } else if (response.isSuccess && response.body != null) {
                                hideProgress()
                                if (response.body.errorCode.equals("000", ignoreCase = true)) {
                                    val shareIntent = Intent(Intent.ACTION_SEND)
                                    shareIntent.type = "text/plain"
                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
                                    val shareMessage = "$linkMsg \n$shortLink"
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                                    try {
                                        fragmentContext.startActivity(Intent.createChooser(shareIntent, title))
                                    } catch (ex : ActivityNotFoundException) {
                                        Toast.makeText(fragmentContext,"NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    showAlert(response.body.errorMessage)
                                }
                            } else {
                                hideProgress()
                                showAlert(response.errorMessage)
                            }
                        }
                } else {
                    // Error
                    // ...
                }
            }
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(activity)
        binding.rvAudioCategory.addItemDecoration(
            DividerItemDecoration(
                activity, DividerItemDecoration.VERTICAL
            )
        )
        binding.rvAudioCategory.layoutManager = layoutManager
        adapter = FriendsPickerAdapter {
            val conversation = Conversation()
            conversation.id = "EMPTY"
            conversation.userName = "${it.firstName} ${it.lastName}"
            conversation.userTwo = it.userId
            conversation.profilePicture = it.profilePicture
            conversation.userTypeId = it.userType
            navController.navigate(
                FriendPickerFragmentDirections.actionNavFriendPickerToNavConversationDetails(
                    conversation
                )
            )

        }

        adapter.addSongs(friendList as MutableList<UserList>)

        binding.rvAudioCategory.adapter = adapter

    }


}