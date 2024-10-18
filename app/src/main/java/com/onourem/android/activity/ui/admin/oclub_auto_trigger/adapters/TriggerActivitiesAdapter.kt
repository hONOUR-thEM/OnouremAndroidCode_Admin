package com.onourem.android.activity.ui.admin.oclub_auto_trigger.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemUpdateOclubCategoryBinding
import com.onourem.android.activity.models.UpdateOclubCategoryInfo

class TriggerActivitiesAdapter(private val items: ArrayList<UpdateOclubCategoryInfo>) :
    RecyclerView.Adapter<TriggerActivitiesAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_update_oclub_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val options = items[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {

        private val rowBinding: ItemUpdateOclubCategoryBinding? = DataBindingUtil.bind(itemView)
        private var dayNumberChanged = false
        private var dayPriorityChanged = false

        fun bind(item: UpdateOclubCategoryInfo) {
            rowBinding?.tvName?.text = item.name
            rowBinding?.edtDayNumber?.setText(item.dayNumber)
            rowBinding?.edtDayPriority?.setText(item.dayPriority)

            rowBinding?.edtDayNumber?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.dayNumber = s.toString()
                    dayNumberChanged = s.toString().isNotBlank()
                    validateFields()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            rowBinding?.edtDayPriority?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.dayPriority = s.toString()
                    dayPriorityChanged = s.toString().isNotBlank()
                    validateFields()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

        }

        private fun validateFields() {
            val item = items[bindingAdapterPosition]
            if (dayNumberChanged && !dayPriorityChanged) {
                // dayNumber is filled but dayPriority is empty
                rowBinding?.edtDayPriority?.error = "dayPriority"
                item.hasDayPriorityError = rowBinding?.edtDayPriority?.error.toString()
            } else {
                rowBinding?.edtDayPriority?.error = null
                item.hasDayPriorityError = ""
            }

            if (dayPriorityChanged && !dayNumberChanged) {
                // dayPriority is filled but dayNumber is empty
                rowBinding?.edtDayNumber?.error = "dayNumber"
                item.hasDayNumberError = rowBinding?.edtDayNumber?.error.toString()

            } else {
                rowBinding?.edtDayNumber?.error = null
                item.hasDayNumberError = ""
            }
        }

        init {
//            rowBinding?.parent?.setOnClickListener(ViewClickListener {
//
//            })
        }
    }

}