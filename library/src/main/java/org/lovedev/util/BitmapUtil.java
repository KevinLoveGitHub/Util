package org.lovedev.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtil {
    /**
     * 从文件中解图 解大图内存不足时尝试5此, samplesize增大
     *
     * @param max 宽或高的最大值, <= 0 , 能解多大解多大, > 0, 最大max, 内存不足解更小
     */
    public static Bitmap getByteBitmapFromFileLimitSize(byte[] filePath, int max) {
//		if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
//			return null;
//		}
        if (filePath.length <= 0) {
            return null;
        }
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        if (max > 0) {
            options.inJustDecodeBounds = true;
            // 获取这个图片的宽和高
//			bm = BitmapFactory.decodeFile(filePath, options);
            bm = BitmapFactory.decodeByteArray(filePath, 0, filePath.length, options);
            options.inJustDecodeBounds = false;

            float blW = (float) options.outWidth / max;
            float blH = (float) options.outHeight / max;

            if (blW > 1 || blH > 1) {
                if (blW > blH) {
                    options.inSampleSize = (int) (blW + 0.9f);
                } else {
                    options.inSampleSize = (int) (blH + 0.9f);
                }
            }
        }
        int i = 0;
        while (i <= 10) {
            i++;
            try {
                bm = BitmapFactory.decodeByteArray(filePath, 0, filePath.length, options);
                break;
            } catch (OutOfMemoryError e) {
                options.inSampleSize++;
                e.printStackTrace();
            }
        }
        return bm;
    }

    /**
     * 从文件中解图 解大图内存不足时尝试5此, samplesize增大
     *
     * @param width 宽或高的最大值, <= 0 , 能解多大解多大, > 0, 最大max, 内存不足解更小
     */
    public static Bitmap getBitmapFromFileLimitSize(String filePath, int width, int height) {
        if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
            return null;
        }

        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        if (width > 0 && height > 0) {
            options.inJustDecodeBounds = true;
            // 获取这个图片的宽和高
            bm = BitmapFactory.decodeFile(filePath, options);
            options.inJustDecodeBounds = false;

            float blW = (float) options.outWidth / width;
            float blH = (float) options.outHeight / height;

            if (blW > 1 || blH > 1) {
                if (blW > blH) {
                    options.inSampleSize = (int) (blW + 0.9f);
                } else {
                    options.inSampleSize = (int) (blH + 0.9f);
                }
            }
        }
        int i = 0;
        while (i <= 10) {
            i++;
            try {
                bm = BitmapFactory.decodeFile(filePath, options);
                break;
            } catch (OutOfMemoryError e) {
                options.inSampleSize++;
                e.printStackTrace();
            }
        }
        return bm;
    }

    /**
     * 压缩图片
     * @param filePath
     * @param width
     * @param height
     */
    public static void compressImage(String filePath, int width, int height) {
        if (width >= 1000) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            options.outWidth = 1000;
//            options.outHeight = height * 1000 / width;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            saveBmpToFile(bitmap, filePath, CompressFormat.JPEG);
//            bitmap.recycle();
        }
    }


    /**
     * 保存图片到文件
     */
    public static boolean saveBmpToFile(Bitmap bmp, String path, CompressFormat format) {
        if (bmp == null || bmp.isRecycled())
            return false;

        OutputStream stream = null;
        try {
            File file = new File(path);
            File filePath = file.getParentFile();
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            stream = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return bmp.compress(format, 100, stream);
    }

    /**
     * 获取图片旋转角度
     */
    public static int getRotateDegree(Context context, Uri uri) {
        if (uri == null) {
            return 0;
        }
        String file = uri.getPath();
        if (TextUtils.isEmpty(file)) {
            return 0;
        }
        ExifInterface exif;
        try {
            exif = new ExifInterface(file);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        int degree = 0;
        if (orientation != ExifInterface.ORIENTATION_UNDEFINED) {
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } else {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String orientationDb = cursor.getString(cursor.getColumnIndex("orientation"));
                cursor.close();
                if (!TextUtils.isEmpty(orientationDb)) {
                    degree = Integer.parseInt(orientationDb);
                }
            }
        }
        return degree;
    }

    /**
     * 旋转图片
     */
    public static Bitmap rotateBitmap(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);

            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {

            }
        }
        return b;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public static Bitmap byte2bitmap(byte[] bytes, Camera camera) {
        Bitmap bitmap = null;

        Camera.Size size = camera.getParameters().getPreviewSize(); // 获取预览大小
        final int w = size.width; // 宽度
        final int h = size.height;
        final YuvImage image = new YuvImage(bytes, ImageFormat.NV21, w, h,
                null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
        if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
            return null;
        }
        byte[] tmp = os.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);

        Matrix matrix = new Matrix();
        matrix.setRotate(-90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return bitmap;
    }

}
