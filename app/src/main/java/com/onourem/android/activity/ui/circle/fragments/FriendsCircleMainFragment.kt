package com.onourem.android.activity.ui.circle.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentFriendsCircleMainBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class FriendsCircleMainFragment :
    AbstractBaseViewModelBindingFragment<FriendCircleGameViewModel, FragmentFriendsCircleMainBinding>() {

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun viewModelType(): Class<FriendCircleGameViewModel> {
        return FriendCircleGameViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_friends_circle_main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val args = FriendsCircleMainFragmentArgs.fromBundle(requireArguments())

//        if (args.hadGamePlayed) {
//            navController.navigate(
//                MobileNavigationDirections.actionGlobalNavFriendsThoughts(
//                    true
//                )
//            )
//        }


        binding.btnStartGame.isEnabled = false
        binding.btnStartGame.setBackgroundColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.gray_color_light
            )
        )

        binding.btnStartGame.setOnClickListener(ViewClickListener {
            navController.navigate(
                FriendsCircleMainFragmentDirections.actionNavFriendsCircleMainToNavFriendsCircleAddNumber(
                    false
                )
            )
        })

        binding.btnSkip.setOnClickListener(ViewClickListener {
            navController.popBackStack(R.id.nav_home, false)
        })

        viewModel.reset()

        if ((fragmentContext as DashboardActivity).isUserPlayedGame() <= 0) {
            binding.btnStartGame.isEnabled = true
            binding.btnStartGame.setBackgroundColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.good_red
                )
            )
        }

    }

}