package com.onourem.android.activity.ui.games.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemQuestionCardBinding
import com.onourem.android.activity.models.ActivityTagUtils.getTagColor
import com.onourem.android.activity.models.ActivityTagUtils.getTagInfoText
import com.onourem.android.activity.models.ActivityType
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.glide.GlideApp
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.tooltip.ViewTooltip
import com.onourem.android.activity.ui.utils.zoomy.Zoomy

class PlaySoloGamesAdapter(
    playGroup: PlayGroup,
    items: List<LoginDayActivityInfoList>?,
    sharedPreferenceHelper: SharedPreferenceHelper,
    private val onItemClickListener: OnItemClickListener<Triple<LoginDayActivityInfoList?, Int?, String?>?>
) : PaginationRVAdapter<LoginDayActivityInfoList?>(
    items?.let { ArrayList(it) }!!
) {
    private val PlayGroupID: String?

    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(1)) //15
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    //    private final RequestOptions optionsRoundedCorners = new RequestOptions()
    //            .fitCenter()
    //            .transform(new FitCenter(), new RoundedCorners(15))
    //            .placeholder(R.drawable.default_place_holder)
    //            .error(R.drawable.default_place_holder);
    var preferenceHelper: SharedPreferenceHelper
    override fun emptyLoadingItem(): LoginDayActivityInfoList {
        return LoginDayActivityInfoList()
    }

    override fun emptyFooterItem(): LoginDayActivityInfoList {
        return LoginDayActivityInfoList()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return PlaySoloGamesItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_question_card, parent, false)
        )
    }

    fun notifyDataUpdated(data: LoginDayActivityInfoList?) {
        notifyItemChanged(items.indexOf(data))
    }

    override fun removeItem(item: LoginDayActivityInfoList?) {
        val position = items.indexOf(item)
        items.remove(item)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(loginDayActivityInfoList: List<LoginDayActivityInfoList?>?) {
        items.clear()
        notifyDataSetChanged()
        items.addAll(loginDayActivityInfoList!!)
        notifyDataSetChanged()
    }

    internal inner class PlaySoloGamesItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val cardBinding: ItemQuestionCardBinding
        private val context: Context
        @SuppressLint("RestrictedApi")
        override fun onBind(position: Int) {
            val activity = items[position]!!
            if (activity.activityType != null) {
                cardBinding.ivActivityType.setImageResource(
                    ActivityType.getActivityTypeIcon(
                        activity.activityType
                    )
                )
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "External",
                    ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.visibility = View.GONE
                cardBinding.tvExternalContent.visibility = View.VISIBLE
            } else {
                cardBinding.tvQuestion.visibility = View.VISIBLE
                cardBinding.tvExternalContent.visibility = View.GONE
            }
            if (activity.activityType != null && activity.activityType.equals(
                    "Card",
                    ignoreCase = true
                )
            ) {
                cardBinding.tvQuestion.text = "Share to make friends smile"
            } else {
                if (activity.activityType != null && activity.activityType.equals(
                        "External",
                        ignoreCase = true
                    )
                ) {
                    cardBinding.tvExternalContent.text = activity.activityText
                } else {
                    cardBinding.tvQuestion.text = activity.activityText
                }
            }
            if (!TextUtils.isEmpty(activity.activityImageUrl)) {
                cardBinding.ivQuestionImage.visibility = View.VISIBLE
                cardBinding.ivPlayVideo.visibility = View.GONE
                //                cardBinding.progressBar.setVisibility(View.VISIBLE);
                GlideApp.with(itemView.context)
                    .load(activity.activityImageLargeUrl).thumbnail(0.33f)
                    .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                    .into(cardBinding.ivQuestionImage)
                cardBinding.ivQuestionImage.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(cardBinding.root.context as Activity)
                    .target(cardBinding.ivQuestionImage)
                    .enableImmersiveMode(false)
                    .tapListener { v: View ->
                        val item = items[bindingAdapterPosition]!!
                        onItemClickListener!!.onItemClick(
                            Triple(
                                item,
                                CLICK_MEDIA,
                                v.tag.toString()
                            )
                        )
                    }
                //                        .longPressListener(v -> {
//                            LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
//                            onItemClickListener.onItemClick(new Triple<>(item, CLICK_MEDIA, String.valueOf(v.getTag())));
//                        }).doubleTapListener(v -> Toast.makeText(context, "Double tap on "
//                                + v.getTag(), Toast.LENGTH_SHORT).show());
                builder.register()
            } else {
                cardBinding.ivQuestionImage.visibility = View.GONE
                cardBinding.ivPlayVideo.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(activity.questionVideoUrl)) {
                val color = R.color.black_trans_60
                if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                    cardBinding.ivQuestionImage.visibility = View.VISIBLE
                    cardBinding.ivPlayVideo.visibility = View.VISIBLE
                    //String url = "https://img.youtube.com/vi/" + Common.extractVideoId(activity.getQuestionVideoUrl()) + "/maxresdefault.jpg";
                    GlideApp.with(itemView.context)
                        .load(activity.activityImageLargeUrl).thumbnail(0.33f)
                        .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                        .into(cardBinding.ivQuestionImage)
                } else if (!TextUtils.isEmpty(activity.youTubeVideoId)) {
                    cardBinding.ivQuestionImage.visibility = View.VISIBLE
                    cardBinding.ivPlayVideo.visibility = View.GONE
                    GlideApp.with(itemView.context)
                        .load(activity.activityImageLargeUrl).thumbnail(0.33f)
                        .apply(options.override(activity.imageWidth!!, activity.imageHeight!!))
                        .into(cardBinding.ivQuestionImage)
                } else {
                    cardBinding.ivPlayVideo.visibility = View.VISIBLE
                }
                //                cardBinding.ivQuestionImage.setForeground(AppCompatResources.getDrawable(context, color));
//                cardBinding.ivQuestionImage.setForegroundTintMode(PorterDuff.Mode.SRC_ATOP);
            } else {
                val color = R.color.transparent
                //                cardBinding.ivQuestionImage.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
//                cardBinding.ivQuestionImage.setForegroundTintMode(PorterDuff.Mode.SRC_ATOP);
                cardBinding.ivPlayVideo.visibility = View.GONE
            }
            cardBinding.ibFirst.iconGravity = MaterialButton.ICON_GRAVITY_START
            cardBinding.ibFirst.icon =
                AppCompatResources.getDrawable(context, R.drawable.ic_answer_question)
            //boolean isTagLabelHidden = false;
            if (activity.activityType != null) {
                when (activity.activityType) {
                    "1to1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text =
                                context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.tag =
                                context.getString(R.string.click_action_start_game)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst =
                                AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text =
                                context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag =
                                context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text =
                                    context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.tag =
                                    context.getString(R.string.click_action_start_game)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context,
                                    R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context,
                                    R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text =
                                    context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag =
                                    context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context,
                                    R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.colorAccent)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text =
                                    context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag =
                                    context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst =
                                    AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "Dto1" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text =
                                context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag =
                                context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst =
                                AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text =
                                context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag =
                                context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Waiting" || activity.userParticipationStatus == "Pending" || activity.userParticipationStatus == "Settled") {
                                cardBinding.ibSecond.text =
                                    context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.tag =
                                    context.getString(R.string.click_action_ask_friends)
                                cardBinding.ibSecond.icon = AppCompatResources.getDrawable(
                                    context,
                                    R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableSecond = AppCompatResources.getDrawable(
                                    context,
                                    R.drawable.ic_ask_question
                                )
                                cardBinding.ibSecond.icon = drawableSecond
                                cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            }
                            if (activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text =
                                    context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag =
                                    context.getString(R.string.click_action_answer)
                                //highlight colors
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context,
                                    R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.colorAccent)
                            } else if (activity.userParticipationStatus != "None") {
                                cardBinding.ibFirst.text =
                                    context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag =
                                    context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                //highlight colors
                                val drawableFirst =
                                    AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            } else {
                                cardBinding.ibFirst.visibility = View.INVISIBLE
                            }
                        }
                    }
                    "1toM" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        //changed for My Ques on 30 Sept 12:41 Pm
                        // if (PlayGroupID.equals("ZZZ") || PlayGroupID.equals("YYY")) {
                        if (PlayGroupID == "ZZZ") {
                            //isTagLabelHidden = true;
                            cardBinding.ibSecond.text =
                                context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag =
                                context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE
                            //highlight colors
                            val drawableSecond =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst =
                                AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            cardBinding.ibFirst.text =
                                context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.tag =
                                context.getString(R.string.click_action_all_answers)
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibSecond.text =
                                context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.tag =
                                context.getString(R.string.click_action_ask_friends)
                            cardBinding.ibSecond.icon =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.visibility = View.VISIBLE

                            //highlight colors
                            val drawableSecond =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            //highlight colors
                            if (activity.userParticipationStatus == "None" || activity.userParticipationStatus == "Pending") {
                                cardBinding.ibFirst.text =
                                    context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.tag =
                                    context.getString(R.string.click_action_answer)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst = AppCompatResources.getDrawable(
                                    context,
                                    R.drawable.ic_answer_question
                                )
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.colorAccent)
                            } else if (activity.userParticipationStatus == "Settled") {
                                cardBinding.ibFirst.text =
                                    context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.tag =
                                    context.getString(R.string.click_action_all_answers)
                                cardBinding.ibFirst.visibility = View.VISIBLE
                                //highlight colors
                                val drawableFirst =
                                    AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                                cardBinding.ibFirst.icon = drawableFirst
                                cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                            }

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_answers));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Card" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text =
                            context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.tag =
                            context.getString(R.string.click_action_send_to_friends)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst =
                            AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.colorAccent)
                        cardBinding.ibSecond.text =
                            context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.tag =
                            context.getString(R.string.click_action_all_cards)
                        cardBinding.ibSecond.visibility = View.VISIBLE
                        val drawableSecond =
                            AppCompatResources.getDrawable(context, R.drawable.ic_show_all)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }
                    "Survey" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        cardBinding.ibFirst.text =
                            context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_see_stats)
                        cardBinding.ibFirst.visibility = View.VISIBLE
                        val drawableFirst =
                            AppCompatResources.getDrawable(context, R.drawable.ic_btn_survey)
                        cardBinding.ibFirst.icon = drawableFirst


//                         cardBinding.ibSecond.setText(context.getString(R.string.click_action_all_cards));
//                         cardBinding.ibSecond.setTag(context.getString(R.string.click_action_all_cards));
                        cardBinding.ibSecond.visibility = View.INVISIBLE
                    }
                    "External" -> {
                        cardBinding.cardIvActivityType.visibility = View.INVISIBLE
                        cardBinding.ibFirst.text =
                            context.getString(R.string.click_action_show_more)
                        cardBinding.ibFirst.tag = context.getString(R.string.click_action_show_more)
                        val drawableFirst =
                            AppCompatResources.getDrawable(context, R.drawable.ic_open_internet)
                        cardBinding.ibFirst.icon = drawableFirst
                        cardBinding.ibFirst.setIconTintResource(R.color.colorAccent)
                        if (!TextUtils.isEmpty(activity.youTubeVideoId) || activity.questionVideoUrl == null || activity.questionVideoUrl.equals(
                                "",
                                ignoreCase = true
                            )
                        ) {
                            cardBinding.ibFirst.visibility = View.INVISIBLE
                        } else {
                            cardBinding.ibFirst.visibility = View.VISIBLE
                        }
                        cardBinding.ibSecond.text = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.tag = context.getString(R.string.click_action_share)
                        cardBinding.ibSecond.visibility = View.VISIBLE
                        val drawableSecond =
                            AppCompatResources.getDrawable(context, R.drawable.ic_share_card)
                        cardBinding.ibSecond.icon = drawableSecond
                        cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                    }
                    "Task" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text =
                                context.getString(R.string.click_action_share_experience)
                            cardBinding.ibFirst.tag =
                                context.getString(R.string.click_action_share_experience)
                            cardBinding.ibSecond.text =
                                context.getString(R.string.click_action_read_experience)
                            cardBinding.ibSecond.tag =
                                context.getString(R.string.click_action_read_experience)

                            //highlight colors
                            val drawableSecond = AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_answer_question
                            )
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibFirst.icon = drawableFirst
                            cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                        } else {
                            when (activity.userParticipationStatus) {
                                "None" -> {
                                    cardBinding.ibFirst.text =
                                        context.getString(R.string.click_action_i_will_do_it)
                                    cardBinding.ibFirst.tag =
                                        context.getString(R.string.click_action_i_will_do_it)
                                    //highlight colors
                                    val drawableFirst = AppCompatResources.getDrawable(
                                        context,
                                        R.drawable.ic_commit
                                    )
                                    cardBinding.ibFirst.icon = drawableFirst
                                    cardBinding.ibFirst.setIconTintResource(R.color.colorAccent)
                                }
                                "Pending" -> {
                                    cardBinding.ibFirst.text =
                                        context.getString(R.string.click_action_share_experience)
                                    cardBinding.ibFirst.tag =
                                        context.getString(R.string.click_action_share_experience)
                                    //highlight colors
                                    val drawableFirst = AppCompatResources.getDrawable(
                                        context,
                                        R.drawable.ic_answer_question
                                    )
                                    cardBinding.ibFirst.icon = drawableFirst
                                    cardBinding.ibFirst.setIconTintResource(R.color.colorAccent)
                                }
                                "Settled" -> {
                                    cardBinding.ibFirst.text =
                                        context.getString(R.string.click_action_read_experience)
                                    cardBinding.ibFirst.tag =
                                        context.getString(R.string.click_action_read_experience)
                                    //highlight colors
                                    val drawableFirst = AppCompatResources.getDrawable(
                                        context,
                                        R.drawable.ic_show_all
                                    )!!
                                    DrawableCompat.setTint(
                                        drawableFirst,
                                        ContextCompat.getColor(context, R.color.color_black)
                                    )
                                    cardBinding.ibFirst.icon = drawableFirst
                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                                }
                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_experience));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                    "Message" -> {
                        cardBinding.cardIvActivityType.visibility = View.VISIBLE
                        if (PlayGroupID == "ZZZ" || PlayGroupID == "YYY") {
                            //isTagLabelHidden = true;
                            cardBinding.ibFirst.text =
                                context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.tag =
                                context.getString(R.string.click_action_send_now)
                            cardBinding.ibFirst.icon =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.text =
                                context.getString(R.string.click_action_read_messages)
                            cardBinding.ibSecond.tag =
                                context.getString(R.string.click_action_read_messages)

                            //highlight colors
                            val drawableSecond =
                                AppCompatResources.getDrawable(context, R.drawable.ic_ask_question)
                            cardBinding.ibSecond.icon = drawableSecond
                            cardBinding.ibSecond.setIconTintResource(R.color.color_black)
                            val drawableFirst = AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_ask_question
                            )!!
                            DrawableCompat.setTint(
                                drawableFirst,
                                ContextCompat.getColor(context, R.color.color_black)
                            )
                            cardBinding.ibFirst.icon = drawableFirst
                            //ZZZ or YYY may not be getting used. @neeraj's comment
                        } else {
                            when (activity.userParticipationStatus) {
                                "None" -> {
                                    cardBinding.ibFirst.text =
                                        context.getString(R.string.click_action_i_want_to_send)
                                    cardBinding.ibFirst.tag =
                                        context.getString(R.string.click_action_i_want_to_send)
                                    //highlight colors
                                    val drawableFirst = AppCompatResources.getDrawable(
                                        context,
                                        R.drawable.ic_commit
                                    )
                                    cardBinding.ibFirst.icon = drawableFirst
                                    cardBinding.ibFirst.setIconTintResource(R.color.colorAccent)
                                }
                                "Pending" -> {
                                    cardBinding.ibFirst.text =
                                        context.getString(R.string.click_action_send_now)
                                    cardBinding.ibFirst.tag =
                                        context.getString(R.string.click_action_send_now)
                                    //highlight colors
                                    //highlight colors
                                    val drawableFirst = AppCompatResources.getDrawable(
                                        context,
                                        R.drawable.ic_answer_question
                                    )
                                    cardBinding.ibFirst.icon = drawableFirst
                                    cardBinding.ibFirst.setIconTintResource(R.color.colorAccent)
                                }
                                "Settled" -> {
                                    cardBinding.ibFirst.text =
                                        context.getString(R.string.click_action_read_messages)
                                    cardBinding.ibFirst.tag =
                                        context.getString(R.string.click_action_read_messages)
                                    //highlight colors
                                    val drawableFirst = AppCompatResources.getDrawable(
                                        context,
                                        R.drawable.ic_show_all
                                    )
                                    cardBinding.ibFirst.icon = drawableFirst
                                    cardBinding.ibFirst.setIconTintResource(R.color.color_black)
                                }
                            }
                            cardBinding.ibSecond.visibility = View.INVISIBLE

//                        if (activity.getUserParticipationStatus().equals("Settled")) {
//                            cardBinding.ibSecond.setText(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setTag(context.getString(R.string.click_action_read_messages));
//                            cardBinding.ibSecond.setVisibility(View.VISIBLE);
//                        } else {
//                            cardBinding.ibSecond.setVisibility(View.INVISIBLE);
//                        }
                        }
                    }
                }
            }

//            if (isTagLabelHidden) {
//                cardBinding.cardTooltip.setVisibility(View.GONE);
//                cardBinding.tvTooltip.setVisibility(View.GONE);
//            } else {
//
//            }
            if (activity.friendCount != null) {
                if (activity.friendCount!! > 0) {
                    cardBinding.cardTooltip.visibility = View.VISIBLE
                    cardBinding.tvTooltip.text = activity.friendCount.toString()
                } else {
                    cardBinding.tvTooltip.text = " "
                    cardBinding.cardTooltip.visibility = View.GONE
                }
            } else {
                cardBinding.tvTooltip.text = " "
                cardBinding.cardTooltip.visibility = View.GONE
            }
            val color = getTagColor(activity)
            if (color != -1) {

//                    if (activity.getFriendCount() !=null && activity.getFriendCount() == 0) {
//                        cardBinding.tvTooltip.setText(" ");
//                        cardBinding.cardTooltip.setVisibility(View.GONE);
//                    } else {
//                        cardBinding.cardTooltip.setVisibility(View.VISIBLE);
//                        cardBinding.tvTooltip.setText(String.valueOf(activity.getFriendCount()));
//                    }
                //cardBinding.cardTooltip.setVisibility(View.VISIBLE);
                cardBinding.tvTooltip.visibility = View.VISIBLE
                cardBinding.tvTooltip.supportBackgroundTintList =
                    AppCompatResources.getColorStateList(context, color)
            } else {
                //TODO Neeraj put this condition to make sure the tag is visible in one to many, if friend count is greater than 0 even if status is settled
                if (!activity.activityType.equals("1toM", ignoreCase = true)) {
                    cardBinding.tvTooltip.visibility = View.INVISIBLE
                    cardBinding.cardTooltip.visibility = View.INVISIBLE
                }
            }
            when (preferenceHelper.getInt(Constants.KEY_SELECTED_FILTER_INDEX)) {
                -1, 0, 1 -> if (activity.activityType.equals("1toM", ignoreCase = true)
                    && (activity.userParticipationStatus.equals("None", ignoreCase = true)
                            || activity.userParticipationStatus.equals(
                        "Pending",
                        ignoreCase = true
                    ))
                ) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                    if (!(activityPlayGroupId().equals("0", ignoreCase = true)
                                || activityPlayGroupId().equals("1", ignoreCase = true)
                                || activityPlayGroupId().equals("2", ignoreCase = true)
                                || activityPlayGroupId().equals("3", ignoreCase = true))
                    ) {
                        if (activity.userParticipationStatus.equals("Pending", ignoreCase = true)) {
                            cardBinding.ibMenu.visibility = View.GONE
                            cardBinding.cardActivityRemove.visibility = View.VISIBLE
                        } else {
                            cardBinding.ibMenu.visibility = View.GONE
                        }
                    }
                } else if (activity.activityType.equals("Survey", ignoreCase = true)) {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                3 -> if (activity.isQuestionEditable) {
                    cardBinding.ibMenu.visibility = View.VISIBLE
                } else {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
                else -> {
                    cardBinding.ibMenu.visibility = View.GONE
                    cardBinding.cardActivityRemove.visibility = View.GONE
                }
            }
        }

        //        @Override
        //        public void onClick(View v) {
        //            if (onItemClickListener != null && items != null && !items.isEmpty()) {
        //                onItemClickListener.onItemClick(new Triple<>(items.get(getBindingAdapterPosition()), CLICK_BUTTON, (String) v.getTag()));
        //            }
        //        }
        private fun activityPlayGroupId(): String? {
            var activityPlayGroupId: String? = null
            if (PlayGroupID != null) {
                val playGroupId: String = PlayGroupID
                activityPlayGroupId = if (playGroupId.equals("FFF", ignoreCase = true)) {
                    "0"
                } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                    "1"
                } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                    "2"
                } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                    "3"
                } else {
                    playGroupId
                }
            }
            return activityPlayGroupId
        }

        init {
            cardBinding = ItemQuestionCardBinding.bind(itemView)
            context = itemView.context
            cardBinding.ibFirst.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition],
                        CLICK_BUTTON,
                        cardBinding.ibFirst.tag.toString()
                    )
                )
            })
            cardBinding.ibSecond.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition],
                        CLICK_BUTTON,
                        cardBinding.ibSecond.tag.toString()
                    )
                )
            })
            cardBinding.ivActivityType.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_ACTIVITY, v.tag.toString()
                    )
                )
            })
            cardBinding.ibMenu.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MENU, v.tag.toString()
                    )
                )
            })
            cardBinding.cardActivityRemove.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_REMOVE, "Remove"
                    )
                )
            })
            cardBinding.tvExternalContent.setOnClickListener(ViewClickListener { v: View ->
                onItemClickListener.onItemClick(
                    Triple(
                        items[bindingAdapterPosition], CLICK_MEDIA, v.tag.toString()
                    )
                )
            })
            cardBinding.containerQuestion.setOnClickListener(ViewClickListener { v: View? ->
                val item = items[bindingAdapterPosition]!!
                if (item.activityType != null) {
                    if (item.activityType.equals("1toM", ignoreCase = true)) {
                        onItemClickListener.onItemClick(
                            Triple(
                                items[bindingAdapterPosition],
                                CLICK_BUTTON,
                                cardBinding.ibFirst.tag.toString()
                            )
                        )
                    } else {
                        if (cardBinding.ibFirst.visibility == View.VISIBLE) {
                            onItemClickListener.onItemClick(
                                Triple(
                                    items[bindingAdapterPosition],
                                    CLICK_BUTTON,
                                    cardBinding.ibFirst.tag.toString()
                                )
                            )
                        } else {
                            onItemClickListener.onItemClick(
                                Triple(
                                    items[bindingAdapterPosition],
                                    CLICK_BUTTON,
                                    cardBinding.ibSecond.tag.toString()
                                )
                            )
                        }
                    }
                }
            })
            cardBinding.tvTooltip.setOnClickListener(ViewClickListener { v: View? ->
                ViewTooltip
                    .on(v)
                    .text(getTagInfoText(items[bindingAdapterPosition]!!))
                    .textColor(Color.WHITE)
                    .textSize(1, 16f)
                    .autoHide(true, 1000)
                    .color(itemView.context.resources.getColor(getTagColor(items[bindingAdapterPosition]!!)))
                    .position(ViewTooltip.Position.BOTTOM)
                    .show()
            })
        }
    }

    companion object {
        const val CLICK_ACTIVITY = 0
        const val CLICK_WHOLE_ITEM = 1
        const val CLICK_BUTTON = 2
        const val CLICK_MENU = 3
        const val CLICK_MEDIA = 4
        const val CLICK_SHARE = 5
        const val CLICK_REMOVE = 10
    }

    init {
        PlayGroupID = playGroup.playGroupId
        preferenceHelper = sharedPreferenceHelper
    }
}