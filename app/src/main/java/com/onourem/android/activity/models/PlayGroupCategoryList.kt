package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlayGroupCategoryList(
    @field:Expose @field:SerializedName("id") internal var id: Int,
    @field:Expose @field:SerializedName("categoryName") internal var categoryName: String,
    @field:Expose @field:SerializedName(
        "isActive"
    ) private var isActive: String
) {
    private var isSelected = false
    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getCategoryName(): String {
        return categoryName
    }

    fun setCategoryName(categoryName: String) {
        this.categoryName = categoryName
    }

    fun getIsActive(): String {
        return isActive
    }

    fun setIsActive(isActive: String) {
        this.isActive = isActive
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }

    override fun toString(): String {
        return categoryName
    }
}