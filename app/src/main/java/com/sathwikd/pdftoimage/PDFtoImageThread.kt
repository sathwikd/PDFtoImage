package com.sathwikd.pdftoimage

import android.content.Context
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
    private val context: Context,
    private val selectedPages: IntArray
) : Runnable, PdfConversionProgressListener {

    override fun run() {
        try {
            handler.post {
                progressBar.visibility = View.VISIBLE
                progressBar.max = 100 // Default value, will be updated in onMaxProgressDetermined
            }
            val message = PdfUtilsSat.savePDFasImage(pfd, fileName, quality, formatImage, this, selectedPages)
            handler.post {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()  // Use context instead of activity
            }
        } catch (e: Exception) {
            handler.post {
                Toast.makeText(context, "Error during conversion: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        } finally {
            handler.post { progressBar.visibility = View.INVISIBLE }
        }
    }

    override fun onProgressUpdate(pageNumber: Int) {
        handler.post {
            progressBar.progress = pageNumber
        }
    }

    override fun onMaxProgressDetermined(maxProgress: Int) {
        handler.post {
            progressBar.max = maxProgress
        }
    }
}