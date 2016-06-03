package com.beessoft.dyyd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beessoft.dyyd.R;

import java.util.List;

public class NoteAddrTypeAdapter extends BaseAdapter {

//	private Context context;
	private LayoutInflater mInflater;
	private List<String> mDatas;
	private int mPosition =0;

	@SuppressLint("UseSparseArrays")
	public NoteAddrTypeAdapter(Context context, List<String> datas) {
//		this.context = context;
		this.mDatas = datas;
		mInflater = LayoutInflater.from(context);
	}

	public void addAll(List<String> mDatas) {
		this.mDatas.addAll(mDatas);
	}

	public void setDatas(List<String> mDatas) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder hold;
		if (convertView == null) {
			hold = new Holder();
			
			convertView = mInflater.inflate(R.layout.item_search_more_mainlist, null);
			hold.txt = (TextView) convertView.findViewById(R.id.Search_more_mainitem_txt);
			hold.layout = (LinearLayout) convertView.findViewById(R.id.Search_more_mainitem_layout);
			
			convertView.setTag(hold);
		} else {
			hold = (Holder) convertView.getTag();
		}
		String name = mDatas.get(position);
		hold.txt.setText(name);
		hold.layout.setBackgroundResource(R.drawable.search_more_mainlistselect);
		if (position == mPosition) {
			hold.layout.setBackgroundResource(R.drawable.list_bkg_line_u);
		}
		return convertView;
	}

	public void setSelectItem(int i) {
		mPosition = i;
	}

	public int getSelectItem() {
		return mPosition;
	}

	private static class Holder {
		LinearLayout layout;
//		ImageView img;
		TextView txt;
	}
}
