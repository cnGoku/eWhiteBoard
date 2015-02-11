//////////////////////////////////////////////////////////////////////////////////////////////

//Copyright (c) 2011-2012 南京数模微电子有限公司 （ Cixom Co. Ltd）All Rights Reserved 

//////////////////////////////////////////////////////////////////////////////////////////////

//Author：胡磊

//Revision history：

package com.cixom.ewhiteboard.gallery;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.cixom.ewhiteboard.Constants;
import com.cixom.ewhiteboard.R;
import com.cixom.ewhiteboard.Utils;
import com.cixom.ewhiteboard.gallery.AsyncImageLoader.ImageCallback;

public class ImageAdapter extends BaseAdapter
{
    private Context mContext;
    private List<String> mData;
    private LayoutInflater mLayoutInflater;
    private int mDestWidth;
    private int mDestHeight;
    private Gallery mGallery;
    private AsyncImageLoader asyncImageLoader;

    public ImageAdapter(Context c, Gallery gallery, List<String> data, int destWidth, int destHeight)
    {
        mContext = c;
        mGallery = gallery;
        mData = data;
        mDestWidth = destWidth;
        mDestHeight = destHeight;

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        asyncImageLoader = new AsyncImageLoader();
    }

    public int getCount()
    {
        return mData.size();
    }

    public Object getItem(int position)
    {
        return mData.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        String name = mData.get(position);

        if (convertView == null)
        {
            convertView = mLayoutInflater.inflate(R.layout.item_pages, null);
            convertView.setLayoutParams(new GalleryFlow.LayoutParams(mDestWidth, mDestHeight));

            viewHolder = new ViewHolder();
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.index.setText((position + 1) + "");
        viewHolder.delete.setVisibility(mData.size() == 1 ? View.INVISIBLE : View.VISIBLE);
        setDeleteListener(viewHolder.delete, name);
        viewHolder.image.setTag(name);
        viewHolder.image.setBackgroundColor(Constants.BACK_COLOR);

        Drawable cachedImage = asyncImageLoader.loadDrawable(name, new ImageCallback()
        {
            public void imageLoaded(Drawable imageDrawable, String name)
            {
                ImageView imageView = (ImageView) mGallery.findViewWithTag(name);

                if (imageView != null)
                {
                    imageView.setImageDrawable(imageDrawable);
                }
            }
        });

        if (cachedImage != null)
        {
            viewHolder.image.setImageDrawable(cachedImage);
        }

        return convertView;
    }

    private void setDeleteListener(View view, final String name)
    {
        view.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                new AlertDialog.Builder(mContext).setMessage("确定要删除吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                new Task().execute(name);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                }).create().show();
            }
        });
    }

    class Task extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            Utils.deleteFile(params[0]);
            mData.remove(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            notifyDataSetChanged();
        }
    }

    class ViewHolder
    {
        private ImageView delete;
        private ImageView image;
        private TextView index;
    }
}
