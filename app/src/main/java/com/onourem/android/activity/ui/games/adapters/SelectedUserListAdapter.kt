package com.onourem.android.activity.ui.games.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SelectedUserListAdapter(
    private val visibleDataList: List<UserList?>,
    private val onItemClickListener: OnItemClickListener<UserList>
) : RecyclerView.Adapter<SelectedUserListAdapter.FriendViewHolder>() {
    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_selected_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(visibleDataList[position])
    }

    override fun getItemCount(): Int {
        return visibleDataList.size
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: AppCompatTextView
        private val rivImageView: AppCompatImageView
        private val ibClose: AppCompatImageButton
        @SuppressLint("SetTextI18n")
        fun bind(user: UserList?) {
            if (user != null) {
                tvName.text = user.firstName
                Glide.with(itemView.context)
                    .load(user.profilePicture)
                    .apply(options)
                    .into(rivImageView)
            }
        }

        init {
            tvName = itemView.findViewById(R.id.tvName)
            rivImageView = itemView.findViewById(R.id.rivProfile)
            ibClose = itemView.findViewById(R.id.ibClose)

//            itemView.setOnClickListener(new ViewClickListener(v -> {
//                UserList user = visibleDataList.get(getBindingAdapterPosition());
//                if (onItemClickListener != null) {
//                    onItemClickListener.onItemClick(user);
//                }
//            }));
            ibClose.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataList[bindingAdapterPosition]
                onItemClickListener.onItemClick(user)
            })
        }
    }
}