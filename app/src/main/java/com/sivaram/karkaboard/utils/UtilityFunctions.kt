package com.sivaram.karkaboard.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import java.util.Date
import java.util.Locale

object UtilityFunctions {
    fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun convertMillisToDateMonthFormat(millis: Long): String{
        val formatter = SimpleDateFormat("dd MMM,yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor?.moveToFirst()
        val name = nameIndex?.let { cursor.getString(it) }
        cursor?.close()
        return name
    }
    @Composable
    fun getGradient(): Brush {
        val brush = Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.inversePrimary,
                MaterialTheme.colorScheme.onPrimaryContainer
            ),
        )
        return brush
    }
    fun convertLongToDateTimeAmPm(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
        return format.format(date)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerModal(onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }
}