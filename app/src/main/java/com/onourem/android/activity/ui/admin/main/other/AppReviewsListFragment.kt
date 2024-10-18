package com.onourem.android.activity.ui.admin.main.other

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentAppReviewsListBinding
import com.onourem.android.activity.models.CsvRow
import com.onourem.android.activity.models.MoodInfoData
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList


class AppReviewsListFragment : AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentAppReviewsListBinding>() {

    private lateinit var layoutManager: LinearLayoutManager
    private var adapter: UserReviewsAdapter? = null
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private var selectedRadioButton: String = "5Star"



    private var formattedDateTime: String = ""

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_app_reviews_list
    }

    companion object {

        fun create(): AppReviewsListFragment {
            val fragment = AppReviewsListFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(activity)
        binding.rvReviews.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.fab.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            pickFileLauncher.launch(intent)
        }
    }

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->

                val models = readCsvToList(uri)

                if (models.isNotEmpty()) {
                    val listOf5Star = models.filter{
                        it.column10 == "5"
                    }

                    val listOf4Star = models.filter{
                        it.column10 == "4"
                    }

                    setAdapter(models as ArrayList<CsvRow>)
                }

            }
        }
    }

    private fun readCsvToList(uri: Uri): List<CsvRow> {
        val models = mutableListOf<CsvRow>()

        fragmentContext.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                var lineNumber = 0
                while (reader.readLine().also { line = it } != null) {
                    if (lineNumber == 0) {
                        // Skip header line
                        lineNumber++
                        continue
                    }
                    val columns = line!!.split(",") // Assuming CSV columns are separated by commas
                    if (columns.size == 16) {
                        val model = CsvRow(
                            columns[0], columns[1], columns[2], columns[3], columns[4], columns[5], columns[6], columns[7],
                            columns[8], columns[9], columns[10], columns[11], columns[12], columns[13], columns[14], columns[15]
                        )
                        models.add(model)
                    } else {
                        // Handle incomplete or incorrect lines as needed
                        // For example, you might want to log a warning
                        // Log.w("CSV Parsing", "Invalid line at line number $lineNumber: $line")
                    }
                    lineNumber++
                }
            }
        }

        return models
    }

    private fun setAdapter(list: ArrayList<CsvRow>) {
        isLastPage = false

        adapter = UserReviewsAdapter(
            list
        ) {

        }

        binding.rvReviews.adapter = adapter

        if (adapter != null && adapter!!.itemCount == 0) {
            binding.tvMessage.visibility = View.VISIBLE
        } else {
            binding.tvMessage.visibility = View.GONE
        }

        binding.rvReviews.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                this@AppReviewsListFragment.isLoading = true
//                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@AppReviewsListFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@AppReviewsListFragment.isLoading
            }
        })

    }
}