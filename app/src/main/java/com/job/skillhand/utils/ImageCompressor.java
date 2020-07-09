package com.job.skillhand.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by mmislam3 on 16/08/2016.
 */
public class ImageCompressor {

    String picOneFilename="";
    String inboundPicFileName = " ";

    String outboundPicFileName = " ";
    String picInboundFName="";
    String picOutboundFName = "";
    String picTwoFname = "";

    public File compressPicOutboundTempImage(String imageUri, String imgName, Context context) {

        File file = null;
        String filePath = getRealPathFromURI(imageUri, context);


        File mfile = new File(filePath);
        long length = mfile.length() / 1024;
//        if (length<=700){
//            picOneFilename=lowQualityImageCompress(filePath, imgName,context);
//        }else {
            Bitmap scaledBitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;


            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);
        /*int width = ((size.x) * 60) / 100;
        int height = ((size.y) * 70) / 100;*/


//            float maxHeight = 1920;
//            float maxWidth = 1080;
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;

            float imgRatio = 0;
            float maxRatio = 0;

            try {
                imgRatio = actualWidth / actualHeight;
                maxRatio = maxWidth / maxHeight;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            //options.inTempStorage = new byte[20 * 1024];
            options.inTempStorage = new byte[16 * 1024];
            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }


            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;

             file = getPicOutBoundFilePath();

            try {
                out = new FileOutputStream(file);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
                outboundPicFileName = file.getAbsolutePath();
                out.flush();
                out.close();
                if (canvas != null) {
                    canvas.setBitmap(null);
                    canvas = null;
                }

                if (scaledBitmap != null && !scaledBitmap.isRecycled()) {
                    scaledBitmap.recycle();
                    scaledBitmap = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


//        }
        return file;

    }

    public File compressPicInboundTempImage(String imageUri, String imgName, Context context) {

        File file = null;
        String filePath = getRealPathFromURI(imageUri, context);


        File mfile = new File(filePath);
        long length = mfile.length() / 1024;
//        if (length<=700){
//            picOneFilename=lowQualityImageCompress(filePath, imgName,context);
//        }else {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        /*int width = ((size.x) * 60) / 100;
        int height = ((size.y) * 70) / 100;*/


//            float maxHeight = 1920;
//            float maxWidth = 1080;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;

        float imgRatio = 0;
        float maxRatio = 0;

        try {
            imgRatio = actualWidth / actualHeight;
            maxRatio = maxWidth / maxHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        //options.inTempStorage = new byte[20 * 1024];
        options.inTempStorage = new byte[16 * 1024];
        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
            bmp = null;
        }


        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;

        file = getPicInBoundFilePath();

        try {
            out = new FileOutputStream(file);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
            inboundPicFileName = file.getAbsolutePath();
            out.flush();
            out.close();
            if (canvas != null) {
                canvas.setBitmap(null);
                canvas = null;
            }

            if (scaledBitmap != null && !scaledBitmap.isRecycled()) {
                scaledBitmap.recycle();
                scaledBitmap = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


//        }
        return file;

    }

    public File getPicOutBoundFilePath() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/out_bound");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 500000000;
        n = generator.nextInt(n);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
        //String newDateTime = dateTime.replaceAll(" ", "_");

        picOutboundFName = dateTime + "-" + n +".jpg";
        File file = new File(myDir, picOutboundFName);
        if (file.exists ()) file.delete ();


        return file;
    }
    public File getPicInBoundFilePath() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/in_bound");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 500000000;
        n = generator.nextInt(n);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
        //String newDateTime = dateTime.replaceAll(" ", "_");

        picInboundFName = dateTime + "-" + n +".jpg";
        File file = new File(myDir, picInboundFName);
        if (file.exists ()) file.delete ();


        return file;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private String getRealPathFromURI(String contentURI, Context context) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }


    public static Bitmap decodeSampledBitmapFromByteArray(String byteString, int reqWidth, int reqHeight) {
        byte[] decodedBytes = Base64.decode(byteString, 0);


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);


        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

    }


    public String getInboundPicFileName() {
        return inboundPicFileName;
    }

    public void setInboundPicFileName(String inboundPicFileName) {
        this.inboundPicFileName = inboundPicFileName;
    }


    public String getOutboundPicFileName() {
        return outboundPicFileName;
    }

    public void setOutboundPicFileName(String outboundPicFileName) {
        this.outboundPicFileName = outboundPicFileName;
    }

}
