package com.beessoft.dyyd;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beessoft.dyyd.check.CheckFragment;
import com.beessoft.dyyd.dailywork.DailyWorkFragment;
import com.beessoft.dyyd.material.MaterialFragment;
import com.beessoft.dyyd.mymeans.MyMeansFragment;
import com.beessoft.dyyd.update.UpdateManager;
import com.beessoft.dyyd.utils.ChatAdatper;
import com.beessoft.dyyd.utils.GetInfo;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private View check;
    private View dailywork;
    private View material;
    private View mymeans;

    private ImageView checkImage;
    private ImageView dailyworkImage;
    private ImageView materialImage;
    private ImageView mymeansImage;
    private TextView checkText;

    private TextView dailyworkText;
    private TextView materialText;
    private TextView mymeansText;

    private UpdateManager mUpdateManager;

    /**
     * 用于对Fragment进行管理
     */
//	private FragmentManager fragmentManager;

    private ViewPager viewPager;// 声明一个viewpager对象
    private Context context;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ShareActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        // 初始化布局元素
        setUpView();
        initView();

        mUpdateManager = new UpdateManager(this);//判断是否应该升级
        mUpdateManager.checkUpdate(false);//是否弹出版本更新信息

        // fragmentManager = getSupportFragmentManager();
        // 第一次启动时选中第0个tab
        if (GetInfo.getIfCheck(context)) {
            checkImage.setImageResource(R.drawable.check_selected);
        } else {
            check.setVisibility(View.GONE);
            dailyworkImage.setImageResource(R.drawable.dailywork_selected);
        }
        if (GetInfo.getIfSf(context))
            material.setVisibility(View.VISIBLE);
        else
            material.setVisibility(View.GONE);
//		checkText.setTextColor(Color.parseColor("#4db0f6"));
    }

    /**
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
     */

    private void setUpView() {
        // 实例化对象
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        check = findViewById(R.id.check);
        dailywork = findViewById(R.id.dailywork);
        material = findViewById(R.id.material);
        mymeans = findViewById(R.id.mymeans);

        checkImage = (ImageView) findViewById(R.id.check_image);
        dailyworkImage = (ImageView) findViewById(R.id.dailywork_image);
        materialImage = (ImageView) findViewById(R.id.material_image);
        mymeansImage = (ImageView) findViewById(R.id.mymeans_image);

        checkText = (TextView) findViewById(R.id.check_text);
        dailyworkText = (TextView) findViewById(R.id.dailywork_text);
        materialText = (TextView) findViewById(R.id.material_text);
        mymeansText = (TextView) findViewById(R.id.mymeans_text);

        if (GetInfo.getIfCheck(context)) {
            check.setOnClickListener(new MyOnClickListener(0));
            dailywork.setOnClickListener(new MyOnClickListener(1));
            if (GetInfo.getIfSf(context)) {
                material.setOnClickListener(new MyOnClickListener(2));
                mymeans.setOnClickListener(new MyOnClickListener(3));
            } else {
                mymeans.setOnClickListener(new MyOnClickListener(2));
            }
        } else {
            dailywork.setOnClickListener(new MyOnClickListener(0));
            if (GetInfo.getIfSf(context)) {
                material.setOnClickListener(new MyOnClickListener(1));
                mymeans.setOnClickListener(new MyOnClickListener(2));
            } else {
                mymeans.setOnClickListener(new MyOnClickListener(1));
            }
        }
    }

    private void initView() {
        ArrayList<Fragment> list = new ArrayList<Fragment>();
        // 设置数据源
        CheckFragment checkFragment = new CheckFragment();
        DailyWorkFragment dailyWorkFragment = new DailyWorkFragment();
        MaterialFragment materialFragment = new MaterialFragment();
        MyMeansFragment mymeansFragment = new MyMeansFragment();

        if (GetInfo.getIfCheck(context)) {
            list.add(checkFragment);
        }
        list.add(dailyWorkFragment);
        if (GetInfo.getIfSf(context))
            list.add(materialFragment);
        list.add(mymeansFragment);

        ChatAdatper adapter = new ChatAdatper(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            // 每次选中之前先清楚掉上次的选中状态
            clearSelection();
            if (GetInfo.getIfCheck(context)) {
                // Animation animation = null;
                switch (arg0) {
                    case 0:
                        checkImage.setImageResource(R.drawable.check_selected);
                        // checkText.setTextColor(Color.parseColor("#4db0f6"));
                        break;
                    case 1:
                        dailyworkImage
                                .setImageResource(R.drawable.dailywork_selected);
                        // dailyworkText.setTextColor(Color.parseColor("#4db0f6"));
                        break;
                    case 2:
                        if (GetInfo.getIfSf(context))
                            materialImage.setImageResource(R.drawable.material_selected);
                        else
                            mymeansImage.setImageResource(R.drawable.mymeans_selected);
                        // materialText.setTextColor(Color.parseColor("#4db0f6"));
                        break;
                    case 3:
                        mymeansImage.setImageResource(R.drawable.mymeans_selected);
                        // mymeansText.setTextColor(Color.parseColor("#4db0f6"));
                        break;
                }
            } else {
                // Animation animation = null;
                switch (arg0) {
                    case 0:
                        dailyworkImage.setImageResource(R.drawable.dailywork_selected);
                        // dailyworkText.setTextColor(Color.parseColor("#4db0f6"));
                        break;
                    case 1:
                        if (GetInfo.getIfSf(context))
                            materialImage.setImageResource(R.drawable.material_selected);
                        else
                            mymeansImage.setImageResource(R.drawable.mymeans_selected);
                        // materialText.setTextColor(Color.parseColor("#4db0f6"));
                        break;
                    case 2:
                        mymeansImage.setImageResource(R.drawable.mymeans_selected);
                        // mymeansText.setTextColor(Color.parseColor("#4db0f6"));
                        break;
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    ;

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        checkImage.setImageResource(R.drawable.check_unselected);
        checkText.setTextColor(Color.WHITE);
        dailyworkImage.setImageResource(R.drawable.dailywork_unselected);
        dailyworkText.setTextColor(Color.WHITE);
        materialImage.setImageResource(R.drawable.material_unselected);
        materialText.setTextColor(Color.WHITE);
        mymeansImage.setImageResource(R.drawable.mymeans_unselected);
        mymeansText.setTextColor(Color.WHITE);
    }

    @Override
    // 点返回时不退出 ，亦可不用（因service已保持在后台运行）
    // 改写返回键事件监听，使得back键功能类似home键，让Acitivty退至后台时不被系统销毁，代码如下：
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(
                new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME), 0);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityInfo ai = homeInfo.activityInfo;
            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent
                    .setComponent(new ComponentName(ai.packageName, ai.name));
            startActivitySafely(startIntent);
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务 //
     */
    // private void hideFragments(FragmentTransaction transaction) {
    // if (checkFragment != null) {
    // transaction.hide(checkFragment);
    // }
    // if (dailyWorkFragment != null) {
    // transaction.hide(dailyWorkFragment);
    // }
    // // if (constructionFragment != null) {
    // // transaction.hide(constructionFragment);
    // // }
    // // if (manageFragment != null) {
    // // transaction.hide(manageFragment);
    // // }
    // if(myMeansFragment != null){
    // transaction.hide(myMeansFragment);
    // }
    // }
}
