package com.onourem.android.activity.ui.games.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.AppLanguageList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SingleChoiceLanguageAdapter(
    private val context: Context,
    private val alertDialog: AlertDialog,
    private var categoryList: ArrayList<AppLanguageList>?,
    private val onItemClickListener: OnItemClickListener<AppLanguageList?>?
) : RecyclerView.Adapter<SingleChoiceLanguageAdapter.SingleViewHolder>() {
    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = -1
    @SuppressLint("NotifyDataSetChanged")
    fun setCategoryList(categoryList: ArrayList<AppLanguageList>?) {
        this.categoryList = ArrayList()
        this.categoryList = categoryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_question_options, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(categoryList!![position])
    }

    override fun getItemCount(): Int {
        return categoryList!!.size
    }

    val selected: AppLanguageList?
        get() = if (checkedPosition != -1) {
            categoryList!![checkedPosition]
        } else null

    fun updateItem(itemId: String) {
        val position: Int
        if (categoryList != null && !categoryList!!.isEmpty()) {
            for (item in categoryList!!) {
                if (item.id == itemId.toInt()) {
                    position = categoryList!!.indexOf(item)
                    checkedPosition = position
                    notifyItemChanged(position)
                    break
                }
            }
        }
    }

    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView
        private val imageView: ImageView
        fun bind(surveyOption: AppLanguageList) {
            if (checkedPosition == -1) {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_radio_unchecked
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
                            R.drawable.ic_radio_unchecked
                        )
                    )
                }
            }
            textView.text = surveyOption.languageName
            itemView.setOnClickListener(ViewClickListener { view: View? ->
                if (checkedPosition != bindingAdapterPosition) {
                    checkedPosition = bindingAdapterPosition
                    notifyDataSetChanged()
                    onItemClickListener?.onItemClick(selected)
                }
                alertDialog.dismiss()
            })
        }

        init {
            textView = itemView.findViewById(R.id.textView)
            imageView = itemView.findViewById(R.id.imageView)
        }
    }
}