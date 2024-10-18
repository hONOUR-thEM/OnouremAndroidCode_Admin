package com.onourem.android.activity.ui.admin.create.surveys

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.with
import com.google.android.material.textfield.TextInputLayout
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.CustomSuccessLayoutBinding
import com.onourem.android.activity.databinding.FragmentAdminSurveysBinding
import com.onourem.android.activity.models.RequestAddSurvey
import com.onourem.android.activity.models.UploadSurveyResponse
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.PermissionsUtil
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.progress.OnouremProgressDialog
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AddSurveyFragment  //    private int counter = 0;
    : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAdminSurveysBinding>() {

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper
    private var adapter: ArrayAdapter<String>? = null
    private var adapterShowStats: ArrayAdapter<String>? = null
    private var adapterOClub: ArrayAdapter<String>? = null
    private var large = ""
    private var small = ""
    private var uri = ""
    var textInputLayoutArrayList: ArrayList<TextInputLayout>? = null
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.ic_gallery)
        .error(R.drawable.ic_gallery)

    override fun layoutResource(): Int {
        return R.layout.fragment_admin_surveys
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    //Crop image(Optional), Check Customization for more option
    //Final image size will be less than 1 MB(Optional)
    //Final image resolution will be less than 1080 x 1080(Optional)
    private val image: Unit
        get() {
            with(this)
                .crop()
                .galleryOnly() //Crop image(Optional), Check Customization for more option
                .compress(1024) //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                ) //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionsUtil.PERMISSION_ALL) {
            if (grantResults.isNotEmpty()) {
                val indexesOfPermissionsNeededToShow: MutableList<Int> = ArrayList()
                for (i in permissions.indices) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            permissions[i]
                        )
                    ) {
                        indexesOfPermissionsNeededToShow.add(i)
                    }
                }
                val size = indexesOfPermissionsNeededToShow.size
                if (size != 0) {
                    var i = 0
                    var isPermissionGranted = true
                    while (i < size && isPermissionGranted) {
                        isPermissionGranted = (grantResults[indexesOfPermissionsNeededToShow[i]]
                                == PackageManager.PERMISSION_GRANTED)
                        i++
                    }
                    if (!isPermissionGranted) {
                        showDialogNotCancelable { dialogInterface: DialogInterface?, i1: Int ->
                            PermissionsUtil.askPermissions(
                                activity
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showDialogNotCancelable(okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(activity)
            .setTitle("Permissions mandatory")
            .setMessage("All the permissions are required for this app")
            .setPositiveButton("OK", okListener)
            .setCancelable(false)
            .create()
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            if (data != null) {
                val fileUri = data.data
                Glide.with(requireActivity()).asBitmap().load(fileUri)
                    .addListener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            requireActivity().runOnUiThread {
                                Glide.with(requireActivity())
                                    .load(resource)
                                    .apply(options)
                                    .into(binding.ivSelectedMedia)
                                large = getBase64String(resource, 500)
                                small = getBase64String(resource, 100)
                                if (fileUri != null) {
                                    uri = fileUri.path.toString()
                                }
                                binding.btnChoosePic.visibility = View.VISIBLE
                                binding.card.visibility = View.VISIBLE
                            }
                            return true
                        }
                    }).submit()
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tilSpinnerShowStats.isEnabled  = false
        binding.tilSpinnerOClubSpecific.isEnabled  = false

        if (adapter == null) {
            adapter = ArrayAdapter(
                requireActivity(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.survey)
            )
        }
        binding.tilSpinner.setAdapter(adapter)

        if (adapterShowStats == null) {
            adapterShowStats = ArrayAdapter(
                requireActivity(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.yesNo)
            )
        }
        binding.tilSpinnerShow.setAdapter(adapterShowStats)

        if (adapterOClub == null) {
            adapterOClub = ArrayAdapter(
                requireActivity(),
                R.layout.dropdown_menu_popup_item,
                resources.getStringArray(R.array.yesNo)
            )
        }
        binding.tilSpinnerOClubSpecificEdittext.setAdapter(adapterOClub)

        binding.tilSpinner.setOnItemClickListener { _, _, _, _ ->
            binding.tilSpinnerShowStats.isEnabled = true
        }

        binding.tilSpinnerShow.setOnItemClickListener { _, _, _, _ ->
            binding.tilSpinnerOClubSpecific.isEnabled = true
        }

        binding.btnChoosePic.setOnClickListener(ViewClickListener { v: View? -> image })
        binding.btnDate.setOnClickListener(ViewClickListener { v: View? ->
            // Get Current Date
            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR]
            val mMonth = c[Calendar.MONTH]
            val mDay = c[Calendar.DAY_OF_MONTH]

            @SuppressLint("SetTextI18n")
            val datePickerDialog = DatePickerDialog(
                binding.root.context,
                { view1: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val cal = Calendar.getInstance()
                    cal[Calendar.YEAR] = year
                    cal[Calendar.MONTH] = monthOfYear
                    cal[Calendar.DATE] = dayOfMonth
                    val date = cal.time
                    @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat("dd-MM-yyyy")
                    binding.btnDate.text = formatter.format(date)
                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()
        })
        binding.btnSubmit.setOnClickListener(ViewClickListener { v: View? ->
            if (isValidData) {
                val requestAddSurvey = RequestAddSurvey()
                requestAddSurvey.surveyDate = binding.btnDate.text.toString()

                requestAddSurvey.surveyText = binding.tilSurveyText.editText!!.text.toString()

                requestAddSurvey.surveyOption1 = binding.tilOption1.editText!!.text.toString()
                requestAddSurvey.surveyOption2 = binding.tilOption2.editText!!.text.toString()
                requestAddSurvey.surveyOption3 = binding.tilOption3.editText!!.text.toString()
                requestAddSurvey.surveyOption4 = binding.tilOption4.editText!!.text.toString()
                requestAddSurvey.surveyOption5 = binding.tilOption5.editText!!.text.toString()
                requestAddSurvey.surveyOption6 = binding.tilOption6.editText!!.text.toString()
                requestAddSurvey.surveyOption7 = binding.tilOption7.editText!!.text.toString()
                requestAddSurvey.surveyOption8 = binding.tilOption8.editText!!.text.toString()

                requestAddSurvey.userSurveyType = binding.tilSpinnerInput.editText!!.text.toString()

                requestAddSurvey.showStats = if (binding.tilSpinnerShowStats.editText!!.text.toString() == "Yes") {
                    "Y"
                } else {
                    "N"
                }
                requestAddSurvey.oclubSpecific = if (binding.tilSpinnerOClubSpecific.editText!!.text.toString() == "Yes") {
                    "Y"
                } else {
                    "N"
                }

                val onouremProgressDialog = OnouremProgressDialog(fragmentContext)

                val progressCallback = object : ProgressCallback {
                    override fun onProgressUpdate(percentage: Int) {
                        onouremProgressDialog.setProgress(
                            percentage
                        )
                    }
                }

                viewModel.addSurveyInfo(requestAddSurvey, uri, progressCallback)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UploadSurveyResponse> ->
                        if (apiResponse.loading) {
                            if (!uri.equals("", ignoreCase = true)) {
                                onouremProgressDialog.showDialogWithText("Uploading Image", true)
                            } else {
                                showProgress()
                            }
                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                            onouremProgressDialog.hideDialog()
                            hideProgress()
                            showAlertDialogButtonClicked(apiResponse.body)
                        } else {
                            onouremProgressDialog.hideDialog()
                            hideProgress()
                            showAlert(
                                getString(R.string.label_network_error),
                                apiResponse.errorMessage
                            )
                        }
                    }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun showAlertDialogButtonClicked(uploadSurveyResponse: UploadSurveyResponse?) {
        val builder = AlertDialog.Builder(activity)
        val dialogViewBinding = CustomSuccessLayoutBinding.inflate(
            LayoutInflater.from(
                activity
            )
        )
        builder.setView(dialogViewBinding.root)
        builder.setCancelable(false)
        val alertDialog = builder.create()
        dialogViewBinding.tvDialogTitle.setText(R.string.label_success)
        dialogViewBinding.txtSurvey.text = uploadSurveyResponse!!.surveyText
        dialogViewBinding.txtId.text =
            String.format("Survey Id : %s", uploadSurveyResponse.surveyId)
        dialogViewBinding.txtDate.text = uploadSurveyResponse.surveyDate
        if (!uploadSurveyResponse.imageUrl.equals("", ignoreCase = true)) {
            dialogViewBinding.surveyImage.visibility = View.VISIBLE
            Glide.with(binding.root.context)
                .load(uploadSurveyResponse.imageUrl)
                .apply(options)
                .into(dialogViewBinding.surveyImage)
        } else {
            dialogViewBinding.surveyImage.visibility = View.GONE
        }
        dialogViewBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? ->
            binding.btnDate.text = "Select Date"
            uri = ""
            binding.ivSelectedMedia.setImageDrawable(null)
            binding.ivSelectedMedia.visibility = View.GONE
            binding.tilSurveyText.editText!!.setText("")
            binding.tilOption1.editText!!.setText("")
            binding.tilOption2.editText!!.setText("")
            binding.tilOption3.editText!!.setText("")
            binding.tilOption4.editText!!.setText("")
            binding.tilOption5.editText!!.setText("")
            binding.tilOption6.editText!!.setText("")
            binding.tilOption7.editText!!.setText("")
            binding.tilOption8.editText!!.setText("")
            binding.tilOption1.editText!!.clearFocus()
            binding.tilOption2.editText!!.clearFocus()
            binding.tilOption3.editText!!.clearFocus()
            binding.tilOption4.editText!!.clearFocus()
            binding.tilOption5.editText!!.clearFocus()
            binding.tilOption6.editText!!.clearFocus()
            binding.tilOption7.editText!!.clearFocus()
            binding.tilOption8.editText!!.clearFocus()
            binding.tilSpinnerInput.editText!!.setText("")
            binding.tilSpinnerShowStats.editText!!.setText("")
            binding.tilSpinnerOClubSpecific.editText!!.setText("")

            binding.tilSpinnerShowStats.isEnabled  = false
            binding.tilSpinnerOClubSpecific.isEnabled  = false
            binding.parent.smoothScrollTo(0,0)
            alertDialog.dismiss()
        })
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private val isValidData: Boolean
        get() {
            var result = true
            textInputLayoutArrayList = ArrayList()
            textInputLayoutArrayList!!.add(binding.tilOption1)
            textInputLayoutArrayList!!.add(binding.tilOption2)
            textInputLayoutArrayList!!.add(binding.tilOption3)
            textInputLayoutArrayList!!.add(binding.tilOption4)
            textInputLayoutArrayList!!.add(binding.tilOption5)
            textInputLayoutArrayList!!.add(binding.tilOption6)
            textInputLayoutArrayList!!.add(binding.tilOption7)
            textInputLayoutArrayList!!.add(binding.tilOption8)
            textInputLayoutArrayList!!.add(binding.tilSpinnerInput)
            if (binding.tilSpinnerInput.editText!!.text.toString().trim()
                    .equals("", ignoreCase = true)
            ) {
                showAlert("Please enter survey type.")
                binding.tilSpinnerInput.editText!!.setText("")
                result = false
            }else if (binding.tilSpinnerOClubSpecific.editText!!.text.toString().trim()
                    .equals("", ignoreCase = true)
            ) {
                showAlert("Please enter survey for oclub or not.")
                binding.tilSpinnerOClubSpecific.editText!!.setText("")
                result = false
            }else if (binding.tilSurveyText.editText!!.text.toString().trim()
                    .equals("", ignoreCase = true)
            ) {
                showAlert("Please enter survey Text.")
                binding.tilSurveyText.editText!!.setText("")
                result = false
            } else if (checkEditText() < 2) {
                showAlert("Please enter minimum two survey options.")
                result = false
            } else if (!binding.btnDate.text.toString().contains("-")) {
                showAlert("Please select date.")
                result = false
            }
            return result
        }

    private fun checkEditText(): Int {
        var counter = 0
        for (textInputLayout in textInputLayoutArrayList!!) {
            if (!textInputLayout.editText!!.text.toString().trim().equals("", ignoreCase = true)) {
                counter++
            } else {
                textInputLayout.editText!!.setText("")
            }
        }
        return counter
    }

    companion object {

        fun create(): AddSurveyFragment {
            val fragment = AddSurveyFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }

        fun getBase64String(bitmap: Bitmap?, intendedWidth: Int): String {
            var btmp = bitmap ?: return ""
            try {
                btmp = resizeImage(btmp, intendedWidth)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return if (btmp == null) {
                ""
            } else {
                bitmapToBase64(btmp)
            }
        }

        private fun resizeImage(bump: Bitmap, intendedWidth: Int): Bitmap {
            var btmp = bump
            try {
                btmp = Bitmap.createScaledBitmap(
                    btmp,
                    intendedWidth,
                    getResizedHeightByIntendedWidth(btmp, intendedWidth),
                    true
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return btmp
        }

        private fun bitmapToBase64(bitmap: Bitmap?): String {
            try {
                if (bitmap != null) {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()
                    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return ""
        }

        private fun getResizedHeightByIntendedWidth(photo: Bitmap?, intendedWidth: Int): Int {
            return if (photo == null) {
                intendedWidth
            } else {
                if (intendedWidth > photo.width) {
                    val widthScalePercentage =
                        (intendedWidth - photo.width).toFloat() * 100 / photo.width
                    val increaseHeightBy = photo.height.toFloat() * widthScalePercentage / 100
                    photo.height + increaseHeightBy.toInt()
                } else if (intendedWidth < photo.width) {
                    val widthScalePercentage =
                        (photo.width - intendedWidth).toFloat() * 100 / photo.width
                    val decreaseHeightBy = photo.height.toFloat() * widthScalePercentage / 100
                    photo.height - decreaseHeightBy.toInt()
                } else {
                    photo.height
                }
            }
        }
    }
}