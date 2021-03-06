package com.strollimo.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils {
    private final static String TAG = BitmapUtils.class.getSimpleName();

    public static Bitmap saveResizedBitmap(File inFile, int reqWidth, int reqHeight) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inFile.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap myBitmap = BitmapFactory.decodeFile(inFile.getAbsolutePath(), options);
        saveBitmap(inFile, myBitmap);
        return myBitmap;
    }

    public static Bitmap getBitmapFromFile(File inFile, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inFile.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap myBitmap = BitmapFactory.decodeFile(inFile.getAbsolutePath(), options);
        return myBitmap;
    }

    private static void saveBitmap(File file, Bitmap bitmap) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.flush();
        out.close();

    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        int height = 0;
        int width = 0;
        if (options.outHeight > options.outWidth) {
            height = options.outHeight;
            width = options.outWidth;
        } else {
            width = options.outHeight;
            height = options.outWidth;
        }
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static File saveImageToFile(Context context, String filename, Bitmap bitmap) {
        File storageDir = context.getFilesDir();
        File imageFile = null;
        try {
            imageFile = File.createTempFile(
                    filename,
                    "",
                    storageDir
            );
            saveBitmap(imageFile, bitmap);
        } catch (IOException e) {
            Log.e(TAG, "IOException saving file: " + filename, e);
        }
        return imageFile;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
