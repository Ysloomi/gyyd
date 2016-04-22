package com.beessoft.dyyd.swipemenulistview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.beessoft.dyyd.R;


/**
 * Created by wongxl on 16/2/1.
 */
public class SwipeMenuHelper {

    Context context;

    public SwipeMenuHelper(Context context){
        this.context = context;
    }


    public SwipeMenuCreator getSwipeMenuCreator() {
        // step 1. create a MenuCreator
        return new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "Top" item
                SwipeMenuItem topItem = new SwipeMenuItem(context.getApplicationContext());
                // set item background
                topItem.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.TopGray)));
                // set item width
                topItem.setWidth(dp2px(90));
                // set item title
                topItem.setTitle("置顶");
                // set item title fontsize
                topItem.setTitleSize(18);
                // set item title font color
                topItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(topItem);

                // create "delete" item
//                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
//                // set item background
//                deleteItem.setBackground(ContextCompat.getColor(context, R.color.ChangeOrange));
//                // set item width
//                deleteItem.setWidth(dp2px(90));
//                // set a icon
//                deleteItem.setIcon(R.drawable.ic_delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);

                // create "change" item
                SwipeMenuItem changeItem = new SwipeMenuItem(context.getApplicationContext());
                // set item background
                changeItem.setBackground(new ColorDrawable(ContextCompat.getColor(context,R.color.ChangeOrange)));
                // set item width
                changeItem.setWidth(dp2px(90));
                // set item title
                changeItem.setTitle("修改");
                // set item title fontsize
                changeItem.setTitleSize(18);
                // set item title font color
                changeItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(changeItem);


                // create "change" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(context.getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(context,R.color.DeleteRed)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set item title
                deleteItem.setTitle("删除");
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);

            }
        };
    }

    public SwipeMenuCreator getSwipeMenuDelectCreator() {
        // step 1. create a MenuCreator
        return new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "change" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(context.getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(context,R.color.DeleteRed)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set item title
                deleteItem.setTitle("删除");
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
