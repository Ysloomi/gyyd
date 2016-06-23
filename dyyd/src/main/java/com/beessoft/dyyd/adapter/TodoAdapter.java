package com.beessoft.dyyd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beessoft.dyyd.R;

import java.util.HashMap;
import java.util.List;

public class TodoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<HashMap<String, String>> mDatas;

    public TodoAdapter(Context context, List<HashMap<String, String>> datas) {
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
            convertView = mInflater.inflate(R.layout.item_todo, null);
            holder = new ViewHolder();
            holder.mStep = (TextView) convertView.findViewById(R.id.step);
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.mTimes = (TextView) convertView.findViewById(R.id.do_proportion);
            holder.mTime = (TextView) convertView.findViewById(R.id.time_last);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, String> map = mDatas.get(position);
        holder.mStep.setText(map.get("step"));
        holder.mName.setText(map.get("name"));
        String times = map.get("done");

        if (!TextUtils.isEmpty(times)) {
            holder.mTimes.setText("完成次数:" +times);
            holder.mTimes.setVisibility(View.VISIBLE);
        } else
            holder.mTimes.setVisibility(View.GONE);

        String time = map.get("undo");
        if (!TextUtils.isEmpty(time)) {
            holder.mTime.setText("完成时长:" + time);
            holder.mTime.setVisibility(View.VISIBLE);
        } else
            holder.mTime.setVisibility(View.GONE);
        return convertView;
    }

    private final class ViewHolder {
        TextView mStep;
        TextView mName;
        TextView mTimes;
        TextView mTime;

    }
}
