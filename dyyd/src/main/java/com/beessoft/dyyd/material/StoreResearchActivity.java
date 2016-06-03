package com.beessoft.dyyd.material;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.Escape;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.PhotoHelper;
import com.beessoft.dyyd.utils.PhotoUtil;
import com.beessoft.dyyd.utils.PreferenceUtil;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.Tools;
import com.beessoft.dyyd.utils.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

public class  StoreResearchActivity extends BaseActivity {
	
    private Context context;
    
    private EditText belongEdit;
    private EditText numEdit;
    private EditText nameEdit;
    private EditText typeEdit;
    private EditText attributeEdit;
    private EditText personEdit;
    private EditText phoneEdit;
    private EditText totalEdit;
    private EditText ydEdit;
    private EditText dxEdit;
    private EditText ltEdit;
    private EditText ifnetEdit;
    private EditText operaterEdit;
    private EditText reasonEdit;
    private EditText netcostEdit;
    private EditText netovertimeEdit;
    private EditText ifmixEdit;
    private EditText tvEdit;
    private EditText tvcostEdit;
    private EditText tvnumEdit;
    private EditText telEdit;
    private EditText safeEdit;
    private EditText messageEdit;
    private EditText vnetEdit;
    private EditText ringEdit;
    private EditText posEdit;
    private EditText ydnetEdit;
    private EditText ltnetEdit;
    private EditText dxnetEdit;
    
    private String company;
    private String department;
    private String departmentCode;
    private String street;
    private String belong;
    private String num;
    private String name;
    private String type;
    private String attribute;
    private String person;
    private String phone;
    private String total;
    private String yd;
    private String dx;
    private String lt;
    private String ifnet;
    private String operater;
    private String reason;
    private String netcost;
    private String netovertime;
    private String ifmix;
    private String tv;
    private String tvcost;
    private String tvnum;
    private String tel;
    private String safe;
    private String message;
    private String vnet;
    private String ring;
    private String pos;
    private String ydnet;
    private String ltnet;
    private String dxnet;
    
    private String itype;
    private String id;
    private String max;
    
    private ProgressDialog progressDialog;
    
	// 照片
	public static final int PHOTO_CODE = 5;
	public static final int ALBUM_CODE = 4;
	
	private String uploadBuffer = "";
	
	private String imgPath;
	private File imageFile = null;
	private Bitmap bitmap = null;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String imageLink;
	private ImageView photoImage;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeresearch);
        context = StoreResearchActivity.this;
        
        company="什邡分公司";
        department=getIntent().getStringExtra("department");
        departmentCode=getIntent().getStringExtra("departmentcode");
        street=getIntent().getStringExtra("street");
        max=getIntent().getStringExtra("maxstreet");
        itype=getIntent().getStringExtra("itype");
        id=getIntent().getStringExtra("id");
       

        initView();

		netovertimeEdit.setInputType(InputType.TYPE_NULL);// 不弹出输入键盘
		netovertimeEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new DatePickerDialog(context,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
												  int monthOfYear, int dayOfMonth) {
								String yearStr = String.valueOf(year);
								String month = String.valueOf(monthOfYear + 1);
								String day = String.valueOf(dayOfMonth);
								if ((monthOfYear + 1) < 10) {
									month = "0" + month;
								}
								if (dayOfMonth < 10) {
									day = "0" + day;
								}
								netovertimeEdit.setText(yearStr + "-" + month + "-" + day);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
						.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		findViewById(R.id.confirm_bt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				belong= belongEdit.getText().toString();
				num= numEdit.getText().toString();
				name= nameEdit.getText().toString();
				type= typeEdit.getText().toString();
				attribute= attributeEdit.getText().toString();
				person= personEdit.getText().toString();
				phone= phoneEdit.getText().toString();
				total= totalEdit.getText().toString();
				yd= ydEdit.getText().toString();
				lt= ltEdit.getText().toString();
				dx= dxEdit.getText().toString();
				ifnet= ifnetEdit.getText().toString();
				operater= operaterEdit.getText().toString();
				reason= reasonEdit.getText().toString();
				netcost= netcostEdit.getText().toString();
				netovertime= netovertimeEdit.getText().toString();
				ifmix= ifmixEdit.getText().toString();
				tv= tvEdit.getText().toString();
				tvcost= tvcostEdit.getText().toString();
				tvnum= tvnumEdit.getText().toString();
				tel= telEdit.getText().toString();
				safe= safeEdit.getText().toString();
				message= messageEdit.getText().toString();
				vnet= vnetEdit.getText().toString();
				ring= ringEdit.getText().toString();
				pos= posEdit.getText().toString();
				ydnet= ydnetEdit.getText().toString();
				ltnet= ltnetEdit.getText().toString();
				dxnet= dxnetEdit.getText().toString();


				Boolean a = true;
				String b = num.replaceAll("，", ",");
				String[] c = b.split(",");
				for (int i = 0; i < c.length; i++) {
					if(Integer.valueOf(c[i]) > Integer.valueOf(max)){
						a = false;
						break;
					}
				}
				if(a){
					//				显示ProgressDialog
					progressDialog = ProgressDialog.show(context, "载入中...", "请等待...", true, true);
					visitServer();
				}else{
					ToastUtil.toast(context, "门牌号不能大于街道门牌");
				}
			}
		});

		findViewById(R.id.photo_iv).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Tools.isSDCardExit()) {
					imgPath = Tools.getSDPath() + "/sfyd/photo.jpg";
//					Logger.e("first "+imgPath);
//					PreferenceUtil.write(context,"photopath",imgPath);
					takePhoto();
				} else {
					ToastUtil.toast(context, "内存卡不存在不能拍照");
				}
			}
		});
		photoImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Tools.isEmpty(imgPath)) {
					PhotoHelper.openPictureDialog(context, imgPath);
				} else if (!Tools.isEmpty(imageLink)) {
					PhotoHelper.openPictureDialog(context, imageLink, imageLoader, options);
				}
			}
		});
        
        if(!Tools.isEmpty(id)){
			progressDialog = ProgressDialog.show(context, "载入中...", "请等待...", true, true);
			visitGet();
		}
    }

	private void initView() {
		TextView companyText = (TextView) findViewById(R.id.company_tv);
		TextView departmentText = (TextView) findViewById(R.id.department_tv);
		TextView streetTextView = (TextView) findViewById(R.id.street_tv);
		
		companyText.setText(company);
		departmentText.setText(department);
		streetTextView.setText(street);
		
		belongEdit = (EditText) findViewById(R.id.belong_tv);
		numEdit = (EditText) findViewById(R.id.num_tv);
		nameEdit = (EditText) findViewById(R.id.name_tv);
		typeEdit = (EditText) findViewById(R.id.type_tv);
		attributeEdit = (EditText) findViewById(R.id.attribute_tv);
		typeEdit = (EditText) findViewById(R.id.type_tv);
		attributeEdit = (EditText) findViewById(R.id.attribute_tv);
		personEdit = (EditText) findViewById(R.id.person_tv);
		phoneEdit = (EditText) findViewById(R.id.phone_tv);
		totalEdit = (EditText) findViewById(R.id.total_tv);
		ydEdit = (EditText) findViewById(R.id.yd_tv);
		dxEdit = (EditText) findViewById(R.id.dx_tv);
		ltEdit = (EditText) findViewById(R.id.lt_tv);
		
		ifnetEdit = (EditText) findViewById(R.id.ifnet_tv);
		operaterEdit = (EditText) findViewById(R.id.operater_tv);
		reasonEdit = (EditText) findViewById(R.id.reason_tv);
		netcostEdit = (EditText) findViewById(R.id.netcost_tv);
		netovertimeEdit = (EditText) findViewById(R.id.netovertime_tv);
		ifmixEdit = (EditText) findViewById(R.id.ifmix_tv);
		tvEdit = (EditText) findViewById(R.id.tv_tv);
		
		tvcostEdit = (EditText) findViewById(R.id.tvcost_tv);
		tvnumEdit = (EditText) findViewById(R.id.tvnum_tv);
		telEdit = (EditText) findViewById(R.id.tel_tv);
		safeEdit = (EditText) findViewById(R.id.safe_tv);
		messageEdit = (EditText) findViewById(R.id.message_tv);
		vnetEdit = (EditText) findViewById(R.id.vnet_tv);
		ringEdit = (EditText) findViewById(R.id.ring_tv);
		posEdit = (EditText) findViewById(R.id.pos_tv);
		ydnetEdit = (EditText) findViewById(R.id.ydnet_tv);
		ltnetEdit = (EditText) findViewById(R.id.ltnet_tv);
		dxnetEdit = (EditText) findViewById(R.id.dxnet_tv);
		
		photoImage= (ImageView) findViewById(R.id.store_image);
	} 
	
	public void takePhoto() {
		// 必须确保文件夹路径存在，否则拍照后无法完成回调
		File vFile = new File(imgPath);
		if (!vFile.exists()) {
			File vDirPath = vFile.getParentFile();
			vDirPath.mkdirs();
		}
		PreferenceUtil.write(context, "photopath", imgPath);
		Uri uri = Uri.fromFile(vFile);
		Intent intent = new Intent();
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		// 打开新的activity，这里是系统摄像头
		startActivityForResult(intent, PHOTO_CODE);
	}

	// 相机返回处理
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			uploadBuffer = "";
			switch (requestCode) {
			case PHOTO_CODE:
				String imagePath = PreferenceUtil.readString(context, "photopath");
//				if(data!=null){
//					Uri uri = data.getData();
//					Logger.e("uri"+uri);
//				}
//				Logger.e("imagePath>>>"+imagePath);
//				Bitmap bitmap = get
				if(imagePath != null){
					File file = new File(imagePath);
					bitmap = PhotoUtil.imageEncode(file,true);
					photoImage.setImageBitmap(bitmap);
					uploadBuffer = PhotoUtil.encodeTobase64(bitmap);
				}
				break;
			default:
				break;
			}
		}
	}
	
	private void visitGet() {
		
		String httpUrl = User.mainurl + "survey/AppSurveyShopQuery";
		AsyncHttpClient client_request = new AsyncHttpClient();
		RequestParams parameters_userInfo = new RequestParams();
		
		parameters_userInfo.put("mac", GetInfo.getIMEI(context));
		parameters_userInfo.put("id", id);

		client_request.post(httpUrl, parameters_userInfo,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							JSONObject dataJson = new JSONObject(Escape.unescape(response));
							String code = dataJson.getString("code");
							if (code.equals("0")) {
								JSONArray array = dataJson.getJSONArray("list");
								JSONObject obj = array.getJSONObject(0);
								belongEdit.setText(obj.getString("gssk"));
								numEdit.setText(obj.getString("mph"));
								nameEdit.setText(obj.getString("spm"));
								typeEdit.setText(obj.getString("splx"));
								attributeEdit.setText(obj.getString("spsx"));
								personEdit.setText(obj.getString("splxrxm"));
								phoneEdit.setText(obj.getString("splxrdh"));
								totalEdit.setText(obj.getString("spzrs"));
								ydEdit.setText(obj.getString("ydyhs"));
								dxEdit.setText(obj.getString("dxyhs"));
								ltEdit.setText(obj.getString("ltyhs"));
								ifnetEdit.setText(obj.getString("spsfsykd"));
								operaterEdit.setText(obj.getString("yys"));
								reasonEdit.setText(obj.getString("wyydyy"));
								netcostEdit.setText(obj.getString("kdzf"));
								netovertimeEdit.setText(obj.getString("idate"));
								ifmixEdit.setText(obj.getString("sfrhcp"));
								tvEdit.setText(obj.getString("dsyys"));
								tvcostEdit.setText(obj.getString("dszf"));
								tvnumEdit.setText(obj.getString("dssl"));
								telEdit.setText(obj.getString("zjsyqk"));
								safeEdit.setText(obj.getString("afsyqk"));
								messageEdit.setText(obj.getString("qfdxsyqk"));
								vnetEdit.setText(obj.getString("Vwsyqk"));
								ringEdit.setText(obj.getString("jtcxsyqk"));
								posEdit.setText(obj.getString("pos"));
								ydnetEdit.setText(obj.getString("sffgydkd"));
								ltnetEdit.setText(obj.getString("sffgltkd"));
								dxnetEdit.setText(obj.getString("sffgdxkd"));
								imageLink = User.mainurl + obj.getString("imgfile");
								imageLoader.displayImage(imageLink, photoImage, options);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							progressDialog.dismiss();
						}
					}

					@Override
					public void onFailure(Throwable error, String data) {
						error.printStackTrace(System.out);
						progressDialog.dismiss();
					}
				});
	}
		private void visitServer() {
			
			String httpUrl = User.mainurl + "survey/AppSurveyShopSave";
			
			AsyncHttpClient client_request = new AsyncHttpClient();
			RequestParams parameters_userInfo = new RequestParams();
			
			parameters_userInfo.put("mac", GetInfo.getIMEI(context));
			parameters_userInfo.put("cdepcode", departmentCode);
			parameters_userInfo.put("fgs", Escape.escape(company));
			parameters_userInfo.put("gssk", Escape.escape(belong));
			parameters_userInfo.put("jd", Escape.escape(street));
			parameters_userInfo.put("mph", Escape.escape(num));
			parameters_userInfo.put("spm", Escape.escape(name));
			parameters_userInfo.put("splx", Escape.escape(type));
			parameters_userInfo.put("spsx", Escape.escape(attribute));
			parameters_userInfo.put("splxrxm", Escape.escape(person));
			parameters_userInfo.put("splxrdh", Escape.escape(phone));
			parameters_userInfo.put("spzrs", Escape.escape(total));
			parameters_userInfo.put("ydyhs", Escape.escape(yd));
			parameters_userInfo.put("dxyhs", Escape.escape(dx));
			parameters_userInfo.put("ltyhs", Escape.escape(lt));
			parameters_userInfo.put("spsfsykd", Escape.escape(ifnet));
			parameters_userInfo.put("yys", Escape.escape(operater));
			parameters_userInfo.put("wyydyy", Escape.escape(reason));
			parameters_userInfo.put("kdzf", Escape.escape(netcost));
			parameters_userInfo.put("idate", netovertime);
			parameters_userInfo.put("sfrhcp", Escape.escape(ifmix));
			parameters_userInfo.put("dsyys", Escape.escape(tv));
			parameters_userInfo.put("dszf", Escape.escape(tvcost));
			parameters_userInfo.put("dssl", Escape.escape(tvnum));
			parameters_userInfo.put("zjsyqk", Escape.escape(tel));
			parameters_userInfo.put("afsyqk", Escape.escape(safe));
			parameters_userInfo.put("qfdxsyqk", Escape.escape(message));
			parameters_userInfo.put("Vwsyqk", Escape.escape(vnet));
			parameters_userInfo.put("jtcxsyqk", Escape.escape(ring));
			parameters_userInfo.put("pos", Escape.escape(pos));
			parameters_userInfo.put("sffgydkd", Escape.escape(ydnet));
			parameters_userInfo.put("sffgltkd", Escape.escape(ltnet));
			parameters_userInfo.put("sffgdxkd", Escape.escape(dxnet));
			parameters_userInfo.put("sffgdxkd", Escape.escape(dxnet));
			
			parameters_userInfo.put("image", uploadBuffer);
			
			parameters_userInfo.put("itype", itype);
			if("edit".equals(itype)){
				parameters_userInfo.put("id", id);
			}

			client_request.post(httpUrl, parameters_userInfo,
					new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							try {
								JSONObject dataJson = new JSONObject(response);
								String code = dataJson.getString("code");
								if (code.equals("0")) {
									ToastUtil.toast(context, "上传成功");
									finish();
								} else if (code.equals("1")) {
									ToastUtil.toast(context, "上传失败");
								} else if (code.equals("2")) {
									ToastUtil.toast(context, "门牌号重复提交");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}finally {
								progressDialog.dismiss();
							}
						}

						@Override
						public void onFailure(Throwable error, String data) {
							error.printStackTrace(System.out);
							progressDialog.dismiss();
						}
					});
		}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("UserInfoActivity", "onConfigurationChanged");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("UserInfoActivity", "横屏");
			Configuration o = newConfig;
			o.orientation = Configuration.ORIENTATION_PORTRAIT;
			newConfig.setTo(o);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("UserInfoActivity", "竖屏");
		}
		super.onConfigurationChanged(newConfig);
	}
}
      
