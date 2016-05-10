package com.beessoft.dyyd.mymeans;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beessoft.dyyd.DetailActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.update.UpdateManager;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.User;

public class MyMeansFragment extends Fragment {
	private TextView unitTxt, departTxt, nameTxt, telTxt, macTxt, onlineTxt;
	private Button updateBtn;
	private UpdateManager mUpdateManager;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context= getActivity();
		View view = inflater.inflate(R.layout.fragment_mymeans, container, false);
		initview(view);
		return view;
	}
	
	private void initview(View view) {

		unitTxt = (TextView) view.findViewById(R.id.unit_text);
		departTxt = (TextView) view.findViewById(R.id.department_text);
		nameTxt = (TextView) view.findViewById(R.id.name_text);
		telTxt = (TextView) view.findViewById(R.id.tel_text);
		macTxt = (TextView) view.findViewById(R.id.mac_text);
		onlineTxt = (TextView) view.findViewById(R.id.online_text);
		updateBtn = (Button) view.findViewById(R.id.btn_update);
		updateBtn.setText("在线升级   v"+ User.getVersionName(context));
		updateBtn.setOnClickListener(onClickListener);

		view.findViewById(R.id.btn_changepassword).setOnClickListener(onClickListener);
		view.findViewById(R.id.btn_advise).setOnClickListener(onClickListener);
		view.findViewById(R.id.txt_detail).setOnClickListener(onClickListener);
		
		unitTxt.setText(PreferenceUtil.readString(context, "dw"));
		departTxt.setText(PreferenceUtil.readString(context, "cdepname"));
		nameTxt.setText(PreferenceUtil.readString(context, "name"));
		telTxt.setText(PreferenceUtil.readString(context, "tel"));
		macTxt.setText(PreferenceUtil.readString(context, "sim"));
	}

	
	OnClickListener onClickListener =new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_update:
				mUpdateManager = new UpdateManager(context);
				mUpdateManager.checkUpdate(true);
				break;
			case R.id.btn_changepassword:
				intent.setClass(context, ChangePasswordActivity.class);
				startActivity(intent);
				break;
			case R.id.txt_detail:
				intent.setClass(context, DetailActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_advise:
				intent.setClass(context, AdviseTypeActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}		
		}
	};
}