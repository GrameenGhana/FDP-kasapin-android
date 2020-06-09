package org.grameen.fdp.kasapin.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.ListView;

import org.grameen.fdp.kasapin.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.OnScrollListener;

public class ImageUtil {

    private static boolean isEndOfTable = false;

    public static Bitmap base64ToScaledBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(base64Str.getBytes(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 10, bitmap.getHeight() / 10, false);
    }

    public static Bitmap base64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(base64Str.getBytes(), Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        context.getContentResolver().notifyChange(selectedImage, null);
        int MAX_HEIGHT = 640;
        int MAX_WIDTH = 480;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);
        options.inJustDecodeBounds = false;
        // Decode bitmap with inSampleSize set

        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);
        img = rotateImageIfRequired(context, img, selectedImage);

        if (imageStream != null)
            imageStream.close();

        return img;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public static String captureTableScreenshot(TableView tableView, String activityName) {
        tableView.getBackground().setAlpha(0);
        tableView.setVerticalScrollBarEnabled(false);

        ListView tableDataListView = tableView.findViewById(R.id.table_data_view);
        int measuredHeight = tableDataListView.getMeasuredHeight();
        //Scroll tableView to the first item or position if the first item is not visible
        while (tableDataListView.getFirstVisiblePosition() != 0) {
            tableDataListView.scrollListBy(-measuredHeight);
        }

        View headerView = tableView.findViewById(R.id.table_header_view);
        List<Bitmap> bitmaps = new ArrayList<>();

        int allitemsheight = 0;

        File imageFile = FileUtils.createFolder("screenCaptures", activityName + "_view.jpg");

        OnScrollListener scrollListener = new OnScrollListener() {
            @Override
            public void onScroll(ListView tableDataView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isEndOfTable = (firstVisibleItem + visibleItemCount) == totalItemCount;
            }

            @Override
            public void onScrollStateChanged(ListView tableDateView, ScrollState scrollState) {
            }
        };
        tableView.addOnScrollListener(scrollListener);

        try {
            //Add header view first
            headerView.setDrawingCacheEnabled(true);
            bitmaps.add(Bitmap.createBitmap(headerView.getDrawingCache()));
            headerView.setDrawingCacheEnabled(false);

            tableDataListView.setDrawingCacheEnabled(true);
            do {
                bitmaps.add(Bitmap.createBitmap(tableDataListView.getDrawingCache()));
                allitemsheight += measuredHeight;

                tableDataListView.scrollListBy(measuredHeight);
            } while (!isEndOfTable);

            tableDataListView.setDrawingCacheEnabled(false);

            Bitmap bigBitmap = Bitmap.createBitmap(tableDataListView.getMeasuredWidth(), allitemsheight + (headerView.getMeasuredHeight() * 2), Bitmap.Config.ARGB_8888);
            Canvas bitCanvas = new Canvas(bigBitmap);
            bitCanvas.drawColor(Color.WHITE);

            Paint paint = new Paint();
            int iHeight = 0;

            for (int i = 0; i < bitmaps.size(); i++) {
                Bitmap bitmap = bitmaps.get(i);
                bitCanvas.drawBitmap(bitmap, 0, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
                bitmap = null;
            }

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bigBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            bigBitmap.recycle();

            AppLogger.e("ImageUtil", "Width == " + bigBitmap.getWidth() + " Height == " + bigBitmap.getHeight());

            return imageFile.getAbsolutePath();
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        } finally {
            tableView.removeOnScrollListener(scrollListener);
            tableView.getBackground().setAlpha(1);
            tableView.setVerticalScrollBarEnabled(true);

            tableDataListView.smoothScrollToPosition(0);
        }
        return null;
    }


}