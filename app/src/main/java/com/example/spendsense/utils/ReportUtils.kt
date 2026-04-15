package com.example.spendsense.utils

import android.content.Context
import com.example.spendsense.model.Transaction
import java.io.File

object ReportUtils {

    fun exportCSV(context: Context, list: List<Transaction>): File {

        val file = File(context.filesDir, "spendsense_report.csv")

        file.printWriter().use { out ->

            // CSV HEADER
            out.println("ID,Type,Category,Amount,Timestamp,Note")

            list.forEach {

                val safeNote = it.note.replace(",", " ")

                out.println(
                    "${it.id},${it.type},${it.category},${it.amount},${it.timestamp},$safeNote"
                )
            }
        }

        return file
    }

    fun generateSummary(list: List<Transaction>): String {

        val income = list.filter {
            it.type.equals("INCOME", true)
        }.sumOf { it.amount }

        val expense = list.filter {
            it.type.equals("EXPENSE", true)
        }.sumOf { it.amount }

        val balance = income - expense

        return """
            Income: $income
            Expense: $expense
            Balance: $balance
        """.trimIndent()
    }
}