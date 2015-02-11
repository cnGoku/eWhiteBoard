package com.cixom.ewhiteboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.net.MalformedURLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TeacherInfo extends Activity
{
    private TextView mTeacherName;
    private ListView allTeacher;
    private String baseURL = "http://192.168.1.100/jddr/user/manage_menu_online.php";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.e("TeacherInfo", "onCreate1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_info);

        mTeacherName = (TextView)findViewById(R.id.teacher_name);
        allTeacher = (ListView) findViewById(R.id.all_teacher);

        WindowManager wm = this.getWindowManager();
        Integer height = wm.getDefaultDisplay().getHeight();
        Integer width = wm.getDefaultDisplay().getWidth();

        mTeacherName.getLayoutParams().width = (int) (0.9*width);
        Intent intent=getIntent();
        String STeacherName = intent.getStringExtra("uName");
        Log.d("STeacherName3",STeacherName.toString());
        Log.d("height",height.toString());
        Log.d("width",width.toString());
        mTeacherName.setText(STeacherName);
        String rst = null;
        rst = FetchString(baseURL);
        List<String> Slist = new ArrayList<String>();
        try {
            JSONObject root = new JSONObject(rst);
            JSONArray Jarray = root.getJSONArray("usersonline");
            for(int i=0;i<Jarray.length();i++){
                JSONObject uname = Jarray.getJSONObject(i);
                String temp = uname.getString("UserName");
                Slist.add(temp);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        allTeacher.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,Slist));
    }

    private String FetchString(final String url){
        String rst = null;
        AsyncTask<String,Void,String> subtask = new AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
                try {
                    Log.d("result","in1");

                    HttpGet httpGet = new HttpGet(url);

                    Log.d("result","in2");
                    // 将请求体内容加入请求中

                    Log.d("result","in3");
                    // 需要客户端对象来发送请求
                    HttpClient httpClient = new DefaultHttpClient();

                    Log.d("result","in4");
                    // 发送请求
                    HttpResponse response = httpClient.execute(httpGet);
                    Log.d("result","in5");
                    // 显示响应

                    HttpEntity httpEntity = response.getEntity();
                    InputStream is = httpEntity.getContent();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    StringBuilder builder=new StringBuilder();
                    while((line = br.readLine())!=null){
                        builder.append(line);
                    }
                    br.close();
                    isr.close();
                    is.close();
                    return builder.toString();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    Log.e("e","12");
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("e","12");
                }
                return null;
            }

            @Override
            protected void onCancelled() {
                // TODO Auto-generated method stub
                super.onCancelled();
            }

            @Override
            protected void onCancelled(String result) {
                // TODO Auto-generated method stub
                super.onCancelled(result);
            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                Toast.makeText(TeacherInfo.this, "begin read", Toast.LENGTH_LONG).show();
                Log.e("begin","12");
                super.onPreExecute();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                // TODO Auto-generated method stub
                super.onProgressUpdate(values);
            }

        };
        subtask.execute();
        try {
            rst = subtask.get(6,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rst;
    }


}