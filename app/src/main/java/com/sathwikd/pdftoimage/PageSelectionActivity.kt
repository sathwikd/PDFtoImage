package com.sathwikd.pdftoimage

import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PageSelectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pageAdapter: PageAdapter

    private lateinit var pages: ArrayList<SinglePage>
    private lateinit var pdfUri: Uri
    private var selectedPages: MutableList<Int> = mutableListOf()
    private lateinit var checkBoxSelectAll: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtilsBasic.applyThemeAndEdgeToEdge(this)
        setContentView(R.layout.activity_select_pages)

        recyclerView = findViewById(R.id.pageRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // Grid layout with 3 columns

        // Get the PDF URI from the intent

        val pdfUriString = intent.getStringExtra("PDF_URI")
        val fileName = intent.getStringExtra("PDF_FILE_NAME")
        pdfUri = Uri.parse(pdfUriString)

        // Initialize pages list
        pages = ArrayList()

        // Set up adapter with page data
        pageAdapter = PageAdapter(pages) { pageNumber, isSelected ->
            if (isSelected) {
                selectedPages.add(pageNumber)
            } else {
                selectedPages.remove(pageNumber)
            }
        }
        recyclerView.adapter = pageAdapter

        // Load the PDF thumbnails
        loadPdfThumbnails(pdfUri)

        // Confirm button to return the selected pages
        val btnConfirm = findViewById<Button>(R.id.buttonConfirm)
        btnConfirm.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("SELECTED_PAGES", selectedPages.toIntArray())
                putExtra("PDF_URI", pdfUriString)
                putExtra("PDF_FILE_NAME", fileName)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Get reference to the Select All checkbox
        checkBoxSelectAll = findViewById(R.id.checkSelectAll)

        // Set the check change listener
        checkBoxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            //Update selected pages
            selectedPages.clear()
            for (i in 0 until pages.size) {
                pages[i].isSelected = isChecked
                if (isChecked){
                    selectedPages.add(pages[i].number)
                }
            }

            // Notify the adapter to update the UI
            pageAdapter.notifyItemRangeChanged(0, pages.size)
        }
    }

    private val THUMBNAIL_MAX_WIDTH = 500
    private val THUMBNAIL_MAX_HEIGHT = 500

    private fun loadPdfThumbnails(pdfUri: Uri) {
        try {
            val pfd: ParcelFileDescriptor? = contentResolver.openFileDescriptor(pdfUri, "r")
            if (pfd != null) {
                val pdfRenderer = PdfRenderer(pfd)
                val pageCount = pdfRenderer.pageCount

                for (i in 0 until pageCount) {
                    // Add placeholder SinglePage objects to the list for each page
                    pages.add(SinglePage(number = i + 1))

                    // Open the PDF page
                    val page = pdfRenderer.openPage(i)
                    val pageWidth = page.width.toFloat()
                    val pageHeight = page.height.toFloat()
                    val aspectRatio = pageWidth / pageHeight

                    val thumbnailWidth: Int
                    val thumbnailHeight: Int

                    if (aspectRatio > 1) {
                        // Landscape or wide
                        thumbnailWidth = THUMBNAIL_MAX_WIDTH
                        thumbnailHeight = (THUMBNAIL_MAX_WIDTH / aspectRatio).toInt()
                    } else {
                        // Portrait or tall
                        thumbnailHeight = THUMBNAIL_MAX_HEIGHT
                        thumbnailWidth = (THUMBNAIL_MAX_HEIGHT * aspectRatio).toInt()
                    }
                    val bitmap = createBitmap(thumbnailWidth, thumbnailHeight)

                    // Render the PDF page into the bitmap
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    // Assign the thumbnail to the corresponding page
                    pages[i].thumbnail = bitmap

                    // Close the page after rendering
                    page.close()

                    // Update the adapter about the new thumbnail
                    pageAdapter.notifyItemChanged(i)
                }

                // Close the PdfRenderer after processing
                pdfRenderer.close()
            }
        } catch (e: Exception) {
            Log.e("PageSelectionActivity", "Error loading PDF thumbnails", e)
        }
    }
}