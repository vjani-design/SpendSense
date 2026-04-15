package com.example.spendsense.utils

import android.content.Context
import android.os.Environment
import com.example.spendsense.model.Transaction
import java.io.File
import java.io.FileWriter

object CsvExporter {

    fun exportToFile(context: Context, list: List<Transaction>): File? {

        return try {

            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

            if (dir == null) return null

            val file = File(dir, "spendsense_report.csv")

            val writer = FileWriter(file)

            // HEADER (UNCHANGED)
            writer.append("ID,TYPE,CATEGORY,AMOUNT,NOTE,PAYMENT,TIMESTAMP\n")

            // DATA (UNCHANGED LOGIC)
            list.forEach {
                writer.append(
                    "${it.id},${it.type},${it.category},${it.amount},${it.note},${it.paymentMethod},${it.timestamp}\n"
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