package com.onourem.android.activity.ui.dashboard.subscription

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPurchaseSheetBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.dashboard.subscription.ccavenue.activity.WebViewActivity
import com.onourem.android.activity.ui.dashboard.subscription.ccavenue.utility.AvenuesParams
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject


class SubscriptionPurchaseFragment :
    AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentPurchaseSheetBinding>() {

    private lateinit var packageInfoList: java.util.ArrayList<SubscriptionPackageRes>
    private lateinit var adapter: SubscriptionPackageAdapter

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_purchase_sheet
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            RESULT_OK -> {

                if (result.data != null) {
                    val paymentStatus = result.data!!.getParcelableExtra<PaymentStatus>("paymentStatus")

                    if (paymentStatus != null) {
                        var orderStatus = ""
                        when (paymentStatus.status) {

                            "Successful" -> {
                                orderStatus = "Successful"
                            }

                            "Failed" -> {
                                orderStatus = "Failed"
                            }

                            "Aborted" -> {
                                orderStatus = "Aborted"
                            }

                            "UserWentBack" -> {
                                orderStatus = "UserWentBack"
                            }

                            else -> {
                                orderStatus = "Error"
                            }
                        }

                        viewModel.updateOrderStatus(
                            paymentStatus.orderId ?: "",
                            paymentStatus.html ?: "",
                            paymentStatus.packageId ?: "",
                            paymentStatus.discountCode ?: "",
                            orderStatus
                        )
                            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                                if (apiResponse.isSuccess && apiResponse.body != null) {
                                    if (apiResponse.loading) {
                                        showProgress()
                                    }
                                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                        if (!TextUtils.isEmpty(apiResponse.body.message)) {
                                            showAlert(apiResponse.body.message)
                                        } else {
                                            if (paymentStatus.status != "Successful") {
                                                binding.tvCodeAppliedText.performClick()
                                            }

                                            navController.navigate(
                                                MobileNavigationDirections.actionGlobalNavPurchaseStatus(
                                                    paymentStatus
                                                )
                                            )

                                        }
                                        hideProgress()
                                    } else {
                                        hideProgress()
                                        showAlert(apiResponse.body.errorMessage)
                                    }
                                }

                            }
                    }
                }
                // logic
            }
            else -> {
                // logic
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog.setCanceledOnTouchOutside(false)

        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)

        getSubscriptionPackageInfo()

        binding.tvDialogTitle.text = "Select your Package"

        binding.btnSubmit.setOnClickListener(ViewClickListener {

            if (adapter.getSelectedPackage() != null) {

                if (binding.edtCoupon.text.toString().isNotEmpty() && binding.edtCoupon.visibility == View.VISIBLE) {
                    showAlert("You have entered a discount coupon but have not applied it. Click 'Apply' button above first to apply the discount coupon or delete the coupon code.")
                } else {
                    getOrderInfo(
                        adapter.getSelectedPackage()!!.discountCost.toString(),
                        adapter.getSelectedPackage()!!.code,
                        adapter.getSelectedPackage()!!.currency!!,
                    )

//                val vAccessCode = "AVMP04KE87AE04PMEA"
//                val vMerchantId = "2332070"
//                val vCurrency = "INR"
//                val vAmount = ServiceUtility.chkNull(adapter.getSelectedPackage()!!.discountCost).toString().trim()
//                if (vAmount != "") {
//                    val intent = Intent(fragmentContext, WebViewActivity::class.java)
//                    intent.putExtra(AvenuesParams.ACCESS_CODE, vAccessCode)
//                    intent.putExtra(AvenuesParams.MERCHANT_ID, vMerchantId)
//                    intent.putExtra(AvenuesParams.ORDER_ID, "726947WSED")
//                    intent.putExtra(AvenuesParams.CURRENCY, vCurrency)
//                    intent.putExtra(AvenuesParams.AMOUNT, vAmount)
//                    intent.putExtra(AvenuesParams.REDIRECT_URL, "http://52.38.26.47/onouremweb/ccavResponseHandler.jsp")
//                    intent.putExtra(AvenuesParams.CANCEL_URL, "http://52.38.26.47/onouremweb/ccavResponseHandler.jsp")
//                    intent.putExtra(AvenuesParams.RSA_KEY_URL, "https://onourem.net/onouremweb/GetRSA.jsp")
//
//                    //resultLauncher.launch(intent)
//
//                    startActivity(intent)
//
//                } else {
//                    //showToast("All parameters are mandatory.")
//                }
                }

            } else {
                Toast.makeText(fragmentContext, "Please select package.", Toast.LENGTH_SHORT).show()
            }

        })

        binding.cvSubmit.setOnClickListener(ViewClickListener {
            dismiss()
        })

        binding.btnApply.setOnClickListener(ViewClickListener {

            if (binding.edtCoupon.text.toString() != "") {
                applyDiscountCode(binding.edtCoupon.text.toString())
            } else {
                Toast.makeText(fragmentContext, "Please enter valid code.", Toast.LENGTH_SHORT).show()
            }

        })

        binding.tvCodeAppliedText.setOnClickListener(ViewClickListener {
            binding.tvCodeAppliedText.visibility = View.GONE
            binding.edtCoupon.visibility = View.VISIBLE
            binding.edtCoupon.setText("")
            binding.btnApply.visibility = View.VISIBLE

            getSubscriptionPackageInfo()

        })

    }

    private fun applyDiscountCode(code: String) {
        viewModel.applyDiscountCode(code)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetSubscriptionPackageResListRessponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                        if (!TextUtils.isEmpty(apiResponse.body.message)) {
                            showAlert(apiResponse.body.message)
                        } else {

                            if (apiResponse.body.subscriptionPackageResList.isNotEmpty()) {
                                setData(apiResponse.body.subscriptionPackageResList[0])
                                binding.tvCodeAppliedText.visibility = View.VISIBLE
                                binding.edtCoupon.visibility = View.GONE
                                binding.btnApply.visibility = View.GONE

                                packageInfoList.clear()
                                packageInfoList.add(apiResponse.body.subscriptionPackageResList[0])

                                packageInfoList.forEachIndexed { index, packageInfo ->
                                    if (index == 0) {
                                        packageInfo.selectionStatus = "Y"
                                    } else {
                                        packageInfo.selectionStatus = "N"
                                    }
                                }
                                setData(packageInfoList[0])
                                adapter.notifyDataSetChanged()
                            }
                        }
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

//    private fun updateUserSubscriptionDetails(packageId: String, discountCode: String) {
//        viewModel.updateUserSubscriptionDetails(packageId, discountCode)
//            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
//                if (apiResponse.loading) {
//                    showProgress()
//                } else if (apiResponse.isSuccess && apiResponse.body != null) {
//                    hideProgress()
//                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//
////                        if (!TextUtils.isEmpty(apiResponse.body.message)) {
////                            showAlert(apiResponse.body.message)
////                        } else {
////                            showAlert("Purchase Successful") {
////                                startActivity(Intent.makeRestartActivityTask(requireActivity().intent.component));
////                            }
////                        }
//
//                    } else {
//                        showAlert(apiResponse.body.errorMessage)
//                    }
//                } else {
//                    hideProgress()
//                    showAlert(apiResponse.errorMessage)
//                }
//            }
//    }

    private fun getOrderInfo(amount: String, code: String?, currency: String) {
        viewModel.getOrderInfo(amount, currency, code!!, binding.edtCoupon.text.toString())
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetOrderInfoRes> ->
                if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.loading) {
                        showProgress()
                    }
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {


                        if (TextUtils.isEmpty(apiResponse.body.message)) {

                            if (amount != "") {

                                if (amount.toInt() > 0) {
                                    val intent = Intent(fragmentContext, WebViewActivity::class.java)
                                    intent.putExtra(AvenuesParams.CURRENCY, currency)
                                    intent.putExtra(AvenuesParams.AMOUNT, amount)
                                    intent.putExtra(AvenuesParams.TRANS_URL, apiResponse.body.transactionUrl)
                                    intent.putExtra(AvenuesParams.PACKAGE_ID, code)

//                            intent.putExtra(AvenuesParams.ACCESS_CODE, apiResponse.body.accessCode)
//                            intent.putExtra(AvenuesParams.MERCHANT_ID, apiResponse.body.merchantId)
                                    intent.putExtra(AvenuesParams.ORDER_ID, apiResponse.body.createdOrderId)
                                    intent.putExtra(AvenuesParams.REDIRECT_URL, apiResponse.body.redirectUrl)
//                            intent.putExtra(AvenuesParams.CANCEL_URL, apiResponse.body.cancelUrl)
//                            intent.putExtra(AvenuesParams.RSA_KEY_URL, apiResponse.body.rsaKeyUrl)
//                            intent.putExtra(AvenuesParams.ENC_VAL, apiResponse.body.encVal)

                                    if (binding.edtCoupon.text.toString() != "") {
                                        intent.putExtra(AvenuesParams.DISCOUNT_CODE, binding.edtCoupon.text.toString())
                                    } else {
                                        intent.putExtra(AvenuesParams.DISCOUNT_CODE, "")
                                    }

                                    resultLauncher.launch(intent)
                                } else {
                                    viewModel.setUpdateFreePaymentStatus("Y")
                                }

                            }
                        } else {
                            binding.tvCodeAppliedText.performClick()
                            showAlert(apiResponse.body.message)
                        }

                        hideProgress()

                    } else {

                        hideProgress()

                        showAlert(apiResponse.body.errorMessage)

                    }
                }
            }
    }

    private fun getSubscriptionPackageInfo() {

        val layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)

        binding.rvItem.layoutManager = layoutManager

        packageInfoList = ArrayList()

        adapter = SubscriptionPackageAdapter(packageInfoList) {
            setData(it)
        }

        binding.rvItem.adapter = adapter

        viewModel.getSubscriptionPackageInfo()
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetSubscriptionPackageResListRessponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.loading) {
                        showProgress()
                    }
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        packageInfoList.addAll(apiResponse.body.subscriptionPackageResList)

                        packageInfoList.forEachIndexed { index, packageInfo ->
                            if (index == 0) {
                                packageInfo.selectionStatus = "Y"
                            } else {
                                packageInfo.selectionStatus = "N"
                            }
                        }
                        if (packageInfoList.isNotEmpty()) {
                            setData(packageInfoList[0])
                        }
                        adapter.notifyDataSetChanged()

                        hideProgress()

                    } else {
                        hideProgress()
                        showAlert(apiResponse.body.errorMessage)
                    }
                }
            }
    }

    private fun setData(packageInfo: SubscriptionPackageRes) {

        binding.tvCostText.text = "${getString(R.string.Rs)} ${packageInfo.cost}"
        if (packageInfo.cost == packageInfo.discountCost) {
            binding.tvDiscountText.text = "${getString(R.string.Rs)} 0"
        } else {
            binding.tvDiscountText.text = "${getString(R.string.Rs)} ${packageInfo.cost - packageInfo.discountCost}"
        }

        binding.tvTotalText.text = "${getString(R.string.Rs)} ${packageInfo.discountCost}"

        if (packageInfo.discountCost > 0) {
            binding.btnSubmit.text = "Pay Now"
            binding.tvDialogTitle.text = "Select Your Package"
        } else {
            binding.btnSubmit.text = "Get It For Free"
            binding.tvDialogTitle.text = "Subscription for your Coupon"
        }

    }


}