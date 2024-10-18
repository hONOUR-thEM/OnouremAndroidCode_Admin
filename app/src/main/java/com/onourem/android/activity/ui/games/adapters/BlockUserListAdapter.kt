package com.onourem.android.activity.ui.games.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemBlockUserBinding
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.games.adapters.BlockUserListAdapter.BlockUserViewHolder
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class BlockUserListAdapter(
    private val visibleDataList: MutableList<UserList>,
    private val onItemClickListener: OnItemClickListener<UserList>?
) : RecyclerView.Adapter<BlockUserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockUserViewHolder {
        return BlockUserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_block_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BlockUserViewHolder, position: Int) {
        holder.bind(visibleDataList[position])
    }

    override fun getItemCount(): Int {
        return visibleDataList.size
    }

    fun removeItem(item: UserList) {
        val position = visibleDataList.indexOf(item)
        visibleDataList.remove(item)
        notifyItemRemoved(position)
    }

    inner class BlockUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val view: ItemBlockUserBinding
        private val buttonColor: ColorStateList
        private val options = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)

        fun bind(user: UserList) {
            view.tvName.text = String.format("%s %s", user.firstName, user.lastName)
            Glide.with(itemView.context)
                .load(user.profilePicture)
                .apply(options)
                .into(view.rivProfile)
            view.btnBlock.backgroundTintList = buttonColor
            Utilities.verifiedUserType(itemView.context, user.userType, view.ivIconVerified)
        }

        init {
            view = ItemBlockUserBinding.bind(itemView)
            view.btnBlock.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener?.onItemClick(
                    visibleDataList[bindingAdapterPosition]
                )
            })
            buttonColor =
                AppCompatResources.getColorStateList(itemView.context, R.color.color_black)
        }
    }
}