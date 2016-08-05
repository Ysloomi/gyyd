package com.beessoft.dyyd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.beessoft.dyyd.utils.GetInfo;

public class BaseActivity extends Activity {
    public Context context;
    public String mac;
    public String username;
    public String ifSf;

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.detail_actions, menu);
//		return super.onCreateOptionsMenu(menu);
//
//	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);
        ifSf = GetInfo.getIfSf(context) ? "0" : "1";


        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);// 使导航栏出现返回按钮
    }



    private ProgressDialog progressDialog;

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("和外勤加载中..");
            progressDialog.show();
        } else {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
    }

    //隐藏ProgressDialog
    public void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
