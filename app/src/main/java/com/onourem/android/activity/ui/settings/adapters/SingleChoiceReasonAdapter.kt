package com.onourem.android.activity.ui.settings.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.onourem.android.activity.R
import com.onourem.android.activity.models.ReasonList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class SingleChoiceReasonAdapter(
    private val context: Context,
    private var reasonsList: ArrayList<ReasonList?>,
    private val onItemClickListener: OnItemClickListener<ReasonList?>?
) : RecyclerView.Adapter<SingleChoiceReasonAdapter.SingleViewHolder>() {
    private val compositeDisposable: CompositeDisposable

    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = -1
    private val xxx: Disposable? = null
    fun setReasonsList(reasonsList: ArrayList<ReasonList?>) {
        this.reasonsList = ArrayList()
        this.reasonsList = reasonsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SingleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_reason, viewGroup, false)
        return SingleViewHolder(view)
    }

    override fun onBindViewHolder(singleViewHolder: SingleViewHolder, position: Int) {
        singleViewHolder.bind(reasonsList[position]!!)
    }

    override fun getItemCount(): Int {
        return reasonsList.size
    }

    val selected: ReasonList?
        get() = if (checkedPosition != -1) {
            reasonsList[checkedPosition]
        } else null

    //    public void updateItem(String itemId) {
    //        int position;
    //        if (reasonsList != null && !reasonsList.isEmpty()) {
    //            for (ReasonList item : reasonsList) {
    //                if (item.getId() == Integer.parseInt(itemId)) {
    //                    position = reasonsList.indexOf(item);
    //                    checkedPosition = position;
    //                    notifyItemChanged(position);
    //                    break;
    //                }
    //            }
    //        }
    //    }
    inner class SingleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView
        private val imageView: ImageView
        private val tilOtherText: TextInputLayout
        fun bind(item: ReasonList) {
            if (checkedPosition == -1) {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_radio_unchecked
                    )
                )
            } else {
                if (checkedPosition == bindingAdapterPosition) {
                    imageView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.ic_checkbox_on_background
                        )
                    )
                } else {
                    imageView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            R.drawable.ic_radio_unchecked
                        )
                    )
                }
            }
            textView.text = item.reason


            //Objects.requireNonNull(tilOtherText.getEditText()).setText("");
            if (checkedPosition == reasonsList.size - 1 && item.reason.equals(
                    "Other",
                    ignoreCase = true
                )
            ) {
                tilOtherText.visibility = View.VISIBLE
                //item.setOtherReason(Objects.requireNonNull(tilOtherText.getEditText()).getText().toString());
            } else {
                tilOtherText.visibility = View.GONE
                item.otherReason = null
            }
            itemView.setOnClickListener(ViewClickListener { view: View? ->
                if (checkedPosition != bindingAdapterPosition) {
                    checkedPosition = bindingAdapterPosition
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(selected)
                        //updateItem(String.valueOf(item.getId()));
                        notifyDataSetChanged()
                    }
                }
            })
        }

        init {
            textView = itemView.findViewById(R.id.textView)
            imageView = itemView.findViewById(R.id.imageView)
            tilOtherText = itemView.findViewById(R.id.tilOtherText)
            tilOtherText.editText!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {
                    reasonsList[bindingAdapterPosition]!!.otherReason =
                        tilOtherText.editText!!.text.toString()
                }
            })
        }
    }

    init {
        compositeDisposable = CompositeDisposable()
    }
}