package com.beessoft.dyyd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.NoteQuery;
import com.beessoft.dyyd.utils.Tools;

import java.util.List;

public class NoteQueryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<NoteQuery> mDatas;

	public NoteQueryAdapter(Context context, List<NoteQuery> datas) {
		this.mDatas = datas;
		mInflater = LayoutInflater.from(context);
	}

	public void addAll(List<NoteQuery> mDatas) {
		this.mDatas.addAll(mDatas);
	}

	public void setDatas(List<NoteQuery> mDatas) {
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
			convertView = mInflater.inflate(R.layout.item_note_query, null);

			holder = new ViewHolder();

			holder.llQuestion = (LinearLayout) convertView.findViewById(R.id.ll_question);
			holder.llAdvise = (LinearLayout) convertView.findViewById(R.id.ll_advise);
			holder.llReason = (LinearLayout) convertView.findViewById(R.id.ll_reason);
			holder.llDate = (LinearLayout) convertView.findViewById(R.id.ll_date);
			holder.llEffect = (LinearLayout) convertView.findViewById(R.id.ll_effect);
			holder.llEffectDate = (LinearLayout) convertView.findViewById(R.id.ll_effect_date);

			holder.mTitleAddr = (TextView) convertView.findViewById(R.id.txt_title_addr);
			holder.mAddr = (TextView) convertView.findViewById(R.id.txt_addr);
			holder.mQuestion = (TextView) convertView.findViewById(R.id.txt_question);
			holder.mAdvise = (TextView) convertView.findViewById(R.id.txt_advise);
			holder.mReason = (TextView) convertView.findViewById(R.id.txt_reason);
			holder.mDate = (TextView) convertView.findViewById(R.id.txt_date);
			holder.mEffect = (TextView) convertView.findViewById(R.id.txt_effect);
			holder.mEffectDate = (TextView) convertView.findViewById(R.id.txt_effect_date);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		NoteQuery noteQuery = mDatas.get(position);

		holder.mTitleAddr.setText(noteQuery.getType());
		holder.mAddr.setText(noteQuery.getAddr());
		if (Tools.isEmpty(noteQuery.getQuestion())){
			holder.llQuestion.setVisibility(View.GONE);
		}else {
			holder.llQuestion.setVisibility(View.VISIBLE);
			holder.mQuestion.setText(noteQuery.getQuestion());
		}
		if (Tools.isEmpty(noteQuery.getAdvise())){
			holder.llAdvise.setVisibility(View.GONE);
		}else {
			holder.llAdvise.setVisibility(View.VISIBLE);
			holder.mAdvise.setText(noteQuery.getAdvise());
		}
		if (Tools.isEmpty(noteQuery.getReason())){
			holder.llReason.setVisibility(View.GONE);
		}else {
			holder.llReason.setVisibility(View.VISIBLE);
			holder.mReason.setText(noteQuery.getReason());
		}
		if (Tools.isEmpty(noteQuery.getDate())){
			holder.llDate.setVisibility(View.GONE);
		}else {
			holder.llDate.setVisibility(View.VISIBLE);
			holder.mDate.setText(noteQuery.getDate());
		}
		if (Tools.isEmpty(noteQuery.getEffect())){
			holder.llEffect.setVisibility(View.GONE);
		}else {
			holder.llEffect.setVisibility(View.VISIBLE);
			holder.mEffect.setText(noteQuery.getEffect());
		}
		if (Tools.isEmpty(noteQuery.getDateEffect())){
			holder.llEffectDate.setVisibility(View.GONE);
		}else {
			holder.llEffectDate.setVisibility(View.VISIBLE);
			holder.mEffectDate.setText(noteQuery.getDateEffect());
		}
		return convertView;
	}

	private final class ViewHolder {
		TextView mTitleAddr;
		TextView mAddr;
		TextView mQuestion;
		TextView mAdvise;
		TextView mReason;
		TextView mDate;
		TextView mEffect;
		TextView mEffectDate;
		LinearLayout llQuestion;
		LinearLayout llAdvise;
		LinearLayout llReason;
		LinearLayout llDate;
		LinearLayout llEffect;
		LinearLayout llEffectDate;
	}
}
