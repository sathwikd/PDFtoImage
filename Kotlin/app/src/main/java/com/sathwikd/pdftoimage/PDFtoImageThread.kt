package com.sathwikd.pdftoimage

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.ParcelFileDescriptor
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

class PDFtoImageThread(
    private val handler: Handler,
    private val pfd: ParcelFileDescriptor,
    private val fileName: String,
    private val quality: Int,
    private val formatImage: Bitmap.CompressFormat,
    private val progressBar: ProgressBar,
    private val activity: Activity
) : Runnable, PdfConversionProgressListener {

    override fun run() {
        activity.runOnUiThread {
            progressBar.visibility = View.VISIBLE
            progressBar.max = 100 // Default value, will be updated in onMaxProgressDetermined
        }
        val message = PdfUtilsSat.savePDFasImage(pfd, fileName, quality, formatImage, this)
        handler.post { Toast.makeText(activity, message, Toast.LENGTH_SHORT).show() }
        activity.runOnUiThread { progressBar.visibility = View.INVISIBLE }
    }

    override fun onProgressUpdate(pageNumber: Int) {
        activity.runOnUiThread {
            progressBar.progress = pageNumber
        }
    }

    override fun onMaxProgressDetermined(maxProgress: Int) {
        activity.runOnUiThread {
            progressBar.max = maxProgress
        }
    }
}