package com.onourem.android.activity.ui.notifications.adapters;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.navigation.NavController;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.onourem.android.activity.R;
import com.onourem.android.activity.databinding.ItemNotificationRowBinding;
import com.onourem.android.activity.models.NotificationInfoList;
import com.onourem.android.activity.ui.notifications.NotificationActionType;
import com.onourem.android.activity.ui.survey.BaseViewHolder;
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter;
import com.onourem.android.activity.ui.utils.TimeUtil;
import com.onourem.android.activity.ui.utils.TimeUtilBackward;
import com.onourem.android.activity.ui.utils.Utilities;
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener;
import com.onourem.android.activity.ui.utils.listners.ViewClickListener;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends PaginationRVAdapter<NotificationInfoList> {

    public static int CLICK_ACCEPT = 0;
    public static int CLICK_ACCEPT_WATCHLIST = 5;
    public static int CLICK_IGNORE = 1;
    public static int CLICK_IGNORE_WATCHLIST = 6;
    public static int CLICK_READ_POST = 2;
    public static int CLICK_PROFILE = 3;
    public static int CLICK_WHOLE = 4;
    private final NavController navController;
    private final List<NotificationInfoList> notificationInfoLists;
    private final OnItemClickListener<Pair<Integer, NotificationInfoList>> onItemClickListener;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());


    public NotificationAdapter(NavController navController, List<NotificationInfoList> notificationInfoLists, OnItemClickListener<Pair<Integer, NotificationInfoList>> onItemClickListener) {
        super(notificationInfoLists);
        this.navController = navController;
        this.notificationInfoLists = notificationInfoLists;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected NotificationInfoList emptyLoadingItem() {
        return null;
    }

    @Override
    protected NotificationInfoList emptyFooterItem() {
        return null;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_row, parent, false));
    }

    public void notifyDataUpdated(NotificationInfoList data) {

        notifyItemChanged(notificationInfoLists.indexOf(data));
    }

    public void notifyItemRemoved(NotificationInfoList data) {
        notifyItemRemoved(notificationInfoLists.indexOf(data));
    }

    public void removeItem(int position) {
        notificationInfoLists.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(NotificationInfoList item, int position) {
        notificationInfoLists.add(position, item);
        notifyItemInserted(position);
    }

    public List<NotificationInfoList> getData() {
        return notificationInfoLists;
    }

    class NotificationViewHolder extends BaseViewHolder {

        private final RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.default_user_profile_image)
                .error(R.drawable.default_user_profile_image);
        ItemNotificationRowBinding itemBinding;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemNotificationRowBinding.bind(itemView);

            itemBinding.getRoot().setOnClickListener(new ViewClickListener(
                    v1 -> {
                        NotificationInfoList notificationItem = notificationInfoLists.get(getBindingAdapterPosition());
                        onItemClickListener.onItemClick(Pair.create(CLICK_WHOLE, notificationItem));
                    }
            ));

            itemBinding.tvIsFriend.setOnClickListener(new ViewClickListener(
                    v1 -> {
                        NotificationInfoList notificationItem = notificationInfoLists.get(getBindingAdapterPosition());
                        if (itemBinding.tvIsFriend.getText().toString().equalsIgnoreCase("Accept")) {
                            onItemClickListener.onItemClick(Pair.create(CLICK_ACCEPT, notificationItem));
                        } else if (itemBinding.tvIsFriend.getText().toString().equalsIgnoreCase("Read Post")) {
                            onItemClickListener.onItemClick(Pair.create(CLICK_READ_POST, notificationItem));
                        } else if (itemBinding.tvIsFriend.getText().toString().equalsIgnoreCase("Yes")) {
                            onItemClickListener.onItemClick(Pair.create(CLICK_ACCEPT_WATCHLIST, notificationItem));
                        }
                    }
            ));

            itemBinding.tvCancel.setOnClickListener(new ViewClickListener(
                    v1 -> {
                        NotificationInfoList notificationItem = notificationInfoLists.get(getBindingAdapterPosition());
                        if (itemBinding.tvCancel.getText().toString().equalsIgnoreCase("No")) {
                            onItemClickListener.onItemClick(Pair.create(CLICK_IGNORE_WATCHLIST, notificationItem));
                        } else {
                            onItemClickListener.onItemClick(Pair.create(CLICK_IGNORE, notificationItem));
                        }

                    }
            ));

            itemBinding.rivProfile.setOnClickListener(new ViewClickListener(
                    v1 -> {
                        NotificationInfoList notificationItem = notificationInfoLists.get(getBindingAdapterPosition());
                        onItemClickListener.onItemClick(Pair.create(CLICK_PROFILE, notificationItem));
                    }
            ));
        }

        @Override
        public void onBind(int position) {

            NotificationInfoList item = items.get(position);
            itemBinding.tvName.setText(item.getMessage());
            if (item.getCheckedStatus().equalsIgnoreCase("false")) {
                itemBinding.tvReadStatus.setVisibility(View.VISIBLE);
            } else {
                itemBinding.tvReadStatus.setVisibility(View.INVISIBLE);
            }

//            item.setActionDateTime("2023-11-30 12:46:11.0");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                itemBinding.tvDate.setText(TimeUtil.INSTANCE.getRelatedTime(item.getActionDateTime()));
            }else {
                itemBinding.tvDate.setText(TimeUtilBackward.getRelatedTime(item.getActionDateTime()));
            }

//            try {
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Utilities.APP_DISPLAY_DATE_TIME_FORMAT, Locale.getDefault());
//                simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Utilities.getServerTimeZone(item.getActionDateTime())));
//                Date serverDate = ((simpleDateFormat)).parse(item.getActionDateTime());
//
//                if (serverDate != null) {
//                    String text = TimeAgo.using(serverDate.getTime());
//                    itemBinding.tvDate.setText(text);
//                }
//
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            actionsOnItem(item);

            Glide.with(itemBinding.getRoot().getContext())
                    .load(item.getActionByPic())
                    .apply(options)
                    .placeholder(R.drawable.default_user_profile_image)
                    .into(itemBinding.rivProfile);

            Utilities.verifiedUserType(itemBinding.getRoot().getContext(), item.getUserType(), itemBinding.ivIconVerified);

        }

        private void actionsOnItem(NotificationInfoList item) {
            itemBinding.tvIsFriend.setVisibility(View.GONE);
            itemBinding.tvCancel.setVisibility(View.GONE);

//            Drawable drawable = AppCompatResources.getDrawable(itemView.getContext(), R.drawable.shape_filled_rectangle_friend_status);
//            Drawable drawablePink = AppCompatResources.getDrawable(itemView.getContext(), R.drawable.shape_filled_rectangle_friend_status);
            switch (item.getActionType()) {

                case NotificationActionType.FRIEND_REQUEST:
                    itemBinding.tvIsFriend.setVisibility(View.VISIBLE);
                    itemBinding.tvIsFriend.setText("Accept");
//                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_green));
//                    itemBinding.tvIsFriend.setBackgroundDrawable(drawable);
                    itemBinding.tvIsFriend.setBackgroundTintList(AppCompatResources.getColorStateList(itemView.getContext(), R.color.color_green));

                    itemBinding.tvCancel.setVisibility(View.VISIBLE);
                    itemBinding.tvCancel.setText("Ignore");
//                    DrawableCompat.setTint(drawablePink, itemView.getResources().getColor(R.color.color_pink));
//                    itemBinding.tvCancel.setBackgroundDrawable(drawablePink);
                    itemBinding.tvCancel.setBackgroundTintList(AppCompatResources.getColorStateList(itemView.getContext(), R.color.color_pink));

                    break;
                case NotificationActionType.WATCHLIST_REQUEST:
                    itemBinding.tvIsFriend.setVisibility(View.VISIBLE);
                    itemBinding.tvIsFriend.setText("Yes");
//                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_green));
//                    itemBinding.tvIsFriend.setBackgroundDrawable(drawable);
                    itemBinding.tvIsFriend.setBackgroundTintList(AppCompatResources.getColorStateList(itemView.getContext(), R.color.color_green));

                    itemBinding.tvCancel.setVisibility(View.VISIBLE);
                    itemBinding.tvCancel.setText("No");
//                    DrawableCompat.setTint(drawablePink, itemView.getResources().getColor(R.color.color_pink));
//                    itemBinding.tvCancel.setBackgroundDrawable(drawablePink);
                    itemBinding.tvCancel.setBackgroundTintList(AppCompatResources.getColorStateList(itemView.getContext(), R.color.color_pink));

                    break;

                case NotificationActionType.WATCH_REQUEST_ACCEPTED:
                    itemBinding.tvIsFriend.setVisibility(View.GONE);
                    itemBinding.tvIsFriend.setText("Yes");
//                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_green));
//                    itemBinding.tvIsFriend.setBackgroundDrawable(drawable);
                    itemBinding.tvIsFriend.setBackgroundTintList(AppCompatResources.getColorStateList(itemView.getContext(), R.color.color_green));

                    itemBinding.tvCancel.setVisibility(View.GONE);
                    itemBinding.tvCancel.setText("No");
//                    DrawableCompat.setTint(drawablePink, itemView.getResources().getColor(R.color.color_pink));
//                    itemBinding.tvCancel.setBackgroundDrawable(drawablePink);
                    itemBinding.tvCancel.setBackgroundTintList(AppCompatResources.getColorStateList(itemView.getContext(), R.color.color_pink));

                    break;
                case NotificationActionType.FRIEND_SUGGESTION_REQUEST:


                    break;
                case NotificationActionType.FRIEND_REQUEST_ACCEPTED:

                    break;
                case NotificationActionType.POST_REQUEST:
                case NotificationActionType.POST_SEND:
                    itemBinding.tvIsFriend.setVisibility(View.VISIBLE);
                    itemBinding.tvIsFriend.setText("Read Post");
//                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_blue));
//                    itemBinding.tvIsFriend.setBackgroundDrawable(drawable);
                    itemBinding.tvIsFriend.setBackgroundTintList(AppCompatResources.getColorStateList(itemView.getContext(), R.color.color_blue));

                    break;
//                case NotificationActionType.POST_REQUEST_ACCEPTED:
//
//                    break;
//                case NotificationActionType.STARTED_FOLLOWING:
//
//                    break;
//                case NotificationActionType.LIKED_POST:
//
//                    break;
//                case NotificationActionType.COMMENTED_POST:
//
//                    break;
//                case NotificationActionType.SHARED_POST:
//
//                    break;
//                case NotificationActionType.REPLIED_POST:
//
//                    break;
//                case NotificationActionType.ONE_TO_1_GAME_RECEIVED:
//
//                    break;
//                case NotificationActionType.D_TO_1_GAME_RECEIVED:
//
//                    break;
//                case NotificationActionType.ONE_TO_M_GAME_RECEIVED:
//
//                    break;
//                case NotificationActionType.ONE_TO_1_RESPONSE_SUBMITTED:
//
//                    break;
//                case NotificationActionType.D_TO_1_RESPONSE_SUBMITTED:
//
//                    break;
//                case NotificationActionType.ONE_TO_M_RESPONSE_SUBMITTED:
//
//                    break;
//                case NotificationActionType.COMMENTED_GAME:
//
//                    break;
//                case NotificationActionType.PLAY_GROUP_1_TO_M_RESPONSE_SUBMITTED:
//
//                    break;
//                case NotificationActionType.PLAY_GROUP_1_TO_M_GAME_RECEIVED:
//
//                    break;
//                case NotificationActionType.PLAY_GROUP_COMMENTED_GAME:
//
//                    break;
//                case NotificationActionType.USER_ADDED_TO_PLAY_GROUP:
//
//                    break;
//                case NotificationActionType.ADMIN_ADDED_TO_PLAY_GROUP:
//
//                    break;
//                case NotificationActionType.LINK_USER_ADDED_TO_PLAY_GROUP:
//
//                    break;
//                case NotificationActionType.NOTIFY_ADMIN_FOR_NEW_LINK_USER:
//
//                    break;
//                case NotificationActionType.SURVEY_RECEIVED:
//
//                    break;

                default:

                case NotificationActionType.NONE:
                    break;

            }

        }
    }


}
