package com.sathwikd.pdftoimage

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.button.MaterialButtonToggleGroup
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var textViewUri: TextView
    private lateinit var textViewFileName: TextView

    private var qualityOfImage = 2
    private var formatOfImage = Bitmap.CompressFormat.PNG

    private lateinit var filePickerHandler: FilePickerHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtilsBasic.loadTheme(this)
        setContentView(R.layout.activity_main)

        requestFilePermission(this)

        progressBar = findViewById(R.id.progressBarIndef)
        progressBar.visibility = View.INVISIBLE

        // Material Button Toggle Image Format
        val materialButtonToggleForm = findViewById<MaterialButtonToggleGroup>(R.id.materialButtonFormat)
        materialButtonToggleForm.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                formatOfImage = if (checkedId == R.id.buttonJPG) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG
            }
        }

        // Material Button Toggle Image Quality
        val materialButtonToggleQual = findViewById<MaterialButtonToggleGroup>(R.id.materialButtonQuality)
        materialButtonToggleQual.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                qualityOfImage = when (checkedId) {
                    R.id.buttonOKqual -> 1
                    R.id.buttonHigh -> 3
                    else -> 2
                }
            }
        }

        textViewUri = findViewById(R.id.textViewPath)
        textViewFileName = findViewById(R.id.textViewName)

        // Important activity which opens file explorer and after selection, does the PDF to Image conversion
        filePickerHandler = FilePickerHandler(
            activity = this,
            textViewUri = textViewUri,
            textViewFileName = textViewFileName,
            onFilePicked = { uri, fileName ->
                handleFileConversion(uri, fileName)
            }
        )
    }

    // File Permissions (For Android 10 and below)
    private fun requestFilePermission(a: Activity) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && checkWriteExternalPermission(a)) {
            ActivityCompat.requestPermissions(a, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }

    // Checks for External Write Storage permission
    private fun checkWriteExternalPermission(a: Activity): Boolean {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val res = a.checkCallingOrSelfPermission(permission)
        return (res != PackageManager.PERMISSION_GRANTED)
    }

    // When Button Select PDF File got pressed
    fun btnSelectFilePress(view: View) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && checkWriteExternalPermission(this)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            } else {
                // User has chosen Don't ask again, redirect to system settings
                Toast.makeText(this, "Write permission not granted. Please enable from settings", Toast.LENGTH_LONG).show()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            return
        }

        filePickerHandler.launchFilePicker()
    }

    private fun handleFileConversion(uri: Uri, fileName: String) {
        try {
            if (!fileName.lowercase().endsWith(".pdf")) {
                Toast.makeText(this, "Please select a PDF file", Toast.LENGTH_LONG).show()
                return
            }

            // Continue to process PDF to Img
            val pfd = contentResolver.openFileDescriptor(uri, "r")
            if (pfd != null) {
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())

                val pdftoimageThread = PDFtoImageThread(handler, pfd, removeFileExtension(fileName), qualityOfImage, formatOfImage, progressBar, this)
                executor.execute(pdftoimageThread)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling file URI", e)
        }
    }

    private fun removeFileExtension(fileName: String): String {
        // Remove extension
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex > 0) fileName.substring(0, dotIndex) else fileName
    }

    fun btnInfoPress(view: View) {
        startActivity(Intent(this@MainActivity, InfoActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about_item -> showAboutMsg()
            R.id.exit_item -> showExitMsg()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAboutMsg() {
        AlertDialog.Builder(this)
            .setTitle("About")
            .setMessage("Developer: sathwik_d\n\nVersion: ${versionName()}")
            .setNeutralButton("Ok") { _, _ -> }
            .show()
    }

    private fun versionName(): String? {
        return try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    private fun showExitMsg() {
        AlertDialog.Builder(this)
            .setTitle("Exit Application")
            .setMessage("Are you sure you want to exit this application?")
            .setPositiveButton("Yes") { _, _ -> finish() }
            .setNegativeButton("No") { _, _ -> }
            .show()
    }
}