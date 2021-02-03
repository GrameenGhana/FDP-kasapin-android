package org.grameen.fdp.kasapin.utilities;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.widget.Toast;

import org.grameen.fdp.kasapin.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FDPPrintManager {
    PrintManager printManager;
    String jobName;
    String filePath;
    Context context;
    String fileNameToSave;


    public FDPPrintManager(Context context, String _filePath, String _fileNameToSave) {
        printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        jobName = context.getString(R.string.app_name) + " Document";
        this.filePath = _filePath;
        this.context = context;
        this.fileNameToSave = _fileNameToSave;

        AppLogger.e("FDPPrintManager", _filePath);
    }


    void print() {
        printManager.print(jobName, pda, null);
    }

    PrintDocumentAdapter pda = new PrintDocumentAdapter() {

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            InputStream input = null;
            OutputStream output = null;

            try {
                input = new FileInputStream(filePath);
                output = new FileOutputStream(destination.getFileDescriptor());

                byte[] buf = new byte[1024];
                int bytesRead;

                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

            } catch (Exception e) {
                //Catch exception
                e.fillInStackTrace();
                CustomToast.makeToast(context, "An error occurred printing.\nPlease try again.", Toast.LENGTH_LONG).show();

            } finally {
                try {
                    if(input != null)
                    input.close();
                    if(output != null)
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    CustomToast.makeToast(context, "An error occurred printing.\nPlease try again.", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }


            PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(fileNameToSave).setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
            callback.onLayoutFinished(pdi, true);
        }
    };
}
