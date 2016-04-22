package com.beessoft.dyyd.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beessoft.dyyd.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class AdviseAdapter extends SimpleAdapter {
	private Context context;
	private List<Map<String, String>> mDataList;

	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		ImageView imageView =(ImageView) v.findViewById(R.id.adviselist_image);
		HashMap<String, String> map =(HashMap<String, String>) mDataList.get(position);
		String state = map.get("state");
		if("1".equals(state)){
//			System.out.println(state);
			imageView.setVisibility(View.GONE);
		}
//		Button btn = (Button) v.findViewById(R.id.order_delete);
//		btn.setTag(position);
//		btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(final View v) {
//
//				AlertDialog.Builder builder = new AlertDialog.Builder(context);
//				builder.setTitle("删除产品")
//						.setMessage("确定删除产品")
//						.setNegativeButton("确定",
//								new AlertDialog.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										HashMap<String, String> map =(HashMap<String, String>) mDataList.get(position);
//										String customer = map.get("customer");
//										String name = map.get("name");
//										
//										OrderDao orderDao = new OrderDao(context);
//										orderDao.delete(customer,name);
//										
//										mDataList.remove(position);
//										notifyDataSetChanged();
//										
//									}
//								}).setPositiveButton("取消", null).create()
//						.show();
//			}
//		});
		return v;
	}

	@SuppressWarnings("unchecked")
	public AdviseAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.mDataList = (List<Map<String, String>>) data;
	}
}
