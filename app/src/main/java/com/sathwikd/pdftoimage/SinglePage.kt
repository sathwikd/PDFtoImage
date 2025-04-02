package com.sathwikd.pdftoimage

import android.graphics.Bitmap

data class SinglePage(
    val number: Int,
    var isSelected: Boolean = false,
    var thumbnail: Bitmap? = null // Add thumbnail for each page
)