package com.onourem.android.activity.ui.survey.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.SurveyCOList
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class AllSurveyAdapter(
    private val context: Context,
    surveyOptions: List<SurveyCOList?>?,
    private val onItemClickListener: OnItemClickListener<Pair<SurveyCOList, Int>>?
) : PaginationRVAdapter<SurveyCOList?>(
    surveyOptions!!
) {
    override fun emptyLoadingItem(): SurveyCOList {
        return SurveyCOList()
    }

    override fun emptyFooterItem(): SurveyCOList {
        return SurveyCOList()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return SurveyOptionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_all_surveys, parent, false)
        )
    }

    internal inner class SurveyOptionViewHolder(view: View) : BaseViewHolder(view) {
        private var tvAllSurveyItem: AppCompatTextView
        private var ivActivityType: AppCompatImageView
        var ibFirst: MaterialButton
        override fun onBind(position: Int) {
            val options = items[position]!!
            tvAllSurveyItem.text = options.surveyText
//            if (!TextUtils.isEmpty(options.userAnserForSurvey) && options.userAnserForSurvey == "N") {
//                tvSurveyVerticalBar.background =
//                    AppCompatResources.getDrawable(
//                        context,
//                        R.drawable.bg_survey_vertical_view_green
//                    )
//            } else {
//                tvSurveyVerticalBar.background =
//                    AppCompatResources.getDrawable(context, R.drawable.bg_survey_vertical_view)
//            }
        }

        init {
            tvAllSurveyItem = view.findViewById(R.id.tvQuestion)
            ivActivityType = view.findViewById(R.id.ivActivityType)
            ibFirst = view.findViewById(R.id.ibFirst)

            tvAllSurveyItem.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener?.onItemClick(
                    Pair(items[bindingAdapterPosition]!!, Constants.CLICK_WHOLE_ITEM)
                )
            })

            ivActivityType.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener?.onItemClick(
                    Pair(items[bindingAdapterPosition]!!, Constants.CLICK_MORE)
                )
            })

            ibFirst.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener?.onItemClick(
                    Pair(items[bindingAdapterPosition]!!, Constants.CLICK_WHOLE_ITEM)
                )
            })

        }
    }
}