package com.sathwikd.pdftoimage;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class PdfUtilsSat {

    public static String savePDFasImage(ParcelFileDescriptor pfd, String pdfName, int qualityOfImage, Bitmap.CompressFormat formatOfImage){
        try {
            if (pfd != null){
                PdfRenderer renderer = new PdfRenderer(pfd);
                if (renderer.getPageCount()>50){
                    return "Maximum pages allowed- 50";
                }

                // Bitmap and Canvas creation
                Bitmap bitmap;
                Canvas canvas;
                PdfRenderer.Page page;

                // Create a directory for the images
                File picturesDir;
                picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (!picturesDir.exists()) {
                    if (!picturesDir.mkdir()){
                        Log.e(TAG,"Failed to create Pictures directory");
                        return "Failed to create Pictures directory";
                    }
                }

                File pdf2ImgDir = new File(picturesDir, "PDFtoIMG");
                if (!pdf2ImgDir.exists()) {
                    if (!pdf2ImgDir.mkdir()){
                        Log.e(TAG,"Failed to create sub-directory");
                        return "Failed to create sub-directory";
                    }
                }

                // Bitmap
                for (int i = 0; i < renderer.getPageCount(); i++) {
                    // Create a new bitmap and link it to a canvas for drawing
                    page = renderer.openPage(i);
                    bitmap = Bitmap.createBitmap(page.getWidth() * qualityOfImage, page.getHeight() * qualityOfImage, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(bitmap);

                    // Draw the page onto the canvas
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    Rect rect = new Rect(0, 0, page.getWidth() * qualityOfImage, page.getHeight() * qualityOfImage);
                    page.render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    page.close();

                    String extension;
                    if (formatOfImage == Bitmap.CompressFormat.JPEG){
                        extension = ".jpg";
                    } else {
                        extension = ".png";
                    }

                    // Save the bitmap to a file
                    File imageFile = new File(pdf2ImgDir, pdfName + "(" + (i+1) + ")" + extension);
                    FileOutputStream fos = new FileOutputStream(imageFile);
                    if (qualityOfImage >= 2) {
                        bitmap.compress(formatOfImage, 80, fos);
                    } else {
                        bitmap.compress(formatOfImage, 100, fos);
                    }

                    fos.close();

                    // Clean up resources
                    bitmap.recycle();
                }

                renderer.close();
                pfd.close();
            }
            return "PDF Conversion Completed";
        } catch (Exception e) {
            Log.e(TAG, "Error converting PDF to images", e);
            return "Error converting PDF to images";
        }
    }
}
