package com.cixom.ewhiteboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CameraActivity extends Activity{

    private ImageView mPhoto;
    private Button mTakePicture;
    private Button mGoBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        mPhoto = (ImageView) findViewById(R.id.photo);
        mTakePicture = (Button) findViewById(R.id.take_picture);
        mGoBack = (Button) findViewById(R.id.go_back);

        mTakePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "workupload.jpg"));
                startActivityForResult(intent,Activity.DEFAULT_KEYS_DIALER);
            }
        });

        mGoBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(CameraActivity.this,EWhiteBoardActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult (int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            if (data!= null) {
                Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
                System.out.println("fdf================="+data.getDataString());
                mPhoto.setImageBitmap(cameraBitmap);
                System.out.println("成功======"+cameraBitmap.getWidth()+cameraBitmap.getHeight());
            }
        }
    }
}
