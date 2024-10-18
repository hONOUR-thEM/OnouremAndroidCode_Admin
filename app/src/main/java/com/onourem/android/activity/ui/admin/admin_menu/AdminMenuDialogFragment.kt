package com.onourem.android.activity.ui.admin.admin_menu

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.DialogAdminMenuBinding
import com.onourem.android.activity.models.MenuInfo
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel


class AdminMenuDialogFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, DialogAdminMenuBinding>() {

    override fun layoutResource(): Int {
        return R.layout.dialog_admin_menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search_nav).isVisible = false
        menu.findItem(R.id.phone_nav).isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDialogTitle.text = "Admin Options"

        setupReviewMenu()
        setupCreateMenu()
        setupGetActivitiesMenu()
        setupSubscriptionMenu()
        setupOtherMenu()

    }

    private fun setupGetActivitiesMenu() {
        val layoutManager = GridLayoutManager(fragmentContext, 2) // 2 columns

        binding.rvActivitiesMenu.layoutManager = layoutManager

//        layoutManager.orientation = RecyclerView.HORIZONTAL

        val adminMenuInfoList = ArrayList<MenuInfo>()

        adminMenuInfoList.add(MenuInfo("All \nActivities", "3"))
        adminMenuInfoList.add(MenuInfo("Scheduled \nActivities", "0"))
        adminMenuInfoList.add(MenuInfo("Single Activity | \nInactive Everywhere", "1"))
        adminMenuInfoList.add(MenuInfo("Auto Triggered \nOClub Activities", "2"))

        val adminMenuAdapter = AdminMenuOptionsAdapter(adminMenuInfoList) {

            when (it.id) {

                "0" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavScheduledActivities())
                }

                "1" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavActivityDetails())
                }

                "2" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavAutoTriggeredActivities())
                }

                "3" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavAllActivities("0"))
                }

            }
        }

        binding.rvActivitiesMenu.adapter = adminMenuAdapter

//        Handler(Looper.getMainLooper()).postDelayed({
//            view!!.invalidate()
//        }, 500)

    }

    private fun setupSubscriptionMenu() {
        val layoutManager = GridLayoutManager(fragmentContext, 2) // 2 columns

        binding.rvSubscriptionsMenu.layoutManager = layoutManager

//        layoutManager.orientation = RecyclerView.HORIZONTAL

        val adminMenuInfoList = ArrayList<MenuInfo>()

        adminMenuInfoList.add(MenuInfo("Check \nOrder", "0"))
        adminMenuInfoList.add(MenuInfo("Add \nSubscription", "1"))
        adminMenuInfoList.add(MenuInfo("Add \nSubscription Discount", "3"))
        adminMenuInfoList.add(MenuInfo("Add \nInstitution", "2"))
        adminMenuInfoList.add(MenuInfo("OClubs \nInfo", "4"))
        adminMenuInfoList.add(MenuInfo("Packages \nInfo", "5"))
        adminMenuInfoList.add(MenuInfo("Institute \nInfo", "6"))

        val adminMenuAdapter = AdminMenuOptionsAdapter(adminMenuInfoList) {

            when (it.id) {

                "0" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavPaymentStatusByOrderId())
                }

                "1" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavAddSubscription())
                }

                "2" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavAddInstitute())
                }

                "3" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavAddSubscriptionDiscount())
                }

                "4" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavOclubSettings())
                }

                "5" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavPackagesInfo())
                }

                "6" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavInstituteInfo())
                }

            }
        }

        binding.rvSubscriptionsMenu.adapter = adminMenuAdapter

//        Handler(Looper.getMainLooper()).postDelayed({
//            view!!.invalidate()
//        }, 500)
    }

    private fun setupCreateMenu() {
        val layoutManager = GridLayoutManager(fragmentContext, 3) // 2 columns

        binding.rvCreateMenu.layoutManager = layoutManager

//        layoutManager.orientation = RecyclerView.HORIZONTAL

        val adminMenuInfoList = ArrayList<MenuInfo>()
        adminMenuInfoList.add(MenuInfo("Questions \nList", "0"))
        adminMenuInfoList.add(MenuInfo("Cards \nList", "1"))
        adminMenuInfoList.add(MenuInfo("Tasks | Message \nList", "2"))
        adminMenuInfoList.add(MenuInfo("External \nPosts", "3"))
        adminMenuInfoList.add(MenuInfo("Survey \nList", "4"))
//        adminMenuInfoList.add(MenuInfo("Vocals \nList", "5"))


        val adminMenuAdapter = AdminMenuOptionsAdapter(adminMenuInfoList) {
            navController.navigate(MobileNavigationDirections.actionGlobalNavCreateActivitiesMain(it.id))
        }

        binding.rvCreateMenu.adapter = adminMenuAdapter

//        Handler(Looper.getMainLooper()).postDelayed({
//            view!!.invalidate()
//        }, 500)
    }

    private fun setupOtherMenu() {
//        val layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)

        val layoutManager = GridLayoutManager(fragmentContext, 2) // 2 columns

        binding.rvOtherMenu.layoutManager = layoutManager

//        layoutManager.orientation = RecyclerView.HORIZONTAL

        val adminMenuInfoList = ArrayList<MenuInfo>()
//        adminMenuInfoList.add(MenuInfo("Load AI", "7"))
//        adminMenuInfoList.add(MenuInfo("Add Daily Activity \nIn Bulk ByAdmin", "8"))
        adminMenuInfoList.add(MenuInfo("Portal \nUsers", "6"))
        adminMenuInfoList.add(MenuInfo("Mood Info \nUpdate", "4"))
        adminMenuInfoList.add(MenuInfo("App Reviews", "5"))
        adminMenuInfoList.add(MenuInfo("ContactUs |\n ReportProblem", "3"))
        adminMenuInfoList.add(MenuInfo("Inappropriate \nReports", "2"))
        adminMenuInfoList.add(MenuInfo("Bulk \nEmail", "0"))
        adminMenuInfoList.add(MenuInfo("Delete User \nAccount", "1"))


        val adminMenuAdapter = AdminMenuOptionsAdapter(adminMenuInfoList) {

            when (it.id) {

                "0" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavEmail())
                }

                "1" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavDeleteUser())
                }

                "2" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavUserReport())
                }

                "3" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavUserQuery())
                }

                "4" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavMoodInfoList())
                }

                "5" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavAppReviewList())
                }

                "6" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavPortalUsers())
                }

                "7" -> {
                    viewModel.loadAIMoodIntoDBByAdmin().observe(viewLifecycleOwner) { apiResponse ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            Toast.makeText(fragmentContext, "Success", Toast.LENGTH_LONG).show()
                        } else {
                            hideProgress()
                            showAlert(
                                getString(R.string.label_network_error), apiResponse.errorMessage
                            )
                        }
                    }
                }

                "8" -> {
                    viewModel.addOclubAutoTriggerDailyActivityInBulkByAdmin().observe(viewLifecycleOwner) { apiResponse ->
                        if (apiResponse.loading) {
                            showProgress()
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            hideProgress()
                            Toast.makeText(fragmentContext, "Success", Toast.LENGTH_LONG).show()
                        } else {
                            hideProgress()
                            showAlert(
                                getString(R.string.label_network_error), apiResponse.errorMessage
                            )
                        }
                    }
                }

            }
        }

        binding.rvOtherMenu.adapter = adminMenuAdapter

//        Handler(Looper.getMainLooper()).postDelayed({
//            view!!.invalidate()
//        }, 500)
    }

    private fun setupReviewMenu() {

//        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val layoutManager = GridLayoutManager(fragmentContext, 3) // 2 columns

        binding.rvReviewMenu.layoutManager = layoutManager

//        layoutManager.orientation = RecyclerView.HORIZONTAL

        val adminMenuInfoList = ArrayList<MenuInfo>()
        adminMenuInfoList.add(MenuInfo("Review Vocals", "0"))
        adminMenuInfoList.add(MenuInfo("Public Posts", "1"))

        val adminMenuAdapter = AdminMenuOptionsAdapter(adminMenuInfoList) {

            when (it.id) {

                "0" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavAdminVocals("", "", "", ""))
                }

                "1" -> {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavPublicPost())
                }

            }
        }

        binding.rvReviewMenu.adapter = adminMenuAdapter

//        Handler(Looper.getMainLooper()).postDelayed({
//            view!!.invalidate()
//        }, 500)
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

}