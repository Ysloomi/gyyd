package com.beessoft.dyyd.utils;

import android.content.Context;
import android.text.TextUtils;

/**
 * Created by wxl on 16/8/18.
 */
public class ResourcesUtils {

    public static final String TYPE_DRAWABLE = "drawable";
    public static final String TYPE_COLOR = "color";
    public static final String TYPE_RAW = "raw";
    public static final String TYPE_STRING = "string";
    /**
     * use resouce name to get resource id
     * @param context
     * @param resourcesName the resouce name that you want to get it's id
     * @param type resource type,contains:
     * @return -1 illegal input params, !=-1 resouce id
     *
     * */
    public static int getResourceId(Context context, String resourcesName, String type ){
        if( null == context || TextUtils.isEmpty( resourcesName ) || TextUtils.isEmpty( type ) ){
            return -1;
        }

        return context.getResources( ).getIdentifier(resourcesName, type, context.getPackageName( ) );
    }


    /**
     * use resouce id to get resource name
     * @param context
     * @param resourcesId the resouce id that you want to get it's name
     * @return "" illegal input params, !"" resouce name
     *
     * */
    public static String getResourceName(Context context, int resourcesId){
        if( null == context || resourcesId==0){
            return "";
        }

        return context.getResources( ).getResourceEntryName(resourcesId);
    }

}
