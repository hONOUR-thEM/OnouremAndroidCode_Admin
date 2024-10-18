package com.onourem.android.activity.ui.circle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.onourem.android.activity.R;
import com.onourem.android.activity.ui.circle.fragments.ISDCodesModel;
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener;
import com.onourem.android.activity.ui.utils.listners.ViewClickListener;

import java.util.ArrayList;

public class SingleChoiceCountryAdapter extends RecyclerView.Adapter<SingleChoiceCountryAdapter.SingleViewHolder> {

    private final Context context;
    private final OnItemClickListener<ISDCodesModel> onItemClickListener;
    private ArrayList<ISDCodesModel> categoryList;
    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private int checkedPosition = -1;

    public SingleChoiceCountryAdapter(Context context, ArrayList<ISDCodesModel> categoryList, OnItemClickListener<ISDCodesModel> onItemClickListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.onItemClickListener = onItemClickListener;
    }

    public void setCategoryList(ArrayList<ISDCodesModel> categoryList) {
        this.categoryList = new ArrayList<>();
        this.categoryList = categoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SingleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_country_options, viewGroup, false);
        return new SingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleViewHolder singleViewHolder, int position) {
        singleViewHolder.bind(categoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public ISDCodesModel getSelected() {
        if (checkedPosition != -1) {
            return categoryList.get(checkedPosition);
        }
        return null;
    }

    public void updateItem(String itemId) {
        int position;
        if (categoryList != null && !categoryList.isEmpty()) {
            for (ISDCodesModel item : categoryList) {
                if (item.getIsdCode().equals(itemId)) {
                    position = categoryList.indexOf(item);
                    checkedPosition = position;
                    notifyItemChanged(position);
                    break;
                }
            }
        }
    }

    class SingleViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final ImageView imageView;

        SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(final ISDCodesModel surveyOption) {

            if (checkedPosition == -1) {
                imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_radio_unchecked));

            } else {
                if (checkedPosition == getBindingAdapterPosition()) {
                    imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_checkbox_on_background));
                } else {
                    imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_radio_unchecked));
                }
            }

            textView.setText(surveyOption.getCountryName());

            itemView.setOnClickListener(new ViewClickListener(view -> {
                if (checkedPosition != getBindingAdapterPosition()) {
                    checkedPosition = getBindingAdapterPosition();
                    notifyDataSetChanged();
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getSelected());
                    }
                }
            }));
        }
    }

}