package com.onourem.android.activity.ui.dashboard.mood.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class MoodsChoiceAdapter(
    private val context: Context,
    private val userExpressionLists: List<UserExpressionList>
) : RecyclerView.Adapter<MoodsChoiceAdapter.SingleViewHolder>(), Filterable {
    private var userExpressionListsFiltered: List<UserExpressionList>
    private var onItemClickListener: OnItemClickListener<UserExpressionList?>? = null

    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = 0
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_card_view_mood, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(userExpressionListsFiltered[position])
    }

    override fun getItemCount(): Int {
        return userExpressionListsFiltered.size
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<UserExpressionList?>?) {
        this.onItemClickListener = onItemClickListener
    }

    val selected: UserExpressionList?
        get() = if (checkedPosition != -1) {
            userExpressionListsFiltered[checkedPosition]
        } else null

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                userExpressionListsFiltered = if (charString.isEmpty()) {
                    userExpressionLists
                } else {
                    val filteredList: MutableList<UserExpressionList> = ArrayList()
                    for (row in userExpressionLists) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.expressionText.lowercase(Locale.getDefault()).contains(
                                charString.lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = userExpressionListsFiltered
                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                userExpressionListsFiltered = filterResults.values as ArrayList<UserExpressionList>
                notifyDataSetChanged()
            }
        }
    }

    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView
        private val imageView: ImageView
        fun bind(surveyOption: UserExpressionList) {
            textView.text = surveyOption.expressionText
            val options = RequestOptions()
                .centerCrop()
                .placeholder(surveyOption.moodImage)
                .error(surveyOption.moodImage)

            val drawable = ContextCompat.getDrawable(itemView.context, surveyOption.moodImage)
            if (drawable != null){
                imageView.setImageDrawable(drawable)
            }
//            imageView.setImageResource(surveyOption.moodImage)
            //            Glide.with(context)
//                    .asBitmap()
//                    .load(surveyOption.getMoodImage())
//                    .apply(options)
//                    .into(imageView);
            itemView.setOnClickListener(ViewClickListener { view: View? ->
                if (checkedPosition != bindingAdapterPosition) {
                    checkedPosition = bindingAdapterPosition
                    //                    notifyDataSetChanged();
                }
                if (onItemClickListener != null) {
                    onItemClickListener!!.onItemClick(selected)
                }
            })
        }

        init {
            textView = itemView.findViewById(R.id.textView)
            imageView = itemView.findViewById(R.id.imageView)
        }
    }

    init {
        userExpressionListsFiltered = userExpressionLists
    }
}