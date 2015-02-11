//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.hardware;

import android.view.InputDevice;
import android.view.MotionEvent;

public class PenEventSamsungNote extends PenEvent
{
    final static int SOURCE_S_PEN = InputDevice.SOURCE_KEYBOARD | InputDevice.SOURCE_CLASS_POINTER;

    public boolean isPenEvent(MotionEvent event)
    {
        return (event.getDevice().getSources() & SOURCE_S_PEN) == SOURCE_S_PEN;
    }

}
