package com.onourem.android.activity.ui.games.dialogs

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.databinding.DialogPhoneCallBinding
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class PhoneCallDialogFragment :
    AbstractBaseBottomSheetBindingDialogFragment<QuestionGamesViewModel, DialogPhoneCallBinding>() {
    private val options = RequestOptions()
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    override fun layoutResource(): Int {
        return R.layout.dialog_phone_call
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

        if (dialog != null) dialog!!.setCanceledOnTouchOutside(false)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)

        binding.cvSubmit.setOnClickListener(ViewClickListener { dismiss() })

        Glide.with(requireActivity())
            .load(PhoneCallDialogFragmentArgs.fromBundle(requireArguments()).infoImageUrl)
            .apply(options)
            .into(binding.ivImage)

        Handler(Looper.getMainLooper()).postDelayed({
            view.invalidate()
        }, 500)
    }

    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

}