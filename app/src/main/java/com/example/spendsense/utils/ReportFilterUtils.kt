package com.example.spendsense.utils

import com.example.spendsense.model.Transaction

object ReportFilterUtils {

    fun filterByDateRange(
        list: List<Transaction>,
        start: Long,
        end: Long
    ): List<Transaction> {
        return list.filter { txn ->
            val time = txn.date?.toDate()?.time ?: return@filter false
            time in start..end
        }
    }

    fun filterByCategory(
        list: List<Transaction>,
        category: String
    ): List<Transaction> {
        return list.filter {
            it.category.equals(category, ignoreCase = true)
        }
    }

    fun filterByType(
        list: List<Transaction>,
        type: String
    ): List<Transaction> {
        return list.filter {
            it.type.equals(type, ignoreCase = true)
        }
    }

    fun filter(
        list: List<Transaction>,
        start: Long? = null,
        end: Long? = null,
        category: String? = null,
        type: String? = null
    ): List<Transaction> {

        var result = list

        // ✅ SAFE DATE FILTER
        if (start != null && end != null) {
            result = filterByDateRange(result, start, end)
        }

        // ✅ SAFE CATEGORY FILTER
        if (!category.isNullOrBlank()) {
            result = filterByCategory(result, category)
        }

        // ✅ SAFE TYPE FILTER
        if (!type.isNullOrBlank()) {
            result = filterByType(result, type)
        }

        return result
    }
}