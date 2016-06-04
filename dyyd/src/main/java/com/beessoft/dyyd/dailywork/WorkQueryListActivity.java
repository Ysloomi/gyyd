package com.beessoft.dyyd.dailywork;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.adapter.WorkQueryAdapter;
import com.beessoft.dyyd.model.GetJSON;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.Logger;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkQueryListActivity extends BaseActivity {

	private String level;

	public List<HashMap<String, String>> datas = new ArrayList<HashMap<String, String>>();

	private AutoCompleteTextView autoCompleteTextView;
	private PullToRefreshListView mPullRefreshListView;

	private String currentPage = "1";
	private WorkQueryAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workquerylist);

		context = WorkQueryListActivity.this;

		level = "[全部人员]";

		initView();

		mAdapter = new WorkQueryAdapter(context,datas);

		autoCompleteTextView.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				autoCompleteTextView.showDropDown();// 显示下拉列表
				return false;
			}
		});

		findViewById(R.id.txt_search).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				level = autoCompleteTextView.getText().toString();
				ProgressDialogUtil.showProgressDialog(context);
				visitRefresh(level);
				Tools.closeInput(context, autoCompleteTextView);
			}
		});

		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(
						getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				visitRefresh(level);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(
						getApplicationContext(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				visitLoadMore(level);
			}

		});


		// mPullRefreshListView.isScrollingWhileRefreshingEnabled();//看刷新时是否允许滑动
		// 在刷新时允许继续滑动
		mPullRefreshListView.setScrollingWhileRefreshingEnabled(true);
		// mPullRefreshListView.getMode();//得到模式
		// 上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
		mPullRefreshListView.setAdapter(mAdapter);


		mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> map = datas.get(position-1);
				String idTarget = map.get("idTarget");
				if ("未提交".equals(map.get("state"))) {
					ToastUtil.toast(context, "日志未提交，不能查询");
				} else {
					Intent intent = new Intent(context,WorkQueryActivity.class);
					intent.putExtra("idTarget", idTarget);
					startActivity(intent);
				}
			}
		});


		GetJSON.visitServer_GetInfo_NoSpecial(context, autoCompleteTextView, mac,username);
		autoCompleteTextView.setHint("专业、姓名、分局、日期");
		ProgressDialogUtil.showProgressDialog(context);
		visitRefresh(level);
	}

	public void initView() {
		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.workquery_spinner);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
	}

	private void visitRefresh(String level) {

		String httpUrl = User.mainurl + "sf/checklist";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		currentPage = "1";

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("itype", "1");// 查询人
		parameters_userInfo.put("btn", "1");
		parameters_userInfo.put("psn", Escape.escape(level));
		parameters_userInfo.put("page", currentPage);

		Logger.e(httpUrl+"?"+parameters_userInfo);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
//							Log.e("work",dataJson.toString());
							String code = dataJson.getString("code");
							datas.clear();
							List<HashMap<String, String>> mDatas = new ArrayList<>();
							if ("1".equals(code)) {
								ToastUtil.toast(context, "没有相关信息");
							} else if ("2".equals(code)) {
								ToastUtil.toast(context, "日期格式不对，请按照2015-01-01输入");
							} else if ("0".equals(code)) {
//								datas.clear();
								JSONArray array = dataJson.getJSONArray("list");

								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, String> map = new HashMap<String, String>();
									map.put("idTarget", obj.getString("id"));
									map.put("name", obj.getString("username"));
									map.put("date", obj.getString("iday"));
									map.put("verifier",obj.getString("verifier"));
									map.put("state", obj.getString("shstate"));
									map.put("readtime",obj.getString("cs"));
									mDatas.add(map);
								}
//								Log.e("work", datas.toString());
							}
							datas = mDatas;
							mAdapter.setDatas(mDatas);
							mAdapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ProgressDialogUtil.closeProgressDialog();
							mPullRefreshListView.onRefreshComplete();
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						ProgressDialogUtil.closeProgressDialog();
						ToastUtil.toast(context, "网络连接错误，请检查网络");
						mPullRefreshListView.onRefreshComplete();
					}
				});
	}
	private void visitLoadMore(String level) {

		String httpUrl = User.mainurl + "sf/checklist";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();

		int page = Integer.valueOf(currentPage);
		page += 1;
		currentPage = String.valueOf(page);

		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("usercode", username);
		parameters_userInfo.put("sf", ifSf);
		parameters_userInfo.put("itype", "1");// 查询人
		parameters_userInfo.put("btn", "1");
		parameters_userInfo.put("psn", Escape.escape(level));
		parameters_userInfo.put("page", currentPage);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
//							Log.e("work",dataJson.toString());
							String code = dataJson.getString("code");
							if ("1".equals(code)) {
								ToastUtil.toast(context, "没有相关信息");
							} else if ("2".equals(code)) {
								ToastUtil.toast(context, "日期格式不对，请按照2015-01-01输入");
							} else if ("0".equals(code)) {
								JSONArray array = dataJson.getJSONArray("list");
								List<HashMap<String, String>> mDatas = new ArrayList<HashMap<String, String>>();
								for (int j = 0; j < array.length(); j++) {
									JSONObject obj = array.getJSONObject(j);
									HashMap<String, String> map = new HashMap<String, String>();
									map.put("idTarget", obj.getString("id"));
									map.put("name", obj.getString("username"));
									map.put("date", obj.getString("iday"));
									map.put("verifier",obj.getString("verifier"));
									map.put("state", obj.getString("shstate"));
									map.put("readtime",obj.getString("cs"));
									mDatas.add(map);
								}
//								Log.e("work", datas.toString());
								datas.addAll(mDatas);
								mAdapter.addAll(mDatas);
								mAdapter.notifyDataSetChanged();
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ProgressDialogUtil.closeProgressDialog();
							mPullRefreshListView.onRefreshComplete();
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						ToastUtil.toast(context,"网络连接错误，请检查网络");
						ProgressDialogUtil.closeProgressDialog();
						mPullRefreshListView.onRefreshComplete();
					}
				});
	}
}
