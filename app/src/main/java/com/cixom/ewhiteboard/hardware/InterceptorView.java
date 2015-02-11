//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.hardware;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Hack for the ThinkPad Tablet to detect the pen button
 */
public class InterceptorView extends View implements View.OnLongClickListener
{
    private final Handler handler = new Handler();

    public InterceptorView(Context context)
    {
        super(context);
        setLongClickable(true);
        setOnLongClickListener(this);
    }

    private boolean afterActionDown = false;
    protected static boolean buttonPressed = false;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // this will only be called for ACTION_DOWN because we return false
        afterActionDown = true;
        handler.postDelayed(timer, 200);
        buttonPressed = false;
        // important to trigger the long click
        super.onTouchEvent(event);
        return false;
    }

    /**
     * Timer to cancel a real long click before it is delivered
     */
    private Runnable timer = new Runnable()
    {
        public void run()
        {
            afterActionDown = false;
            cancelLongPress();
        };
    };

    /**
     * The ThinkPad Tablet will call this immediately after ACTION_DOWN if the
     * button is pressed
     */
    public boolean onLongClick(View v)
    {
        handler.removeCallbacks(timer);

        if (!afterActionDown)
        {
            return false;
        }

        buttonPressed = true;
        return true;
    }

}
