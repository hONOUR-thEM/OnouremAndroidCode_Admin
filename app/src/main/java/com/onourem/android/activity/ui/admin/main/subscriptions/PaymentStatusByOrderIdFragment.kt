package com.onourem.android.activity.ui.admin.main.subscriptions

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPaymentStatusByOrderIdBinding
import com.onourem.android.activity.models.CheckOrderInfoResponse
import com.onourem.android.activity.models.OrderInfo
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*

class PaymentStatusByOrderIdFragment :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentPaymentStatusByOrderIdBinding>() {

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_payment_status_by_order_id
    }

    @SuppressLint("CheckResult", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDetails.setOnClickListener(ViewClickListener {
            if (Objects.requireNonNull(
                    binding.edtSearchQuery.text
                ).toString().trim { it <= ' ' }.length >= 9
            ) {
                getOrderInfoByAdmin(binding.edtSearchQuery.text.toString())
            } else {
                showAlert("Please check order id")
            }
        })

        binding.btnSubmit.setOnClickListener(ViewClickListener {
            setOrderData(
                OrderInfo(
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    -1,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    -1,
                    -1,
                )
            )
        })

        binding.ibClear.setOnClickListener(ViewClickListener { v: View? ->
            binding.edtSearchQuery.setText("")
            binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.edtSearchQuery.clearFocus()
            AppUtilities.hideKeyboard(requireActivity())
            binding.ibClear.visibility = View.GONE
        })

        binding.edtSearchQuery.setOnTouchListener { view1: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                // 100 is a fix value for the moment but you can change it
                // according to your view
                binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                binding.ibClear.visibility = View.VISIBLE
            }
            false
        }
    }

    private fun getOrderInfoByAdmin(id: String) {

        viewModel.getOrderInfoByAdmin(id)
            .observe(this) { apiResponse: ApiResponse<CheckOrderInfoResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.loading) {
                        showProgress();
                    } else if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        setData(apiResponse.body.orderInfo)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                        hideProgress()
                    }
                }
            }

    }

    private fun setData(orderInfo: OrderInfo?) {

        if (orderInfo != null) {
            setEditTextData(binding.tilUserId, orderInfo.userId.toString())
            setEditTextData(binding.tilBillingName, orderInfo.billingName)
            setEditTextData(binding.tilStatusMessage, orderInfo.statusMessage)
            setEditTextData(binding.tilBillingCity, orderInfo.billingCity)
            setEditTextData(binding.tilAmount, orderInfo.amount.toString())
            setEditTextData(binding.tilOrderStatus, orderInfo.orderStatus)
            setEditTextData(binding.tilBillingCountry, orderInfo.billingCountry)
            setEditTextData(binding.tilBillingState, orderInfo.billingState)
            setEditTextData(binding.tilResponseCode, orderInfo.responseCode)
            setEditTextData(binding.tilBillingAddress, orderInfo.billingAddress)
            setEditTextData(binding.tilBillingZip, orderInfo.billingZip)
            setEditTextData(binding.tilBillingTelephone, orderInfo.billingTelephone)
            setEditTextData(binding.tilFailureMessage, orderInfo.failureMessage)
            setEditTextData(binding.tilOrderId, orderInfo.orderId)
            setEditTextData(binding.tilBankRefNumber, orderInfo.bankRefNumber)
            setEditTextData(binding.tilStatusCode, orderInfo.statusCode)
            setEditTextData(binding.tilPaymentMode, orderInfo.paymentMode)
            setEditTextData(binding.tilTrackingId, orderInfo.trackingId)
            setEditTextData(binding.tilTransDate, orderInfo.transDate)
            setEditTextData(binding.tilCardName, orderInfo.cardName)
            setEditTextData(binding.tilBillingEmail, orderInfo.billingEmail)
            setEditTextData(binding.tilMerchantAmount, orderInfo.merchantAmount)
            setEditTextData(binding.tilInitiatedDateTime, orderInfo.initiatedDateTime)
            setEditTextData(binding.tilCompletedDateTime, orderInfo.completedDateTime)
            setEditTextData(binding.tilComment, orderInfo.comment)
            setEditTextData(binding.tilRetry, orderInfo.retry)
            setEditTextData(binding.tilPackageCode, orderInfo.packageCode)
            setEditTextData(binding.tilDiscountCode, orderInfo.discountCode)
        } else {
            setEditTextData(binding.tilUserId, "")
            setEditTextData(binding.tilBillingName, "")
            setEditTextData(binding.tilStatusMessage, "")
            setEditTextData(binding.tilBillingCity, "")
            setEditTextData(binding.tilBillingState, "")
            setEditTextData(binding.tilAmount, "")
            setEditTextData(binding.tilOrderStatus, "")
            setEditTextData(binding.tilBillingCountry, "")
            setEditTextData(binding.tilResponseCode, "")
            setEditTextData(binding.tilBillingAddress, "")
            setEditTextData(binding.tilBillingZip, "")
            setEditTextData(binding.tilBillingTelephone, "")
            setEditTextData(binding.tilFailureMessage, "")
            setEditTextData(binding.tilOrderId, "")
            setEditTextData(binding.tilBankRefNumber, "")
            setEditTextData(binding.tilStatusCode, "")
            setEditTextData(binding.tilPaymentMode, "")
            setEditTextData(binding.tilTrackingId, "")
            setEditTextData(binding.tilTransDate, "")
            setEditTextData(binding.tilCardName, "")
            setEditTextData(binding.tilBillingEmail, "")
            setEditTextData(binding.tilMerchantAmount, "")
            setEditTextData(binding.tilInitiatedDateTime, "")
            setEditTextData(binding.tilCompletedDateTime, "")
            setEditTextData(binding.tilComment, "")
            setEditTextData(binding.tilRetry, "")
            setEditTextData(binding.tilPackageCode, "")
            setEditTextData(binding.tilDiscountCode, "")
        }

        hideProgress()
    }


    private fun setOrderData(orderInfo: OrderInfo) {
        orderInfo.userId = getEditTextData(binding.tilUserId).toInt()
        orderInfo.billingName = getEditTextData(binding.tilBillingName)
        orderInfo.statusMessage = "Y"
        orderInfo.billingCity = getEditTextData(binding.tilBillingCity)
        orderInfo.billingState = getEditTextData(binding.tilBillingState)
        orderInfo.amount = getEditTextData(binding.tilAmount).toDouble()
        orderInfo.orderStatus = "Success"
        orderInfo.billingCountry = getEditTextData(binding.tilBillingCountry)
        orderInfo.responseCode = "0"
        orderInfo.billingAddress = getEditTextData(binding.tilBillingAddress)
        orderInfo.billingZip = getEditTextData(binding.tilBillingZip)
        orderInfo.billingTelephone = getEditTextData(binding.tilBillingTelephone)
        orderInfo.failureMessage = ""
        orderInfo.orderId = getEditTextData(binding.tilOrderId)
        orderInfo.bankRefNumber = getEditTextData(binding.tilBankRefNumber)
        //orderInfo.statusCode = getEditTextData(binding.tilStatusCode)
        orderInfo.paymentMode = getEditTextData(binding.tilPaymentMode)
        orderInfo.trackingId = getEditTextData(binding.tilTrackingId)
        orderInfo.transDate = getEditTextData(binding.tilTransDate)
        orderInfo.cardName = getEditTextData(binding.tilCardName)
        orderInfo.billingEmail = getEditTextData(binding.tilBillingEmail)
        orderInfo.merchantAmount = getEditTextData(binding.tilMerchantAmount)
        orderInfo.initiatedDateTime = getEditTextData(binding.tilInitiatedDateTime)
        orderInfo.completedDateTime = getEditTextData(binding.tilCompletedDateTime)
        orderInfo.comment = getEditTextData(binding.tilComment)
        orderInfo.retry = "N"
        orderInfo.currency = getEditTextData(binding.tilCurrency)
        orderInfo.packageCode = getEditTextData(binding.tilPackageCode)
        orderInfo.discountCode = getEditTextData(binding.tilDiscountCode)

        viewModel.updateOrderInfoByAdmin(orderInfo)
            .observe(this) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.loading) {
                        showProgress();
                    } else if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        Toast.makeText(fragmentContext, "Payment Details Updated successfully", Toast.LENGTH_LONG).show()
                        setData(null)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                        hideProgress()
                    }
                }
            }
    }

    private fun getEditTextData(til: TextInputLayout): String {
        if (til.editText!!.text != null) {
            return til.editText!!.text.toString()
        } else {
            return ""
        }
    }

    private fun setEditTextData(til: TextInputLayout, value: String?) {
        if (value != null && value != "") {
            til.editText!!.setText(value)
            til.editText!!.isEnabled = false
            //til.editText!!.setTextColor(ColorStateList.valueOf())
        } else {
            til.editText!!.isEnabled = true
        }
    }

}