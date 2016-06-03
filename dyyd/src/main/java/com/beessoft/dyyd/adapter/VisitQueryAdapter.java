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

public class VisitQueryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HashMap<String,String>> mDatas;

	public VisitQueryAdapter(Context context, List<HashMap<String, String>> datas) {
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
			convertView = mInflater.inflate(R.layout.item_visitquerylist, null);
			holder = new ViewHolder();
			holder.mDate = (TextView) convertView.findViewById(R.id.date);
			holder.mPerson = (TextView) convertView.findViewById(R.id.person);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String,String> map = mDatas.get(position);
		holder.mDate.setText(map.get("idate"));
		holder.mPerson.setText(map.get("name"));
		return convertView;
	}

	private final class ViewHolder {
		TextView mDate;
		TextView mPerson;
	}
}
