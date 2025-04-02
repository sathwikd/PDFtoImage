package com.sathwikd.pdftoimage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ProgressBar
import java.util.concurrent.Executors

object PDFConverter {
    private const val TAG = "PDFConverter"

    fun startConversion(
        context: Context,
        uri: Uri,
        fileName: String,
        selectedPages: IntArray,
        qualityOfImage: Int,
        formatOfImage: Bitmap.CompressFormat,
        progressBar: ProgressBar
    ) {
        try {
            val pfd = context.contentResolver.openFileDescriptor(uri, "r")
            if (pfd != null) {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())

                // Pass selectedPages array to the PDF conversion thread
                val pdfToImageThread = PDFtoImageThread(
                    handler,
                    pfd,
                    removeFileExtension(fileName),
                    qualityOfImage,
                    formatOfImage,
                    progressBar,
                    context,
                    selectedPages // Pass selected pages for conversion
                )
                executor.execute(pdfToImageThread)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling file URI", e)
        }
    }

    private fun removeFileExtension(fileName: String): String {
        return fileName.substringBeforeLast('.')
    }
}