package com.onourem.android.activity.ui.survey.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.SurveyOption
import com.onourem.android.activity.ui.survey.adapters.MultiChoiceAdapter.MultiViewHolder
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class MultiChoiceAdapter(private val context: Context, var all: ArrayList<SurveyOption>) :
    RecyclerView.Adapter<MultiViewHolder>() {
    fun setSurveyOptions(surveyOptions: ArrayList<SurveyOption>) {
        all = ArrayList()
        all = surveyOptions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MultiViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_question_options, viewGroup, false)
        return MultiViewHolder(view)
    }

    override fun onBindViewHolder(multiViewHolder: MultiViewHolder, position: Int) {
        multiViewHolder.bind(all[position])
    }

    override fun getItemCount(): Int {
        return all.size
    }

    val selected: ArrayList<SurveyOption>
        get() {
            val selected = ArrayList<SurveyOption>()
            for (i in all.indices) {
                if (all[i].isSelected) {
                    selected.add(all[i])
                }
            }
            return selected
        }

    inner class MultiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView
        private val imageView: ImageView
        fun bind(surveyOption: SurveyOption) {
            //imageView.setVisibility(surveyOption.isSelected() ? View.VISIBLE : View.GONE);
            if (surveyOption.isSelected) {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_checkbox_on_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_checkbox_off_background
                    )
                )
            }
            textView.text = surveyOption.name
            itemView.setOnClickListener(ViewClickListener { view: View? ->
                surveyOption.isSelected = !surveyOption.isSelected
                notifyDataSetChanged()
            })
        }

        init {
            textView = itemView.findViewById(R.id.textView)
            imageView = itemView.findViewById(R.id.imageView)
        }
    }
}