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
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SingleChoiceAdapter(
    private val context: Context,
    private var surveyOptions: ArrayList<SurveyOption>
) : RecyclerView.Adapter<SingleChoiceAdapter.SingleViewHolder>() {
    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = -1
    fun setSurveyOptions(surveyOptions: ArrayList<SurveyOption>) {
        this.surveyOptions = ArrayList()
        this.surveyOptions = surveyOptions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_question_options, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(surveyOptions[position])
    }

    override fun getItemCount(): Int {
        return surveyOptions.size
    }

    val selected: SurveyOption?
        get() = if (checkedPosition != -1) {
            surveyOptions[checkedPosition]
        } else null

    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView
        private val imageView: ImageView
        fun bind(surveyOption: SurveyOption) {
            if (checkedPosition == -1) {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_checkbox_off_background
                    )
                )
            } else {
                if (checkedPosition == bindingAdapterPosition) {
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
            }
            textView.text = surveyOption.name
            itemView.setOnClickListener(ViewClickListener { view: View? ->
                if (checkedPosition != bindingAdapterPosition) {
                    checkedPosition = bindingAdapterPosition
                    notifyDataSetChanged()
                }
            })
        }

        init {
            textView = itemView.findViewById(R.id.textView)
            imageView = itemView.findViewById(R.id.imageView)
        }
    }
}