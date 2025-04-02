package com.sathwikd.pdftoimage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile

class FilePickerHandler (
    private val activity: AppCompatActivity,
    private val textViewUri: TextView,
    private val textViewFileName: TextView,
    private val onFilePicked: (Uri, String) -> Unit
) {
    private val filePickerActivityResult: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uri = data?.data
            if (uri != null) {
                textViewUri.text = uri.path

                // Get name of the file
                val fileName = getFileName(uri)
                if (fileName != null) {
                    textViewFileName.text = fileName
                    onFilePicked(uri, fileName) // Invoke callback
                } else {
                    Toast.makeText(activity, "Could not retrieve file name", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        val documentFile = DocumentFile.fromSingleUri(activity, uri)
        return documentFile?.name
    }

    fun launchFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf" // Only PDF files
        }
        filePickerActivityResult.launch(intent)
    }
}