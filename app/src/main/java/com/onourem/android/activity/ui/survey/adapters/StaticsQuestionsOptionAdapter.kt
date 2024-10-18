package com.onourem.android.activity.ui.survey.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R

class StaticsQuestionsOptionAdapter(private val surveyOptions: ArrayList<String>) :
    RecyclerView.Adapter<StaticsQuestionsOptionAdapter.SurveyOptionViewHolder>() {
    private val element = arrayOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyOptionViewHolder {
        return SurveyOptionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_statistic_survey, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SurveyOptionViewHolder, position: Int) {
        val options = surveyOptions[position]
        holder.tvSurveyOptionLetter.text = element[position]
        holder.tvStatisticSurveyItem.text = options
    }

    override fun getItemCount(): Int {
        return surveyOptions.size
    }

    class SurveyOptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvStatisticSurveyItem: AppCompatTextView
        var tvSurveyOptionLetter: AppCompatTextView

        init {
            tvStatisticSurveyItem = view.findViewById(R.id.tvStatisticSurveyItem)
            tvSurveyOptionLetter = view.findViewById(R.id.tvSurveyOptionLetter)
        }
    }
}