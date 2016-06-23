package com.beessoft.dyyd.material;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beessoft.dyyd.BaseActivity;
import com.beessoft.dyyd.R;


public class ResearchActivity extends BaseActivity {
	
    private ListView listView;
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        
        context = ResearchActivity.this;
        
        listView = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
        		R.layout.item_baselist,
        		new String[]{
        		"商铺信息",
        		"商铺个人信息",
        		"小区居民信息"});
        listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(
					AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				String a = (String) listView.getItemAtPosition(position);
				Intent intent =new Intent();
//				Log.e("sfyd", a);
				if("小区居民信息".equals(a))
				{
					intent.setClass(context, HouseActivity.class);
				}else{
					intent.setClass(context, StreetActivity.class);
				}
				intent.putExtra("research", a);
				startActivity(intent);
			}
		});
    }  
}
      
