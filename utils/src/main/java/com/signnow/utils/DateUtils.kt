package com.signnow.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val MILLS_IN_SECOND = 1000L
    private const val H_MM_AA = "h:mm aa"
    private const val DOC_UPDATED_DATE_FORMAT = "MMM dd, yyyy"

    fun getFormattedTimestampActionSheet(sec: Long): String {
        val dateFormatForDate = SimpleDateFormat(DOC_UPDATED_DATE_FORMAT, Locale.ENGLISH)
        val date = dateFormatForDate.format(getDateFromTimeInSec(sec))
        val dateFormatForTime = SimpleDateFormat(H_MM_AA, Locale.ENGLISH)
        val time = dateFormatForTime.format(getDateFromTimeInSec(sec))
        return "$date at $time"
    }

    private fun getDateFromTimeInSec(timeInSeconds: Long): Date {
        return Date(timeInSeconds * MILLS_IN_SECOND)
    }
}