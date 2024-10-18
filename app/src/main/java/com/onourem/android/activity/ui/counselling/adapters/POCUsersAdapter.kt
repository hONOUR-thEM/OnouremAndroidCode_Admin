package com.onourem.android.activity.ui.counselling.adapters

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemCounsellingPocBinding
import com.onourem.android.activity.models.OfflineInstitutionResponse
import com.onourem.android.activity.models.POCUser
import com.onourem.android.activity.ui.circle.adapters.ContactsAdapter
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import com.onourem.android.activity.ui.utils.glide.loadCircularImage
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class POCUsersAdapter(
    private val userList: List<OfflineInstitutionResponse>,
    private val onItemClickListener: OnItemClickListener<Pair<Int, OfflineInstitutionResponse>>?
) : RecyclerView.Adapter<POCUsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_counselling_poc, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private val rowBinding: ItemCounsellingPocBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions().fitCenter()

        fun bind(user: OfflineInstitutionResponse) {

            rowBinding!!.ivProfileImage.loadCircularImage(
                user.imageUrl, // or any object supported by Glide
                4f, // default is 0. If you don't change it, then the image will have no border
                Color.WHITE // optional, default is white
            )

//            rowBinding.tvLink.setOnClickListener(ViewClickListener{
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.iitk.ac.in/counsel/index.php"))
//                rowBinding.root.context.startActivity(intent)
//            })

            rowBinding.tvName.text = user.name
//            rowBinding.tvEmail.text = user.email
//            rowBinding.tvPhn.text = user.phone
            rowBinding.tvLocation.text = user.designation
//            rowBinding.designationTextView.text = user.designation
        }

        init {
            rowBinding?.btnEmail?.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val user = userList[bindingAdapterPosition]
                    onItemClickListener.onItemClick(Pair(CLICK_EMAIL, user))
                }
            })

            rowBinding?.btnCall?.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val user = userList[bindingAdapterPosition]
                    onItemClickListener.onItemClick(Pair(CLICK_PHONE, user))
                }
            })
        }
    }

    companion object {
        const val CLICK_PHONE = 2
        const val CLICK_EMAIL = 1
    }

}
