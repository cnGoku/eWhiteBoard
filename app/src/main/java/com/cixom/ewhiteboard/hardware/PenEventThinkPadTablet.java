//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.hardware;

import android.view.MotionEvent;
import android.view.ViewGroup;

public class PenEventThinkPadTablet extends PenEvent
{

    @Override
    public boolean isPenEvent(MotionEvent event)
    {
        return event.getTouchMajor() == 0.0f;
    }

    @Override
    public boolean isPenButtonPressed(MotionEvent event)
    {
        return InterceptorView.buttonPressed;
    }

    public void addPenButtonEvent(ViewGroup viewGroup)
    {
        InterceptorView v = new InterceptorView(viewGroup.getContext());
        viewGroup.addView(v);
    }

}
