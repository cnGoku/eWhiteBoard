

package com.cixom.ewhiteboard;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class MainApplication extends Application
{
    public static final float scale = 210f / 297f;
    public static int CANVAS_WIDTH;
    public static int CANVAS_HEIGHT;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private static Bitmap curBitmap;
    // 原始图片，不会被编辑，恢复时用
    private static Bitmap sourceBitmap;
    private static String curName;

    @Override
    public void onCreate()
    {
        Log.e("MainApplication", "onCreate");
        super.onCreate();
        Display display = ((WindowManager) getSystemService("window")).getDefaultDisplay();

        if (display.getWidth() < display.getHeight())
        {
            SCREEN_HEIGHT = display.getWidth();
            SCREEN_WIDTH = display.getHeight();
        }
        else
        {
            SCREEN_WIDTH = display.getWidth();
            SCREEN_HEIGHT = display.getHeight();
        }

        CANVAS_WIDTH = MainApplication.SCREEN_WIDTH;
        CANVAS_HEIGHT = (int) (MainApplication.SCREEN_WIDTH / scale);

        // 如果找不到当天最后一次打开的文件名，就认为是第一次打开应用
        curName = PreferenceManager.getDefaultSharedPreferences(this).getString(
                (new SimpleDateFormat("yyyyMMdd")).format(new Date()), null);

        if (curName == null)
        {
            // 创建新画布
            createPage();
        }
        else
        {
            // 从缓存读取
            sourceBitmap = Utils.readMutableBitmap(curName);

            if (sourceBitmap != null)
            {
                curBitmap = Bitmap.createBitmap(sourceBitmap);
            }
            else
            {
                // 缓存有可能被手动删掉
                createPage();
            }
        }
    }

    private static Bitmap createBitmap()
    {
        Bitmap bitmap = Bitmap.createBitmap(CANVAS_WIDTH, CANVAS_HEIGHT, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Constants.BACK_COLOR);
        return bitmap;
    }

    public static String getBitmapName()
    {
        return curName;
    }

    public static void setBitmapName(String name)
    {
        curName = name;
    }

    public static Bitmap getBitmap()
    {
        return curBitmap;
    }

    public static void createPage()
    {
        curBitmap = createBitmap();
        curName = (new SimpleDateFormat("HHmmss")).format(new Date());
        saveToFile(curName, curBitmap, false);
    }

    public static Bitmap addNewPage(Bitmap backImg)
    {
        if (sourceBitmap != null)
        {
            sourceBitmap.recycle();
            sourceBitmap = null;
        }

        // 保存当前的图片
        Bitmap bitmap = curBitmap;
        String name = curName;
        saveToFile(name, bitmap, true);

        // 新增
        sourceBitmap = createBitmap();
        Canvas canvas = new Canvas(sourceBitmap);
        canvas.drawBitmap(backImg, 0, 0, null);

        curBitmap = Bitmap.createBitmap(sourceBitmap);
        curName = (new SimpleDateFormat("HHmmss")).format(new Date());
        saveToFile(curName, curBitmap, false);
        return curBitmap;
    }

    /**
     * 新增一页
     *
     * @param backImg
     */
    public static Bitmap addNewPage()
    {
        if (sourceBitmap != null)
        {
            sourceBitmap.recycle();
            sourceBitmap = null;
        }

        // 保存当前的图片
        Bitmap bitmap = curBitmap;
        String name = curName;
        saveToFile(name, bitmap, true);

        // 新增
        curBitmap = createBitmap();
        curName = (new SimpleDateFormat("HHmmss")).format(new Date());
        saveToFile(curName, curBitmap, false);
        return curBitmap;
    }

    /**
     * 选择图片进行编辑
     */
    public static Bitmap loadBitmap(String name)
    {
        if (sourceBitmap != null)
        {
            sourceBitmap.recycle();
        }

        sourceBitmap = Utils.readMutableBitmap(name);

        if (sourceBitmap != null)
        {
            curBitmap.recycle();
            curBitmap = Bitmap.createBitmap(sourceBitmap);
            curName = name;
        }

        return curBitmap;
    }

    /**
     * 获取源图片，恢复时用
     *
     * @return
     */
    public static Bitmap getSourceBitmap()
    {
        return sourceBitmap;
    }

    public static void saveBitmap(Context context)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString((new SimpleDateFormat("yyyyMMdd")).format(new Date()), curName).commit();

        // pause时进行保存，有可能恢复进行编辑不进行回收
        saveToFile(curName, curBitmap, false);
    }

    private static void saveToFile(final String name, final Bitmap bitmap, final boolean needRcycle)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                Utils.saveToFile(name, bitmap);

                if (needRcycle)
                {
                    bitmap.recycle();
                }
            }
        }.start();
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        curBitmap.recycle();
        curBitmap = null;
    }

}
