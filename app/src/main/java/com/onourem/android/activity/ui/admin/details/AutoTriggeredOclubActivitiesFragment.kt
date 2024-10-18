package com.onourem.android.activity.ui.admin.details

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.DialogUpdateAutoTriggerInOclubBinding
import com.onourem.android.activity.databinding.FragmentAutoTriggerActivitiesBinding
import com.onourem.android.activity.models.AutoTrigggerDailyActivityRes
import com.onourem.android.activity.models.GetPlayGroupCategories
import com.onourem.android.activity.models.PlayGroupCategoryList
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.DialogUtils

class AutoTriggeredOclubActivitiesFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAutoTriggerActivitiesBinding>() {

    private lateinit var questionGamesViewModel: QuestionGamesViewModel
    private var selectedCategoryId = 0
    private var selectedCategory = ""

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_auto_trigger_activities
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCategories()

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rv.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[QuestionGamesViewModel::class.java]
    }

    private fun setAdapter(settingsItemList: ArrayList<AutoTrigggerDailyActivityRes>) {
        binding.rv.adapter = null
        binding.rv.adapter = OclubActivityAdapter(settingsItemList) { item: AutoTrigggerDailyActivityRes ->
            showDialog(fragmentContext, item)
        }
    }

    private fun showDialog(context: Context, item: AutoTrigggerDailyActivityRes) {

        var selectedStatus = ""
        val binding: DialogUpdateAutoTriggerInOclubBinding?
        val dialog: AlertDialog?

        val dialogBuilder = AlertDialog.Builder(
            context
        )

        binding = DialogUpdateAutoTriggerInOclubBinding.inflate(
            LayoutInflater.from(
                context
            )
        )

        dialogBuilder.setView(binding.root)

        dialog = dialogBuilder.create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.actSpinnerOClubCategory.setAdapter(null)
        val list = ArrayList<String>()
        list.add("Active")
        list.add("In-Active")
        binding.actSpinnerOClubCategory.setText(
            if (item.status == "0") {
                "Active"
            } else {
                "In-Active"
            }
        )
        val adapterCountry =
            ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, list)
        binding.actSpinnerOClubCategory.setAdapter(adapterCountry)
        binding.actSpinnerOClubCategory.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                selectedStatus = list[position]
            }
        binding.tilDayNumber.editText?.setText(item.dayNumber)
        binding.tilDayPriority.editText?.setText(item.dayPriority)



        binding.btnDialogOk.setOnClickListener {
            if (selectedStatus.isNotEmpty()) {
                updateOclubAutoTrigger(
                    item.categoryName,
                    item.id,
                    binding.tilDayNumber.editText?.text.toString(),
                    binding.tilDayPriority.editText?.text.toString(),
                    selectedStatus,
                    dialog
                )
            } else {
                DialogUtils.showToast(fragmentContext, "Please select status first")
            }
        }

        dialog.show()

    }

    private fun updateOclubAutoTrigger(
        oclubCategoryName: String,
        oclubCategoryId: String,
        dayNumber: String,
        dayPriority: String,
        status: String,
        dialog: AlertDialog
    ) {
        viewModel.updateOclubAutoTriggerDailyActivityByAdmin(
            oclubCategoryName,
            oclubCategoryId,
            dayNumber,
            dayPriority,
            if (status == "Active") {
                "0"
            } else {
                "1"
            },
        )
            .observe(viewLifecycleOwner) { apiResponse ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (selectedCategoryId != 0) {
                        getOclubAutoTriggerDailyActivityByAdmin(selectedCategory)
                    }

//                    if (apiResponse.body.message!!.isNotEmpty()) {
//                        showAlert(apiResponse.body.message)
//                    }
                    dialog.dismiss()

                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }

    }


    private fun getCategories() {
        questionGamesViewModel.getPlayGroupCategory().observe(viewLifecycleOwner) { standardResponseApiResponse ->
            if (standardResponseApiResponse.loading) {
                showProgress()
            } else if (standardResponseApiResponse.isSuccess && standardResponseApiResponse.body != null) {
                hideProgress()
                val playGroupCategories: GetPlayGroupCategories = standardResponseApiResponse.body
                if (playGroupCategories.errorCode.equals("000")) {
                    val categoriesResponse = standardResponseApiResponse.body
                    val playGroupCategoryList = java.util.ArrayList<PlayGroupCategoryList>()
                    playGroupCategoryList.addAll(categoriesResponse.playGroupCategoryList!!)

                    binding.actSpinnerOClubCategory.setAdapter(null)
                    val adapterCountry =
                        ArrayAdapter(requireActivity(), R.layout.dropdown_menu_popup_item, playGroupCategoryList)
                    binding.actSpinnerOClubCategory.setAdapter(adapterCountry)
                    binding.actSpinnerOClubCategory.onItemClickListener =
                        AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                            selectedCategoryId = playGroupCategoryList[position].id
                            selectedCategory = playGroupCategoryList[position].categoryName
                            getOclubAutoTriggerDailyActivityByAdmin(selectedCategory)
                        }

                    binding.actSpinnerOClubCategory.setText(adapterCountry.getItem(0).toString(), false)
                    selectedCategoryId = playGroupCategoryList[0].id
                    selectedCategory = playGroupCategoryList[0].categoryName
                    getOclubAutoTriggerDailyActivityByAdmin(selectedCategory)

                } else {
                    showAlert(playGroupCategories.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(standardResponseApiResponse.errorMessage)
            }
        }
    }

    private fun getOclubAutoTriggerDailyActivityByAdmin(id: String) {
        viewModel.getOclubAutoTriggerDailyActivityByAdmin(id.toString())
            ?.observe(viewLifecycleOwner) { standardResponseApiResponse ->
                if (standardResponseApiResponse.loading) {
                    showProgress()
                } else if (standardResponseApiResponse.isSuccess && standardResponseApiResponse.body != null) {
                    hideProgress()
                    if (standardResponseApiResponse.body.autoTrigggerDailyActivityResList != null) {
                        setAdapter(standardResponseApiResponse.body.autoTrigggerDailyActivityResList as ArrayList<AutoTrigggerDailyActivityRes>)
                    } else {
                        hideProgress()
                        showAlert(standardResponseApiResponse.errorMessage)
                    }

                } else {
                    hideProgress()
                    showAlert(standardResponseApiResponse.errorMessage)
                }
            }
    }
}