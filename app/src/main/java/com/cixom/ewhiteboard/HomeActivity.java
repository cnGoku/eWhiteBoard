package com.cixom.ewhiteboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeActivity extends Activity {
    //the big frame
    private RelativeLayout mPersonalInfo;

    private ImageView mPersonalPic;
    private TextView mPersonalName;

    //the small frame
    private RelativeLayout mFrame;
    private TextView mTime1;
    private TextView mTime2;
    private TextView mCredit1;
    private TextView mCredit2;

    //additional
    private Button mPersonalButton;
    private Button mTeacherButton;
    private Button mTestSocketButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mPersonalPic = (ImageView) findViewById(R.id.personal_pic);
        mPersonalName = (TextView) findViewById(R.id.personal_name);
        mPersonalButton = (Button) findViewById(R.id.personal_button);

        mPersonalInfo = (RelativeLayout) findViewById(R.id.personal_info);
        mFrame = (RelativeLayout) findViewById(R.id.personal_frame);
        mTime1 = (TextView) findViewById(R.id.personal_time1);
        mTime2 = (TextView) findViewById(R.id.personal_time2);
        mCredit1 = (TextView) findViewById(R.id.personal_credit1);
        mCredit2 = (TextView) findViewById(R.id.personal_credit2);

        //新增的按钮

        mTeacherButton = (Button) findViewById(R.id.teacher_button);
        mTestSocketButton = (Button) findViewById(R.id.socket_button);

        WindowManager wm = getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        mPersonalInfo.getLayoutParams().width = (int) (0.4*width);
        mPersonalPic.getLayoutParams().width = (int) (0.2*width);
        mPersonalName.getLayoutParams().width = (int) (0.2*width);
        mFrame.getLayoutParams().width = (int) (0.38*width);
        mTime1.getLayoutParams().width = (int) (0.14*width);
        mTime2.getLayoutParams().width = (int) (0.20*width);
        mCredit1.getLayoutParams().width = (int) (0.14*width);
        mCredit2.getLayoutParams().width = (int) (0.20*width);

        mPersonalPic.getLayoutParams().height = (int) (0.2*width);
        mPersonalButton.setOnClickListener(mPersonalButtonListener);



        mTeacherButton.setOnClickListener(mTeacherButtonListener);
        mTestSocketButton.setOnClickListener(mTestSocketButtonListener);
        //this is test
        Intent intent = getIntent();
        String temp = intent.getStringExtra("uName");
        mPersonalName.setText(temp);
    }

    private OnClickListener mPersonalButtonListener = new View.OnClickListener() {
        public void onClick(View v){

            Intent intent = getIntent();
            String STeacherName = intent.getStringExtra("uName");
            Log.e("STeacherName2",STeacherName);
            intent.setClass(HomeActivity.this,EWhiteBoardActivity.class);
            startActivity(intent);
            //finish();//这里可以确定是否可以退栈到这个界面
        }
    };

    private OnClickListener mTeacherButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = getIntent();
            intent.setClass(HomeActivity.this,TeacherInfo.class);
            startActivity(intent);
            //finish();//这里可以确定是否可以退栈到这个界面
        }
    };

    private  OnClickListener mTestSocketButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = getIntent();
            intent.setClass(HomeActivity.this,SocketTest.class);
            startActivity(intent);
        }
    };
}