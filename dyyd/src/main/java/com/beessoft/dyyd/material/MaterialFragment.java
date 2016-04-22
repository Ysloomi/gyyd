package com.beessoft.dyyd.material;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.PreferenceUtil;

public class MaterialFragment extends Fragment {
	private Button button1, button2, button3, button4, button5, button6,button7;
	private String role;
	private Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mymeans = inflater.inflate(R.layout.material, container, false);
		context = getActivity();
		role = PreferenceUtil.readString(context, "role");
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
		Drawable drawableTopTerminal = getResources().getDrawable(
				R.drawable.terminal_untap_icon);
		Drawable drawableTopSales = getResources().getDrawable(
				R.drawable.sales_untap_icon);
		Drawable drawableTopBoss = getResources().getDrawable(
				R.drawable.boss_untap_icon);
		Drawable drawableTopWorkBook = getResources().getDrawable(
				R.drawable.workbook_untap_icon);
		Drawable drawableTopCompany = getResources().getDrawable(
				R.drawable.company_untap_icon1);
		Drawable drawableTopBranch = getResources().getDrawable(
				R.drawable.branch_untap_icon);
		Drawable drawableTopTarget = getResources().getDrawable(
				R.drawable.target_untap_icon);

		if ("3".equals(role)) {
//			button1.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTerminal,
//					null, null);
//			button1.setTextColor(0xffc8c8c8);
			button3.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBoss,
					null, null);
			button3.setTextColor(0xffc8c8c8);
			button4.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopWorkBook,
					null, null);
			button4.setTextColor(0xffc8c8c8);
			button5.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopCompany,
					null, null);
			button5.setTextColor(0xffc8c8c8);
			button6.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBranch,
					null, null);
			button6.setTextColor(0xffc8c8c8);
//			button7.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTarget,
//					null, null);
//			button7.setTextColor(0xffc8c8c8);
		}
		if ("2".equals(role)) {
//			button1.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTerminal,
//					null, null);
//			button1.setTextColor(0xffc8c8c8);
			button4.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopWorkBook,
					null, null);
			button4.setTextColor(0xffc8c8c8);
			button5.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopCompany,
					null, null);
			button5.setTextColor(0xffc8c8c8);
//			button6.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBranch,
//					null, null);
//			button6.setTextColor(0xffc8c8c8);
//			button7.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTarget,
//					null, null);
//			button7.setTextColor(0xffc8c8c8);
		}
		if ("4".equals(role)) {
			button1.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTerminal,
					null, null);
			button1.setTextColor(0xffc8c8c8);
			button2.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopSales,
					null, null);
			button2.setTextColor(0xffc8c8c8);
			button3.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBoss,
					null, null);
			button3.setTextColor(0xffc8c8c8);
			button4.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopWorkBook,
					null, null);
			button4.setTextColor(0xffc8c8c8);
			button5.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopCompany,
					null, null);
			button5.setTextColor(0xffc8c8c8);
			button6.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBranch,
					null, null);
			button6.setTextColor(0xffc8c8c8);
			button7.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTarget,
					null, null);
			button7.setTextColor(0xffc8c8c8);
		}
		
		button1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!"4".equals(role)){
					Intent intent = new Intent();
					intent.setClass(getActivity(), TerminalActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		//店员手册
		button2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!"4".equals(role)){
					Intent intent = new Intent();
					intent.setClass(getActivity(), SalesWorkBookActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		//老板手册
		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 if("3".equals(role)||"4".equals(role)){
						Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
								.show();
					} else  {
					Intent intent = new Intent();
					intent.setClass(getActivity(), BossWorkBookActivity.class);
					startActivity(intent);
				}
			}
		});
		//员工手册
		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), WorkBookActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		//公司日报
		button5.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), CompanyTargetActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		//分局日报
		button6.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 if("3".equals(role)||"4".equals(role)){
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}else{
					Intent intent = new Intent();
					intent.setClass(getActivity(), BranchTargetActivity.class);
					startActivity(intent);
				}
			}
		});
		//目标查询
		button7.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				if ("0".equals(MainActivity.role)
//						|| "1".equals(MainActivity.role)) {
				if (!"4".equals(role)){
					Intent intent = new Intent();
					intent.setClass(getActivity(), TargetQueryListActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}
}