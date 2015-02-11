//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.gallery;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.cixom.ewhiteboard.HandwriterView;
import com.cixom.ewhiteboard.MainApplication;
import com.cixom.ewhiteboard.R;
import com.cixom.ewhiteboard.Utils;

public class PageActivity extends Activity
{
    private ImageAdapter imgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);
        String selName = getIntent().getStringExtra("selName");

        int height = (int) (MainApplication.SCREEN_HEIGHT * 0.75f);
        int width = (int) (MainApplication.SCREEN_HEIGHT * 0.75f * HandwriterView.scale);
        List<String> files = Utils.listFiles();

        // 获取当前屏幕宽度
        GalleryFlow galleryFlow = (GalleryFlow) findViewById(R.id.gallery);
        imgAdapter = new ImageAdapter(this, galleryFlow, files, width, height);
        galleryFlow.setAdapter(imgAdapter);
        galleryFlow.setSelection(files.indexOf(selName));
        galleryFlow.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent();
                intent.putExtra("name", view.findViewById(R.id.image).getTag().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }
}
