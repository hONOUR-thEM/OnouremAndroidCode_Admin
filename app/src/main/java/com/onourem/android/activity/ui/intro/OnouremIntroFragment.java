package com.onourem.android.activity.ui.intro;

import static com.onourem.android.activity.ui.utils.Constants.KEY_INTRO_VIDEO_ENGLISH;
import static com.onourem.android.activity.ui.utils.Constants.KEY_INTRO_VIDEO_HINDI;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.onourem.android.activity.MobileNavigationDirections;
import com.onourem.android.activity.R;
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment;
import com.onourem.android.activity.databinding.FragmentOnouremIntroBinding;
import com.onourem.android.activity.prefs.SharedPreferenceHelper;
import com.onourem.android.activity.ui.contactus.ContactUsViewModel;
import com.onourem.android.activity.ui.utils.listners.ViewClickListener;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class OnouremIntroFragment extends AbstractBaseViewModelBindingFragment<ContactUsViewModel, FragmentOnouremIntroBinding> {

    @Inject
    SharedPreferenceHelper preferenceHelper;

    public OnouremIntroFragment() {
        // Required empty public constructor
    }

    @Override
    protected int layoutResource() {
        return R.layout.fragment_onourem_intro;
    }

    @Override
    public Class<ContactUsViewModel> viewModelType() {
        return ContactUsViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.clVideo.setVisibility(View.VISIBLE);

        Glide.with(getFragmentContext())
                .load(R.drawable.english_thumbnail)
                .into(binding.imageView1);

        Glide.with(getFragmentContext())
                .load(R.drawable.hindi_thumbnail)
                .into(binding.imageView2);

        binding.imageView1.setOnClickListener(getListener(preferenceHelper.getString(KEY_INTRO_VIDEO_ENGLISH)));
        binding.imageView2.setOnClickListener(getListener(preferenceHelper.getString(KEY_INTRO_VIDEO_HINDI)));

    }

    @NotNull
    private ViewClickListener getListener(String s) {
        return new ViewClickListener(view -> navController.navigate(MobileNavigationDirections.actionGlobalNavMediaView(2, s)));
    }
}