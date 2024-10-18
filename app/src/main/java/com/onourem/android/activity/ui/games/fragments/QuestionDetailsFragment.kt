package com.onourem.android.activity.ui.games.fragments

import android.os.Bundle
import android.view.View
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentQuestionDetailsBinding
import com.onourem.android.activity.models.ActivityType
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import com.onourem.android.activity.ui.utils.glide.GlideApp
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class QuestionDetailsFragment :
    AbstractBaseViewModelBindingFragment<CommentsViewModel, FragmentQuestionDetailsBinding>() {
    private val question: LoginDayActivityInfoList? = null
    override fun viewModelType(): Class<CommentsViewModel> {
        return CommentsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_question_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //        question = QuestionDetailsFragmentArgs.fromBundle(getArguments()).getQuestion();
        binding.tvQuestion.text = question!!.activityText
        //binding.ivRelation.setImageResource(ActivityType.getActivityTypeIcon(question.activityType))
        val imageOptions =
            RequestOptions().fitCenter().transform(RoundedCorners(20)).placeholder(R.drawable.default_place_holder)
                .error(R.drawable.default_place_holder)

        GlideApp.with(binding.root.context)
            .load(ActivityType.getActivityTypeIcon(Objects.requireNonNull(question.activityType)))
            .apply(imageOptions)
            .into(binding.ivRelation)
        binding.btnAskToFriends.setOnClickListener(ViewClickListener { v: View? -> })
    }
}