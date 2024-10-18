package com.onourem.android.activity.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.NavController
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.models.Conversation
import com.onourem.android.activity.models.UserWatchList
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.text.SimpleDateFormat
import java.util.*

class ViewPagerAdapter(
    private val context: Context,
    userWatchLists: ArrayList<UserWatchList>,
    navController: NavController,
    preferenceHelper: SharedPreferenceHelper
) : PagerAdapter() {
    private val navController: NavController
    private val preferenceHelper: SharedPreferenceHelper
    private val options = RequestOptions()
        .centerInside()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    //    RequestOptions options = RequestOptions
    //            .bitmapTransform(new GlideCircleBorderTransform(2, R.color.gray_color))
    //            .placeholder(R.drawable.default_user_profile_image)
    //            .error(R.drawable.default_user_profile_image)
    //            .diskCacheStrategy(DiskCacheStrategy.DATA);
    private val moodOptions = RequestOptions()
        .centerInside()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .placeholder(R.drawable.ic_mood_place_holder)
        .error(R.drawable.ic_mood_place_holder)
    private val userWatchLists: List<UserWatchList>
    override fun getCount(): Int {
        return userWatchLists.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_mood_today, container, false)
        val item = userWatchLists[position]
        val profile = itemView.findViewById<AppCompatImageView>(R.id.ivProfile)
        val mood = itemView.findViewById<AppCompatImageView>(R.id.ivMood)
        val date = itemView.findViewById<MaterialTextView>(R.id.tvDate)
        val nameMood = itemView.findViewById<MaterialTextView>(R.id.tvNameMood)
        date.text = getDateMonth(item.createdOn!!)
        nameMood.text = String.format("%s is %s", item.firstName, item.expressionName)
        Glide.with(context)
            .load(item.profilePictureUrl)
            .apply(options)
            .into(profile)
        Glide.with(context)
            .load(item.expressionImageUrl)
            .apply(moodOptions)
            .into(mood)
        container.addView(itemView)
        val btnAskWhy = itemView.findViewById<MaterialButton>(R.id.btnAskWhy)
        btnAskWhy.setOnClickListener(ViewClickListener { view: View? ->
            val shareMessage = String.format(
                "Hi %s, I just saw you felt" + " " + item.expressionName!!.lowercase(
                    Locale.getDefault()
                ) + ". What made you feel that way?", item.firstName
            )
            val conversation = Conversation()
            conversation.id = "EMPTY"
            conversation.userName = item.firstName + " " + item.lastName
            conversation.userOne = preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID)
            conversation.userTwo = item.userId
            conversation.profilePicture = item.profilePictureUrl
            conversation.userTypeId = item.userTypeId
            conversation.userMessageFromWatchlist = shareMessage
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavConversations(
                    conversation
                )
            )
        })
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    companion object {
        private fun getDateMonth(strDate: String): String {
            var str = ""
            try {
                @SuppressLint("SimpleDateFormat") val fmInput = SimpleDateFormat("yyyy-MM-dd")
                val date = fmInput.parse(strDate)
                val cal = Calendar.getInstance()
                cal.time = date
                val d = Date(cal.timeInMillis)
                val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(d)
                val day = cal[Calendar.DAY_OF_MONTH].toString()
                str = "$month $day"
                return str
            } catch (e: Exception) {
            }
            return ""
        }
    }

    init {
        this.userWatchLists = userWatchLists
        this.navController = navController
        this.preferenceHelper = preferenceHelper
    }
}