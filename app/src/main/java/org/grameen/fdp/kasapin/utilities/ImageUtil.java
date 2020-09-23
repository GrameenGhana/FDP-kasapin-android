package org.grameen.fdp.kasapin.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Base64;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class ImageUtil {
    public static Bitmap base64ToScaledBitmap(String base64Str) throws IllegalArgumentException {
        Bitmap bitmap = base64ToBitmap(base64Str);
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 20, bitmap.getHeight() / 20, false);
    }

    public static Bitmap base64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(base64Str.getBytes(), Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
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
         Bitmap img = rotateImageIfRequired(context, BitmapFactory.decodeStream(imageStream, null, options), selectedImage);

        if (imageStream != null)
            imageStream.close();
        return img;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        if(input == null)
            return img;

        ExifInterface ei = new ExifInterface(input);
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
            inSampleSize = Math.min(heightRatio, widthRatio);
            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;
            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize+=1;
            }
        }
        return inSampleSize;
    }

    public static Bitmap mergeIconsToBitmap(LinkedList<Bitmap> bitmaps) {
        if(bitmaps.size() == 0) return null;

        int iconSize = 80;
        int noOfColumns = 2;
        int sizeOfBitmapsArray = bitmaps.size();

        int totalWidth = (sizeOfBitmapsArray <= noOfColumns) ? (sizeOfBitmapsArray * iconSize) : (noOfColumns * iconSize);
        int totalHeight = iconSize + iconSize * (bitmaps.size() / noOfColumns);
        int top = 0;
        int left = 0;
        Bitmap bigBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas bitCanvas = new Canvas(bigBitmap);

        for (int i = 0; i < sizeOfBitmapsArray; i++) {
            Bitmap bitmap =  Bitmap.createScaledBitmap(bitmaps.get(i), iconSize, iconSize, false);
            bitCanvas.drawBitmap(bitmap, left, top, null);
            bitmap.recycle();

            left = ((i + 1) % noOfColumns == 0) ? 0 : left + iconSize;
            top = ((i + 1) % noOfColumns == 0) ? top + iconSize + 2 : top;
        }
        return bigBitmap;
    }

}