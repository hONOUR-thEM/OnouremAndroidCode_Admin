package com.onourem.android.activity.ui.circle.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemContactViewpagerBinding
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class ContactsAdapter(
    private val items: ArrayList<QuestionForContacts>,
    private val bgColor: Int,
    private val waveColor: Int,
    private val onItemClickListener: OnItemClickListener<Pair<Int, QuestionForContacts>>?
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    companion object {
        const val CLICK_UPDATE_CONTACT = 2
        const val CLICK_ADD_CONTACT = 1
        const val CLICK_DELETE_CONTACT = 0
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact_viewpager, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ContactViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemContactViewpagerBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        fun bind(settingsItem: QuestionForContacts) {

            rowBinding!!.materialCardView.setCardBackgroundColor(
                bgColor
            )
            if (settingsItem.selected) {

                rowBinding!!.tvName.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            rowBinding!!.root.context,
                            R.color.white
                        )
                    )
                )
                rowBinding!!.ivCheck.setImageDrawable(
                    AppCompatResources.getDrawable(
                        rowBinding!!.ivCheck.context,
                        R.drawable.ic_checkbox_on_background_white
                    )
                )

                rowBinding!!.materialCardView.setCardBackgroundColor(ColorStateList.valueOf(waveColor))

//                ImageViewCompat.setImageTintList(
//                    rowBinding!!.ivCheck,
//                    ColorStateList.valueOf(waveColor)
//                );

                rowBinding!!.ivCheck.visibility = View.VISIBLE
            } else {
                rowBinding!!.tvName.setTextColor(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            rowBinding!!.root.context,
                            R.color.color_black
                        )
                    )
                )
                rowBinding!!.materialCardView.setCardBackgroundColor(ColorStateList.valueOf(bgColor))
                rowBinding!!.ivCheck.setImageDrawable(
                    AppCompatResources.getDrawable(
                        rowBinding!!.ivCheck.context,
                        R.drawable.ic_check_off
                    )
                )
                rowBinding!!.ivCheck.visibility = View.VISIBLE
            }
            rowBinding!!.tvName.text = settingsItem.userName
        }

        init {

//            rowBinding!!.imageButton.setOnClickListener(ViewClickListener { v: View? ->
//                if (onItemClickListener != null) {
//                    val settingsItem = items[bindingAdapterPosition]
//                    onItemClickListener.onItemClick(Pair(CLICK_DELETE_CONTACT, settingsItem))
//                }
//            })

            rowBinding!!.materialCardView.setOnClickListener(ViewClickListener { v: View? ->

                if (items.isNotEmpty()) {
                    val position = bindingAdapterPosition
                    val clickedItem: QuestionForContacts = items[position]
                    clickedItem.selected = !clickedItem.selected
                    notifyItemChanged(position, clickedItem)
                    onItemClickListener?.onItemClick(Pair(CLICK_UPDATE_CONTACT, clickedItem))
                }
            })
        }
    }

}