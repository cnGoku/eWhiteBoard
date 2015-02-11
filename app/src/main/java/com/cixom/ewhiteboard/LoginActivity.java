package com.cixom.ewhiteboard;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.client.HttpClient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText mUsername;
    private EditText mPassword;
    private TextView mForgetPassword;
    private Button mLogin;
    private Button mRegister;
    private ImageView mLogo;
    private TextView mText1;
    private TextView mText2;
    private String baseURL = "http://192.168.1.100/jddr/login_go_app.php";
    private String netinfo=null;
    private boolean fetchfinish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mUsername = (EditText) findViewById(R.id.loginUsername);
        mPassword = (EditText) findViewById(R.id.loginPassword);
        mForgetPassword = (TextView) findViewById(R.id.forgetPassword);
        mLogin = (Button) findViewById(R.id.loginButton);
        mRegister = (Button) findViewById(R.id.registerButton);
        mLogo = (ImageView) findViewById(R.id.login_logo);
        mText1 = (TextView) findViewById(R.id.login_slogan1);
        mText2 = (TextView) findViewById(R.id.login_slogan2);

        WindowManager wm = getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        mLogin.getBackground().setAlpha(100);
        mRegister.getBackground().setAlpha(100);
        mUsername.getBackground().setAlpha(150);
        mPassword.getBackground().setAlpha(150);

        mLogin.getLayoutParams().width = (int) (0.12*width);
        mRegister.getLayoutParams().width = (int) (0.12*width);
        mUsername.getLayoutParams().width = (int) (0.3*width);
        mPassword.getLayoutParams().width = (int) (0.3*width);

        mLogin.getLayoutParams().height = (int) (0.07*height);
        mRegister.getLayoutParams().height = (int) (0.07*height);
        mUsername.getLayoutParams().height = (int) (0.07*height);
        mPassword.getLayoutParams().height = (int) (0.07*height);

        mLogo.getLayoutParams().width = (int) (0.3*height);
        mLogo.getLayoutParams().height = (int) (0.3*height);

        mLogin.setOnClickListener(mLoginListener);
        mRegister.setOnClickListener(mRegisterListener);
        mForgetPassword.setOnClickListener(mForgetPasswordListener);
    }

    private OnClickListener mLoginListener = new View.OnClickListener() {
        public void onClick(View v){
//          String name = "chenly12";
//          String key = "chenly19940617";

			String name = mUsername.getText().toString();
	        String key = mPassword.getText().toString();

            NameValuePair pair1 = new BasicNameValuePair("UserName", name);
            NameValuePair pair2 = new BasicNameValuePair("Password", key);

            List<NameValuePair> pairList = new ArrayList<NameValuePair>();
            pairList.add(pair1);
            pairList.add(pair2);

            String info;
            info = FetchString(baseURL,pairList);

            if(info == null) {
                AlertDialog.Builder build = new AlertDialog.Builder(LoginActivity.this);
                build.setTitle("AnsURight").setMessage("NET CONECTION ERR")
                        .setPositiveButton("SURE", new DialogInterface.OnClickListener() {
                    //@Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //finish();
                    }
                })
                        .show();
            }
            else {
                if(info.equals("LoginSuccess")) {
                    Intent intent = new Intent();
                    intent.putExtra("uName", mUsername.getText().toString());
                    Log.e("STeacherName1",mUsername.getText().toString());
                    intent.setClass(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                    //finish();
                }
                else if(info.equals("PasswordError")) {
                    AlertDialog.Builder build = new AlertDialog.Builder(LoginActivity.this);
                    build.setTitle("AnsURight")
                            .setMessage("PasswordError")
                            .setPositiveButton("SURE", new DialogInterface.OnClickListener() {
                                //@Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    //finish();
                                }
                            })
                            .show();
                }
                else if(info.equals("UserLock")) {
                    AlertDialog.Builder build=new AlertDialog.Builder(LoginActivity.this);
                    build.setTitle("AnsURight")
                            .setMessage("User is Locked, Try 5 min later")
                            .setPositiveButton("SURE", new DialogInterface.OnClickListener() {
                                //@Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    //finish();
                                }
                            })
                            .show();
                }
                else if(info.equals("UserAct")) {
                    AlertDialog.Builder build=new AlertDialog.Builder(LoginActivity.this);
                    build.setTitle("AnsURight")
                            .setMessage("User is not act")
                            .setPositiveButton("SURE", new DialogInterface.OnClickListener() {
                                //@Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    //finish();
                                }
                            })
                            .show();
                }
                else if(info.equals("NoUser")) {
                    AlertDialog.Builder build=new AlertDialog.Builder(LoginActivity.this);
                    build.setTitle("AnsURight")
                            .setMessage("No this User,regist ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                //@Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    Intent intent = new Intent();
                                    intent.setClass(LoginActivity.this,HomeActivity.class);
                                    startActivity(intent);
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
                }
            }
        }
    };

    private String FetchString(final String url,final List<NameValuePair> pairList) {

        String rst = null;
        AsyncTask<String,Void,String> subtask = new AsyncTask<String,Void,String>() {
            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
                try {
                    HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList);
                    Log.d("result","in1");
                    // URL使用基本URL即可，其中不需要加参数
                    HttpPost httpPost = new HttpPost(url);

                    Log.d("result","in2");
                    // 将请求体内容加入请求中
                    httpPost.setEntity(requestHttpEntity);

                    Log.d("result","in3");
                    // 需要客户端对象来发送请求
                    HttpClient httpClient = new DefaultHttpClient();

                    Log.d("result","in4");
                    // 发送请求
                    HttpResponse response = httpClient.execute(httpPost);
                    Log.d("result","in5");
                    // 显示响应

                    HttpEntity httpEntity = response.getEntity();
                    InputStream is = httpEntity.getContent();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    StringBuilder builder = new StringBuilder();
                    while((line = br.readLine()) != null) {
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
                Log.e("do", result);
                fetchfinish = true;
                netinfo = result;
                super.onPostExecute(result);
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                Toast.makeText(LoginActivity.this, "begin read", Toast.LENGTH_LONG).show();
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

    private OnClickListener mRegisterListener = new View.OnClickListener() {
        public void onClick(View v){

        }
    };

    private OnClickListener mForgetPasswordListener = new View.OnClickListener() {
        public void onClick(View v){

        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder build = new AlertDialog.Builder(this);
                build.setTitle("AnsURight")
                        .setMessage("Are you sure to quit?")
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