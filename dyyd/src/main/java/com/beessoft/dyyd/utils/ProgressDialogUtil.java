package com.beessoft.dyyd.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtil {
	
	private static ProgressDialog progressDialog;
//	private static CustomProgressDialog dialog;

	public static void showProgressDialog(Context context, String msg) {
		try {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(context);
				progressDialog.setMessage(msg);
				progressDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showProgressDialog(Context context) {
		try {
			if (progressDialog == null) {
//			dialog =new CustomProgressDialog(context, "智慧什邡努力加载中..",R.anim.progressdialog_anim);
//			dialog.show();
				progressDialog = new ProgressDialog(context);
				progressDialog.setMessage("加载中..");
				progressDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeProgressDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
