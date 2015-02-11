
package com.cixom.ewhiteboard;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.cixom.ewhiteboard.hardware.Hardware;
import com.cixom.ewhiteboard.shape.Circle;
import com.cixom.ewhiteboard.shape.IShape;
import com.cixom.ewhiteboard.shape.Line;
import com.cixom.ewhiteboard.shape.Oval;
import com.cixom.ewhiteboard.shape.Path;
import com.cixom.ewhiteboard.shape.Rectangle;
import com.cixom.ewhiteboard.shape.Square;

public class HandwriterView extends ViewGroup
{
    private final static String TAG = "HandwriterView";

    public static final int MODEL_NORMAL = 1;
    public static final int MODEL_ERASE = 2;

    protected int mModel = MODEL_NORMAL;

    public static final int SHAPE_PATH = 1;
    public static final int SHAPE_LINE = 2;
    public static final int SHAPE_CIRCLE = 3;
    public static final int SHAPE_SQUARE = 4;
    public static final int SHAPE_RECT = 5;
    public static final int SHAPE_OVAL = 6;

    private int mShapeType = SHAPE_PATH;

    public static final float scale = 210f / 297f;
    public static final int CANVAS_WIDTH = MainApplication.SCREEN_WIDTH;
    public static final int CANVAS_HEIGHT = (int) (MainApplication.SCREEN_WIDTH / scale);

    private Context mContext;
    public Hardware mHardware;
    public boolean mOnlyPenInput;

    protected Canvas mCanvas;
    protected Bitmap mBitmap;

    protected float mPenWidth = 1f;
    protected float mEraserWidth = 10f;
    protected int mColor = Color.BLACK;
    private Paint mBitmapPaint;

    private IShape mEraser;
    private IShape mShape;
    private ArrayList<IShape> mUndoStack;
    private ArrayList<IShape> mRedoStack;

    private int mStartX;
    private int mStartY;

    protected boolean mIsCanvasMoving;
    private boolean mIsTouchUp;
    private Handler mHandler;

    public HandwriterView(Context context, Handler handler)
    {
        super(context);
        setWillNotDraw(false);
        mContext = context;
        mHandler = handler;
        mHardware = Hardware.getInstance(mContext);
        mHardware.addPenButtonEvent(this);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mBitmap = MainApplication.getBitmap();
        mCanvas = new Canvas(mBitmap);
        mUndoStack = new ArrayList<IShape>();
        mRedoStack = new ArrayList<IShape>();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            getChildAt(i).layout(l, t, r, b);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // 在View上绘制
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        if (mShape != null && !mIsTouchUp)
        {
            mShape.drawShape(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mOnlyPenInput)
        {
            if (mHardware.hasPenDigitizer() && mHardware.isPenEvent(event))
            {
                return touchEvent(event);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return touchEvent(event);
        }
    }

    private boolean touchEvent(MotionEvent event)
    {
        if ((mHardware.hasPenDigitizer() && mHardware.isPenEvent(event)) || !mOnlyPenInput)
        {
            // 笔的按钮只有在move才能检测到，所以有可能在move时改变形状
            // 所以所有的绘画都在move中去做
            if (mModel == MODEL_ERASE || mHardware.isPenButtonPressed(event))
            {
                mShape = mEraser;
            }

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();

                    // 创建一个橡皮，因为随时有可能在move的时候切换成橡皮
                    mEraser = new Path(this, MODEL_ERASE);

                    // 根据选择创建相应图形
                    switch (mShapeType)
                    {
                        case SHAPE_PATH:
                            mShape = new Path(this, MODEL_NORMAL);
                            break;
                        case SHAPE_LINE:
                            mShape = new Line(this, MODEL_NORMAL);
                            break;
                        case SHAPE_CIRCLE:
                            mShape = new Circle(this, MODEL_NORMAL);
                            break;
                        case SHAPE_SQUARE:
                            mShape = new Square(this, MODEL_NORMAL);
                            break;
                        case SHAPE_RECT:
                            mShape = new Rectangle(this, MODEL_NORMAL);
                            break;
                        case SHAPE_OVAL:
                            mShape = new Oval(this, MODEL_NORMAL);
                            break;
                        default:
                            break;
                    }

                    return true;

                case MotionEvent.ACTION_MOVE:

                    // 进入拖动模式，一旦进入拖动模式只有在touch up才会恢复
                    if (event.getPointerCount() >= 2)
                    {
                        mIsCanvasMoving = true;
                        resetView();
                        return false;
                    }

                    // 虽然只有一个触摸点，但是没恢复绘画模式
                    if (!mIsCanvasMoving)
                    {
                        // 是否结束绘画
                        mIsTouchUp = false;
                        mShape.touchMove(mStartX, mStartY, (int) event.getX(), (int) event.getY());
                    }

                    return true;
                case MotionEvent.ACTION_UP:

                    if (!mIsCanvasMoving)
                    {
                        mIsTouchUp = true;
                        mUndoStack.add(mShape);
                        mRedoStack.clear();
                        mShape.drawShape(mCanvas);
                        mHandler.sendEmptyMessage(0);
                        invalidate();
                    }
                    else
                    {
                        // 恢复绘画模式
                        mIsCanvasMoving = false;
                    }

                    return true;
            }

        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.e(TAG, "onKeyDown");

        if (mHardware.onKeyDown(keyCode, event))
        {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setContentView(int homeActivity) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        Log.e(TAG, "onKeyUp");

        if (mHardware.onKeyUp(keyCode, event))
        {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    public void resetView()
    {
        mIsTouchUp = true;
        invalidate();
    }

    public void clear()
    {
        restoreBitmap();
        invalidate();
        // 新增一个空的操作
        mUndoStack.add(null);
        mRedoStack.clear();
    }

    /**
     * 新增没有背景图片的页面
     */
    public void addNewBitmap()
    {
        mBitmap = MainApplication.addNewPage();
        mCanvas = new Canvas(mBitmap);
        invalidate();
        resetStack();
    }

    /**
     * 新增有背景图片的页面
     *
     * @param backImg
     */
    public void addNewBitmap(Bitmap backImg)
    {
        mBitmap = MainApplication.addNewPage(backImg);
        mCanvas = new Canvas(mBitmap);
        invalidate();
        resetStack();
    }

    public void setCurBitmap(String name)
    {
        if (name.equals(MainApplication.getBitmapName()))
        {
            return;
        }

        resetStack();
        mBitmap = MainApplication.loadBitmap(name);
        mCanvas = new Canvas(mBitmap);
        invalidate();
    }

    public void setPenWidth(float penWidth)
    {
        mPenWidth = penWidth;
    }

    public float getPenWidth()
    {
        return mPenWidth;
    }

    public void setEraserWidth(float penWidth)
    {
        mEraserWidth = penWidth;
    }

    public float getEraserWidth()
    {
        return mEraserWidth;
    }

    public void setPenColor(int color)
    {
        mColor = color;
    }

    public int getPenColor()
    {
        return mColor;
    }

    public void setModel(int model)
    {
        mModel = model;
    }

    public void setShape(int shape)
    {
        mShapeType = shape;
    }

    public int getShap()
    {
        return mShapeType;
    }

    public boolean useForWriting(MotionEvent event)
    {
        return !mOnlyPenInput || mHardware.isPenEvent(event);
    }

    public boolean useForTouch(MotionEvent event)
    {
        return !mOnlyPenInput || (mOnlyPenInput && !mHardware.isPenEvent(event));
    }

    public void undo()
    {
        if (!canUndo())
        {
            return;
        }

        restoreBitmap();
        mRedoStack.add(mUndoStack.remove(mUndoStack.size() - 1));

        for (int i = mUndoStack.size() - 1; i >= 0; i--)
        {
            IShape shape = mUndoStack.get(i);

            if (shape != null)
            {
                shape.drawShape(mCanvas);
            }
            else
            {
                break;
            }
        }

        invalidate();
    }

    public void restoreBitmap()
    {
        Bitmap sourceBitmap = MainApplication.getSourceBitmap();

        // 如果源图片存在就要恢复
        if (sourceBitmap == null)
        {
            mBitmap.eraseColor(Constants.BACK_COLOR);
        }
        else
        {
            Canvas canvas = new Canvas(mBitmap);
            canvas.drawBitmap(sourceBitmap, 0, 0, null);
        }

        mCanvas = new Canvas(mBitmap);
    }

    public void redo()
    {
        if (!canRedo())
        {
            return;
        }

        IShape shape = mRedoStack.remove(mRedoStack.size() - 1);

        if (shape != null)
        {
            shape.drawShape(mCanvas);
        }
        else
        {
            restoreBitmap();
        }

        mUndoStack.add(shape);
        invalidate();
    }

    public boolean canUndo()
    {
        return mUndoStack.size() != 0;
    }

    public boolean canRedo()
    {
        return mRedoStack.size() != 0;
    }

    private void resetStack()
    {
        mUndoStack.clear();
        mRedoStack.clear();
        mHandler.sendEmptyMessage(0);
    }

    public void setOnlyPenInput(boolean b)
    {
        mOnlyPenInput = b;
    }
}
