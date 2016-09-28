package com.beessoft.dyyd.dailywork;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.utils.ImageLoader;
import com.beessoft.dyyd.utils.User;

import java.util.ArrayList;
import java.util.List;


public class PhotoImagePagerActivity extends Activity {

    private final static String POSITION_KEY = "position";
    private final static String IMAGES_KEY = "images";

    private int pagerPosition = 0;
    private ArrayList<String> mDatas;

    private ViewPager pager;
    private ImagePagerAdapter imagePagerAdapter;


    public static void navToPhotoImagePager(Context context,int position,ArrayList<String> mDatas){
        Intent intent = new Intent(context,PhotoImagePagerActivity.class);
        intent.putExtra(POSITION_KEY,position);
        intent.putStringArrayListExtra(IMAGES_KEY,mDatas);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepage);


        pagerPosition = getIntent().getIntExtra(POSITION_KEY, 0);
        mDatas = getIntent().getStringArrayListExtra(IMAGES_KEY);

        imagePagerAdapter = new ImagePagerAdapter(this, mDatas);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(imagePagerAdapter);
        pager.setCurrentItem(pagerPosition);

        imagePagerAdapter.notifyDataSetChanged();
    }


    private class ImagePagerAdapter extends PagerAdapter {

        private List<String> images = new ArrayList<String>();
        private Context context;

        public ImagePagerAdapter(Context context, ArrayList<String> datas) {
            this.context = context;
            this.images = datas;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            ImageView photoView = new ImageView(view.getContext());
            ImageLoader.load(context, User.mainurl + images.get(position), photoView);
            // Now just add PhotoView to ViewPager and return it
            view.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

    /**
     * 点击返回按钮
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }
}