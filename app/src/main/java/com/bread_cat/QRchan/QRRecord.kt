// QRRecord.kt
package com.bread_cat.QRchan

import java.io.Serializable
import java.text.SimpleDateFormat

data class QRRecord(
    val content: String,
    val date: String
) : Serializable
