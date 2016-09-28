package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.ImageLoader;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NewApi")
public class PhotoQueryActivity extends BaseActivity {

    @BindView(R.id.location_txt)
    TextView locationTxt;
    @BindView(R.id.phototype_txt)
    TextView phototypeTxt;
    @BindView(R.id.photo_image)
    ImageView photoImage;
    @BindView(R.id.context_txt)
    TextView contextTxt;
    @BindView(R.id.date_txt)
    TextView dateTxt;

    private String imgUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoquery);
        ButterKnife.bind(this);

        context = PhotoQueryActivity.this;


        String idTarget = getIntent().getStringExtra("id");

        visitServer(idTarget);
    }

    private void visitServer(String idTarget) {
        String httpUrl = User.mainurl + "sf/imglist3";
        AsyncHttpClient client_request = new AsyncHttpClient();
        RequestParams parameters_userInfo = new RequestParams();

        parameters_userInfo.put("mac", mac);
        parameters_userInfo.put("usercode", username);
        parameters_userInfo.put("sf", ifSf);
        parameters_userInfo.put("id", idTarget);

        client_request.post(httpUrl, parameters_userInfo,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject dataJson = new JSONObject(response);

                            if (dataJson.getString("code").equals("0")) {
                                JSONArray array = dataJson.getJSONArray("list");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(0);
                                    locationTxt.setText(obj.getString("iadd"));
                                    phototypeTxt.setText(obj.getString("imgtype"));
                                    contextTxt.setText(obj.getString("context"));
                                    dateTxt.setText(obj.getString("itime"));
                                    imgUrl =  obj.getString("imgfile");
                                    ImageLoader.load(context,User.mainurl +imgUrl,photoImage);
                                }
                            } else if ("1".equals(dataJson.getString("code"))) {
                                Toast.makeText(PhotoQueryActivity.this, "没有数据",
                                        Toast.LENGTH_SHORT).show();
                            } else if ("-2".equals(dataJson.getString("code"))) {
                                Toast.makeText(PhotoQueryActivity.this, "无权限",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @OnClick(R.id.photo_image)
    public void onClick() {

        ArrayList<String> imgs = new ArrayList<String>();
        imgs.add(imgUrl);

        PhotoImagePagerActivity.navToPhotoImagePager(context,0,imgs);
    }
}
