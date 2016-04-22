package com.beessoft.dyyd.material;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beessoft.dyyd.R;

public class MaterialFragment extends Fragment {
	private Button button1, button2, button3, button4, button5, button6,button7;
	private Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mymeans = inflater.inflate(R.layout.material, container, false);
		context = getActivity();
//		role = PreferenceUtil.readString(context, "role");
		initView(mymeans);
		return mymeans;
	}

	private void initView(View mymeans) {

		button1 = (Button) getActivity().findViewById(R.id.terminal_button);
		button2 = (Button) getActivity().findViewById(R.id.sales_button);
		button3 = (Button) getActivity().findViewById(R.id.boss_button);
		button4 = (Button) getActivity().findViewById(R.id.workbook_button);
		button5 = (Button) getActivity()
				.findViewById(R.id.companytarget_button);
		button6 = (Button) getActivity().findViewById(R.id.branchtarget_button);
		button7 = (Button) getActivity().findViewById(R.id.targetquery_button);
		
//		button1.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				if (!"4".equals(role)){
//					Intent intent = new Intent();
//					intent.setClass(getActivity(), TerminalActivity.class);
//					startActivity(intent);
//				} else {
//					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//							.show();
//				}
//			}
//		});
//		//店员手册
//		button2.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				if (!"4".equals(role)){
//					Intent intent = new Intent();
//					intent.setClass(getActivity(), SalesWorkBookActivity.class);
//					startActivity(intent);
//				} else {
//					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//							.show();
//				}
//			}
//		});
//		//老板手册
//		button3.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				 if("3".equals(role)||"4".equals(role)){
//						Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//								.show();
//					} else  {
//					Intent intent = new Intent();
//					intent.setClass(getActivity(), BossWorkBookActivity.class);
//					startActivity(intent);
//				}
//			}
//		});
//		//员工手册
//		button4.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				if ("0".equals(role) || "1".equals(role)) {
//					Intent intent = new Intent();
//					intent.setClass(getActivity(), WorkBookActivity.class);
//					startActivity(intent);
//				} else {
//					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//							.show();
//				}
//			}
//		});
//		//公司日报
//		button5.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				if ("0".equals(role) || "1".equals(role)) {
//					Intent intent = new Intent();
//					intent.setClass(getActivity(), CompanyTargetActivity.class);
//					startActivity(intent);
//				} else {
//					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//							.show();
//				}
//			}
//		});
//		//分局日报
//		button6.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				 if("3".equals(role)||"4".equals(role)){
//					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//							.show();
//				}else{
//					Intent intent = new Intent();
//					intent.setClass(getActivity(), BranchTargetActivity.class);
//					startActivity(intent);
//				}
//			}
//		});
//		//目标查询
//		button7.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
////				if ("0".equals(MainActivity.role)
////						|| "1".equals(MainActivity.role)) {
//				if (!"4".equals(role)){
//					Intent intent = new Intent();
//					intent.setClass(getActivity(), TargetQueryListActivity.class);
//					startActivity(intent);
//				} else {
//					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
//							.show();
//				}
//			}
//		});
	}
}