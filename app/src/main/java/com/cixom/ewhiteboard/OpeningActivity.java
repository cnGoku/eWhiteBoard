package com.cixom.ewhiteboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class OpeningActivity extends Activity {
    private ImageView mImage;
    private TextView mText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_activity);
        mImage = (ImageView) findViewById(R.id.openingImage);

        WindowManager wm = getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        mImage.getLayoutParams().width = (int) (0.3*width);
        mImage.getLayoutParams().height = (int) (0.3*height);

        new Thread(new Runnable(){
            public void run(){
                try{
                    Thread.sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                Intent intent=new Intent();
                intent.setClass(OpeningActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }
}