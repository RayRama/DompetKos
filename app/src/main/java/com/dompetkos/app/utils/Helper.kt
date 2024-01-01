package com.dompetkos.app.utils

import java.text.SimpleDateFormat
import java.util.Date

object Helper {
    fun formatDate(date: Date?): String {
        val dateFormat = SimpleDateFormat("dd MMMM, yyyy")
        return dateFormat.format(date)
    }

    fun formatDateWithTime(date: Date?): String {
        val dateFormat = SimpleDateFormat("dd MMMM, yyyy - HH:mm:ss")
        return dateFormat.format(date)
    }

    fun formatDateByMonth(date: Date?): String {
        val dateFormat = SimpleDateFormat("MMMM, yyyy")
        return dateFormat.format(date)
    }
}