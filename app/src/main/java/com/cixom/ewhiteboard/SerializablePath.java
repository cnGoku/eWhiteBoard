//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard;

import android.graphics.Path;

import java.util.ArrayList;

import java.io.Serializable;

public class SerializablePath extends Path implements Serializable
{
    private static final long serialVersionUID = 1L;
    private ArrayList<float[]> pathPoints;

    public SerializablePath()
    {
        super();
        pathPoints = new ArrayList<float[]>();
    }

    public SerializablePath(SerializablePath path)
    {
        super(path);
        pathPoints = path.pathPoints;
    }

    public void addPathPoints(float[] points)
    {
        pathPoints.add(points);
    }

    public void loadPathPointsAsQuadTo()
    {
        float[] initPoints = pathPoints.remove(0);
        moveTo(initPoints[0], initPoints[1]);

        for (float[] pointSet : pathPoints)
        {
            quadTo(pointSet[0], pointSet[1], pointSet[2], pointSet[3]);
        }
    }
}