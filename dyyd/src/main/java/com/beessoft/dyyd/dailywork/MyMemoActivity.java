package com.beessoft.dyyd.dailywork;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.DateUtil;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ProgressDialogUtil;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class MyMemoActivity extends BaseActivity {

	private String idate, itime, memo, state, idTarget, result, pass, btn;
	private Button button1, button2, button3;
	private EditText editText1, editText2, editText3, editText4;

	private TextView textView1, textView2;
	Calendar calendar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymemo);

		context = MyMemoActivity.this;

		idTarget = getIntent().getStringExtra("id");
		pass = GetInfo.getPass(this);

		initView();

		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				idate = editText1.getText().toString();
				itime = editText2.getText().toString();
				memo = editText3.getText().toString();
				ProgressDialogUtil.showProgressDialog(context);
				visitServer_add();
			}
		});
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				idate = editText1.getText().toString();
				itime = editText2.getText().toString();
				memo = editText3.getText().toString();
				result = editText4.getText().toString();
				btn = "1";// 完成
				ProgressDialogUtil.showProgressDialog(context);
				visitServer_comfirm();
			}
		});

		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				idate = editText1.getText().toString();
				itime = editText2.getText().toString();
				memo = editText3.getText().toString();
				result = editText4.getText().toString();
				btn = "0";// 修改
				ProgressDialogUtil.showProgressDialog(context);
				visitServer_comfirm();
			}
		});

		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		editText1.setText(DateUtil.Date());
		editText1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(MyMemoActivity.this,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								String yearStr = String.valueOf(year);
								String month = String.valueOf(monthOfYear + 1);
								String day = String.valueOf(dayOfMonth);

								calendar.set(Calendar.YEAR, year);
								calendar.set(Calendar.MONTH, monthOfYear);
								calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

								if ((monthOfYear + 1) < 10) {
									month = "0" + month;
								}
								if (dayOfMonth < 10) {
									day = "0" + day;
								}

								editText1.setText(yearStr + "-" + month + "-"
										+ day);
							}
						}, calendar.get(Calendar.YEAR), calendar
								.get(Calendar.MONTH), calendar
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		editText2.setText(DateUtil.Time());
		editText2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Calendar c = Calendar.getInstance();
				new TimePickerDialog(MyMemoActivity.this,
						new TimePickerDialog.OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								String hour = String.valueOf(hourOfDay);
								String min = String.valueOf(minute);

								calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
								calendar.set(Calendar.MINUTE, minute);

								if (hourOfDay < 10) {
									hour = "0" + hour;
								}
								if (minute < 10) {
									min = "0" + min;
								}
								editText2.setText(hour + ":" + min + ":00");
							}
						}, calendar.get(Calendar.HOUR_OF_DAY), calendar
								.get(Calendar.MINUTE), true).show();
			}
		});


	}

	private void initView() {
		editText1 = (EditText) findViewById(R.id.date_text);
		editText1.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘
		editText2 = (EditText) findViewById(R.id.time_text);
		editText2.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘
		editText3 = (EditText) findViewById(R.id.memo_text);
		editText4 = (EditText) findViewById(R.id.result_text);

		button1 = (Button) findViewById(R.id.mymemo_add);
		button2 = (Button) findViewById(R.id.mymemo_confirm);
		button3 = (Button) findViewById(R.id.mymemo_change);

		textView1 = (TextView) findViewById(R.id.state_text);
		textView2 = (TextView) findViewById(R.id.updatetime_text);


		if (!"add".equals(idTarget)) {
			// editText1.setKeyListener(null);
			// editText2.setKeyListener(null);
			button1.setVisibility(View.GONE);
			ProgressDialogUtil.showProgressDialog(context);
			visitServer_get();
		} else {
			button2.setVisibility(View.GONE);
			button3.setVisibility(View.GONE);
			editText4.setKeyListener(null);
			textView1.setBackgroundResource(R.drawable.unedit_text_bg);
			textView2.setBackgroundResource(R.drawable.unedit_text_bg);
			editText4.setBackgroundResource(R.drawable.unedit_text_bg);
		}
	}

	private void visitServer_get() {
		String httpUrl = User.mainurl + "sf/memo_show";

		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("id", idTarget);
		// parameters_userInfo.put("state", state);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							if (dataJson.getString("code").equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								for (int i = 0; i < array.length(); i++) {
									JSONObject obj = array.getJSONObject(0);
									editText1.setText(obj.getString("date"));
									editText2.setText(obj.getString("time"));
									editText3.setText(obj.getString("item"));
									textView1.setText(obj.getString("state"));
									textView2.setText(obj.getString("cmakertime"));
									editText4.setText(obj.getString("result"));
								}
								state = textView1.getText().toString();
								if ("已完成".equals(state)) {
									editText4.setKeyListener(null);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							ProgressDialogUtil.closeProgressDialog();
						}
					}
						@Override
						public void onFailure(Throwable error, String data) {
							error.printStackTrace(System.out);
							ProgressDialogUtil.closeProgressDialog();
						}

				});
	}

	private void visitServer_add() {
		String httpUrl = User.mainurl + "sf/memo_up";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("date", idate);
		parameters_userInfo.put("time", itime);
		parameters_userInfo.put("item", Escape.escape(memo));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						// System.out.println("response" + response);
						try {
							JSONObject dataJson = new JSONObject(Escape
									.unescape(response));
							if (dataJson.getString("code").equals("0")) {
								Toast.makeText(MyMemoActivity.this,
										"备忘录数据添加成功", Toast.LENGTH_SHORT).show();

//								AlarmUtils.startAlarmService(
//										MyMemoActivity.this,
//										MemoAlarmService.class,
//										MemoAlarmService.ACTION,
//										calendar.getTimeInMillis());

								finish();
							} else if (dataJson.getString("code").equals("1")) {
								Toast.makeText(MyMemoActivity.this, "上传失败",
										Toast.LENGTH_SHORT).show();
							} else if (dataJson.getString("code").equals("-2")) {
								Toast.makeText(MyMemoActivity.this, "无权限",
										Toast.LENGTH_SHORT).show();
							}

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ProgressDialogUtil.closeProgressDialog();
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						ProgressDialogUtil.closeProgressDialog();
					}
				});
	}

	// private void doalarm() {
	// // 定义闹钟参数
	// AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	// Intent in = new Intent(this, MyMemoListActivity.class);
	// // in.setAction("Memo");
	// in.putExtra("memo", "事件提醒");
	// PendingIntent pi = PendingIntent.getBroadcast(this, 0, in, 0);
	// am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
	// }

	private void visitServer_comfirm() {
		String httpUrl = User.mainurl + "sf/memo_do";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		parameters_userInfo.put("mac", mac);
		parameters_userInfo.put("pass", pass);
		parameters_userInfo.put("id", idTarget);
		parameters_userInfo.put("date", idate);
		parameters_userInfo.put("time", itime);
		parameters_userInfo.put("item", Escape.escape(memo));
		parameters_userInfo.put("result", Escape.escape(result));
		parameters_userInfo.put("btn", Escape.escape(btn));

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(response);
							String code = dataJson.getString("code");
							if ("0".equals(code)) {
								Toast.makeText(MyMemoActivity.this,
										"备忘录数据上传成功", Toast.LENGTH_SHORT).show();
								
//								AlarmUtils.startAlarmService(
//										MyMemoActivity.this,
//										MemoAlarmService.class,
//										MemoAlarmService.ACTION,
//										calendar.getTimeInMillis());
								
								finish();
							} else if ("1".equals(code)) {
								Toast.makeText(MyMemoActivity.this, "失败",
										Toast.LENGTH_SHORT).show();
							} else if ("-2".equals(code)) {
								Toast.makeText(MyMemoActivity.this, "无权限",
										Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ProgressDialogUtil.closeProgressDialog();
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						ProgressDialogUtil.closeProgressDialog();
					}
				});
	}
}
