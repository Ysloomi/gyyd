package com.beessoft.dyyd.mymeans;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beessoft.dyyd.DetailActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.update.UpdateManager;
import com.beessoft.dyyd.utils.PreferenceUtil;

public class MyMeansFragment extends Fragment {
	private TextView textView1, textView2, textView3, textView4, textView5, textView6;
	private UpdateManager mUpdateManager;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context= getActivity();
		View view = inflater.inflate(R.layout.mymeans, container, false);
		initview(view);
		return view;
	}
	
	private void initview(View view) {

		textView1 = (TextView) view.findViewById(R.id.unit_text);
		textView2 = (TextView) view.findViewById(R.id.department_text);
		textView3 = (TextView) view.findViewById(R.id.name_text);
		textView4 = (TextView) view.findViewById(R.id.tel_text);
		textView5 = (TextView) view.findViewById(R.id.mac_text);
//		textView6 = (TextView) getActivity().findViewById(R.id.online_text);

		view.findViewById(R.id.update).setOnClickListener(onClickListener);
		view.findViewById(R.id.changepassword).setOnClickListener(onClickListener);
		view.findViewById(R.id.detail_button).setOnClickListener(onClickListener);
		view.findViewById(R.id.advise_button).setOnClickListener(onClickListener);
		
		textView1.setText(PreferenceUtil.readString(context, "dw"));
		textView2.setText(PreferenceUtil.readString(context, "cdepname"));
		textView3.setText(PreferenceUtil.readString(context, "name"));
		textView4.setText(PreferenceUtil.readString(context, "tel"));
		textView5.setText(PreferenceUtil.readString(context, "sim"));
	}

	
	OnClickListener onClickListener =new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.update:
				mUpdateManager = new UpdateManager(getActivity());
				mUpdateManager.checkUpdate(true);
				break;
			case R.id.changepassword:
				intent.setClass(getActivity(), ChangePasswordActivity.class);
				startActivity(intent);
				break;
			case R.id.detail_button:
				intent.setClass(getActivity(), DetailActivity.class);
				startActivity(intent);
				break;
			case R.id.advise_button:
				intent.setClass(getActivity(), AdviseTypeActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}		
		}
	};
}