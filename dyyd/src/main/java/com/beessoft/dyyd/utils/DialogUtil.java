package com.beessoft.dyyd.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beessoft.dyyd.R;

public class DialogUtil {

	@SuppressLint({ "InflateParams", "SetJavaScriptEnabled" })
	public void inputExamineDialog(final Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.examine, null);

		 EditText editText1 = (EditText) view
				.findViewById(R.id.examine_text);
	
	

		 final AlertDialog myDialog = new AlertDialog.Builder(
				context).setView(view)
				.setPositiveButton("确认", null).setNegativeButton("取消", null)
				.setCancelable(false).create();

		myDialog.setTitle("请输入检查结果");
		myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {

				Button button = myDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
//						examineResultString = editText1.getText().toString();
//						if ("".equals(examineResultString)) {
							Toast.makeText(context, "请填写检查结果",
									Toast.LENGTH_SHORT).show();
//						} else {
//							myDialog.dismiss();
//						}
					}
				});
				Button button1 = myDialog
						.getButton(AlertDialog.BUTTON_NEGATIVE);
				button1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						myDialog.dismiss();
					}
				});
			}
		});
		myDialog.show();
	}
}
