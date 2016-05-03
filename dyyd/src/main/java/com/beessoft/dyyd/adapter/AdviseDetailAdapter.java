package com.beessoft.dyyd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beessoft.dyyd.R;

import java.util.HashMap;
import java.util.List;

public class AdviseDetailAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HashMap<String, String>> mDatas;

	public AdviseDetailAdapter(Context context, List<HashMap<String, String>> datas) {
		this.mDatas = datas;
		mInflater = LayoutInflater.from(context);

	}

	public void addAll(List<HashMap<String, String>> mDatas) {
		this.mDatas.addAll(mDatas);
	}

	public void setDatas(List<HashMap<String, String>> mDatas) {
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
			convertView = mInflater.inflate(R.layout.adviselist_item, null);
			holder = new ViewHolder();
//			holder.mImg = (ImageView) convertView.findViewById(R.id.adviselist_image);
			holder.mAdvise= (TextView) convertView
					.findViewById(R.id.name);
			holder.mTime = (TextView) convertView.findViewById(R.id.time);
			holder.mAdviseType = (TextView) convertView.findViewById(R.id.text);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HashMap<String, String> map = mDatas.get(position);
		holder.mAdvise.setText(map.get("advise"));
		holder.mTime.setText(map.get("time"));
		holder.mAdviseType.setText(map.get("advise_type"));

		return convertView;
	}

	private final class ViewHolder {
		TextView mAdvise;
		TextView mTime;
		TextView mAdviseType;
	}
}
