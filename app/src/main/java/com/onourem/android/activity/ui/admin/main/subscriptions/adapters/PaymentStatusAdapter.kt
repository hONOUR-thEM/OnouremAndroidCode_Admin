package com.onourem.android.activity.ui.admin.main.subscriptions.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemPaymentStatusRowBinding
import com.onourem.android.activity.databinding.ItemSettingsRowBinding
import com.onourem.android.activity.models.PaymentDetailItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class PaymentStatusAdapter(
    private val PaymentDetailItems: ArrayList<PaymentDetailItem>,
    private val onItemClickListener: OnItemClickListener<PaymentDetailItem>?
) : RecyclerView.Adapter<PaymentStatusAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_payment_status_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = PaymentDetailItems[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return PaymentDetailItems.size
    }

    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemPaymentStatusRowBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        fun bind(item: PaymentDetailItem) {
            rowBinding!!.tvName.text = item.key
            rowBinding.tvValue.text = item.value
        }

        init {
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val paymentDetailItem = PaymentDetailItems[bindingAdapterPosition]
                    if (paymentDetailItem.isEnabled) {
                        onItemClickListener.onItemClick(paymentDetailItem)
                    }
                }
            })
        }
    }
}