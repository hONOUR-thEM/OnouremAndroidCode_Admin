package com.onourem.android.activity.ui.dashboard.mood.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.onourem.android.activity.R;
import com.onourem.android.activity.databinding.ItemLineMoodBinding;
import com.onourem.android.activity.models.UserExpressionList;
import com.onourem.android.activity.models.UserMoodHistory;
import com.onourem.android.activity.ui.survey.BaseViewHolder;
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter;
import com.onourem.android.activity.ui.utils.Base64Utility;
import com.onourem.android.activity.ui.utils.Utilities;
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoodLineAdapter extends PaginationRVAdapter<UserMoodHistory> {

    public static int CLICK_ACCEPT = 0;
    public static int CLICK_ACCEPT_WATCHLIST = 5;
    public static int CLICK_IGNORE = 1;
    public static int CLICK_IGNORE_WATCHLIST = 6;
    public static int CLICK_READ_POST = 2;
    public static int CLICK_PROFILE = 3;
    public static int CLICK_WHOLE = 4;
    private final List<UserMoodHistory> lineList;
    private final OnItemClickListener<Pair<Integer, UserMoodHistory>> onItemClickListener;
    private final HashMap<String, UserExpressionList> expressionListHashMap;
    private String idToHighlight;


    public MoodLineAdapter(List<UserMoodHistory> notificationInfoLists, HashMap<String, UserExpressionList> expressionListHashMap, OnItemClickListener<Pair<Integer, UserMoodHistory>> onItemClickListener) {
        super(notificationInfoLists);
        this.lineList = notificationInfoLists;
        this.expressionListHashMap = expressionListHashMap;
        this.onItemClickListener = onItemClickListener;
        idToHighlight = "";
    }

    @Override
    protected UserMoodHistory emptyLoadingItem() {
        return null;
    }

    @Override
    protected UserMoodHistory emptyFooterItem() {
        return null;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new LineEntryModelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_mood, parent, false));
    }

    public void notifyDataUpdated(UserMoodHistory data) {

        notifyItemChanged(lineList.indexOf(data));
    }

    public void notifyItemRemoved(UserMoodHistory data) {
        notifyItemRemoved(lineList.indexOf(data));
    }

    public void removeItem(int position) {
        lineList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(UserMoodHistory item, int position) {
        lineList.add(position, item);
        notifyItemInserted(position);
    }

    public List<UserMoodHistory> getData() {
        return lineList;
    }

    public void setIdToHighlight(int pos) {
        idToHighlight = String.valueOf(pos);
        notifyItemChanged(pos);
    }

    void animateChange(ItemLineMoodBinding viewHolder) {
        int colorFrom = ContextCompat.getColor(viewHolder.getRoot().getContext(), R.color.color_highlight_game_post);
        int colorTo = ContextCompat.getColor(viewHolder.getRoot().getContext(), R.color.color_white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // milliseconds
        colorAnimation.setStartDelay(300); // milliseconds
        colorAnimation.addUpdateListener(animator -> viewHolder.parent.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.setRepeatCount(1);
        colorAnimation.start();
        idToHighlight = "";
    }

    public boolean checkForEncode(String string) {
        String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(string);
        return m.find();
    }

    class LineEntryModelViewHolder extends BaseViewHolder {

        private final RequestOptions options = new RequestOptions()
                .fitCenter()
//                .transform(new CircleCrop())
                .placeholder(R.drawable.default_user_profile_image)
                .error(R.drawable.default_user_profile_image);
        ItemLineMoodBinding itemBinding;

        LineEntryModelViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemLineMoodBinding.bind(itemView);

//            itemBinding.getRoot().setOnClickListener(new ViewClickListener(
//                    v1 -> {
//                        LineEntryModel conversation = lineList.get(getBindingAdapterPosition());
//                        onItemClickListener.onItemClick(Pair.create(CLICK_WHOLE, conversation));
//                    }
//            ));


//            itemBinding.rivProfile.setOnClickListener(new ViewClickListener(
//                    v1 -> {
//                        LineEntryModel conversation = lineList.get(getBindingAdapterPosition());
//                        onItemClickListener.onItemClick(Pair.create(CLICK_PROFILE, conversation));
//                    }
//            ));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBind(int position) {

            UserMoodHistory item = items.get(position);

            itemBinding.tvName.setText(item.getExpressionName());

            itemBinding.tvDate.setText(Utilities.serverDateConversionTo("dd MMM", item.getCreatedOn()));


            if (Integer.parseInt(item.getPositivity()) > 0) {
                itemBinding.tvName.setTextColor(ContextCompat.getColor(itemBinding.getRoot().getContext(), R.color.good_green_color));

            } else {
                itemBinding.tvName.setTextColor(ContextCompat.getColor(itemBinding.getRoot().getContext(), R.color.good_red_color));
            }

            if (!item.getReason().equalsIgnoreCase("") && checkForEncode(item.getReason())) {
                itemBinding.tvReasonText.setText("Reason : " + Base64Utility.decode(item.getReason()));
            } else {
                if (!item.getReason().equalsIgnoreCase("")) {
                    itemBinding.tvReasonText.setText("Reason : " + item.getReason());
                } else {
                    itemBinding.tvReasonText.setText("Reason : Not Provided");
                }

            }

            //UserExpressionList userExpressionList = expressionListHashMap.get(item.getExpressionId());
//            if (userExpressionList != null) {
//
//            }

            Glide.with(itemBinding.getRoot().getContext())
                    .load(item.getImage())
                    .apply(options)
                    .into(itemBinding.rivProfile);

            if (!idToHighlight.equalsIgnoreCase("")) {
                animateChange(itemBinding);
            }

        }
    }


}
