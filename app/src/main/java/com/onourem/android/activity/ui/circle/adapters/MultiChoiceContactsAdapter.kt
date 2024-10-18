package com.onourem.android.activity.ui.circle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.makeramen.roundedimageview.RoundedImageView
import com.onourem.android.activity.R
import com.onourem.android.activity.ui.circle.models.ContactItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class MultiChoiceContactsAdapter(
    private val list: List<ContactItem>,
    private val onItemClickListener: OnItemClickListener<Triple<Int, ContactItem, Int>>?
) :
    RecyclerView.Adapter<MultiChoiceContactsAdapter.FriendsViewHolder>(), Filterable {

    companion object {
        //        const val CLICK_MULTIPLE_CONTACT = 1
//        const val CLICK_SINGLE_CONTACT = 0
        const val CLICK_CONTACT = 3
    }

    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var listFiltered: List<ContactItem>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        return FriendsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val userList = listFiltered[position]
        holder.bind(userList)
    }

    override fun getItemCount(): Int {
        return listFiltered.size
    }

    val selected: List<ContactItem>
        get() {
            val selected: MutableList<ContactItem> = ArrayList()
            for (item in list) {
                if (item.selected) {
                    selected.add(item)
                }
            }
            return selected
        }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                listFiltered = if (charString.isEmpty()) {
                    list
                } else {
                    val filteredList: MutableList<ContactItem> = ArrayList()
                    for (row in list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.displayName!!.lowercase(Locale.getDefault())
                                .contains(charString.lowercase(Locale.getDefault())) || row.displayName!!.lowercase(
                                Locale.getDefault()
                            )
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = listFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                listFiltered = filterResults.values as ArrayList<ContactItem>
                notifyDataSetChanged()
            }
        }
    }

    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: AppCompatImageView
        private val textView: AppCompatTextView
        private val textNumber: AppCompatTextView
        private val rivProfile: RoundedImageView
        fun bind(item: ContactItem) {

            if (item.photoUrl != null) {
                Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .apply(options)
                    .into(rivProfile)
            }

            textView.text = String.format("%s", item.displayName)

            var numbers = ""
            item.arrayListPhone.forEachIndexed { index, phoneContact ->

                numbers = if (phoneContact.type != "") {
                    numbers.plus(String.format("%s: %s", phoneContact.type, phoneContact.phone))
                } else {
                    numbers.plus(String.format("%s: %s", "Mobile", phoneContact.phone))
                }

                if (item.arrayListPhone.size - 1 != index) {
                    numbers = numbers.plus(" | ")
                }

            }
            textNumber.text = numbers
            if (item.selected) {
                if (item.isRowCreated) {
                    imageView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            imageView.context,
                            R.drawable.ic_checkbox_off_background
                        )
                    )
                } else {
                    imageView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            imageView.context,
                            R.drawable.ic_checkbox_on_background
                        )
                    )
                }
                imageView.visibility = View.VISIBLE
            } else {
                imageView.setImageDrawable(null)
                imageView.visibility = View.INVISIBLE
            }

        }

        init {
            view.setOnClickListener(ViewClickListener { v: View? ->
                val position = bindingAdapterPosition
                val clickedItem = listFiltered[position]

                if (!clickedItem.isRowCreated) {

                    onItemClickListener?.onItemClick(
                        Triple(CLICK_CONTACT, clickedItem, position)
                    )
//                    if (clickedItem.arrayListPhone.size > 1) {
//                        onItemClickListener?.onItemClick(
//                            Triple(CLICK_MULTIPLE_CONTACT, clickedItem, position)
//                        )
//                    } else {
//                        onItemClickListener?.onItemClick(
//                            Triple(CLICK_SINGLE_CONTACT, clickedItem, position)
//                        )
//                    }

                } else {
                    Toast.makeText(
                        itemView.context,
                        "This contact is already in game. You can't unselect this.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
            textView = view.findViewById(R.id.tvName)
            textNumber = view.findViewById(R.id.tvNumber)
            rivProfile = view.findViewById(R.id.rivProfile)
            imageView = view.findViewById(R.id.ivCheck)
        }
    }

    init {
        listFiltered = list
    }
}