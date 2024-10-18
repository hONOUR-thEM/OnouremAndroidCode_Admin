package com.onourem.android.activity.ui.admin.main.other

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import java.util.Locale

class CustomAdapter<T>(context: Context, resource: Int, private val items: List<T>) :
    ArrayAdapter<T>(context, resource, items), Filterable {

    private val originalList: List<T> = ArrayList(items)
    private var filteredList: List<T> = ArrayList(items)

    override fun getCount(): Int = filteredList.size

    override fun getItem(position: Int): T? = filteredList.getOrNull(position)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()

                if (constraint.isNullOrBlank()) {
                    results.values = originalList
                    results.count = originalList.size
                } else {
                    val filteredItems = ArrayList<T>()
                    val constraintLowerCase = constraint.toString().lowercase(Locale.ROOT)

                    for (item in originalList) {
                        val itemName = item.toString().lowercase(Locale.ROOT)
                        if (itemName.contains(constraintLowerCase)) {
                            filteredItems.add(item)
                        }
                    }

                    results.values = filteredItems
                    results.count = filteredItems.size
                }

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as? List<T> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}