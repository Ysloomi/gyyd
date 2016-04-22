package com.beessoft.dyyd.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil
{
	public static void toast(Context context , String msg)
	{
		if (!Tools.isEmpty(msg)){
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}
}
