package com.onourem.android.activity.ui.circle.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemFriendThoughtBinding
import com.onourem.android.activity.models.UserQualityInfo
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.Common


class FriendsThoughtsAdapter(
    private var qualityBubbleColors: ArrayList<String>,
    private var qualityBubbleColorsText: ArrayList<String>,
    private var list: ArrayList<UserQualityInfo>,
    private val onItemClickListener: OnItemClickListener<UserQualityInfo>?,
) : RecyclerView.Adapter<FriendsThoughtsAdapter.ThoughtsViewHolder>() {

//    val qualityBubbleColors =
//        arrayOf("#48D8CF", "#AB6AD8", "#61D836", "#D85756", "#D8A860", "#5D9BD8")

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ThoughtsViewHolder {
        return ThoughtsViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_friend_thought, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ThoughtsViewHolder, position: Int) {
        val item = list[position]
        viewHolder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ThoughtsViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private lateinit var prevBubbleColor: String
        private lateinit var nextBubbleColor: String
        private lateinit var mainBubbleColor: String
        private val rowBinding: ItemFriendThoughtBinding? = DataBindingUtil.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(quality: UserQualityInfo, itemIndex: Int) {

            rowBinding!!.centerTextWithoutBackground.text = quality.qualityName
            rowBinding!!.leftBubbleNumberLabel.text = quality.friendCount.toString()
            rowBinding!!.rightBubbleNumberLabel.text = quality.friendCount.toString()

            if (itemIndex == 0) {

                rowBinding!!.rightBubbleTopView.visibility = View.GONE
                rowBinding!!.leftBubbleMainView.visibility = View.GONE
                rowBinding!!.rightBubbleBottomView.visibility = View.VISIBLE
                rowBinding!!.leftBubbleNumberLabel.visibility = View.GONE
                rowBinding!!.leftBubbleMaskView.visibility = View.GONE

                rowBinding!!.rightBubbleNumberLabel.visibility = View.GONE
                rowBinding!!.leftBubbleTopView.visibility = View.GONE
                rowBinding!!.rightBubbleMainView.visibility = View.GONE
                rowBinding!!.leftBubbleBottomView.visibility = View.GONE
                rowBinding!!.rightBubbleMaskView.visibility = View.GONE


                rowBinding!!.centerTextWithoutBackground.visibility = View.GONE
                rowBinding!!.centerText.visibility = View.GONE

            } else if (itemIndex == 1) {
                rowBinding!!.rightBubbleTopView.visibility = View.VISIBLE
                rowBinding!!.leftBubbleMainView.visibility = View.VISIBLE
                rowBinding!!.rightBubbleBottomView.visibility = View.VISIBLE
                rowBinding!!.leftBubbleNumberLabel.visibility = View.VISIBLE
                rowBinding!!.leftBubbleMaskView.visibility = View.VISIBLE

                rowBinding!!.rightBubbleNumberLabel.visibility = View.GONE
                rowBinding!!.leftBubbleTopView.visibility = View.GONE
                rowBinding!!.rightBubbleMainView.visibility = View.GONE
                rowBinding!!.leftBubbleBottomView.visibility = View.VISIBLE
                rowBinding!!.rightBubbleMaskView.visibility = View.GONE


                rowBinding!!.centerTextWithoutBackground.visibility = View.VISIBLE
                rowBinding!!.centerText.visibility = View.VISIBLE
            } else {
                val bubbleIndex = itemIndex - 1
                rowBinding!!.centerTextWithoutBackground.visibility = View.VISIBLE
                rowBinding!!.centerText.visibility = View.VISIBLE

                if (itemIndex == list.size - 2) {
                    if (isEven(itemIndex)) {
                        rowBinding!!.leftBubbleMainView.visibility = View.GONE
                        rowBinding!!.leftBubbleNumberLabel.visibility = View.GONE
                        rowBinding!!.leftBubbleMaskView.visibility = View.GONE
                        rowBinding!!.leftBubbleTopView.visibility = View.GONE
                        rowBinding!!.leftBubbleBottomView.visibility = View.GONE
                        rowBinding!!.rightBubbleNumberLabel.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleMainView.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleMaskView.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleTopView.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleBottomView.visibility = View.GONE

                    } else {
                        rowBinding!!.leftBubbleMainView.visibility = View.VISIBLE
                        rowBinding!!.leftBubbleNumberLabel.visibility = View.VISIBLE
                        rowBinding!!.leftBubbleMaskView.visibility = View.VISIBLE
                        rowBinding!!.leftBubbleTopView.visibility = View.VISIBLE
                        rowBinding!!.leftBubbleBottomView.visibility = View.GONE
                        rowBinding!!.rightBubbleNumberLabel.visibility = View.GONE
                        rowBinding!!.rightBubbleMainView.visibility = View.GONE
                        rowBinding!!.rightBubbleMaskView.visibility = View.GONE
                        rowBinding!!.rightBubbleTopView.visibility = View.GONE
                        rowBinding!!.rightBubbleBottomView.visibility = View.GONE
                    }

                    rowBinding!!.centerTextWithoutBackground.visibility = View.VISIBLE
                    rowBinding!!.centerText.visibility = View.VISIBLE

                } else if (itemIndex == list.size - 1) {
                    if (isEven(itemIndex)) {
                        rowBinding!!.leftBubbleMainView.visibility = View.GONE
                        rowBinding!!.leftBubbleNumberLabel.visibility = View.GONE
                        rowBinding!!.leftBubbleMaskView.visibility = View.GONE
                        rowBinding!!.leftBubbleTopView.visibility = View.GONE
                        rowBinding!!.leftBubbleBottomView.visibility = View.GONE
                        rowBinding!!.rightBubbleMainView.visibility = View.GONE
                        rowBinding!!.rightBubbleNumberLabel.visibility = View.GONE
                        rowBinding!!.rightBubbleMaskView.visibility = View.GONE
                        rowBinding!!.rightBubbleTopView.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleBottomView.visibility = View.GONE
                    } else {
                        rowBinding!!.leftBubbleMainView.visibility = View.GONE
                        rowBinding!!.leftBubbleNumberLabel.visibility = View.GONE
                        rowBinding!!.leftBubbleMaskView.visibility = View.GONE
                        rowBinding!!.leftBubbleTopView.visibility = View.VISIBLE
                        rowBinding!!.leftBubbleBottomView.visibility = View.GONE
                        rowBinding!!.rightBubbleMainView.visibility = View.GONE
                        rowBinding!!.rightBubbleNumberLabel.visibility = View.GONE
                        rowBinding!!.rightBubbleMaskView.visibility = View.GONE
                        rowBinding!!.rightBubbleTopView.visibility = View.GONE
                        rowBinding!!.rightBubbleBottomView.visibility = View.GONE
                    }

                    rowBinding!!.centerTextWithoutBackground.visibility = View.GONE
                    rowBinding!!.centerText.visibility = View.GONE

                } else {

                    if (isEven(itemIndex)) {
                        rowBinding!!.leftBubbleMainView.visibility = View.GONE
                        rowBinding!!.leftBubbleNumberLabel.visibility = View.GONE
                        rowBinding!!.leftBubbleMaskView.visibility = View.GONE
                        rowBinding!!.leftBubbleTopView.visibility = View.GONE
                        rowBinding!!.leftBubbleBottomView.visibility = View.GONE
                        rowBinding!!.rightBubbleNumberLabel.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleMainView.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleMaskView.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleTopView.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleBottomView.visibility = View.VISIBLE

                    } else {
                        rowBinding!!.leftBubbleMainView.visibility = View.VISIBLE
                        rowBinding!!.leftBubbleNumberLabel.visibility = View.VISIBLE
                        rowBinding!!.leftBubbleMaskView.visibility = View.VISIBLE
                        rowBinding!!.leftBubbleTopView.visibility = View.VISIBLE
                        rowBinding!!.leftBubbleBottomView.visibility = View.VISIBLE
                        rowBinding!!.rightBubbleNumberLabel.visibility = View.GONE
                        rowBinding!!.rightBubbleMainView.visibility = View.GONE
                        rowBinding!!.rightBubbleMaskView.visibility = View.GONE
                        rowBinding!!.rightBubbleTopView.visibility = View.GONE
                        rowBinding!!.rightBubbleBottomView.visibility = View.GONE
                    }

                }

            }


            val dataIndex = itemIndex

            //new logic

            var colorPrevIndex = dataIndex - 1
            var colorMainIndex = dataIndex
            var colorNextIndex = dataIndex + 1

            if (colorPrevIndex < 1) {
                colorPrevIndex = 1
            }
            if (colorMainIndex < 1) {
                colorMainIndex = 1
            }
            if (colorNextIndex < 1) {
                //this case will never happen but just to cover it
                colorNextIndex = 1
            }

            if (colorPrevIndex >= list.size - 1) {
                colorPrevIndex = list.size - 2
            }
            if (colorMainIndex >= list.size - 1) {
                colorMainIndex = list.size - 2
            }
            if (colorNextIndex >= list.size - 1) {
                //this case will never happen but just to cover it
                colorNextIndex = list.size - 2
            }

            val mainBubbleColor = Common.addHash(list[colorMainIndex].waveColor)

            val textMainColor = Common.addHash(list[colorMainIndex].waveColor)

            val prevBubbleColor = Common.addHash(list[colorPrevIndex].waveColor)

            val nextBubbleColor = Common.addHash(list[colorNextIndex].waveColor)




            rowBinding!!.centerText.setCardColor(Color.parseColor(mainBubbleColor))

            rowBinding!!.leftBubbleMainView.setCardColor(Color.parseColor(mainBubbleColor))

            rowBinding!!.leftBubbleMaskView.setViewDrawableColor(Color.parseColor(mainBubbleColor))

            rowBinding!!.rightBubbleMainView.setCardColor(Color.parseColor(mainBubbleColor))

            rowBinding!!.rightBubbleMaskView.setViewDrawableColor(Color.parseColor(mainBubbleColor))



            rowBinding!!.leftBubbleTopView.setCardColor(Color.parseColor(prevBubbleColor))

            rowBinding!!.rightBubbleTopView.setCardColor(Color.parseColor(prevBubbleColor))



            rowBinding!!.leftBubbleBottomView.setCardColor(Color.parseColor(nextBubbleColor))

            rowBinding!!.rightBubbleBottomView.setCardColor(Color.parseColor(nextBubbleColor))


//            rowBinding!!.centerTextWithoutBackground.setMaterialTextColor(
//                Color.parseColor(
//                    textMainColor
//                )
//            )
//            rowBinding!!.leftBubbleNumberLabel.setMaterialTextColor(Color.parseColor(textMainColor))
//            rowBinding!!.rightBubbleNumberLabel.setMaterialTextColor(Color.parseColor(textMainColor))

        }


        private fun isEven(n: Int): Boolean {
            return n % 2 == 0
        }

        init {
            rowBinding!!.parent.setOnClickListener(ViewClickListener {

                //new logic
                val dataIndex = bindingAdapterPosition
                var colorPrevIndex = dataIndex - 1
                var colorMainIndex = dataIndex
                var colorNextIndex = dataIndex + 1

                if (colorPrevIndex < 1) {
                    colorPrevIndex = 1
                }
                if (colorMainIndex < 1) {
                    colorMainIndex = 1
                }
                if (colorNextIndex < 1) {
                    //this case will never happen but just to cover it
                    colorNextIndex = 1
                }

                if (colorPrevIndex >= list.size - 1) {
                    colorPrevIndex = list.size - 2
                }
                if (colorMainIndex >= list.size - 1) {
                    colorMainIndex = list.size - 2
                }
                if (colorNextIndex >= list.size - 1) {
                    //this case will never happen but just to cover it
                    colorNextIndex = list.size - 2
                }


                val item = list[bindingAdapterPosition]
                item.backgroundColor = list[colorMainIndex].backgroundColor
                item.waveColor = list[colorMainIndex].waveColor

                onItemClickListener?.onItemClick(item)
            })
        }


    }
}

 fun View.setViewDrawableColor(color: Int) {
    ViewCompat.setBackgroundTintList(
        this,
        ColorStateList.valueOf(color)
    )
}
 fun MaterialTextView.setMaterialTextColor(color: Int) {
    setTextColor(ColorStateList.valueOf(color))
}
 fun MaterialCardView.setCardColor(color: Int) {
    this.setCardBackgroundColor(color)
//    this.backgroundTintList = ColorStateList.valueOf(color)
//    this.strokeColor = Color.parseColor("#000000")
//    this.strokeWidth = 1
    this.cardElevation = 2f
}