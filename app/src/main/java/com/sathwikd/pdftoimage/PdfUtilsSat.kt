package com.sathwikd.pdftoimage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap

object PdfUtilsSat {
    fun savePDFasImage(pfd: ParcelFileDescriptor?, pdfName: String, qualityOfImage: Int, formatOfImage: Bitmap.CompressFormat, progressListener: PdfConversionProgressListener, selectedPages: IntArray): String {
        try {
            pfd?.let {
                val renderer = PdfRenderer(it)
                if (renderer.pageCount > 80) {
                    return "Maximum pages allowed- 80"
                }

                // Create a directory for the images
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                if (!picturesDir.exists() && !picturesDir.mkdir()) {
                    Log.e(TAG, "Failed to create Pictures directory")
                    return "Failed to create Pictures directory"
                }

                val pdf2ImgDir = File(picturesDir, "PDFtoIMG")
                if (!pdf2ImgDir.exists() && !pdf2ImgDir.mkdir()) {
                    Log.e(TAG, "Failed to create PDFtoIMG directory")
                    return "Failed to create PDFtoIMG directory"
                }

                // PDF sub directory with pdf file name
                val pdfSubDir = File(pdf2ImgDir, pdfName)
                if (!pdfSubDir.exists() && !pdfSubDir.mkdir()) {
                    Log.e(TAG, "Failed to create PDF sub-directory")
                    return "Failed to create PDF sub-directory"
                }

                progressListener.onMaxProgressDetermined(renderer.pageCount)

                // Convert selected pages
                for (pageNumber in selectedPages) {
                    val pageIndex = pageNumber - 1 // Adjust for 0-based indexing
                    if (pageIndex in 0 until renderer.pageCount) {
                        renderer.openPage(pageIndex).use { page ->
                            val bitmap = createBitmap(
                                page.width * qualityOfImage,
                                page.height * qualityOfImage
                            )
                            val canvas = Canvas(bitmap)
                            canvas.drawColor(Color.WHITE)
                            val rect = Rect(0, 0, page.width * qualityOfImage, page.height * qualityOfImage)
                            page.render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                            val extension = if (formatOfImage == Bitmap.CompressFormat.JPEG) ".jpg" else ".png"
                            val imageFile = File(pdfSubDir, "$pdfName($pageNumber)$extension")
                            FileOutputStream(imageFile).use { fos ->
                                bitmap.compress(formatOfImage, if (qualityOfImage >= 2) 80 else 100, fos)
                            }

                            // Clean up resources
                            bitmap.recycle()
                        }
                        progressListener.onProgressUpdate(selectedPages.indexOf(pageNumber) + 1)
                    }
                }

                renderer.close()
                pfd.close()
            }
            return "PDF Conversion Completed"
        } catch (e: Exception) {
            Log.e(TAG, "Error converting PDF to images", e)
            return "Error converting PDF to images"
        }
    }

    private const val TAG = "PdfUtilsSat"
}