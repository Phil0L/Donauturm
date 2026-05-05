package com.pl.donauturm.checklist;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfPrinter {

    private final Activity context;
    private File file;

    public PdfPrinter(View view, String filename, Activity context) {
        this.context = context;
        if (!checkPermission()) {
            requestPermission();
            return;
        }
        try {
            // create a new document
            PdfDocument document = new PdfDocument();

            // create a page description
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int)(view.getWidth() * 1f), (int)(view.getHeight() * 1f), 1).create();

            // start a page
            PdfDocument.Page page = document.startPage(pageInfo);

            // draw something on the page
            view.draw(page.getCanvas());

            // finish the page
            document.finishPage(page);

            // write the document content
            file = new File(view.getContext().getExternalCacheDir(), filename);
            document.writeTo(new FileOutputStream(file));

            // close the document
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void open(){
        if (file == null) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission2 = ContextCompat.checkSelfPermission(context.getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(context, new String[]{READ_EXTERNAL_STORAGE}, 200);
        ActivityCompat.requestPermissions(context, new String[]{WRITE_EXTERNAL_STORAGE}, 200);
    }
}
