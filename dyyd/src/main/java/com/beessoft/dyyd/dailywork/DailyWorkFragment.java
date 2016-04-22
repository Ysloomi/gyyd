package com.beessoft.dyyd.dailywork;

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
import android.widget.TextView;
import android.widget.Toast;

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

public class DailyWorkFragment extends Fragment {

	private Button visitQueryBtn, myworkButton, mileageBtn, queryButton, noticeBtn, workQueryBtn,
			mymemoBtn, workLocationButton, arrangeBtn;
	private TextView textView;
	private Context context;
	private String role;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View dailywork = inflater.inflate(R.layout.dailywork, container, false);
		context = getActivity();
		role = PreferenceUtil.readString(context, "role");
		initView(dailywork);
		return dailywork;
	}

	private void initView(View view) {
		
		textView = (TextView) view.findViewById(R.id.notice_num);

		visitQueryBtn = (Button) view.findViewById(R.id.visitquery_button);
		myworkButton = (Button) view.findViewById(R.id.work_button);
		mileageBtn = (Button) view.findViewById(R.id.mileage_button);
		queryButton = (Button) view.findViewById(R.id.query_button);
		noticeBtn = (Button) view.findViewById(R.id.notice_button);
		workQueryBtn = (Button) view.findViewById(R.id.workquery_button);
		mymemoBtn = (Button) view.findViewById(R.id.mymemo_button);
		workLocationButton = (Button) view.findViewById(R.id.worklocation_button);
		arrangeBtn = (Button) view.findViewById(R.id.arrange_button);

		view.findViewById(R.id.photo_button).setOnClickListener(onClickListener);
		view.findViewById(R.id.checkquery_button).setOnClickListener(onClickListener);
		view.findViewById(R.id.btn_note).setOnClickListener(onClickListener);

		Drawable drawableTopVisitQuery = getResources().getDrawable(
				R.drawable.visitquery_untap_icon);
		Drawable drawableTopMyWork = getResources().getDrawable(
				R.drawable.mywork_untap_icon);
		Drawable drawableTopMileage = getResources().getDrawable(
				R.drawable.mymileage_untap_icon);
		Drawable drawableTopQuery = getResources().getDrawable(
				R.drawable.approvequery_untap_icon);
		Drawable drawableTopNotice = getResources().getDrawable(
				R.drawable.notice_untap_icon);
		Drawable drawableTopLocation = getResources().getDrawable(
				R.drawable.worklocation_untap_icon);
		Drawable drawableTopArrage = getResources().getDrawable(
				R.drawable.arrange_untap_icon);
		Drawable drawableTopWorkQuery = getResources().getDrawable(
				R.drawable.workquery_untap_icon);

		if ("2".equals(role) || "3".equals(role)) {
			visitQueryBtn.setCompoundDrawablesWithIntrinsicBounds(null,drawableTopVisitQuery, null, null);
			visitQueryBtn.setTextColor(0xffc8c8c8);
			myworkButton.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopMyWork, null, null);
			myworkButton.setTextColor(0xffc8c8c8);
			mileageBtn.setCompoundDrawablesWithIntrinsicBounds(null,drawableTopMileage, null, null);
			mileageBtn.setTextColor(0xffc8c8c8);
			queryButton.setCompoundDrawablesWithIntrinsicBounds(null,drawableTopQuery, null, null);
			queryButton.setTextColor(0xffc8c8c8);
//			noticeBtn.setCompoundDrawablesWithIntrinsicBounds(null,
//					drawableTopNotice, null, null);
//			noticeBtn.setTextColor(0xffc8c8c8);
			workQueryBtn.setCompoundDrawablesWithIntrinsicBounds(null,drawableTopWorkQuery, null, null);
			workQueryBtn.setTextColor(0xffc8c8c8);
//			mymemoBtn.setCompoundDrawablesWithIntrinsicBounds(null,
//					drawableTopWorkQuery, null, null);
//			mymemoBtn.setTextColor(0xffc8c8c8);
			workLocationButton.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopLocation, null, null);
			workLocationButton.setTextColor(0xffc8c8c8);
			arrangeBtn.setCompoundDrawablesWithIntrinsicBounds(null,drawableTopArrage, null, null);
			arrangeBtn.setTextColor(0xffc8c8c8);
		}
		if ("4".equals(role)) {
			visitQueryBtn.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopVisitQuery, null, null);
			visitQueryBtn.setTextColor(0xffc8c8c8);
			myworkButton.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopMyWork, null, null);
			myworkButton.setTextColor(0xffc8c8c8);
			mileageBtn.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopMileage, null, null);
			mileageBtn.setTextColor(0xffc8c8c8);
			queryButton.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopQuery, null, null);
			queryButton.setTextColor(0xffc8c8c8);
			noticeBtn.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopNotice, null, null);
			noticeBtn.setTextColor(0xffc8c8c8);
			workQueryBtn.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopWorkQuery, null, null);
			workQueryBtn.setTextColor(0xffc8c8c8);
			mymemoBtn.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopWorkQuery, null, null);
			mymemoBtn.setTextColor(0xffc8c8c8);
			workLocationButton.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopLocation, null, null);
			workLocationButton.setTextColor(0xffc8c8c8);
			arrangeBtn.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopArrage, null, null);
			arrangeBtn.setTextColor(0xffc8c8c8);
		}
		if ("0".equals(role)) {
			workLocationButton.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopLocation, null, null);
			workLocationButton.setTextColor(0xffc8c8c8);
			arrangeBtn.setCompoundDrawablesWithIntrinsicBounds(null,
					drawableTopArrage, null, null);
			arrangeBtn.setTextColor(0xffc8c8c8);
		}

		visitQueryBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), VisitQueryListActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		myworkButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), MyWorkActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		mileageBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), MyMileageActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		queryButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(),
							ApproveQueryListActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		noticeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!"4".equals(role)){
					Intent intent = new Intent();
					intent.setClass(getActivity(), NoticeListActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		workQueryBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("0".equals(role) || "1".equals(role)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), WorkQueryListActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		mymemoBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!"4".equals(role)){
					Intent intent = new Intent();
					intent.setClass(getActivity(), MyMemoListActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		workLocationButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("1".equals(role)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), WorkLocationActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		arrangeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if ("1".equals(role)) {
					Intent intent = new Intent();
					intent.setClass(context, ArrangeActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getActivity(), "无权限", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
	}


	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
				case R.id.checkquery_button:
					if ("0".equals(role) || "1".equals(role)) {
						intent.setClass(getActivity(), NewCheckQueryActivity.class);
						startActivity(intent);
					} else {
						ToastUtil.toast(context, "无权限");
					}
					break;
				case R.id.photo_button:
					intent.setClass(getActivity(), PhotoActivity.class);
					startActivity(intent);
					break;
				case R.id.btn_note:
					intent.setClass(getActivity(), NoteActivity.class);
					startActivity(intent);
					break;
				default:
					break;
			}
		}
	};


//	@Override
//	public void onResume() {
//		visitServer(getActivity());
//		super.onResume();
//	}

	private void visitServer(final Context context) {
		String httpUrl = User.mainurl + "sf/notice";
		String mac = GetInfo.getIMEI(getActivity());
		String pass = GetInfo.getPass(context);
		String username= GetInfo.getUserName(context);
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("usercode", username);


		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							String noticenum = dataJson.getString("icount");
							PreferenceUtil.write(context,"noticenum",noticenum);
							textView.setText(noticenum);
							String online = dataJson.getString("pernum");
							PreferenceUtil.write(context,"online",online);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
}