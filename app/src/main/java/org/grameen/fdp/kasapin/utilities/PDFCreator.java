package org.grameen.fdp.kasapin.utilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.print.PrintHelper;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.model.TableData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.OnScrollListener;

import static org.grameen.fdp.kasapin.utilities.AppConstants.TAG_OTHER_TEXT_VIEW;

public class PDFCreator {
    private final int leftMargin = 10;
    private boolean isPdfCreated = false;

    private final TableView<TableData> tableView;
    private final PdfDocument document = new PdfDocument();
    private final File pdfDocFile;
    Bitmap finalBitmap;
    int scrollDistance;
    String docLabel;
    static int sizeOfItemsPerPage;
    static int totalNoOfItems;

    /**
     * Todo provide actual type for
     *
     * @param _tableView .
     **/
    private PDFCreator(TableView<TableData> _tableView, String _activityName, String label, int sizePerPage) {
        tableView = _tableView;
        sizeOfItemsPerPage = sizePerPage;

        int initialTableSize = _tableView.getDataAdapter().getData().size();
        AppLogger.e("initialTableSize => " + initialTableSize);


        int noEmptyRowsToAdd = initialTableSize % sizeOfItemsPerPage;
        AppLogger.e("noEmptyRowsToAdd => " + noEmptyRowsToAdd);

        if(noEmptyRowsToAdd > 0)
            for(int i = 0; i < sizeOfItemsPerPage - noEmptyRowsToAdd; i++)
                tableView.getDataAdapter().getData().add(new TableData("", null, TAG_OTHER_TEXT_VIEW));

            tableView.getDataAdapter().notifyDataSetChanged();

        docLabel = label;
        totalNoOfItems = tableView.getDataAdapter().getData().size();
        scrollDistance = tableView.getMeasuredHeight();
        pdfDocFile = FileUtils.createFolder("screenCaptures", _activityName + "_document.pdf");
        AppLogger.e("PDFCreator ===> File location will be " + pdfDocFile.getAbsolutePath());

        AppLogger.e("totalNoOfItems => " + totalNoOfItems);
    }


    public static PDFCreator createPdf(TableView<TableData> _tableView, String _activityName, String label, int sizePerPage) {
        PDFCreator pdfCreator = new PDFCreator(_tableView, _activityName, label, sizePerPage);
        pdfCreator.initialize();
        return pdfCreator;
    }

    private void initialize() {
        tableView.getBackground().setAlpha(0);
        tableView.setVerticalScrollBarEnabled(false);

        View headerView = tableView.findViewById(R.id.table_header_view);

        ListView tableDataListView = tableView.findViewById(R.id.table_data_view);
        scrollDistance = tableDataListView.getMeasuredHeight();

        //Scroll tableView to the first item or position if the first item is not visible
        while (tableDataListView.getFirstVisiblePosition() != 0)
            tableDataListView.scrollListBy(-scrollDistance);
        tableDataListView.scrollListBy(-scrollDistance);

        List<Bitmap> bitmaps = new ArrayList<>();

        int allitemsheight = 0;
        try {
            //Add farmer name view first
            Bitmap farmerNameBitmap = textAsBitmap("Farmer: " + docLabel, 30f, Color.BLACK);
            bitmaps.add(farmerNameBitmap);

            //finish farmer name view
            bitmaps.add(getBitmapFromView(headerView));

            //get screenshot chunks from tableView
            for(int i = 0; i < totalNoOfItems/sizeOfItemsPerPage; i++){
                Bitmap scaledBitmap = createScaledBitmap(
                        getBitmapFromView(tableDataListView),  scrollDistance);
                bitmaps.add(scaledBitmap);
                allitemsheight +=  scrollDistance;


                AppLogger.e("Page - " + (i + 1));
                AppLogger.e("Pages left == " + totalNoOfItems/sizeOfItemsPerPage);

                tableDataListView.scrollListBy(scrollDistance);
            }
            //Combine all bitmaps into a single bitmap image
            int totalHeightOfAllBitmaps = allitemsheight + farmerNameBitmap.getHeight() + (headerView.getMeasuredHeight() * 3);

            //Create pdf file from big bitmap in landscape mode specifying a height of a4Height per page
            //return path if created successfully or null
            finalBitmap = combineBitmaps(bitmaps, tableDataListView.getMeasuredWidth(), totalHeightOfAllBitmaps);
            generatePdfFromBitmap(finalBitmap);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            showError();
            e.printStackTrace();
        } finally {
            //tableView.removeOnScrollListener(scrollListener);
            tableView.getBackground().setAlpha(1);
            tableView.setVerticalScrollBarEnabled(true);
            tableDataListView.smoothScrollToPosition(0);
        }
    }


    private Bitmap getBitmapFromView(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }


    private Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setElegantTextHeight(true);

        float baseline = -paint.ascent() + (leftMargin * 2); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height + leftMargin, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }


    public void print() throws Throwable {
        if (!isPdfCreated)
            throw new Throwable("Pdf document has not been created. Did you forget to call PDFCreator.createPdf()?");

        PrintHelper photoPrinter = new PrintHelper(tableView.getContext());
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);

        if(finalBitmap != null)
            photoPrinter.printBitmap(tableView.getContext().getString(R.string.app_name) + " document", finalBitmap);
        else
            showError();
        //photoPrinter.printBitmap(tableView.getContext().getString(R.string.app_name) + " document", Uri.fromFile(pdfDocFile));
    }


    private void showError(){
        CustomToast.makeToast(tableView.getContext(), "An error occurred printing.\nPlease try again.", Toast.LENGTH_LONG).show();

    }
    private Bitmap combineBitmaps(List<Bitmap> bitmaps, int totalWidth, int totalHeight) {
        Bitmap bigBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas bitCanvas = new Canvas(bigBitmap);
        bitCanvas.drawColor(Color.WHITE);

        int iHeight = 0;
        for (int i = 0; i < bitmaps.size(); i++) {
            Bitmap bitmap = bitmaps.get(i);
            bitCanvas.drawBitmap(bitmap, leftMargin, iHeight, null);
            iHeight += bitmap.getHeight();
            bitmap.recycle();
        }
        return bigBitmap;
    }


    private Bitmap createScaledBitmap(Bitmap b, int top) {
        AppLogger.e("Printer  ###", "Bitmap => |  top => " + top);
        if(top == 0) return b;
        else
           return Bitmap.createBitmap(b, 0, b.getHeight() - top, b.getWidth(), scrollDistance);
    }


    private void createPage(Bitmap bitmap, int pageNo, int pageHeight, int beginHeight) {
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth() + leftMargin, pageHeight, pageNo).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int[] pixels = new int[bitmap.getWidth() * pageHeight];

        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, beginHeight, bitmap.getWidth() - leftMargin, pageHeight);

        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), pageHeight, Bitmap.Config.ARGB_8888);
        newBitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), pageHeight);

        canvas.drawBitmap(newBitmap, leftMargin, 0f, null);
        document.finishPage(page);
        newBitmap.recycle();
    }


    private String generatePdfFromBitmap(Bitmap bitmap) {
        int index = 0;
        int beginHeight = 0;
        int totalHeight = bitmap.getHeight();

        int a4Height = 1754;
        while (totalHeight > a4Height) {
            index = index + 1;
            //Creates pages and adds to the pdf document
            createPage(bitmap, index, a4Height, beginHeight);

            totalHeight -= a4Height;
            beginHeight += a4Height;
        }

        //Copy last bits of bitmap
        //Sometimes pixels left is just shite spaces. Ignore if that's the case
        if (totalHeight > a4Height / 6)
            createPage(bitmap, index, totalHeight, beginHeight);

        try {
            document.writeTo(new FileOutputStream(pdfDocFile));
            isPdfCreated = true;
            AppLogger.e("ImageUtil", "Size of pages == " + document.getPages().size());
            return pdfDocFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            document.close();
            //bitmap.recycle();
        }
    }


}
