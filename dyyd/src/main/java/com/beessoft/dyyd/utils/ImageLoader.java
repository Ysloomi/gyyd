package com.beessoft.dyyd.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by wxl on 16/8/13.
 */
public class ImageLoader {

    public static void load(Context context, Uri uri, ImageView view) {
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .into(view);
    }

    public static void load(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .into(view);
    }

    public static void load(Context context, int resourceId, ImageView view) {
        view.setImageResource(resourceId);
    }

    public static void load(Context context, Bitmap bitmap, ImageView view) {
        view.setImageBitmap(bitmap);
    }
}