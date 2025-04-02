package com.sathwikd.pdftoimage

interface PdfConversionProgressListener {
    fun onProgressUpdate(pageNumber: Int)
    fun onMaxProgressDetermined(maxProgress: Int)
}