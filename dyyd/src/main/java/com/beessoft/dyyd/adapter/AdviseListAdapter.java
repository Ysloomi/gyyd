package com.beessoft.dyyd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beessoft.dyyd.R;

import java.util.HashMap;
import java.util.List;

public class AdviseListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<HashMap<String, String>> mDatas;

	public AdviseListAdapter(Context context, List<HashMap<String, String>> datas) {
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_adviselist, null);
			holder = new ViewHolder();
			holder.mImg = (ImageView) convertView.findViewById(R.id.adviselist_image);
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
//		0为已回复,1为待处理，2为处理中，3为已完成
		if ("0".equals(map.get("state"))) {
			holder.mImg.setImageResource(R.drawable.advise_answer);
		} else if ("1".equals(map.get("state")))  {
			holder.mImg.setImageResource(R.drawable.advise_wait_answer);
		}else if ("2".equals(map.get("state")))  {
			holder.mImg.setImageResource(R.drawable.advise_answering);
		}else if ("3".equals(map.get("state")))  {
			holder.mImg.setImageResource(R.drawable.advise_answer_finish);
		}

		return convertView;
	}

	private final class ViewHolder {
		TextView mAdvise;
		TextView mTime;
		TextView mAdviseType;
		ImageView mImg;
	}
}
