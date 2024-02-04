package com.sathwikd.pdftoimage;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView textViewUri;
    TextView textViewFileName;

    private int qualityOfImage = 2;
    private Bitmap.CompressFormat formatOfImage = Bitmap.CompressFormat.PNG;

    ActivityResultLauncher<Intent> filePickerActivityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtilsBasic.loadTheme(this);
        setContentView(R.layout.activity_main);

        requestFilePermission(this);

        progressBar = findViewById(R.id.progressBarIndef);
        progressBar.setVisibility(View.INVISIBLE);

        // Material Button Toggle Image Format
        MaterialButtonToggleGroup materialButtonToggleForm = findViewById(R.id.materialButtonFormat);
        materialButtonToggleForm.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.buttonJPG) {
                    formatOfImage = Bitmap.CompressFormat.JPEG;
                }
                else {
                    formatOfImage = Bitmap.CompressFormat.PNG;
                }
            }
        });

        // Material Button Toggle Image Quality
        MaterialButtonToggleGroup materialButtonToggleQual = findViewById(R.id.materialButtonQuality);
        materialButtonToggleQual.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.buttonOKqual){
                    qualityOfImage = 1;
                } else if (checkedId == R.id.buttonHigh){
                    qualityOfImage = 3;
                } else {
                    qualityOfImage = 2;
                }
            }
        });

        textViewUri = findViewById(R.id.textViewPath);
        textViewFileName = findViewById(R.id.textViewName);

        // Important activity which opens file explorer and after selection, does the PDF to Image conversion
        filePickerActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data =result.getData();

                        Uri uri = data != null ? data.getData() : null;
                        if (uri != null){
                            textViewUri.setText(uri.getPath());

                            // Get name of the file
                            String fileName = getFileName(uri);

                            if (fileName != null){
                                textViewFileName.setText(fileName);
                                handleFileConversion(uri,fileName);
                            } else {
                                Toast.makeText(this,"Could not retrieve file name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );
    }

    // File Permissions (For Android 10 and below)
    private void requestFilePermission(Activity a){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && checkWriteExternalPermission(a)){
            ActivityCompat.requestPermissions(a, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    private static boolean checkWriteExternalPermission(Activity a) {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = a.checkCallingOrSelfPermission(permission);
        return (res != PackageManager.PERMISSION_GRANTED);
    }

    // When Button Select PDF File got pressed
    public void btnSelectFilePress(View view){
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && checkWriteExternalPermission(this)){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                // User has chosen Don't ask again, redirect to system settings
                Toast.makeText(this, "Write permission not granted. Please enable from settings", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");  // Only PDF files
        filePickerActivityResult.launch(intent);
    }

    public void btnInfoPress(View view){
        startActivity(new Intent(MainActivity.this, InfoActivity.class));
        finish();
    }

    private String getFileName(Uri uri){
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        return (documentFile != null) ? documentFile.getName() : null;
    }

    private String removeFileExtension(String fileName){
        // Remove extension
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileName = fileName.substring(0, dotIndex);
        }
        return fileName;
    }

    private void handleFileConversion(Uri uri, String fileName){
        try {
            if (!fileName.toLowerCase().endsWith(".pdf")){
                Toast.makeText(this, "Please select a PDF file", Toast.LENGTH_LONG).show();
                return;
            }

            // Continue to process PDF to Img
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null){
                Executor executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());

                PDFtoImageThread pdftoimageThread = new PDFtoImageThread(handler, pfd, removeFileExtension(fileName), qualityOfImage, formatOfImage, progressBar, this);
                executor.execute(pdftoimageThread);
            }
        } catch (Exception e){
            Log.e(TAG, "Error handling file URI", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about_item) {
            showAboutMsg();
        }
        else if (item.getItemId() == R.id.exit_item) {
            showExitMsg();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutMsg() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("About");
        alert.setMessage("Developer: sathwik_d\n\n" + "Version: " + VersionName());
        alert.setNeutralButton("Ok", (dialogInterface, i) -> { }
        );
        alert.create().show();
    }

    private String VersionName() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showExitMsg() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Exit Application");
        alert.setMessage("Are you sure you want to exit this application?");
        alert.setPositiveButton("Yes", (dialogInterface, i) -> finish());
        alert.setNegativeButton("No", (dialogInterface, i) -> { }
        );

        alert.create().show();
    }
}