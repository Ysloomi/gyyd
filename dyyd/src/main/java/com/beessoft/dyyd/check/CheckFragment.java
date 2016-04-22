package com.beessoft.dyyd.check;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class CheckFragment extends Fragment {

	private Button checkInButton, checkOutButton, button3, button4 ;
	private String itype;
	private String role;
	private Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View check = inflater.inflate(R.layout.fragment_check, container, false);
		context = getActivity();
		role = PreferenceUtil.readString(context, "role");
		initView(check);
		return check;
	}

	private void initView(View view) {
		
		checkInButton = (Button) view.findViewById(R.id.checkin_button);
		checkOutButton = (Button) view.findViewById(R.id.checkout_button);
		button3 = (Button) view.findViewById(R.id.visit_button);
		button4 = (Button) view.findViewById(R.id.leave_button);

		view.findViewById(R.id.collect_button).setOnClickListener(onClickListener);
		view.findViewById(R.id.askleave_button).setOnClickListener(onClickListener);

		Drawable drawableTopCheckIn = getResources().getDrawable(R.drawable.checkin_untap_icon);
		Drawable drawableTopCheckOut = getResources().getDrawable(R.drawable.checkout_untap_icon);
		Drawable drawableTopReach = getResources().getDrawable(R.drawable.reach_untap_icon);
		Drawable drawableTopLeave = getResources().getDrawable(R.drawable.leave_untap_icon);
		Drawable drawableTopCheckQuery = getResources().getDrawable(R.drawable.checkquery_untap_icon);
		
		
		if ("2".equals(role) || "3".equals(role)||"4".equals(role)) {
			checkInButton.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopCheckIn, null, null);
			checkInButton.setTextColor(0xffc8c8c8);
			checkOutButton.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopCheckOut, null, null);
			checkOutButton.setTextColor(0xffc8c8c8);
			button3.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopReach, null, null);
			button3.setTextColor(0xffc8c8c8);
			button4.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopLeave, null, null);
			button4.setTextColor(0xffc8c8c8);
		}

		checkInButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					itype = "checkin";
					visitServer();
				} else {
					ToastUtil.toast(context, "无权限");
				}
			}
		});

		checkOutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					itype = "checkout";
					visitServer();
				} else {
					ToastUtil.toast(context, "无权限");
				}
			}
		});

		button3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					itype = "reach";
					visitServer();
				} else {
					ToastUtil.toast(context, "无权限");
				}
			}
		});

		button4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					itype = "leave";
					visitServer();
				} else {
					ToastUtil.toast(context, "无权限");
				}
			}
		});
	}

	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.collect_button:
				if ("0".equals(role) || "1".equals(role)) {
					Intent intent = new Intent(context, CollectActivity.class);
					startActivity(intent);
				} else {
					ToastUtil.toast(context, "无权限");
				}
				break;
			case R.id.askleave_button:
				if ("0".equals(role) || "1".equals(role)) {
					Intent intent = new Intent(context, AskLeaveActivity.class);
					startActivity(intent);
				} else {
					ToastUtil.toast(context, "无权限");
				}
				break;
			default:
				break;
			}
		}
	};
	
	private void visitServer() {
		String httpUrl = User.mainurl + "sf/startwork_do";
		String mac = GetInfo.getIMEI(context);
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", "");

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
//							Log.e("dyyd", dataJson.toString());
							String code= dataJson.getString("icount");
							Intent intent = new Intent();
							if ("checkin".equals(itype)||"checkout".equals(itype)){
								if ("0".equals(code)) {
									if ("checkin".equals(itype)) {
										intent.setClass(context,CheckInActivity.class);
										startActivity(intent);
									} else {
										ToastUtil.toast(context, "请签到");
									}
								} else if ("1".equals(code)) {
									switch (itype) {
									case "checkin":
										ToastUtil.toast(context, "已签到");
										break;
									case "checkout":
	//									if ("1".equals(dataJson.getString("notice"))) {
	//										ToastUtil.toast(getActivity(), "通知中有未阅读的文件请处理");
	//									} else {
	//										if ("1".equals(dataJson.getString("bin"))) {
	//											ToastUtil.toast(getActivity(), "签到待审批不能签退");
	//										} else {
												intent.setClass(context,CheckOutActivity.class);
												startActivity(intent);
	//										}
	//									}
										break;
									default:
										break;
									}
								} else if ("2".equals(code)) {
									ToastUtil.toast(getActivity(), "已签退");
								}
							}
							if ("reach".equals(itype)||"leave".equals(itype)){
								switch (itype) {
								case "reach":
									if ("0".equals(dataJson.getString("visit"))) {
										intent.setClass(context,VisitReachActivity.class);
										startActivity(intent);
									} else {
										ToastUtil.toast(context, "尚有到达现场，请先离开");
									}
									break;
								case "leave":
									if ("1".equals(dataJson.getString("visit"))) {
										intent.setClass(context,VisitLeaveActivity.class);
										startActivity(intent);
									} else {
										ToastUtil.toast(context, "请先拜访客户");
									}
									break;
								default:
									break;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
}