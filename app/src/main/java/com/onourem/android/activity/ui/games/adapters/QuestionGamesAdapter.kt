package com.onourem.android.activity.ui.games.adapters

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.DataItem
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class QuestionGamesAdapter(
    private val items: MutableList<DataItem<Any>>,
    private val onItemClickListener: OnItemClickListener<DataItem<Any>>?,
    private val onMoreItemClickListener: OnItemClickListener<DataItem<Any>>?,
    private var idToHighlight: String?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    override fun getItemViewType(position: Int): Int {
//        return if (items[position].isSection) {
//            1
//        } else {
//            2
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return QuestionGamesItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_question_games_group, parent, false)
            )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as QuestionGamesItemViewHolder).bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun resetData(dataItems: List<DataItem<Any>>?) {
        items.clear()
        items.addAll(dataItems!!)
        notifyDataSetChanged()
    }

//    internal class QuestionGamesHeaderItemViewHolder  //        private final LayoutQuestionGamesHeaderBinding itemBinding;
//    //        private final Drawable drawable1;
//    //        private final Drawable drawable2;
//    //        private final Drawable drawable3;
//        (itemView: View) //        public void bind() {
//    //
//    //            itemBinding.tvNewMembers.setCompoundDrawablesRelative(drawable1, null, null, null);
//    //
//    //            itemBinding.tvNewQuestions.setCompoundDrawablesRelative(drawable2, null, null, null);
//    //
//    //            itemBinding.tvNewAnswers.setCompoundDrawablesRelative(drawable3, null, null, null);
//    //        }
//        : RecyclerView.ViewHolder(itemView)

//    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val tvSectionName: AppCompatTextView
//        @SuppressLint("SetTextI18n")
//        fun bind(dataItem: DataItem<Any>) {
//            tvSectionName.text = dataItem.data as String
//
//            tvSectionName.setOnClickListener(ViewClickListener { v: View? ->
//                onItemClickListener?.onItemClick(
//                    dataItem
//                )
//            })
//        }
//
//        init {
//            tvSectionName = itemView.findViewById(R.id.tvSectionName)
//        }
//    }

    internal inner class QuestionGamesItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val constraintLayout: ConstraintLayout
        private val clGroups: ConstraintLayout
        private val clSection: ConstraintLayout
        private val tvTitle: AppCompatTextView
        private val tvSectionName: AppCompatTextView
        private val tvSubHeader: AppCompatTextView
        private val tvNewMembersCount: AppCompatTextView
        private val tvNewAnswersCount: AppCompatTextView
        private val tvNewQuestionsCount: AppCompatTextView
        private val ivMoreMenu: AppCompatImageView
        private val ivIcon: AppCompatImageView
        private val ivIconVerified: RoundedImageView
        fun bind(dataItem: DataItem<Any>) {
            if (dataItem.isSection) {
                clSection.visibility  = View.VISIBLE
                clGroups.visibility  = View.GONE
                tvSectionName.text = dataItem.data as String?
                itemView.setOnClickListener(ViewClickListener { v: View? ->
                    onItemClickListener?.onItemClick(
                        dataItem as DataItem<Any>?
                    )
                })
            } else if (dataItem.isGroup) {

                clSection.visibility  = View.GONE
                clGroups.visibility  = View.VISIBLE
                val playGroup = dataItem.data as PlayGroup

                //CR Point 1.8 Highlight O club
                if (!TextUtils.isEmpty(playGroup.playGroupId) && playGroup.playGroupId.equals(
                        idToHighlight, ignoreCase = true
                    )
                ) {
                    itemView.postDelayed({
                        animateChange(constraintLayout)
                        idToHighlight = null
                    }, 1000)
                }
                if (!TextUtils.isEmpty(playGroup.playGroupTypeId) && playGroup.playGroupTypeId.equals(
                        "1",
                        ignoreCase = true
                    )
                ) {
                    ivIconVerified.visibility = View.VISIBLE
                } else {
                    ivIconVerified.visibility = View.GONE
                }
                tvTitle.text = playGroup.playGroupName
                tvNewMembersCount.visibility = View.INVISIBLE
                val newMembersCount = playGroup.newMemeberNumber
                if (newMembersCount > 0) {
                    tvNewMembersCount.visibility = View.VISIBLE
                    if (newMembersCount > 99) {
                        tvNewMembersCount.text = "99+"
                    } else {
                        tvNewMembersCount.text = newMembersCount.toString()
                    }
                }
                tvNewAnswersCount.visibility = View.INVISIBLE
                val newAnswersCount = playGroup.newAnswerNumber
                if (newAnswersCount > 0) {
                    tvNewAnswersCount.visibility = View.VISIBLE
                    if (newAnswersCount > 99) {
                        tvNewAnswersCount.text = "99+"
                    } else {
                        tvNewAnswersCount.text = newAnswersCount.toString()
                    }
                }
                tvNewQuestionsCount.visibility = View.INVISIBLE
                val newQuestionsCount = playGroup.newquestionNumber
                if (newQuestionsCount > 0) {
                    tvNewQuestionsCount.visibility = View.VISIBLE
                    if (newQuestionsCount > 99) {
                        tvNewQuestionsCount.text = "99+"
                    } else {
                        tvNewQuestionsCount.text = newQuestionsCount.toString()
                    }
                }
                if (newMembersCount == 0 && newAnswersCount == 0 && newQuestionsCount == 0) {
                    tvNewMembersCount.visibility = View.GONE
                    tvNewAnswersCount.visibility = View.GONE
                    tvNewQuestionsCount.visibility = View.GONE
                }
                if (playGroup.isUserAdmin.equals("Y", ignoreCase = true)) {
                    tvSubHeader.visibility = View.VISIBLE
                    tvSubHeader.text = itemView.context.getString(R.string.label_admin)
                } else if (playGroup.isUserAdmin.equals("C", ignoreCase = true)) {
                    tvSubHeader.visibility = View.VISIBLE
                    tvSubHeader.text = itemView.context.getString(R.string.label_creator)
                } else {
                    tvSubHeader.visibility = View.GONE
                }
                if (dataItem.isHasMoreAction) {
                    ivMoreMenu.visibility = View.VISIBLE
                    ivMoreMenu.setOnClickListener(ViewClickListener { v: View? ->
                        onMoreItemClickListener?.onItemClick(
                            dataItem as DataItem<Any>
                        )
                    })
                } else {
                    ivMoreMenu.visibility = View.INVISIBLE
                }
                ivIcon.setImageResource(dataItem.icon)
                itemView.setOnClickListener(ViewClickListener { v: View? ->
                    onItemClickListener?.onItemClick(
                        dataItem as DataItem<Any>
                    )
                })
            } else {
                val playGroup = dataItem.data as PlayGroup
                tvTitle.text = playGroup.playGroupName
                ivIcon.setImageResource(dataItem.icon)
                //                tvNewMembersCount.setVisibility(dataItem.isGroup() ? View.INVISIBLE : View.GONE);
//                if (!TextUtils.isEmpty(playGroup.getNewMemeberNumber()) && !playGroup.getNewMemeberNumber().equalsIgnoreCase("0")) {
//                    tvNewMembersCount.setVisibility(View.VISIBLE);
//                    String count = playGroup.getNewMemeberNumber();
//                    if (!TextUtils.isEmpty(count) && !count.equalsIgnoreCase("0")) {
//                        if (Integer.parseInt(count) > 99) {
//                            count = "99+";
//                        }
//                    }
//                    tvNewMembersCount.setText(count);
//                }

//                tvNewAnswersCount.setVisibility(dataItem.isGroup() ? View.INVISIBLE : View.GONE);
//                if (!TextUtils.isEmpty(playGroup.getNewAnswerNumber()) && !playGroup.getNewAnswerNumber().equalsIgnoreCase("0")) {
//                    tvNewAnswersCount.setVisibility(View.VISIBLE);
//                    String count = playGroup.getNewAnswerNumber();
//                    if (!TextUtils.isEmpty(count) && !count.equalsIgnoreCase("0")) {
//                        if (Integer.parseInt(count) > 99) {
//                            count = "99+";
//                        }
//                    }
//                    tvNewAnswersCount.setText(count);
//                }

//                tvNewQuestionsCount.setVisibility(dataItem.isGroup() ? View.INVISIBLE : View.GONE);
//                if (!TextUtils.isEmpty(playGroup.getNewquestionNumber()) && !playGroup.getNewquestionNumber().equalsIgnoreCase("0")) {
//                    tvNewQuestionsCount.setVisibility(View.VISIBLE);
//                    String count = playGroup.getNewquestionNumber();
//                    if (!TextUtils.isEmpty(count) && !count.equalsIgnoreCase("0")) {
//                        if (Integer.parseInt(count) > 99) {
//                            count = "99+";
//                        }
//                    }
//                    tvNewQuestionsCount.setText(count);
//                }
                tvNewMembersCount.visibility = View.INVISIBLE
                val newMembersCount = playGroup.newMemeberNumber
                if (newMembersCount > 0) {
                    tvNewMembersCount.visibility = View.VISIBLE
                    if (newMembersCount > 99) {
                        tvNewMembersCount.text = "99+"
                    } else {
                        tvNewMembersCount.text = newMembersCount.toString()
                    }
                }
                tvNewAnswersCount.visibility = View.INVISIBLE
                val newAnswersCount = playGroup.newAnswerNumber
                if (newAnswersCount > 0) {
                    tvNewAnswersCount.visibility = View.VISIBLE
                    if (newAnswersCount > 99) {
                        tvNewAnswersCount.text = "99+"
                    } else {
                        tvNewAnswersCount.text = newAnswersCount.toString()
                    }
                }
                tvNewQuestionsCount.visibility = View.INVISIBLE
                val newQuestionsCount = playGroup.newquestionNumber
                if (newQuestionsCount > 0) {
                    tvNewQuestionsCount.visibility = View.VISIBLE
                    if (newQuestionsCount > 99) {
                        tvNewQuestionsCount.text = "99+"
                    } else {
                        tvNewQuestionsCount.text = newQuestionsCount.toString()
                    }
                }
                if (newMembersCount == 0 && newAnswersCount == 0 && newQuestionsCount == 0) {
                    tvNewMembersCount.visibility = View.GONE
                    tvNewAnswersCount.visibility = View.GONE
                    tvNewQuestionsCount.visibility = View.GONE
                }
                tvSubHeader.visibility = View.GONE
                ivMoreMenu.visibility = View.INVISIBLE
                itemView.setOnClickListener(ViewClickListener { v: View? ->
                    onItemClickListener?.onItemClick(
                        dataItem
                    )
                })
            }
        }

        fun animateChange(constraintLayout: ConstraintLayout) {
            val colorFrom =
                ContextCompat.getColor(constraintLayout.context, R.color.color_highlight_game_post)
            val colorTo = ContextCompat.getColor(constraintLayout.context, R.color.color_white)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 500 // milliseconds
            colorAnimation.startDelay = 300 // milliseconds
            colorAnimation.addUpdateListener { animator: ValueAnimator ->
                constraintLayout.setBackgroundColor(
                    animator.animatedValue as Int
                )
            }
            colorAnimation.repeatCount = 1
            colorAnimation.start()
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvSectionName = itemView.findViewById(R.id.tvSectionName)
            tvSubHeader = itemView.findViewById(R.id.tvSubHeader)
            tvNewMembersCount = itemView.findViewById(R.id.tvNewMembersCount)
            tvNewAnswersCount = itemView.findViewById(R.id.tvNewAnswersCount)
            tvNewQuestionsCount = itemView.findViewById(R.id.tvNewQuestionsCount)
            ivMoreMenu = itemView.findViewById(R.id.ivMore)
            ivIcon = itemView.findViewById(R.id.checkIcon)
            ivIconVerified = itemView.findViewById(R.id.ivIconVerified)
            constraintLayout = itemView.findViewById(R.id.parent)
            clSection = itemView.findViewById(R.id.clSection)
            clGroups = itemView.findViewById(R.id.clGroups)
        }
    }
}