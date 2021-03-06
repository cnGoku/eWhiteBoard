//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.hardware;

import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Some devices indicate pen touches via Left Alt. Here is some support for this
 * horrible crutch.
 */
public class PenEventLeftAlt extends PenEvent
{
    private final static int MAGIC_KEYCODE = KeyEvent.KEYCODE_ALT_LEFT;
    private boolean magicKeyIsPressed = false;

    @Override
    public boolean isPenEvent(MotionEvent event)
    {
        return magicKeyIsPressed || (event.getDeviceId() >> 27) == 1;
    }

    @Override
    public boolean isPenButtonPressed(MotionEvent event)
    {
        return magicKeyIsPressed;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == MAGIC_KEYCODE)
        {
            magicKeyIsPressed = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        if (keyCode == MAGIC_KEYCODE)
        {
            magicKeyIsPressed = false;
        }
        return super.onKeyUp(keyCode, event);
    }

}
