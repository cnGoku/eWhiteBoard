//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.gallery;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.cixom.ewhiteboard.MainApplication;
import com.cixom.ewhiteboard.Utils;

public class AsyncImageLoader
{
    private HashMap<String, SoftReference<Drawable>> imageCache;
    private static final int scale = 2;

    public AsyncImageLoader()
    {
        imageCache = new HashMap<String, SoftReference<Drawable>>();

        Bitmap curBitmap = Bitmap.createScaledBitmap(MainApplication.getBitmap(), MainApplication.CANVAS_WIDTH / 2,
                MainApplication.CANVAS_HEIGHT / 2, false);
        Drawable drawable = new BitmapDrawable(curBitmap);
        imageCache.put(MainApplication.getBitmapName(), new SoftReference<Drawable>(drawable));
    }

    public Drawable loadDrawable(final String name, final ImageCallback imageCallback)
    {
        if (imageCache.containsKey(name))
        {
            SoftReference<Drawable> softReference = imageCache.get(name);
            Drawable drawable = softReference.get();

            if (drawable != null)
            {
                return drawable;
            }
        }

        final Handler handler = new Handler()
        {
            public void handleMessage(Message message)
            {
                imageCallback.imageLoaded((Drawable) message.obj, name);
            }
        };

        new Thread()
        {
            @Override
            public void run()
            {
                // 显示1/2大小图片
                Bitmap bitmap = Utils.readBitmap(name, scale);
                Drawable drawable = new BitmapDrawable(bitmap);
                imageCache.put(name, new SoftReference<Drawable>(drawable));
                Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
            }
        }.start();

        return null;
    }

    public interface ImageCallback
    {
        public void imageLoaded(Drawable imageDrawable, String name);
    }

}
