package com.sathwikd.pdftoimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PDFtoImageThread implements Runnable {
    private final Activity activity;
    private final ProgressBar progressBar;
    private final Handler handler;
    private final ParcelFileDescriptor pfd;
    private final String fileName;
    private final int quality;
    private final Bitmap.CompressFormat formatImage;
    //private final Context context;

    public PDFtoImageThread(Handler handler, ParcelFileDescriptor pfd, String fileName, int quality, Bitmap.CompressFormat format, ProgressBar progressBar, Activity activity) {
        this.handler = handler;
        this.pfd = pfd;
        this.fileName = fileName;
        this.quality = quality;
        this.formatImage = format;
        //this.context = context;
        this.progressBar = progressBar;
        this.activity = activity;
    }

    @Override
    public void run() {
        activity.runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        String message = PdfUtilsSat.savePDFasImage(pfd, fileName, quality, formatImage);
        handler.post(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
        activity.runOnUiThread(() -> progressBar.setVisibility(View.INVISIBLE));
    }
}
