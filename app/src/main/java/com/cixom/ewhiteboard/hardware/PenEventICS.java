//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.hardware;

import android.view.MotionEvent;

public class PenEventICS extends PenEvent
{
    final static int SOURCE_STYLUS = 0x00004002;

    @Override
    public boolean isPenEvent(MotionEvent event)
    {
        // == InputDevice.SOURCE_STYLUS in ICS
        return (event.getSource() & SOURCE_STYLUS) == SOURCE_STYLUS;
    }

}
