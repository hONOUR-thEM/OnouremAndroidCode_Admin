package com.onourem.android.activity.ui.circle.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentFriendsCircleQuestionViewPagerBinding
import com.onourem.android.activity.models.QualityQuestion
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.circle.adapters.QuestionViewPagerAdapter
import com.onourem.android.activity.ui.circle.models.ContactItem
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.media.Common
import javax.inject.Inject

class FriendsCircleQuestionViewPagerFragment :
    AbstractBaseViewModelBindingFragment<FriendCircleGameViewModel, FragmentFriendsCircleQuestionViewPagerBinding>() {

    private lateinit var contacts: java.util.ArrayList<QuestionForContacts>
    private lateinit var args: FriendsCircleQuestionViewPagerFragmentArgs
    private lateinit var questionViewPagerAdapter: QuestionViewPagerAdapter
    private lateinit var contactsList: ArrayList<ContactItem>
    private lateinit var quesForContactsList: ArrayList<QuestionForContacts>
    private lateinit var questionList: ArrayList<QualityQuestion>
    private var currentPosition = 0

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null


    override fun viewModelType(): Class<FriendCircleGameViewModel> {
        return FriendCircleGameViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_friends_circle_question_view_pager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        contactsList = ArrayList()
        questionList = ArrayList()
        quesForContactsList = ArrayList()
        contacts = ArrayList<QuestionForContacts>()

        setViewPagerData()

        binding.btnNext.setTextColor(
            ContextCompat.getColor(
                fragmentContext,
                R.color.gray_color
            )
        )


        binding.btnAddMoreFriends.setOnClickListener(ViewClickListener {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavContactsList(
                    args.questionListResponse,
                    false
                )
            )
        })

        binding.btnNext.setOnClickListener(ViewClickListener {
            moveNext()
        })

        binding.btnPrev.setOnClickListener(ViewClickListener {
            movePrevious()
        })

        binding.btnSkip.setOnClickListener(ViewClickListener {
            navController.popBackStack(R.id.nav_home, false)
        })

//        viewModel.getUpdateColors().observe(viewLifecycleOwner) {
//            if (it != null) {
//                viewModel.setUpdateColorsConsumed()
//                setColorfulUi(currentPosition)
//            }
//        }

    }

    private fun setViewPagerData() {

        args = FriendsCircleQuestionViewPagerFragmentArgs.fromBundle(requireArguments())

        val questions = args.questionListResponse.qualityQuestionList!!

        questions.forEachIndexed { index, qualityQuestion ->
            val list = viewModel.getContactsByPagerPosition(index)
            qualityQuestion.contactList =
                list as ArrayList<QuestionForContacts> /* = java.util.ArrayList<com.onourem.android.activity.ui.circle.models.QuestionForContacts> */
        }


        questionList.addAll(questions)
        binding.rvQuestionViewPager.isUserInputEnabled = false
        binding.rvQuestionViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.rvQuestionViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPosition = position

                checkSizeOfSelection()

                setColorfulUi(position)

//                viewModel.getAllContactsByPagerPosition(position).observe(viewLifecycleOwner) {
//                    val contacts = ArrayList<QuestionForContacts>()
//                    contacts.addAll(it)
//                    questionList[position].contactList = contacts
//                    //questionViewPagerAdapter.notifyDataSetChanged()
//                    checkSizeOfSelection() /* = java.util.ArrayList<com.onourem.android.activity.ui.circle.models.QuestionForContacts> */
//                    questionViewPagerAdapter.notifyRecyclerViewAdapter(position)
//                }
                super.onPageSelected(position)
            }
        })

        questionViewPagerAdapter =
            QuestionViewPagerAdapter(binding.rvQuestionViewPager, questionList) {

                if (it.first == QuestionViewPagerAdapter.CLICK_DELETE_CONTACT) {
                    viewModel.delete(it.third)
                } else if (it.first == QuestionViewPagerAdapter.CLICK_UPDATE_CONTACT) {
                    viewModel.update(it.third)
                    checkSizeOfSelection()
                }

            }

        binding.rvQuestionViewPager.adapter = questionViewPagerAdapter
        if (questionList.size > 0)
            binding.rvQuestionViewPager.offscreenPageLimit = questionList.size
    }


    override fun onResume() {
        setColorfulUi(currentPosition)
        super.onResume()
    }

    private fun setColorfulUi(position: Int) {
        (fragmentContext as DashboardActivity).setupScreenLayoutForFriendsCircleViewPager(
            Color.parseColor(Common.addHash(questionList[position].backgroundColor)),
            Color.parseColor(Common.addHash(questionList[position].waveColor))
        )

        binding.parentViewPager.setBackgroundColor(Color.parseColor(Common.addHash(questionList[position].backgroundColor)))
        binding.btnPrev.setBackgroundColor(Color.parseColor(Common.addHash(questionList[position].backgroundColor)))
        binding.btnNext.setBackgroundColor(Color.parseColor(Common.addHash(questionList[position].backgroundColor)))
        binding.btnAddMoreFriends.setTextColor(Color.parseColor(Common.addHash(questionList[position].waveColor)))
    }

    private fun moveNext() {
        //it doesn't matter if you're already in the last item
        var sizeOfSelectionList = 0

        questionList[binding.rvQuestionViewPager.currentItem].contactList.forEach {
            if (it.selected) {
                sizeOfSelectionList++
            }
        }

        if (sizeOfSelectionList > 0) {
            binding.btnNext.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.color_black
                )
            )

            if (binding.rvQuestionViewPager.currentItem < questionList.size - 1) {
                binding.rvQuestionViewPager.setCurrentItem(
                    binding.rvQuestionViewPager.currentItem + 1,
                    false
                )
            } else {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavWelDone(
                        Common.addHash(
                            questionList[currentPosition].backgroundColor
                        ), Common.addHash(questionList[currentPosition].waveColor)
                    )
                )
            }
        } else {
            binding.btnNext.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.gray_color
                )
            )
            showAlert(
                "Please answer",
                "Select at least one friend who is suitable for the question. If no friend is suitable, you can add more friends in the game."
            )
        }

    }

    private fun checkSizeOfSelection() {
        //it doesn't matter if you're already in the last item
        var sizeOfSelectionList = 0

        questionList[binding.rvQuestionViewPager.currentItem].contactList.forEach {
            if (it.selected) {
                sizeOfSelectionList++
            }
        }

        if (sizeOfSelectionList > 0) {
            binding.btnNext.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.color_black
                )
            )
        } else {
            binding.btnNext.setTextColor(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.gray_color
                )
            )
        }

    }


    private fun movePrevious() {
        //it doesn't matter if you're already in the first item
        if (binding.rvQuestionViewPager.currentItem == 0) {
            navController.navigate(
                FriendsCircleQuestionViewPagerFragmentDirections.actionNavFriendsCircleQuestionViewPagerToNavSelectContacts(
                    false,
                    true
                )
            )
        } else {
            binding.rvQuestionViewPager.setCurrentItem(
                binding.rvQuestionViewPager.currentItem - 1,
                false
            )
        }
    }

}