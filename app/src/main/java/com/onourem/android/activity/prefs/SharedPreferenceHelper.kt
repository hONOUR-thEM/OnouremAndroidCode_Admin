package com.onourem.android.activity.prefs

import android.accounts.Account
import android.app.Application
import android.content.SharedPreferences
import com.onourem.android.activity.prefs.PhoneticName.ALWAYS_SHOW
import com.onourem.android.activity.prefs.PhoneticName.HIDE_IF_EMPTY
import javax.inject.Inject

class SharedPreferenceHelper @Inject constructor(application: Application) {
    private val sharedPreferences: SharedPreferences
    fun putValue(key: String?, value: Any?) {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> {
                editor.putString(key, value as String?).apply()
            }
            is Int -> {
                editor.putInt(key, (value as Int?)!!).apply()
            }
            is Float -> {
                editor.putFloat(key, (value as Float?)!!).apply()
            }
            is Boolean -> {
                editor.putBoolean(key, (value as Boolean?)!!).apply()
            }
            is Long -> {
                editor.putLong(key, (value as Long?)!!).apply()
            }
        }
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }

    fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, 0L)
    }

    fun getFloat(key: String): Float {
        return sharedPreferences.getFloat(key, 0.0f)
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getIntFromString(key: String): Int {
        val var10000 = sharedPreferences.getString(key, "0")
        return var10000!!.toInt()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    init {
        sharedPreferences = application.getSharedPreferences("OnouremApp-Prefs", 0)
    }


    var defaultAccountForNewContacts: Account?
        get() {
            val accountName = sharedPreferences.getString(ACCOUNT_NAME, null)
            val accountType = sharedPreferences.getString(ACCOUNT_TYPE, null)
            return if (accountName != null && accountType != null) {
                Account(accountName, accountType)
            } else {
                null
            }
        }
        set(value) {
            sharedPreferences
                .edit()
                .putString(ACCOUNT_NAME, value?.name)
                .putString(ACCOUNT_TYPE, value?.type)
                .apply()
        }

    var sortBy: SortBy
        get() = sharedPreferences.getString(SORT_BY, null)?.let(SortBy::valueOf)
            ?: SortBy.FIRST_NAME
        set(value) {
            sharedPreferences
                .edit()
                .putString(SORT_BY, value.name)
                .apply()
        }

    var nameFormat: NameFormat
        get() = sharedPreferences.getString(NAME_FORMAT, null)?.let(NameFormat::valueOf)
            ?: NameFormat.FIRST_NAME_FIRST
        set(value) {
            sharedPreferences
                .edit()
                .putString(NAME_FORMAT, value.name)
                .apply()
        }


    var phoneticName: PhoneticName
        get() = sharedPreferences.getString(PHONETIC_NAME, null)?.let(PhoneticName::valueOf)
            ?: PhoneticName.ALWAYS_SHOW
        set(value) {
            sharedPreferences
                .edit()
                .putString(PHONETIC_NAME, value.name)
                .apply()
        }
}


enum class SortBy(private val value: String) {
    FIRST_NAME("First name"),
    LAST_NAME("Last name");

    override fun toString() = "Sort by: $value"
}

enum class NameFormat(private val value: String) {
    FIRST_NAME_FIRST("First name first"),
    LAST_NAME_FIRST("Last name first");

    override fun toString() = "Name format: $value"
}

/**
 * Determines the visibility of phonetic name fields in the contact details' view mode.
 *
 * - When [ALWAYS_SHOW], phonetic name fields will be shown even if they are all empty.
 * - When [HIDE_IF_EMPTY], phonetic name fields will be hidden if all are empty.
 *
 * Phonetic name fields will always be visible in edit mode.
 *
 * This has nothing to do with the contact primary or alt display name.
 */
enum class PhoneticName(private val value: String) {
    ALWAYS_SHOW("Always show"),
    HIDE_IF_EMPTY("Hide if empty");

    override fun toString() = "Phonetic name: $value"
}

private const val SHARED_PREFS_NAME = "contacts.sample.SHARED_PREFS"

private const val ACCOUNT_NAME = "ACCOUNT_NAME"
private const val ACCOUNT_TYPE = "ACCOUNT_TYPE"

private const val SORT_BY = "SORT_BY"
private const val NAME_FORMAT = "NAME_FORMAT"
private const val PHONETIC_NAME = "PHONETIC_NAME"