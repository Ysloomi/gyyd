package com.beessoft.dyyd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beessoft.dyyd.LocationApplication;
import com.beessoft.dyyd.R;

import java.util.HashMap;
import java.util.List;

public class SpecialApproveAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HashMap<String,String>> mDatas;

	public SpecialApproveAdapter(Context context, List<HashMap<String,String>> datas) {
		this.mDatas = datas;
		mInflater = LayoutInflater.from(context);
	}

	public void addAll(List<HashMap<String,String>> mDatas) {
		this.mDatas.addAll(mDatas);
	}

	public void setDatas(List<HashMap<String,String>> mDatas) {
		this.mDatas.clear();
		this.mDatas.addAll(mDatas);
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_special, null);
			holder = new ViewHolder();
			holder.mName = (TextView) convertView.findViewById(R.id.name);
			holder.mDate = (TextView) convertView.findViewById(R.id.date);
			holder.mAddr = (TextView) convertView.findViewById(R.id.addr);
			holder.mRemarks = (TextView) convertView.findViewById(R.id.remarks);
			holder.mModel = (ImageView) convertView.findViewById(R.id.model);
			holder.mPhoto = (ImageView) convertView.findViewById(R.id.photo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String,String> map = mDatas.get(position);
		holder.mName.setText(map.get("name"));
		holder.mDate.setText(map.get("date"));
		holder.mAddr.setText(map.get("addr"));
		holder.mRemarks.setText( map.get("remarks"));
		LocationApplication.imageLoader.displayImage(map.get("model"),holder.mModel,LocationApplication.options);
		LocationApplication.imageLoader.displayImage(map.get("photo"),holder.mPhoto,LocationApplication.options);
		return convertView;
	}

	private final class ViewHolder {
		TextView mName;
		TextView mDate;
		TextView mAddr;
		TextView mRemarks;
		ImageView mModel;
		ImageView mPhoto;
	}
}
