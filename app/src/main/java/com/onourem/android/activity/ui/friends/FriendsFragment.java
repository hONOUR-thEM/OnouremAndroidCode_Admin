package com.onourem.android.activity.ui.friends;

import com.onourem.android.activity.R;
import com.onourem.android.activity.arch.fragment.AbstractBaseFragment;

public class FriendsFragment extends AbstractBaseFragment<FriendsViewModel> {

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public Class<FriendsViewModel> viewModelType() {
        return FriendsViewModel.class;
    }

    @Override
    protected int layoutResource() {
        return R.layout.fragment_friends;
    }
}
