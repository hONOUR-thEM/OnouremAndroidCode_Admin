package com.onourem.android.activity.ui.dashboard.guest;

import static com.onourem.android.activity.ui.utils.Constants.KEY_SELECTED_FILTER_INDEX;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.onourem.android.activity.R;
import com.onourem.android.activity.databinding.ItemQuestionCardGuestBinding;
import com.onourem.android.activity.models.ActivityTagUtils;
import com.onourem.android.activity.models.ActivityType;
import com.onourem.android.activity.models.LoginDayActivityInfoList;
import com.onourem.android.activity.models.PlayGroup;
import com.onourem.android.activity.prefs.SharedPreferenceHelper;
import com.onourem.android.activity.ui.survey.BaseViewHolder;
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter;
import com.onourem.android.activity.ui.utils.Triple;
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener;
import com.onourem.android.activity.ui.utils.listners.ViewClickListener;
import com.onourem.android.activity.ui.utils.tooltip.ViewTooltip;
import com.onourem.android.activity.ui.utils.zoomy.Zoomy;

import java.util.ArrayList;
import java.util.List;

public class GuestPlaySoloGamesAdapter extends PaginationRVAdapter<LoginDayActivityInfoList> {

    public static final int CLICK_ACTIVITY = 0;
    public static final int CLICK_WHOLE_ITEM = 1;
    public static final int CLICK_BUTTON = 2;
    public static final int CLICK_MENU = 3;
    public static final int CLICK_MEDIA = 4;
    public static final int CLICK_SHARE = 5;
    public static final int CLICK_REMOVE = 10;
    private final String PlayGroupID;
    private final RequestOptions options = new RequestOptions()
            .fitCenter()
            .transform(new FitCenter(), new RoundedCorners(1))//15
            .placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder);
    private final OnItemClickListener<Triple<LoginDayActivityInfoList, Integer, String>> onItemClickListener;
    //    private final RequestOptions optionsRoundedCorners = new RequestOptions()
//            .fitCenter()
//            .transform(new FitCenter(), new RoundedCorners(15))
//            .placeholder(R.drawable.default_place_holder)
//            .error(R.drawable.default_place_holder);
    SharedPreferenceHelper preferenceHelper;

    public GuestPlaySoloGamesAdapter(PlayGroup playGroup, List<LoginDayActivityInfoList> items, SharedPreferenceHelper sharedPreferenceHelper, OnItemClickListener<Triple<LoginDayActivityInfoList, Integer, String>> onItemClickListener) {
        super(new ArrayList<>(items));
        this.onItemClickListener = onItemClickListener;
        this.PlayGroupID = playGroup.getPlayGroupId();
        this.preferenceHelper = sharedPreferenceHelper;
    }

    @Override
    protected LoginDayActivityInfoList emptyLoadingItem() {
        return new LoginDayActivityInfoList();
    }

    @Override
    protected LoginDayActivityInfoList emptyFooterItem() {
        return new LoginDayActivityInfoList();
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new PlaySoloGamesItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_card_guest, parent, false));
    }

    public void notifyDataUpdated(LoginDayActivityInfoList data) {
        notifyItemChanged(items.indexOf(data));
    }

    public void removeItem(LoginDayActivityInfoList item) {
        int position = items.indexOf(item);
        items.remove(item);
        notifyItemRemoved(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<LoginDayActivityInfoList> loginDayActivityInfoList) {
        this.items.clear();
        notifyDataSetChanged();
        this.items.addAll(loginDayActivityInfoList);
        notifyDataSetChanged();
    }

    class PlaySoloGamesItemViewHolder extends BaseViewHolder {

        private final ItemQuestionCardGuestBinding cardBinding;
        private final Context context;

        PlaySoloGamesItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBinding = ItemQuestionCardGuestBinding.bind(itemView);
            context = itemView.getContext();

            cardBinding.ibFirst.setOnClickListener(new ViewClickListener(v -> {
                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, String.valueOf(cardBinding.ibFirst.getTag())));
            }));

            cardBinding.ibSecond.setOnClickListener(new ViewClickListener(v -> {
                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, String.valueOf(cardBinding.ibSecond.getTag())));
            }));

            cardBinding.ivActivityType.setOnClickListener(new ViewClickListener(v -> {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_ACTIVITY, String.valueOf(v.getTag())));
            }));

//            cardBinding.ibMenu.setOnClickListener(new ViewClickListener(v -> {
//                if (onItemClickListener != null)
//                    onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_MENU, String.valueOf(v.getTag())));
//            }));

//            cardBinding.cardActivityRemove.setOnClickListener(new ViewClickListener(v -> {
//                if (onItemClickListener != null)
//                    onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_REMOVE, "Remove"));
//            }));

            cardBinding.containerQuestion.setOnClickListener(new ViewClickListener(v -> {
                if (onItemClickListener != null) {

                    LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
                    if (item.getActivityType() != null) {
                        if (item.getActivityType().equalsIgnoreCase("1toM")) {
                            onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, String.valueOf(cardBinding.ibFirst.getTag())));
                        } else {
                            if (cardBinding.ibFirst.getVisibility() == View.VISIBLE) {
                                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, String.valueOf(cardBinding.ibFirst.getTag())));
                            } else {
                                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, String.valueOf(cardBinding.ibSecond.getTag())));
                            }
                        }
                    }

                }
            }));

            cardBinding.tvTooltip.setOnClickListener(new ViewClickListener(v -> ViewTooltip
                    .on(v)
                    .text(ActivityTagUtils.getTagInfoText(items.get(getBindingAdapterPosition())))
                    .textColor(Color.WHITE)
                    .textSize(1, 16f)
                    .autoHide(true, 1000)
                    .color(itemView.getContext().getResources().getColor(ActivityTagUtils.getTagColor(items.get(getBindingAdapterPosition()))))
                    .position(ViewTooltip.Position.BOTTOM)
                    .show()));
        }


        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("RestrictedApi")
        @Override
        public void onBind(int position) {
            LoginDayActivityInfoList activity = items.get(position);

            if (activity.getActivityType() != null) {
                cardBinding.ivActivityType.setImageResource(ActivityType.getActivityTypeIcon(activity.getActivityType()));
            }

            if (activity.getActivityType() != null && activity.getActivityType().equalsIgnoreCase("Card")) {
                cardBinding.tvQuestion.setText("Share to make friends smile");
            } else {
                cardBinding.tvQuestion.setText(activity.getActivityText());
            }


            if (!TextUtils.isEmpty(activity.getActivityImageUrl())) {
                cardBinding.ivQuestionImage.setVisibility(View.VISIBLE);
                cardBinding.ivPlayVideo.setVisibility(View.GONE);
//                cardBinding.progressBar.setVisibility(View.VISIBLE);

                Glide.with(itemView.getContext())
                        .load(activity.getActivityImageLargeUrl())
                        .apply(options)
                        .into(cardBinding.ivQuestionImage);

                cardBinding.ivQuestionImage.setTag(getBindingAdapterPosition());
                Zoomy.Builder builder = new Zoomy.Builder((Activity) cardBinding.getRoot().getContext())
                        .target(cardBinding.ivQuestionImage)
                        .enableImmersiveMode(false)
                        .tapListener(v -> {
                            LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
                            onItemClickListener.onItemClick(new Triple<>(item, CLICK_MEDIA, String.valueOf(v.getTag())));
                        });
//                        .longPressListener(v -> {
//                            LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
//                            onItemClickListener.onItemClick(new Triple<>(item, CLICK_MEDIA, String.valueOf(v.getTag())));
//                        }).doubleTapListener(v -> Toast.makeText(context, "Double tap on "
//                                + v.getTag(), Toast.LENGTH_SHORT).show());

                builder.register();

            } else {
                cardBinding.ivQuestionImage.setVisibility(View.GONE);
                cardBinding.ivPlayVideo.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(activity.getQuestionVideoUrl())) {
                int color = R.color.black_trans_60;
                cardBinding.ivPlayVideo.setVisibility(View.VISIBLE);
//                cardBinding.ivQuestionImage.setForeground(AppCompatResources.getDrawable(context, color));
//                cardBinding.ivQuestionImage.setForegroundTintMode(PorterDuff.Mode.SRC_ATOP);
            } else {
                int color = R.color.transparent;
//                cardBinding.ivQuestionImage.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
//                cardBinding.ivQuestionImage.setForegroundTintMode(PorterDuff.Mode.SRC_ATOP);
                cardBinding.ivPlayVideo.setVisibility(View.GONE);
            }


            cardBinding.ibFirst.setIconGravity(MaterialButton.ICON_GRAVITY_START);

            cardBinding.ibFirst.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_answer_question));
            //boolean isTagLabelHidden = false;
            if (activity.getActivityType() != null) {
                switch (activity.getActivityType()) {
                    case "1to1":
                        if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_start_game));
                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_start_game));
                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
                            //highlight colors
                            Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question);
                            cardBinding.ibSecond.setIcon(drawableSecond);
                            cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);

                            Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
                            cardBinding.ibFirst.setIcon(drawableFirst);
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black);


                            cardBinding.ibFirst.setText(context.getString(R.string.click_action_all_answers));
                            cardBinding.ibFirst.setTag(context.getString(R.string.click_action_all_answers));
                            cardBinding.ibFirst.setVisibility(View.VISIBLE);
                        } else {
                            if (activity.getUserParticipationStatus().equals("None")
                                    || activity.getUserParticipationStatus().equals("Waiting")
                                    || activity.getUserParticipationStatus().equals("Pending")
                                    || activity.getUserParticipationStatus().equals("Settled")) {
                                cardBinding.ibSecond.setText(context.getString(R.string.click_action_start_game));
                                cardBinding.ibSecond.setTag(context.getString(R.string.click_action_start_game));
                                cardBinding.ibSecond.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_ask_question));
                                cardBinding.ibSecond.setVisibility(View.VISIBLE);
                                //highlight
                                //highlight colors
                                Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question);
                                cardBinding.ibSecond.setIcon(drawableSecond);
                                cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);

                            }


                            if (activity.getUserParticipationStatus().equals("Pending")) {
                                cardBinding.ibFirst.setText(context.getString(R.string.click_action_answer));
                                cardBinding.ibFirst.setTag(context.getString(R.string.click_action_answer));
                                //highlight colors
                                //highlight colors
                                Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question);
                                cardBinding.ibFirst.setIcon(drawableFirst);
                                cardBinding.ibFirst.setIconTintResource(R.color.colorAccent);
                            } else if (!activity.getUserParticipationStatus().equals("None")) {
                                cardBinding.ibFirst.setText(context.getString(R.string.click_action_all_answers));
                                cardBinding.ibFirst.setTag(context.getString(R.string.click_action_all_answers));
                                cardBinding.ibFirst.setVisibility(View.VISIBLE);
                                //highlight colors
                                //highlight colors
                                Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
                                cardBinding.ibFirst.setIcon(drawableFirst);
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black);

                            } else {
                                cardBinding.ibFirst.setVisibility(View.INVISIBLE);
                            }
                        }
//                    askQuestionTitle = "Start Game"
//                    ansQuestionTitle = "Answer"
//                    showAnswerTitle = "All Answers"

                        break;
                    case "Dto1":
                        if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_ask_friends));
                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_ask_friends));
                            cardBinding.ibSecond.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_ask_question));
                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
                            //highlight colors
                            Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question);
                            cardBinding.ibSecond.setIcon(drawableSecond);
                            cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);

                            Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
                            cardBinding.ibFirst.setIcon(drawableFirst);
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black);

                            cardBinding.ibFirst.setText(context.getString(R.string.click_action_all_answers));
                            cardBinding.ibFirst.setTag(context.getString(R.string.click_action_all_answers));
                            cardBinding.ibFirst.setVisibility(View.VISIBLE);
                        } else {
                            if (activity.getUserParticipationStatus().equals("None")
                                    || activity.getUserParticipationStatus().equals("Waiting")
                                    || activity.getUserParticipationStatus().equals("Pending") ||
                                    activity.getUserParticipationStatus().equals("Settled")) {
                                cardBinding.ibSecond.setText(context.getString(R.string.click_action_ask_friends));
                                cardBinding.ibSecond.setTag(context.getString(R.string.click_action_ask_friends));
                                cardBinding.ibSecond.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_ask_question));
                                cardBinding.ibSecond.setVisibility(View.VISIBLE);
                                //highlight colors
                                //highlight colors
                                Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question);
                                cardBinding.ibSecond.setIcon(drawableSecond);
                                cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);

                            }

                            if (activity.getUserParticipationStatus().equals("Pending")) {
                                cardBinding.ibFirst.setText(context.getString(R.string.click_action_answer));
                                cardBinding.ibFirst.setTag(context.getString(R.string.click_action_answer));
                                //highlight colors
                                //highlight colors
                                Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question);
                                cardBinding.ibFirst.setIcon(drawableFirst);
                                cardBinding.ibFirst.setIconTintResource(R.color.colorAccent);
                            } else if (!activity.getUserParticipationStatus().equals("None")) {
                                cardBinding.ibFirst.setText(context.getString(R.string.click_action_all_answers));
                                cardBinding.ibFirst.setTag(context.getString(R.string.click_action_all_answers));
                                cardBinding.ibFirst.setVisibility(View.VISIBLE);
                                //highlight colors
                                //highlight colors
                                Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
                                cardBinding.ibFirst.setIcon(drawableFirst);
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black);
                            } else {
                                cardBinding.ibFirst.setVisibility(View.INVISIBLE);
                            }
                        }
//                    askQuestionTitle = "Ask Friends"
//                    ansQuestionTitle = "Answer"
//                    showAnswerTitle = "All Answers"
                        break;
                    case "1toM":
                        //changed for My Ques on 30 Sept 12:41 Pm
                        // if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                        if (PlayGroupID.equals("ZZZ") /*|| PlayGroupID.equals("YYY")*/) {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_ask_friends));
                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_ask_friends));
                            cardBinding.ibSecond.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_ask_question));
                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
                            //highlight colors
                            Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question);
                            cardBinding.ibSecond.setIcon(drawableSecond);
                            cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);

                            Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
                            cardBinding.ibFirst.setIcon(drawableFirst);
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black);

                            cardBinding.ibFirst.setText(context.getString(R.string.click_action_all_answers));
                            cardBinding.ibFirst.setTag(context.getString(R.string.click_action_all_answers));
                            cardBinding.ibFirst.setVisibility(View.VISIBLE);
                        }
//                    else {
//                        if (activity.getUserParticipationStatus().equals("None")
//                                || activity.getUserParticipationStatus().equals("Pending")) {
//                            cardBinding.ibFirst.setText(context.getString(R.string.click_action_answer));
//                            cardBinding.ibFirst.setTag(context.getString(R.string.click_action_answer));
//                        } else if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibFirst.setText(context.getString(R.string.click_action_ask_friends));
//                            cardBinding.ibFirst.setTag(context.getString(R.string.click_action_ask_friends));
//                            cardBinding.ibFirst.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_ask_question));
//                        }
//
//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
//                    }
                        else {

                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_ask_friends));
                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_ask_friends));
                            cardBinding.ibSecond.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_ask_question));
                            cardBinding.ibSecond.setVisibility(View.VISIBLE);

                            //highlight colors
                            Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question);
                            cardBinding.ibSecond.setIcon(drawableSecond);
                            cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);
                            //highlight colors
                            if (activity.getUserParticipationStatus().equals("None")
                                    || activity.getUserParticipationStatus().equals("Pending")) {
                                cardBinding.ibFirst.setText(context.getString(R.string.click_action_answer));
                                cardBinding.ibFirst.setTag(context.getString(R.string.click_action_answer));
                                cardBinding.ibFirst.setVisibility(View.VISIBLE);
                                //highlight colors
                                Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question);
                                cardBinding.ibFirst.setIcon(drawableFirst);
                                cardBinding.ibFirst.setIconTintResource(R.color.colorAccent);
                            } else if (activity.getUserParticipationStatus().equals("Settled")) {
                                cardBinding.ibFirst.setText(context.getString(R.string.click_action_all_answers));
                                cardBinding.ibFirst.setTag(context.getString(R.string.click_action_all_answers));
                                cardBinding.ibFirst.setVisibility(View.VISIBLE);
                                //highlight colors
                                Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
                                cardBinding.ibFirst.setIcon(drawableFirst);
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black);

                            }

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
//                    askQuestionTitle = "Ask Friends"
//                    ansQuestionTitle = "Answer"
//                    showAnswerTitle = "All Answers"

                        break;

                    case "Card": {
                        cardBinding.ibFirst.setText(context.getString(R.string.click_action_send_to_friends));
                        cardBinding.ibFirst.setTag(context.getString(R.string.click_action_send_to_friends));
                        cardBinding.ibFirst.setVisibility(View.VISIBLE);
                        Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_share_card);
                        cardBinding.ibFirst.setIcon(drawableFirst);
                        cardBinding.ibFirst.setIconTintResource(R.color.colorAccent);

                        cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.setVisibility(View.VISIBLE);
                        Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
                        cardBinding.ibSecond.setIcon(drawableSecond);
                        cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);
                    }

                    break;

                    case "Survey": {

                        cardBinding.ibFirst.setText(context.getString(R.string.click_action_see_stats));
                        cardBinding.ibFirst.setTag(context.getString(R.string.click_action_see_stats));
                        cardBinding.ibFirst.setVisibility(View.VISIBLE);
                        Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_btn_survey);
                        cardBinding.ibFirst.setIcon(drawableFirst);


//                         cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
//                         cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                         Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
//                         cardBinding.ibSecond.setIcon(drawableSecond);
//                         cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);

//                        if (activity.getSurveySeeStats() != null && activity.getSurveySeeStats().equalsIgnoreCase("answered")) {
//                            cardBinding.ibFirst.setEnabled(false);
//                            cardBinding.ibFirst.setIconTintResource(R.color.gray_color_light);
//                        } else {
//                            cardBinding.ibFirst.setEnabled(true);
//                            cardBinding.ibFirst.setIconTintResource(R.color.colorAccent);
//                        }
                    }

                    break;

                    case "Task":
                        if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.setText(context.getString(R.string.click_action_share_experience));
                            cardBinding.ibFirst.setTag(context.getString(R.string.click_action_share_experience));
                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));

                            //highlight colors
                            Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question);
                            cardBinding.ibSecond.setIcon(drawableSecond);
                            cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);

                            Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question);
                            cardBinding.ibFirst.setIcon(drawableFirst);
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black);
                        } else {
                            switch (activity.getUserParticipationStatus()) {
                                case "None": {
                                    cardBinding.ibFirst.setText(context.getString(R.string.click_action_i_will_do_it));
                                    cardBinding.ibFirst.setTag(context.getString(R.string.click_action_i_will_do_it));
                                    //highlight colors
                                    Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_commit);
                                    cardBinding.ibFirst.setIcon(drawableFirst);
                                    cardBinding.ibFirst.setIconTintResource(R.color.colorAccent);

                                    break;
                                }
                                case "Pending": {
                                    cardBinding.ibFirst.setText(context.getString(R.string.click_action_share_experience));
                                    cardBinding.ibFirst.setTag(context.getString(R.string.click_action_share_experience));
                                    //highlight colors
                                    Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question);
                                    cardBinding.ibFirst.setIcon(drawableFirst);
                                    cardBinding.ibFirst.setIconTintResource(R.color.colorAccent);
                                    break;
                                }
                                case "Settled": {
                                    cardBinding.ibFirst.setText(context.getString(R.string.click_action_read_experience));
                                    cardBinding.ibFirst.setTag(context.getString(R.string.click_action_read_experience));
                                    //highlight colors
                                    Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
                                    assert drawableFirst != null;
                                    DrawableCompat.setTint(drawableFirst, ContextCompat.getColor(context, R.color.color_black));
                                    cardBinding.ibFirst.setIcon(drawableFirst);
                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black);
                                    break;
                                }
                            }

                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
//                    commitTitle = "I Will Do It"
//                    sendMessageTitle = "Share Experience"
//                    showAnswerTitle = "Read Experience"

                        break;

                    case "Message":
                        if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.setText(context.getString(R.string.click_action_send_now));
                            cardBinding.ibFirst.setTag(context.getString(R.string.click_action_send_now));
                            cardBinding.ibFirst.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_ask_question));
                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));

                            //highlight colors
                            Drawable drawableSecond = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question);
                            cardBinding.ibSecond.setIcon(drawableSecond);
                            cardBinding.ibSecond.setIconTintResource(R.color.color_gray_cross);

                            Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_ask_question);
                            assert drawableFirst != null;
                            DrawableCompat.setTint(drawableFirst, ContextCompat.getColor(context, R.color.color_black));
                            cardBinding.ibFirst.setIcon(drawableFirst);
                            //ZZZ or YYY may not be getting used. @neeraj's comment
                        } else {
                            switch (activity.getUserParticipationStatus()) {
                                case "None": {
                                    cardBinding.ibFirst.setText(context.getString(R.string.click_action_i_want_to_send));
                                    cardBinding.ibFirst.setTag(context.getString(R.string.click_action_i_want_to_send));
                                    //highlight colors
                                    Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_commit);
                                    cardBinding.ibFirst.setIcon(drawableFirst);
                                    cardBinding.ibFirst.setIconTintResource(R.color.colorAccent);
                                    break;
                                }
                                case "Pending": {
                                    cardBinding.ibFirst.setText(context.getString(R.string.click_action_send_now));
                                    cardBinding.ibFirst.setTag(context.getString(R.string.click_action_send_now));
                                    //highlight colors
                                    //highlight colors
                                    Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_answer_question);
                                    cardBinding.ibFirst.setIcon(drawableFirst);
                                    cardBinding.ibFirst.setIconTintResource(R.color.colorAccent);
                                    break;
                                }
                                case "Settled": {
                                    cardBinding.ibFirst.setText(context.getString(R.string.click_action_read_messages));
                                    cardBinding.ibFirst.setTag(context.getString(R.string.click_action_read_messages));
                                    //highlight colors
                                    Drawable drawableFirst = AppCompatResources.getDrawable(context, R.drawable.ic_show_all);
                                    cardBinding.ibFirst.setIcon(drawableFirst);
                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black);
                                    break;
                                }
                            }

                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                        break;
//                commitTitle = "I Want To Send"
//                sendMessageTitle = "Send Now"
//                showAnswerTitle = "Read Messages"

                }
            }

//            if (isTagLabelHidden) {
//                cardBinding.cardTooltip.setVisibility(View.GONE);
//                cardBinding.tvTooltip.setVisibility(View.GONE);
//            } else {
//
//            }

            if (activity.getFriendCount() != 0) {
                if (activity.getFriendCount() > 0) {
                    cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                    cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
                } else {
                    cardBinding.tvTooltip.setText(" ");
                    cardBinding.cardTooltip.setVisibility(View.GONE);
                }

            } else {
                cardBinding.tvTooltip.setText(" ");
                cardBinding.cardTooltip.setVisibility(View.GONE);
            }
            int color = ActivityTagUtils.getTagColor(activity);
            if (color != -1) {

//                    if (activity.getFriendCount() !=null && activity.getFriendCount() == 0) {
//                        cardBinding.tvTooltip.setText(" ");
//                        cardBinding.cardTooltip.setVisibility(View.GONE);
//                    } else {
//                        cardBinding.cardTooltip.setVisibility(View.VISIBLE);
//                        cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
//                    }
                //cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.setSupportBackgroundTintList(AppCompatResources.getColorStateList(context, color));
            } else {
                //TODO Neeraj put this condition to make sure the tag is visible in one to many, if friend count is greater than 0 even if status is settled
                if (!activity.getActivityType().equalsIgnoreCase("1toM")) {
                    cardBinding.tvTooltip.setVisibility(View.INVISIBLE);
                    cardBinding.cardTooltip.setVisibility(View.INVISIBLE);
                }

            }

            //Added for Delete Edit Ignore Question
            switch (preferenceHelper.getInt(KEY_SELECTED_FILTER_INDEX)) {
                case -1:

                case 0:
                case 1:
                    if (activity.getActivityType().equalsIgnoreCase("1toM")
                            && (activity.getUserParticipationStatus().equalsIgnoreCase("None")
                            || activity.getUserParticipationStatus().equalsIgnoreCase("Pending"))) {
                        cardBinding.ibMenu.setVisibility(View.GONE);
                        cardBinding.cardActivityRemove.setVisibility(View.VISIBLE);
                        if (!(activityPlayGroupId().equalsIgnoreCase("0")
                                || activityPlayGroupId().equalsIgnoreCase("1")
                                || activityPlayGroupId().equalsIgnoreCase("2")
                                || activityPlayGroupId().equalsIgnoreCase("3"))) {
                            if (activity.getUserParticipationStatus().equalsIgnoreCase("Pending")) {
                                cardBinding.ibMenu.setVisibility(View.GONE);
                                cardBinding.cardActivityRemove.setVisibility(View.VISIBLE);
                            } else {
                                cardBinding.ibMenu.setVisibility(View.GONE);
                            }

                        }

                    } else if (activity.getActivityType().equalsIgnoreCase("Survey")) {
                        cardBinding.ibMenu.setVisibility(View.GONE);
                        cardBinding.cardActivityRemove.setVisibility(View.VISIBLE);
                    } else {
                        cardBinding.ibMenu.setVisibility(View.GONE);
                        cardBinding.cardActivityRemove.setVisibility(View.GONE);
                    }
                    break;
                case 3:
                    if (activity.isQuestionEditable()) {
                        cardBinding.ibMenu.setVisibility(View.VISIBLE);
                    } else {
                        cardBinding.ibMenu.setVisibility(View.GONE);
                        cardBinding.cardActivityRemove.setVisibility(View.GONE);
                    }
                    break;
                default:
                    cardBinding.ibMenu.setVisibility(View.GONE);
                    cardBinding.cardActivityRemove.setVisibility(View.GONE);
                    break;
            }


        }

//        @Override
//        public void onClick(View v) {
//            if (onItemClickListener != null && items != null && !items.isEmpty()) {
//                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, (String) v.getTag()));
//            }
//        }

        private String activityPlayGroupId() {
            String activityPlayGroupId = null;
            if (PlayGroupID != null) {
                String playGroupId = PlayGroupID;
                if (playGroupId.equalsIgnoreCase("FFF")) {
                    activityPlayGroupId = "0";
                } else if (playGroupId.equalsIgnoreCase("AAA")) {
                    activityPlayGroupId = "1";
                } else if (playGroupId.equalsIgnoreCase("YYY")) {
                    activityPlayGroupId = "2";
                } else if (playGroupId.equalsIgnoreCase("ZZZ")) {
                    activityPlayGroupId = "3";
                } else {
                    activityPlayGroupId = playGroupId;
                }
            }

            return activityPlayGroupId;
        }

    }


}