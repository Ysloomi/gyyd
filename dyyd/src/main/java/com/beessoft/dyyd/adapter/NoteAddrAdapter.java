package com.beessoft.dyyd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.NoteAddr;

import java.util.List;

public class NoteAddrAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<NoteAddr> mDatas;
	private boolean haveCheck;
// 	CheckChangeLister checkChangeLister;
//
//	public interface  CheckChangeLister{
//		void changeCheck(int position,boolean isChecked);
//	}
	//haveCheck 是否包含checkbox
	public NoteAddrAdapter(Context context, List<NoteAddr> datas,boolean haveCheck) {
		this.mDatas = datas;
		this.haveCheck = haveCheck;
		mInflater = LayoutInflater.from(context);
//		try {
//			// Instantiate the DeleteListener so we can send events to the host
//			checkChangeLister = (CheckChangeLister) context;
//		} catch (ClassCastException e) {
//			// The activity doesn't implement the interface, throw exception
//			throw new ClassCastException(context.toString()
//					+ " must implement DeleteListener");
//		}
	}

	public void addAll(List<NoteAddr> mDatas) {
		this.mDatas.addAll(mDatas);
	}

	public void setDatas(List<NoteAddr> mDatas) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_note_addr, null);
			holder = new ViewHolder();

			holder.mIfCheck = (CheckBox) convertView.findViewById(R.id.ifcheck);
			holder.mName = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		NoteAddr noteAddr = mDatas.get(position);
		holder.mName.setText(noteAddr.getName());
		if (haveCheck){
			holder.mIfCheck.setVisibility(View.VISIBLE);
			if ("1".equals(noteAddr.getIscheck())){
				holder.mIfCheck.setChecked(true);
			}else{
				holder.mIfCheck.setChecked(false);
			}
		}else {
			holder.mIfCheck.setVisibility(View.GONE);
		}


//		holder.mIfCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				checkChangeLister.changeCheck(position,isChecked);
//			}
//		});

		return convertView;
	}

	private final class ViewHolder {
		CheckBox mIfCheck;
		TextView mName;
	}
}
