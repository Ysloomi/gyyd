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

public class MyWorkAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<HashMap<String, String>> mDatas;

    public MyWorkAdapter(Context context, List<HashMap<String, String>> datas) {
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
            convertView = mInflater.inflate(R.layout.item_mywork, null);
            holder = new ViewHolder();
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.mMes = (TextView) convertView.findViewById(R.id.message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, String> map = mDatas.get(position);
        String name = map.get("name");
        holder.mName.setText(name);
        holder.mMes.setText(map.get("message"));


        return convertView;
    }

    private final class ViewHolder {
        TextView mName;
        TextView mMes;
    }
}
