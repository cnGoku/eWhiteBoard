//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;

public class Utils
{
    public static int dip2px(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static FileInputStream readFile(String filePath)
    {
        try
        {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                File file = new File(filePath);

                if (file.exists())
                {
                    FileInputStream fis = new FileInputStream(file);
                    return fis;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap getBitmapFromResource(Context context, int resId)
    {
        InputStream is = context.getResources().openRawResource(resId);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        return bmpDraw.getBitmap();
    }

    public static Bitmap readMutableBitmap(String name)
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            return null;
        }

        File sdCardDir = Environment.getExternalStorageDirectory();
        File file = new File(sdCardDir.getPath() + File.separator + Constants.FILE_PATH + File.separator
                + (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()) + File.separator + Constants.DIR_NAME
                + File.separator + name + ".png");

        if (!file.exists())
        {
            return null;
        }

        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            return BitmapFactory.decodeFile(file.getPath(), options);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap readBitmap(String name, int scale)
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            return null;
        }

        File sdCardDir = Environment.getExternalStorageDirectory();
        File file = new File(sdCardDir.getPath() + File.separator + Constants.FILE_PATH + File.separator
                + (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()) + File.separator + Constants.DIR_NAME
                + File.separator + name + ".png");

        if (!file.exists())
        {
            return null;
        }

        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            return BitmapFactory.decodeFile(file.getPath(), options);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static List<String> listFiles()
    {
        List<String> list = new ArrayList<String>();

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            return list;
        }

        File sdCardDir = Environment.getExternalStorageDirectory();
        File dir = new File(sdCardDir.getPath() + File.separator + Constants.FILE_PATH + File.separator
                + (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()) + File.separator + Constants.DIR_NAME);
        File[] files = dir.listFiles();

        if (files != null)
        {
            for (int i = files.length - 1; i >= 0; i--)
            {
                String name = files[i].getName();
                list.add(files[i].getName().substring(0, name.length() - 4));
            }
        }

        return list;
    }

    public static void deleteFile(String name)
    {
        // 在保存之前需要判断 SDCard 是否存在,并且是否具有可写权限：
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            // 获取SDCard目录,2.2的时候为:/mnt/sdcart 2.1的时候为：/sdcard
            File sdCardDir = Environment.getExternalStorageDirectory();
            File file = new File(sdCardDir.getPath() + File.separator + Constants.FILE_PATH + File.separator
                    + (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()) + File.separator + Constants.DIR_NAME
                    + File.separator + name + ".png");
            file.delete();
        }
    }

    public static boolean saveToFile(String name, Bitmap bitmap)
    {
        // 在保存之前需要判断 SDCard 是否存在,并且是否具有可写权限：
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            return false;
        }

        // 获取SDCard目录,2.2的时候为:/mnt/sdcart 2.1的时候为：/sdcard
        File sdCardDir = Environment.getExternalStorageDirectory();
        File dir = new File(sdCardDir.getPath() + File.separator + Constants.FILE_PATH + File.separator
                + (new SimpleDateFormat("yyyy-MM-dd")).format(new Date()) + File.separator + Constants.DIR_NAME);

        if (!dir.exists())
        {
            dir.mkdirs();
        }

        try
        {
            File saveFile = new File(dir.getPath(), name + ".png");
            FileOutputStream outStream = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    public static Bitmap createReflectedImage(Bitmap originalImage)
    {
        // The gap we want between the reflection and the original image
        final int reflectionGap = 0;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // Draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }
}
