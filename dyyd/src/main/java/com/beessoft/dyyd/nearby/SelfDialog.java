package com.beessoft.dyyd.nearby;
  
import android.app.Dialog;  
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/** 
 * 创建自定义的dialog，主要学习其实现原理 
 * Created by Yelo on 2017/2/25.
 */  
public class SelfDialog extends Dialog {
    private TextView tvType;
    private TextView tvOperator;
    private TextView tvName;
    private ImageView igPicture;


    private String picture;
    private String type;
    private String operator;
    private String name;

    private Thread thread;
    private Bitmap bitmap;
    private static Handler handler = new Handler();


    public SelfDialog(Context context, String picture, String type, String name, String operator) {
        super(context);
        this.picture = picture;
        this.type = type;
        this.operator = operator;
        this.name = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.map_click_layout);
        //按空白处不能取消动画  
        setCanceledOnTouchOutside(true);

        initlise();

    }

    private void initlise() {
        tvType = (TextView) findViewById(R.id.type);
        tvOperator = (TextView) findViewById(R.id.operator);
        tvName = (TextView) findViewById(R.id.name);
        igPicture = (ImageView) findViewById(R.id.imageView);

        if (type.equals("gpx")){
            tvType.setText("光配箱");
            tvName.setText("运营商: "+operator);
            tvOperator.setText("名称: "+name);
            setImage();
        }else if (type.equals("gjx")){
            tvType.setText("光配箱");
            tvName.setText("运营商: "+operator);
            tvOperator.setText("名称: "+name);
            setImage();
        }else {
            tvOperator.setText("点击了当前位置");
            tvOperator.setTextSize(20);
            tvType.setVisibility(View.INVISIBLE);
            tvName.setVisibility(View.INVISIBLE);
            igPicture.setVisibility(View.INVISIBLE);
        }


    }
    private void setImage(){

        picture = picture.replaceAll("\\\\", "/");


        final String httpUrl = User.mainurlImage + picture;
        AsyncHttpClient client = new AsyncHttpClient();

        thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(httpUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestProperty("charset","UTF-8");

                    if (200==connection.getResponseCode()){
                        InputStream in = connection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(in);

                        handler.post(new Runnable() {

                            @Override
                            public void run() {//回到了主线程
                                igPicture.setImageBitmap(bitmap);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}