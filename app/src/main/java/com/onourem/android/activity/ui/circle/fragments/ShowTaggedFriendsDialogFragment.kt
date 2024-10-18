package com.onourem.android.activity.ui.circle.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogShowTaggedFriendsBinding
import com.onourem.android.activity.models.GetTaggedByUserListResponse
import com.onourem.android.activity.models.QualityQuestion
import com.onourem.android.activity.models.User
import com.onourem.android.activity.models.UserQualityInfo
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.circle.adapters.FriendsListAdapter
import com.onourem.android.activity.ui.circle.adapters.ViewPagerHeaderAdapter
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class ShowTaggedFriendsDialogFragment :
    AbstractBaseDialogBindingFragment<FriendCircleGameViewModel, DialogShowTaggedFriendsBinding>() {

    private lateinit var args: ShowTaggedFriendsDialogFragmentArgs
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    private lateinit var questionList: ArrayList<QualityQuestion>

    private var userActionViewModel: UserActionViewModel? = null


    override fun layoutResource(): Int {
        return R.layout.dialog_show_tagged_friends
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )
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

        args = ShowTaggedFriendsDialogFragmentArgs.fromBundle(requireArguments())

        binding.parent.setOnClickListener(ViewClickListener {
            dismiss()
        })

        binding.ivClose.setOnClickListener(ViewClickListener {
            dismiss()
        })
        binding.materialCardView.setCardBackgroundColor(Color.parseColor(Common.addHash(args.userQualityInfo.backgroundColor)))
        binding.view.setViewDrawableColor(Color.parseColor(Common.addHash(args.userQualityInfo.waveColor)))
//        binding.view.background =
//        changeDrawableColor(R.drawable.shape_filled_rectangle_gray_color, Color.parseColor(args.userQualityInfo.backgroundColor))

        getTaggedByUserList(args.userQualityInfo)

    }

    private fun View.setViewDrawableColor(color: Int) {
        ViewCompat.setBackgroundTintList(
            this,
            ColorStateList.valueOf(color)
        )
    }

    fun changeDrawableColor(icon: Int, newColor: Int): Drawable {
        val mDrawable = ContextCompat.getDrawable(fragmentContext, icon)!!.mutate()
        mDrawable.colorFilter = PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN)
        return mDrawable
    }


    override fun viewModelType(): Class<FriendCircleGameViewModel> {
        return FriendCircleGameViewModel::class.java
    }

    private fun setAdapter(taggedResponse: GetTaggedByUserListResponse?) {
        val layoutManager = LinearLayoutManager(activity)

        binding.rvUsers.layoutManager = layoutManager


        val headerAdapter = ViewPagerHeaderAdapter(
            "Following Friends Tagged You On The Below Quality", args.userQualityInfo.imageUrl
        )

        val friendsList = ArrayList<User>()
        taggedResponse!!.userList.forEach {
            friendsList.add(it)
        }

        val friendsListAdapter = FriendsListAdapter(friendsList) {

        }

        binding.rvUsers.adapter = ConcatAdapter(headerAdapter, friendsListAdapter)

    }

    private fun getTaggedByUserList(userQualityInfo: UserQualityInfo) {

        viewModel.getTaggedByUserList(userQualityInfo.questionId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetTaggedByUserListResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()

                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    setAdapter(apiResponse.body)
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