package com.cixom.ewhiteboard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SocketTest extends Activity {

    EditText ip;
    EditText editText;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_test);

        ip = (EditText) findViewById(R.id.ip);
        editText = (EditText) findViewById(R.id.edit);
        text = (TextView) findViewById(R.id.text);

        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                connect();
            }
        });

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                send();
            }
        });
    }

    //-------------------------------------

    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;

    public void connect() {

        final AsyncTask<Void, String, Void> read = new AsyncTask<Void, String, Void>() {

            @Override
            protected Void doInBackground(Void... arg0) {
                Log.e("q","0");
                try {
                    Log.e("q","1");
                    socket = new Socket(ip.getText().toString(), 12345);

                    Log.e("q","2");
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                    Log.e("q","3");
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    publishProgress("@success");

                } catch (UnknownHostException e1) {
                    Toast.makeText(SocketTest.this, "无法建立链接", Toast.LENGTH_SHORT).show();
                } catch (IOException e1) {
                    Toast.makeText(SocketTest.this, "无法建立链接", Toast.LENGTH_SHORT).show();
                }
                try {
                    String line;
                    while ((line = reader.readLine())!= null) {
                        publishProgress(line);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                if (values[0].equals("@success")) {
                    Toast.makeText(SocketTest.this, "链接成功！", Toast.LENGTH_SHORT).show();
                }
                text.append("other："+values[0]+"\n");
                super.onProgressUpdate(values);
            }
        };
        read.execute();
        new Thread(){
            public void run() {
                try {
                    read.get(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {

                } catch (ExecutionException e) {

                } catch (TimeoutException e) {
                    Log.e("e","timeout");
                    read.cancel(true);
                    while(true){
                        if(read.isCancelled()){
                            Log.e("e","cancel");
                            break;
                        }
                        try {
                            sleep(500);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }//请求超时
            };
        }.start();

    }

    public void send() {
        try {
            text.append("ME："+editText.getText().toString()+"\n");
            writer.write(editText.getText().toString()+"\n");
            writer.flush();
            editText.setText("");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
