

package com.cixom.ewhiteboard;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cixom.ewhiteboard.gallery.PageActivity;

public class EWhiteBoardActivity extends Activity
{
    private static final int REQ_SEL_PAGE = 0;
    private PopupWindow mPopupWindow;
    private LayoutInflater mLayoutInflater;

    private HandwriterView drawView;
    private ImageView mColorBlack;
    private ImageView mColorBlue;
    private ImageView mColorGreen;
    private ImageView mColorRed;
    private ImageView mColorYellow;
    private ImageView mColor;
    private ImageView mEdit;
    private ImageView mEraser;
    private ImageView mSettings;
    private ImageView mPage;
    private ImageView mNewPage;
    private ImageView mShape;
    private ImageView mUndo;
    private ImageView mRedo;
    private Handler mHandler;
    private ImageView mSaveButton;
    private ImageView mClearButton;
    private ImageView mPicture;
    private LinearLayout mToolbar;
    //private TextView mBlank;

    int mY;
    int mX;
    int mPopWidth;

    private View boardFrame;
    private int inColor;
    private float inPenWidth;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.e("EWhiteBoardActivity", "onCreate");
        setContentView(R.layout.activity_ewhiteboard);
        mHandler = new Handler()
        {
            public void handleMessage(Message message)
            {
                setUndoState();
            }
        };

        drawView = new HandwriterView(this, mHandler);
        ((ViewGroup) findViewById(R.id.container)).addView(drawView);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        boolean needCrop = intent.getBooleanExtra("needcrop", false);
        inColor = intent.getIntExtra("color", Color.BLACK);
        inPenWidth = intent.getFloatExtra("penwidth", 1f);
        drawView.setPenColor(inColor);
        drawView.setPenWidth(inPenWidth);

        Bitmap backImg = readBackImgFile(path, needCrop);

        boardFrame = findViewById(R.id.board_frame);
        mSettings = (ImageView) findViewById(R.id.settings);
        mColor = (ImageView) findViewById(R.id.color);
        mEdit = (ImageView) findViewById(R.id.edit);
        mEraser = (ImageView) findViewById(R.id.eraser);
        mPage = (ImageView) findViewById(R.id.page);
        mNewPage = (ImageView) findViewById(R.id.new_page);
        mShape = (ImageView) findViewById(R.id.shape);
        mUndo = (ImageView) findViewById(R.id.undo);
        mRedo = (ImageView) findViewById(R.id.redo);
        mClearButton = (ImageView) findViewById(R.id.button1);
        mSaveButton = (ImageView) findViewById(R.id.saveButton);
        mPicture = (ImageView) findViewById(R.id.loadPicture);
        mToolbar = (LinearLayout) findViewById(R.id.tool_bar);
        //mBlank = (TextView) findViewById(R.id.blank);

        WindowManager wm = getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();
        int width = wm.getDefaultDisplay().getWidth();

        mToolbar.getLayoutParams().height = (int) (0.09*height);
        int toolbar_height = mToolbar.getLayoutParams().height;
        mPopWidth = (int) (0.9 * toolbar_height);

        mUndo.getLayoutParams().width = mPopWidth;
        mRedo.getLayoutParams().width = mPopWidth;
        mPage.getLayoutParams().width = mPopWidth;
        mNewPage.getLayoutParams().width = mPopWidth;
        mSettings.getLayoutParams().width = mPopWidth;

        mSaveButton.getLayoutParams().height =(int) (0.9 * toolbar_height);
        mClearButton.getLayoutParams().height =(int) (0.9 * toolbar_height);
        mEdit.getLayoutParams().height =(int) (0.9 * toolbar_height);
        mShape.getLayoutParams().height =(int) (0.9 * toolbar_height);
        mEraser.getLayoutParams().height =(int) (0.9 * toolbar_height);
        mColor.getLayoutParams().height =(int) (0.9 * toolbar_height);
        mPicture.getLayoutParams().height =(int) (0.9 * toolbar_height);

        mSaveButton.getLayoutParams().width =(int) (0.9 * toolbar_height);
        mClearButton.getLayoutParams().width =(int) (0.9 * toolbar_height);
        mEdit.getLayoutParams().width =(int) (0.9 * toolbar_height);
        mShape.getLayoutParams().width =(int) (0.9 * toolbar_height);
        mEraser.getLayoutParams().width =(int) (0.9 * toolbar_height);
        mColor.getLayoutParams().width =(int) (0.9 * toolbar_height);
        mPicture.getLayoutParams().width =(int) (0.9 * toolbar_height);

        //drawView.addNewBitmap(drawable);
        if (backImg != null){
            drawView.addNewBitmap(backImg);
        }

        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopupWindow = new PopupWindow(new View(this));
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mPicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 2);
            }
        });

        initToolbox();
        initColor();
        initPenSize();
        initEraserSize();
        initShape();
    }




    private void initToolbox()
    {
        setIndexText(MainApplication.getBitmapName());
        mPage.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra("selName", MainApplication.getBitmapName());
                intent.setClass(EWhiteBoardActivity.this, PageActivity.class);
                startActivityForResult(intent, REQ_SEL_PAGE);
            }
        });

        mNewPage.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Animation animation = AnimationUtils.loadAnimation(EWhiteBoardActivity.this, R.anim.statusbar_left_out);
                animation.setAnimationListener(new AnimationListener()
                {
                    public void onAnimationStart(Animation animation)
                    {
                        findViewById(R.id.line).setVisibility(View.VISIBLE);
                    }

                    public void onAnimationRepeat(Animation animation)
                    {
                    }

                    public void onAnimationEnd(Animation animation)
                    {
                        drawView.addNewBitmap();
                        findViewById(R.id.line).setVisibility(View.GONE);
                        resetBoardParam();
                    }
                });

                // boardFram.startAnimation(animation);
                drawView.addNewBitmap();
                resetBoardParam();
                setIndexText(MainApplication.getBitmapName());
                Toast.makeText(EWhiteBoardActivity.this, "New page", Toast.LENGTH_SHORT).show();
            }
        });

        mEdit.setSelected(true);
        drawView.setModel(HandwriterView.MODEL_NORMAL);


        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                onPause();
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                drawView.clear();
            }
        });

        setUndoState();
        mUndo.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                drawView.undo();
                setUndoState();
            }
        });

        mRedo.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                drawView.redo();
                setUndoState();
            }
        });

        mSettings.setSelected(false);
        mSettings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setClass(EWhiteBoardActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUndoState()
    {
        mUndo.setImageResource(drawView.canUndo() ? R.drawable.ic_undo : R.drawable.ic_undo_disabled);
        mRedo.setImageResource(drawView.canRedo() ? R.drawable.ic_redo : R.drawable.ic_redo_disabled);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) boardFrame.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                if (event.getPointerCount() >= 2) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    if (mY == 0) mY = y;

                    if (mX == 0) mX = x;

                    int b = (int) (y - mY);

                    if (b >= MainApplication.SCREEN_HEIGHT / 2){
                        layoutParams.topMargin = MainApplication.SCREEN_HEIGHT / 2;
                    }
                    else if (b <= -(HandwriterView.CANVAS_HEIGHT - MainApplication.SCREEN_HEIGHT / 2)){
                        layoutParams.topMargin = -(HandwriterView.CANVAS_HEIGHT - MainApplication.SCREEN_HEIGHT / 2);
                    }
                    else{
                        layoutParams.topMargin = b;
                    }

                    int a = (int) (x - mX);

                    if (a > MainApplication.SCREEN_WIDTH / 2){
                        layoutParams.leftMargin = MainApplication.SCREEN_WIDTH / 2;
                    }
                    else if (a <= -(HandwriterView.CANVAS_WIDTH - MainApplication.SCREEN_WIDTH / 2)){
                        layoutParams.leftMargin = -(HandwriterView.CANVAS_WIDTH - MainApplication.SCREEN_WIDTH / 2);
                    }
                    else{
                        layoutParams.leftMargin = a;
                    }

                    boardFrame.setLayoutParams(layoutParams);
                    return true;
                }
                return false;
            // ACTION_POINTER_UP
            default:
                mX = (int) event.getX() - layoutParams.leftMargin;
                mY = (int) event.getY() - layoutParams.topMargin;
                break;
        }
        return super.onTouchEvent(event);
    }

    public static Bitmap getBitmapFromResource(Context context, int resId) {
        InputStream is = context.getResources().openRawResource(resId);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        return bmpDraw.getBitmap();
    }

    private void initShape() {
        final int POP_WINDOW_WIDTH = mPopWidth;
        final int POP_WINDOW_HEIGHT = WindowManager.LayoutParams.WRAP_CONTENT;
        final View popupView = mLayoutInflater.inflate(R.layout.view_popup_shape, null);
        final View shape1 = popupView.findViewById(R.id.shape1);
        final View shape2 = popupView.findViewById(R.id.shape2);
        final View shape3 = popupView.findViewById(R.id.shape3);
        final View shape4 = popupView.findViewById(R.id.shape4);
        final View shape5 = popupView.findViewById(R.id.shape5);
        final View shape6 = popupView.findViewById(R.id.shape6);

        shape1.getLayoutParams().width = mPopWidth;
        shape2.getLayoutParams().width = mPopWidth;
        shape3.getLayoutParams().width = mPopWidth;
        shape4.getLayoutParams().width = mPopWidth;
        shape5.getLayoutParams().width = mPopWidth;
        shape6.getLayoutParams().width = mPopWidth;

        shape1.getLayoutParams().height = mPopWidth;
        shape2.getLayoutParams().height = mPopWidth;
        shape3.getLayoutParams().height = mPopWidth;
        shape4.getLayoutParams().height = mPopWidth;
        shape5.getLayoutParams().height = mPopWidth;
        shape6.getLayoutParams().height = mPopWidth;

        mShape.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramView)
            {
                mPopupWindow.setContentView(popupView);
                mPopupWindow.setWidth(POP_WINDOW_WIDTH);
                mPopupWindow.setHeight(POP_WINDOW_HEIGHT);
                mPopupWindow.setAnimationStyle(R.style.pop_settings);
                mPopupWindow.showAtLocation(mShape, Gravity.LEFT | Gravity.TOP, mShape.getLeft(), mShape.getBottom());
                int penSize = drawView.getShap();

                if (penSize == HandwriterView.SHAPE_PATH)
                {
                    shape1.setSelected(true);
                    shape2.setSelected(false);
                    shape3.setSelected(false);
                    shape4.setSelected(false);
                    shape5.setSelected(false);
                    shape6.setSelected(false);
                }
                else if (penSize == HandwriterView.SHAPE_LINE)
                {
                    shape2.setSelected(true);
                    shape1.setSelected(false);
                    shape3.setSelected(false);
                    shape4.setSelected(false);
                    shape5.setSelected(false);
                    shape6.setSelected(false);
                }
                else if (penSize == HandwriterView.SHAPE_CIRCLE)
                {
                    shape3.setSelected(true);
                    shape1.setSelected(false);
                    shape2.setSelected(false);
                    shape4.setSelected(false);
                    shape5.setSelected(false);
                    shape6.setSelected(false);
                }
                else if (penSize == HandwriterView.SHAPE_SQUARE)
                {
                    shape4.setSelected(true);
                    shape1.setSelected(false);
                    shape2.setSelected(false);
                    shape3.setSelected(false);
                    shape5.setSelected(false);
                    shape6.setSelected(false);
                }
                else if (penSize == HandwriterView.SHAPE_RECT)
                {
                    shape5.setSelected(true);
                    shape1.setSelected(false);
                    shape2.setSelected(false);
                    shape3.setSelected(false);
                    shape4.setSelected(false);
                    shape6.setSelected(false);
                }
                else if (penSize == HandwriterView.SHAPE_OVAL)
                {
                    shape6.setSelected(true);
                    shape1.setSelected(false);
                    shape2.setSelected(false);
                    shape3.setSelected(false);
                    shape4.setSelected(false);
                    shape5.setSelected(false);
                }
            }
        });

        shape1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                drawView.setShape(HandwriterView.SHAPE_PATH);
                mPopupWindow.dismiss();
                //mShape.setText(R.string.shape1);
            }
        });

        shape2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                drawView.setShape(HandwriterView.SHAPE_LINE);
                mPopupWindow.dismiss();
                //mShape.setText(R.string.shape2);
            }
        });

        shape3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                drawView.setShape(HandwriterView.SHAPE_CIRCLE);
                mPopupWindow.dismiss();
                //mShape.setText(R.string.shape3);
            }
        });

        shape4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                drawView.setShape(HandwriterView.SHAPE_SQUARE);
                mPopupWindow.dismiss();
                //mShape.setText(R.string.shape4);
            }
        });

        shape5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                drawView.setShape(HandwriterView.SHAPE_RECT);
                mPopupWindow.dismiss();
                //mShape.setText(R.string.shape5);
            }
        });

        shape6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                drawView.setShape(HandwriterView.SHAPE_OVAL);
                mPopupWindow.dismiss();
                //mShape.setText(R.string.shape6);
            }
        });
    }

    private void initColor() {
        final int POP_WINDOW_WIDTH =  mPopWidth;
        final int POP_WINDOW_HEIGHT = WindowManager.LayoutParams.WRAP_CONTENT;
        final View popupView = mLayoutInflater.inflate(R.layout.view_color_popup, null);

        mColor.setOnClickListener(new View.OnClickListener(){
            public void onClick(View paramView){
                mPopupWindow.setContentView(popupView);
                mPopupWindow.setWidth(POP_WINDOW_WIDTH);
                mPopupWindow.setHeight(POP_WINDOW_HEIGHT);
                mPopupWindow.setAnimationStyle(R.style.pop_settings);
                mPopupWindow.showAtLocation(mColor, Gravity.LEFT|Gravity.TOP, mColor.getLeft(), mColor.getBottom());
            }
        });

        mColorBlack = (ImageView) popupView.findViewById(R.id.color_black);
        mColorBlue = (ImageView) popupView.findViewById(R.id.color_blue);
        mColorGreen = (ImageView) popupView.findViewById(R.id.color_green);
        mColorRed = (ImageView) popupView.findViewById(R.id.color_red);
        mColorYellow = (ImageView) popupView.findViewById(R.id.color_yellow);

        mColorBlack.getLayoutParams().width = mPopWidth;
        mColorBlue.getLayoutParams().width = mPopWidth;
        mColorGreen.getLayoutParams().width = mPopWidth;
        mColorRed.getLayoutParams().width = mPopWidth;
        mColorYellow.getLayoutParams().width = mPopWidth;

        mColorBlack.getLayoutParams().height = mPopWidth;
        mColorBlue.getLayoutParams().height = mPopWidth;
        mColorGreen.getLayoutParams().height = mPopWidth;
        mColorRed.getLayoutParams().height = mPopWidth;
        mColorYellow.getLayoutParams().height = mPopWidth;

        mColorBlack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mColor.setBackgroundColor(getResources().getColor(R.color.solid_black));
                drawView.setPenColor(Color.BLACK);
                mPopupWindow.dismiss();
            }
        });

        mColorBlue.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mColor.setText(R.string.color_blue);
                mColor.setBackgroundColor(getResources().getColor(R.color.solid_blue));
                drawView.setPenColor(Color.BLUE);
                mPopupWindow.dismiss();
            }
        });

        mColorGreen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mColor.setText(R.string.color_green);
                mColor.setBackgroundColor(getResources().getColor(R.color.solid_green));
                drawView.setPenColor(Color.GREEN);
                mPopupWindow.dismiss();
            }
        });

        mColorRed.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mColor.setText(R.string.color_red);
                mColor.setBackgroundColor(getResources().getColor(R.color.solid_red));
                drawView.setPenColor(Color.RED);
                mPopupWindow.dismiss();
            }
        });

        mColorYellow.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mColor.setText(R.string.color_yellow);
                mColor.setBackgroundColor(getResources().getColor(R.color.solid_yellow));
                drawView.setPenColor(Color.YELLOW);
                mPopupWindow.dismiss();
            }
        });
    }

    private void initPenSize(){
        final int POP_WINDOW_WIDTH = mPopWidth;
        final int POP_WINDOW_HEIGHT = WindowManager.LayoutParams.WRAP_CONTENT;
        final View popupView = mLayoutInflater.inflate(R.layout.view_popup_pen, null);
        final View width1 = popupView.findViewById(R.id.pen_width1);
        final View width2 = popupView.findViewById(R.id.pen_width2);
        final View width3 = popupView.findViewById(R.id.pen_width3);

        width1.getLayoutParams().width = mPopWidth;
        width2.getLayoutParams().width = mPopWidth;
        width3.getLayoutParams().width = mPopWidth;

        width1.getLayoutParams().height = mPopWidth;
        width2.getLayoutParams().height = mPopWidth;
        width3.getLayoutParams().height = mPopWidth;

        mEdit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View paramView){
                mEdit.setSelected(true);
                mEraser.setSelected(false);
                drawView.setModel(HandwriterView.MODEL_NORMAL);

                mPopupWindow.setContentView(popupView);
                mPopupWindow.setWidth(POP_WINDOW_WIDTH);
                mPopupWindow.setHeight(POP_WINDOW_HEIGHT);
                mPopupWindow.setAnimationStyle(R.style.pop_settings);
                mPopupWindow.showAtLocation(mEdit, Gravity.LEFT | Gravity.TOP, mEdit.getLeft(), mEdit.getBottom());
                float penSize = drawView.getPenWidth();

                if (penSize == 1){
                    width1.setSelected(true);
                    width2.setSelected(false);
                    width3.setSelected(false);
                }
                else if (penSize == 3){
                    width2.setSelected(true);
                    width1.setSelected(false);
                    width3.setSelected(false);
                }
                else if (penSize == 6){
                    width3.setSelected(true);
                    width1.setSelected(false);
                    width2.setSelected(false);
                }
            }
        });

        width1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mEdit.setText(R.string.edit1);
                //mEraser.setText(R.string.eraserButton);
                drawView.setPenWidth(1f);
                mPopupWindow.dismiss();
            }
        });

        width2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mEdit.setText(R.string.edit2);
                //mEraser.setText(R.string.eraserButton);
                drawView.setPenWidth(3f);
                mPopupWindow.dismiss();
            }
        });

        width3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mEdit.setText(R.string.edit3);
                //mEraser.setText(R.string.eraserButton);
                drawView.setPenWidth(6f);
                mPopupWindow.dismiss();
            }
        });
    }

    private void initEraserSize(){
        final int POP_WINDOW_WIDTH = mPopWidth;
        final int POP_WINDOW_HEIGHT = WindowManager.LayoutParams.WRAP_CONTENT;
        final View popupView = mLayoutInflater.inflate(R.layout.view_popup_eraser, null);
        final View width1 = popupView.findViewById(R.id.pen_width1);
        final View width2 = popupView.findViewById(R.id.pen_width2);
        final View width3 = popupView.findViewById(R.id.pen_width3);

        width1.getLayoutParams().width = mPopWidth;
        width2.getLayoutParams().width = mPopWidth;
        width3.getLayoutParams().width = mPopWidth;

        width1.getLayoutParams().height = mPopWidth;
        width2.getLayoutParams().height = mPopWidth;
        width3.getLayoutParams().height = mPopWidth;

        mEraser.setOnClickListener(new View.OnClickListener(){
            public void onClick(View paramView){
                mEdit.setSelected(false);
                mEraser.setSelected(true);
                drawView.setModel(HandwriterView.MODEL_ERASE);

                mPopupWindow.setContentView(popupView);
                mPopupWindow.setWidth(POP_WINDOW_WIDTH);
                mPopupWindow.setHeight(POP_WINDOW_HEIGHT);
                mPopupWindow.setAnimationStyle(R.style.pop_settings);
                mPopupWindow.showAtLocation(mEraser, Gravity.LEFT | Gravity.TOP, mEraser.getLeft(), mEraser.getBottom());

                float eraserSize = drawView.getEraserWidth();

                if (eraserSize == 20){
                    width1.setSelected(true);
                    width2.setSelected(false);
                    width3.setSelected(false);
                }
                else if (eraserSize == 40){
                    width2.setSelected(true);
                    width1.setSelected(false);
                    width3.setSelected(false);
                }
                else if (eraserSize == 60){
                    width3.setSelected(true);
                    width1.setSelected(false);
                    width2.setSelected(false);
                }
            }
        });

        width1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mEraser.setText(R.string.eraser1);
                drawView.setEraserWidth(20f);
                mPopupWindow.dismiss();
            }
        });

        width2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mEraser.setText(R.string.eraser2);
                drawView.setEraserWidth(40f);
                mPopupWindow.dismiss();
            }
        });

        width3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //mEraser.setText(R.string.eraser3);
                drawView.setEraserWidth(60f);
                mPopupWindow.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            if(requestCode == 2){
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

                WindowManager wm = getWindowManager();
                int width = wm.getDefaultDisplay().getWidth();
                int height = wm.getDefaultDisplay().getHeight();

                float bitwidth = bitmap.getWidth();
                float bitheight = bitmap.getHeight();

                Matrix matrix = new Matrix();
                float scaleW = ((float) width)/bitwidth;
                float scaleH = ((float) height)/bitheight;
                matrix.postScale(scaleW, scaleH);

                Bitmap newbitmap = Bitmap.createBitmap(bitmap, 0, 0, (int)bitwidth, (int)bitheight, matrix, true);

                if (data!= null) {
                    drawView.addNewBitmap(newbitmap);
                }
            }
            else{
                String name = data.getStringExtra("name");
                drawView.setCurBitmap(name);
                setIndexText(name);
            }
        }
        
        /*
         * else { List<String> files = Utils.listFiles();
         * 
         * // 如果从面浏览界面pause，第二天再启动时files.size()为0 if (files.size() != 0) {
         * String name = files.get(files.size() - 1);
         * drawView.setCurBitmap(name); setIndexText(name); } }
         */

        resetBoardParam();
    }

    private void setIndexText(String name)
    {
        int index = Utils.listFiles().indexOf(name);
        TextView view = ((TextView) findViewById(R.id.index));

        // 当新增页面时，因为使用多线程处理所以找不到当前页
        if (index == -1)
        {
            view.setText("Page " + (Utils.listFiles().size() + 1));
        }
        else
        {
            view.setText("Page " + (index + 1) );
        }
    }

    private void resetBoardParam()
    {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) boardFrame.getLayoutParams();
        layoutParams.leftMargin = 0;
        layoutParams.topMargin = 0;
        boardFrame.setLayoutParams(layoutParams);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        MainApplication.saveBitmap(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String name = pref.getString((new SimpleDateFormat("yyyyMMdd")).format(new Date()), null);

        // 不存说明是新的一天，直接返回内存中的图片，但是改文件名称为当前时间
        if (name == null)
        {
            setIndexText(name);
            MainApplication.setBitmapName((new SimpleDateFormat("HHmmss")).format(new Date()));
        }

        drawView.setOnlyPenInput(pref.getBoolean("only_pen_input", false));
    }

    private Bitmap readBackImgFile(String filePath, boolean needCrop)
    {
        if (filePath == null)
        {
            return null;
        }

        try
        {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                File file = new File(filePath);

                if (file.exists())
                {

                    if (needCrop)
                    {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = false;
                        Bitmap tmp = BitmapFactory.decodeFile(file.getPath(), options);
                        Bitmap backImg = Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth(), tmp.getHeight()
                                - Constants.STATUS_HEIGHT, null, false);
                        tmp.recycle();
                        return backImg;
                    }
                    else
                    {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        return BitmapFactory.decodeFile(file.getPath(), options);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder build=new AlertDialog.Builder(this);
                build.setTitle("AnsURight")
                        .setMessage("Are you sure to quit drawing?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            //@Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                finish();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            //@Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub

                            }
                        })
                        .show();
                break;

            default:
                break;
        }
        return false;
        //return super.onKeyDown(keyCode, event);
    }

}