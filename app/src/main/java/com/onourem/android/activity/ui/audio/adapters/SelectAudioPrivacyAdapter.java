package com.onourem.android.activity.ui.audio.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.onourem.android.activity.R;
import com.onourem.android.activity.models.PrivacyGroup;
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener;
import com.onourem.android.activity.ui.utils.listners.ViewClickListener;

import java.util.ArrayList;
import java.util.List;

public class SelectAudioPrivacyAdapter extends RecyclerView.Adapter<SelectAudioPrivacyAdapter.PrivacyViewHolder> {

    private final List<PrivacyGroup> privacyGroupList;
    private final OnItemClickListener<List<PrivacyGroup>> onItemClickListener;
    private final List<PrivacyGroup> selected = new ArrayList<>();

    public SelectAudioPrivacyAdapter(List<PrivacyGroup> privacyGroupList, List<PrivacyGroup> defaultSelected, OnItemClickListener<List<PrivacyGroup>> onItemClickListener) {
        this.privacyGroupList = privacyGroupList;

        if (defaultSelected != null && !defaultSelected.isEmpty())
            this.selected.addAll(defaultSelected);

//        if (oldSelected.size() > 0) {
//            this.selected.addAll(oldSelected);
//        }

        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public PrivacyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PrivacyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_audio_privacy, parent, false));
    }

    @Override
    public void onBindViewHolder(final PrivacyViewHolder holder, final int position) {
        PrivacyGroup privacyGroup = privacyGroupList.get(position);
        holder.bind(privacyGroup);
    }

    @Override
    public int getItemCount() {
        return privacyGroupList.size();
    }

    public List<PrivacyGroup> getSelected() {
        return selected;
    }

    class PrivacyViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageView imageView;
        private final AppCompatTextView textView;

        PrivacyViewHolder(View view) {
            super(view);
            view.setOnClickListener(new ViewClickListener(v -> {
                boolean hasDefaultGroup = false;

                for (PrivacyGroup privacyGroup : selected) {
                    if (privacyGroup.getGroupId() <= 4) {
                        hasDefaultGroup = true;
                        break;
                    }
                }
                PrivacyGroup clickedItem = privacyGroupList.get(getBindingAdapterPosition());
                if (hasDefaultGroup || clickedItem.getGroupId() <= 4) {
                    selected.clear();
                }
                if (selected.contains(clickedItem) && selected.size() != 1) {
                    selected.remove(clickedItem);
                } else if (selected.contains(clickedItem) && selected.size() == 1) {
                    selected.remove(clickedItem);
                    selected.add(privacyGroupList.get(0));
                } else
                    selected.add(clickedItem);

                notifyDataSetChanged();

                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(selected);

            }));
            textView = view.findViewById(R.id.textView);
            imageView = view.findViewById(R.id.imageView);
        }

        public void bind(PrivacyGroup privacyGroup) {
            textView.setText(privacyGroup.getGroupName());
            Drawable drawable = AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_custom_signup_eye_image);
            if (selected.isEmpty()) {
//                imageView.setImageDrawable(AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_custom_signup_eye_image));
                if (drawable != null) {
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(itemView.getRootView().getContext(), R.color.gray_color));
                }
            } else {
                if (selected.contains(privacyGroup)) {
                    if (drawable != null) {
                        DrawableCompat.setTint(drawable, ContextCompat.getColor(itemView.getRootView().getContext(), R.color.colorAccent));
                    }
                } else {
                    if (drawable != null) {
                        DrawableCompat.setTint(drawable, ContextCompat.getColor(itemView.getRootView().getContext(), R.color.gray_color));
                    }
                    imageView.setImageDrawable(AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_custom_signup_eye_image));
                }
            }
            imageView.setImageDrawable(drawable);
        }
    }
}