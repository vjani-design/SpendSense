package com.example.spendsense.utils

import android.content.Context
import android.os.Environment
import com.example.spendsense.model.Transaction
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Locale

object CsvExporter {

    fun exportToFile(context: Context, list: List<Transaction>): File? {

        return try {

            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (dir == null) return null

            val file = File(dir, "spendsense_report.csv")
            val writer = FileWriter(file)

            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            writer.append("ID,TYPE,CATEGORY,AMOUNT,NOTE,PAYMENT,DATE\n")

            list.forEach {

                val safeNote = it.note.replace(",", " ")

                val dateString = it.date?.toDate()?.let {
                    formatter.format(it)
                } ?: ""

                writer.append(
                    "${it.id},${it.type},${it.category},${it.amount},$safeNote,${it.paymentMethod},$dateString\n"
                )
            }

            writer.flush()
            writer.close()

            file

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}