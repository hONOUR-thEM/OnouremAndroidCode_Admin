package com.onourem.android.activity.ui.audio.adapters;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.onourem.android.activity.R;
import com.onourem.android.activity.databinding.ItemUserVocalBinding;
import com.onourem.android.activity.models.UserList;
import com.onourem.android.activity.ui.survey.BaseViewHolder;
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter;
import com.onourem.android.activity.ui.utils.Utilities;
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener;
import com.onourem.android.activity.ui.utils.listners.ViewClickListener;

import java.util.List;
import java.util.Locale;

public class VocalsAdapter extends PaginationRVAdapter<UserList> {

    public static final String ACTION_BUTTON_IS_FRIEND = "ACTION_IS_FRIEND";
    public static final String ACTION_BUTTON_IGNORE_FRIEND = "ACTION_IGNORE_FRIEND";
    public static final String ACTION_ROW = "ROW";
    private final List<UserList> visibleDataList;
    private final OnItemClickListener<Pair<String, UserList>> onItemClickListener;
    private final RequestOptions options = new RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image);


    public VocalsAdapter(List<UserList> storeDataLst, OnItemClickListener<Pair<String, UserList>> onItemClickListener) {
        super(storeDataLst);
        this.visibleDataList = storeDataLst;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected UserList emptyLoadingItem() {
        return new UserList();
    }

    @Override
    protected UserList emptyFooterItem() {
        return new UserList();
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new FriendViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_vocal, parent, false));
    }

    public void notifyDataUpdated(UserList data) {
        notifyItemChanged(visibleDataList.indexOf(data));
    }

    class FriendViewHolder extends BaseViewHolder {

        private final ItemUserVocalBinding itemBinding;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemUserVocalBinding.bind(itemView);

            itemView.setOnClickListener(new ViewClickListener(v -> {
                UserList user = visibleDataList.get(getBindingAdapterPosition());
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(new Pair<>(ACTION_ROW, user));
                }
            }));
        }

        @Override
        public void onBind(int position) {

            UserList user = items.get(position);
            itemBinding.tvName.setText(String.format(Locale.getDefault(), "%s %s", user.getFirstName(), user.getLastName()));

            Glide.with(itemView.getContext())
                    .load(user.getProfilePicture())
                    .apply(options)
                    .into(itemBinding.rivProfile);

            Utilities.verifiedUserType(itemBinding.getRoot().getContext(), user.getUserType(), itemBinding.ivIconVerified);
        }
    }
}
