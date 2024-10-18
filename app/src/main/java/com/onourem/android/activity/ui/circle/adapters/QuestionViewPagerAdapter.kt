package com.onourem.android.activity.ui.circle.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemQuestionViewPagerBinding
import com.onourem.android.activity.models.QualityQuestion
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import com.onourem.android.activity.ui.circle.models.QuestionItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.Common


class QuestionViewPagerAdapter(
    private val viewPager: ViewPager2,
    private val list: ArrayList<QualityQuestion>,
    private val onItemClickListener: OnItemClickListener<Triple<Int, QualityQuestion, QuestionForContacts>>?
) : RecyclerView.Adapter<QuestionViewPagerAdapter.QuestGamesViewHolder>() {

    companion object {
        const val CLICK_UPDATE_CONTACT = 2
        const val CLICK_ADD_CONTACT = 1
        const val CLICK_DELETE_CONTACT = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestGamesViewHolder {
        return QuestGamesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_question_view_pager, parent, false)
        )
    }

    override fun onBindViewHolder(holder: QuestGamesViewHolder, position: Int) {
        val options = list[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class QuestGamesViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {

        private lateinit var adapter: ContactsAdapter
        private val layoutManager: LinearLayoutManager

        private val rowBinding: ItemQuestionViewPagerBinding? = DataBindingUtil.bind(itemView)

        fun bind(questionItem: QualityQuestion) {

//            Glide.with(itemView.context)
//                .load(questionItem.drawable)
//                .apply(options)
//                .into(rowBinding!!.ivQuestionImage)
//
//            rowBinding!!.txtIntroTitle.text = questionItem.questionText

            rowBinding!!.rvContacts.layoutManager = layoutManager
            rowBinding.rvContacts.setHasFixedSize(true)
            if (questionItem.contactList.isNotEmpty()) {
                adapter = ContactsAdapter(
                    questionItem.contactList,
                    Color.parseColor(
                        Common.addHash(questionItem.backgroundColor)
                    ),
                    Color.parseColor(Common.addHash(questionItem.waveColor))
                ) {

                    if (it.first == CLICK_DELETE_CONTACT) {
                        onItemClickListener?.onItemClick(
                            Triple(it.first, questionItem, it.second)
                        )
                    } else if (it.first == CLICK_UPDATE_CONTACT) {
                        onItemClickListener?.onItemClick(
                            Triple(it.first, questionItem, it.second)
                        )
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        adapter.notifyDataSetChanged()
                    }, 500)

                }

                val headerAdapter =
                    ViewPagerHeaderAdapter(questionItem.question, questionItem.imageUrl)
                val concatAdapter = ConcatAdapter(headerAdapter, adapter)
                rowBinding.rvContacts.adapter = concatAdapter
            }


        }

        private fun updateAdapter() {
            adapter.notifyDataSetChanged()
        }

        init {
            assert(rowBinding != null)
            layoutManager =
                LinearLayoutManager(rowBinding!!.root.context)

            rowBinding.clPlayRecording.setOnClickListener(ViewClickListener {

            })
        }
    }


    fun updatePlayingItem(item: QuestionItem) {
        var position = 0
        if (list.isNotEmpty()) {
            list.forEach { song ->
//                if (item.audioId == song.audioId) {
//                    song.isPlaying = true
//                    position = audioList.indexOf(song)
//                    notifyItemChanged(position)
//                } else {
//                    song.isPlaying = false
//                    position = audioList.indexOf(song)
//                    notifyItemChanged(position)
//                }
            }
        }
    }

//    fun getPlayingItemPosition(item: Song): Int {
//        var position = 0
//        if (audioList.isNotEmpty()) {
//            audioList.forEach { song ->
//                if (item.audioId == song.audioId) {
//                    position = audioList.indexOf(song)
//                }
//            }
//        }
//        return position
//    }
//
//    fun getPlayingItem(item: Int): Song {
//        return audioList[item]
//    }

    fun clearData() {
        list.clear()
        notifyDataSetChanged()
    }

    fun resetData(newData: List<QualityQuestion>) {
        list.clear()
        notifyDataSetChanged()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyRecyclerViewAdapter(position: Int) {
        val viewHolder =
            (viewPager.getChildAt(position) as RecyclerView).findViewHolderForAdapterPosition(
                position
            ) as QuestGamesViewHolder
        val recyclerView = viewHolder.itemView.findViewById<RecyclerView>(R.id.rvContacts)
        recyclerView.adapter?.notifyDataSetChanged()
    }


}