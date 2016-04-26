package com.beessoft.dyyd.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.beessoft.dyyd.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class Tools {

	/**
	 * 通过目标路径得到输入流
	 * 
	 * @param imgpath
	 * @return InputStream
	 * @throws Exception
	 */
	public InputStream getWebInputStream(String imgpath) throws Exception {
		URL url = new URL(imgpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			return is;
		}

		return null;
	}

	/**
	 * 通过目标路径得到URL中的bitmap对象
	 * 
	 * @param imgpath
	 * @return Bitmap
	 * @throws Exception
	 */

	public Bitmap getBitMap(String imgpath) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = getWebInputStream(imgpath);
		int len = 0;
		byte[] data = new byte[1024];
		while ((len = is.read(data)) != -1) {
			bos.write(data, 0, len);
		}
		is.close();
		bos.toByteArray();
		return BitmapFactory.decodeByteArray(bos.toByteArray(), 0,
				bos.toByteArray().length);
	}

	/**
	 * 得到内存卡的路径
	 * 
	 * @return
	 */

	public File getSDCard(Context context) {
		File sdDir = null;
		// 判断sd卡是否存在
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			// 获取根目录
			sdDir = Environment.getExternalStorageDirectory();
			return sdDir;
		} else {
			Toast.makeText(context, "内存卡不存在", Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	/**
	 * 保存图片到SD卡中
	 * 
	 * @param context
	 * @param imgedatas
	 * @param position
	 */

	public void SaveImge(Context context,
			List<HashMap<String, Object>> imgedatas, int position) {

		String imgName = imgedatas.get(position).get("imgName").toString();
		Bitmap bmap;
		HashMap<String, Object> map = imgedatas.get(position);
		bmap = (Bitmap) map.get("imgPath");
		String imgDectory = getSDCard(context).toString() + "/imges/";// 图片目录路径
		// String imgDectory = getSDCard(context).toString() + "/";// 图片目录路径
		String imgPath = imgDectory + imgName + ".jpg";// 图片路径
		File dectory = new File(imgDectory);
		dectory.mkdirs();
		System.out.println("创建文件成功:" + imgPath);
		File imgFilePath = new File(imgPath);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(imgFilePath));
			bmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 获取中文标志
	 * @author wongxl
	 * @param context
	 * @param autoCompleteTextView
	 */
	public static String[] chineseNum = new String[] {
			"一", "二", "三", "四","五", "六", "七", "八", "九", "十",
			"十一", "十二", "十三", "十四", "十五", "十六","十七", "十八", "十九", "二十",
			"二十一", "二十二", "二十三", "二十四", "二十五", "二十六","二十七", "二十八", "二十九","三十",
			"三十一", "三十二", "三十三", "三十四", "三十五", "三十六","三十七", "三十八", "三十九", "四十" };

	/**
	 * 点击完成按钮后，关闭键盘
	 * 
	 * 
	 * @param context
	 * @param autoCompleteTextView
	 */
	public static void closeInput(Context context,
			AutoCompleteTextView autoCompleteTextView) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(),
					0);
		}
	}

	/**
	 *  根据值, 设置spinner默认选中: 
	 *  @param spinner  
	 * 	@param value  
	 */
	public static void setSpinnerItemSelectedByValue(Spinner spinner,
			String value) {
		SpinnerAdapter apsAdapter = spinner.getAdapter();// 得到SpinnerAdapter对象
		int k = apsAdapter.getCount();
		for (int i = 0; i < k; i++) {
			if (value.equals(apsAdapter.getItem(i).toString())) {
				spinner.setSelection(i, true);//  默认选中项
				break;
			}
		}
	}
	
	/**
	 * 清除列表数据
	 * 
	 * @param datas
	 * @param simAdapter
	 * @param listView
	 */
	public static void cleanlist(List<HashMap<String, Object>> datas,
			SimpleAdapter simAdapter, ListView listView) {
		int size = datas.size();
		if (size > 0) {
			datas.removeAll(datas);
			simAdapter.notifyDataSetChanged();
			listView.setAdapter(simAdapter);
		}
	}
	
	

	/**
	 * 判断sd卡存不存在
	 * 
	 * @return
	 */
	public static Boolean isSDCardExit() {
		try {
			if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	/**
	 * 得到内存卡的路径
	 * 
	 * @return
	 */
	public static String getSDPath() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}
	
	/**
	 * 判断指定的字符串是否是 正确的（不为“”、null 、“null”）
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str) && !"null".equals(str)
				&& !"[]".equals(str))
			return false;
		return true;
	}

	/**
	 * 装载SPINNER
	 *
	 * @param context
	 * @param spinner
	 * @param lists
	 * @return
	 */
	public static  void reloadSpinner(Context context,Spinner spinner,List<String> lists){
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
				R.layout.spinner_item,
				lists);
		spinner.setAdapter(arrayAdapter);
	}

}
