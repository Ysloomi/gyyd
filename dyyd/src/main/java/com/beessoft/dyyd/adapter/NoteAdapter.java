package com.beessoft.dyyd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.Note;

import java.util.List;

public class NoteAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Note> mDatas;

	public NoteAdapter(Context context, List<Note> datas) {
		this.mDatas = datas;
		mInflater = LayoutInflater.from(context);
	}

	public void addAll(List<Note> mDatas) {
		this.mDatas.addAll(mDatas);
	}

	public void setDatas(List<Note> mDatas) {
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
			convertView = mInflater.inflate(R.layout.item_note, null);
			holder = new ViewHolder();

			holder.mDate = (TextView) convertView.findViewById(R.id.date);
			holder.mAddr = (TextView) convertView.findViewById(R.id.addr);
			holder.mPlan = (TextView) convertView.findViewById(R.id.plan);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Note note = mDatas.get(position);
		holder.mDate.setText(note.getStart()+"\r\n          至\r\n"+note.getEnd());
		holder.mAddr.setText(note.getAddr());
		holder.mPlan.setText(note.getPlan());
		return convertView;
	}

	private final class ViewHolder {
		TextView mDate;
		TextView mAddr;
		TextView mPlan;
	}
}
