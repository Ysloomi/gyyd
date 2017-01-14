package com.beessoft.dyyd.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.widget.Button;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.bean.Note;
import com.beessoft.dyyd.bean.NoteQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetInfo {
//	public static String getLocalMacAddressFromWifiInfo(Context context) {
//		WifiManager wifi = (WifiManager) context
//				.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo info = wifi.getConnectionInfo();
//		return info.getMacAddress();
//	}


    public static String getIMEI(Context context) {
        String IMEI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        return IMEI;
    }

    public static String getPass(Context context) {
        return PreferenceUtil.readString(context, "password");
    }

    public static String getUserName(Context context) {
        return PreferenceUtil.readString(context, "username");
//		return "mohuan";
    }

    public static String getRole(Context context) {
        return PreferenceUtil.readString(context, "role");
    }

    public static String getName(Context context) {
        return PreferenceUtil.readString(context, "name");
    }

    public static String getDepart(Context context) {
        return PreferenceUtil.readString(context, "cdepname");
    }

    /*
        * 是否什邡的用户
        *
        * */
    public static boolean getIfSf(Context context) {
//        return PreferenceUtil.readBoolean(context, "ifSf");
        return false;
    }

    /*
        * 是否签到
        *
        * */
    public static boolean getIfCheck(Context context) {
        return PreferenceUtil.readBoolean(context, "ifCheck");
    }

    /*
    * 是否允许室外GPS定位
    *
    * */
    public static boolean getIfGps(Context context) {
        return PreferenceUtil.readBoolean(context, "ifgps");
    }

    /**
     * 获取所有的记事本信息
     *
     * @param jsonObject
     * @return List<Note>
     */
    @NonNull
    public static List<Note> getNotes(JSONObject jsonObject) throws JSONException {
        JSONArray array = jsonObject.getJSONArray("list");
        List<Note> mDatas = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Note note = new Note();
            note.setId(obj.getString("id"));
            note.setRdCode(obj.getString("rdCode"));
            note.setDepart(obj.getString("cdepname"));
            note.setName(obj.getString("username"));
            note.setStart(obj.getString("beginTime"));
            note.setEnd(obj.getString("endTime"));
            note.setAddr(obj.getString("customer"));
            note.setPlan(obj.getString("plan"));
            note.setDate(obj.getString("writeTime"));

            JSONObject object = obj.getJSONObject("map");
            JSONArray arrayFinish = object.getJSONArray("finish");
            ArrayList<NoteQuery> noteQueries = new ArrayList<>();
            for (int j = 0; j < arrayFinish.length(); j++) {
                JSONObject objFinish = arrayFinish.getJSONObject(j);
                NoteQuery noteQuery = new NoteQuery();
                noteQuery.setType("已走访");
                noteQuery.setRtCode(objFinish.getString("rtCode"));
                noteQuery.setAddr(objFinish.getString("customer"));
                noteQuery.setAddrCode(objFinish.getString("id"));
                noteQuery.setQuestion(objFinish.getString("problem"));
                noteQuery.setAdvise(objFinish.getString("opinion"));
                noteQuery.setDate(objFinish.getString("time"));
                noteQuery.setEffect(objFinish.getString("effect"));
                noteQuery.setDateEffect(objFinish.getString("eftime"));
                noteQueries.add(noteQuery);
            }
            JSONArray arrayUndo = object.getJSONArray("unfinish");
            for (int j = 0; j < arrayUndo.length(); j++) {
                JSONObject objUndo = arrayUndo.getJSONObject(j);
                NoteQuery noteQuery = new NoteQuery();
                noteQuery.setType("未走访");
                noteQuery.setAddr(objUndo.getString("customer"));
                noteQuery.setAddrCode(objUndo.getString("id"));
                noteQuery.setReason(objUndo.getString("problem"));
                noteQuery.setDate(objUndo.getString("time"));
                noteQueries.add(noteQuery);
            }
            JSONArray arrayReach = object.getJSONArray("visit");
            for (int j = 0; j < arrayReach.length(); j++) {
                JSONObject objReach = arrayReach.getJSONObject(j);
                NoteQuery noteQuery = new NoteQuery();
                noteQuery.setType("已拜访");
                noteQuery.setAddr(objReach.getString("customer"));
                noteQuery.setAddrCode(objReach.getString("id"));
                noteQuery.setRtCode(objReach.getString("rtCode"));
                noteQueries.add(noteQuery);
            }
            JSONArray arrayLeave = object.getJSONArray("leave");
            for (int j = 0; j < arrayLeave.length(); j++) {
                JSONObject objLeave = arrayLeave.getJSONObject(j);
                NoteQuery noteQuery = new NoteQuery();
                noteQuery.setType("已离开");
                noteQuery.setAddr(objLeave.getString("customer"));
                noteQuery.setAddrCode(objLeave.getString("id"));
                noteQuery.setRtCode(objLeave.getString("rtCode"));
                noteQueries.add(noteQuery);
            }
            JSONArray arrayWait = object.getJSONArray("wait");
            for (int j = 0; j < arrayWait.length(); j++) {
                JSONObject objWait = arrayWait.getJSONObject(j);
                NoteQuery noteQuery = new NoteQuery();
                noteQuery.setType("待走访");
                noteQuery.setAddr(objWait.getString("customer"));
                noteQuery.setAddrCode(objWait.getString("id"));
                noteQueries.add(noteQuery);
            }
            note.setNoteQueries(noteQueries);
            mDatas.add(note);
        }
        return mDatas;
    }

    public static void getButtonRole(Context context, Button button, String type, String from) {
        if (!"0".equals(PreferenceUtil.readString(context, "rolebuttoncode" + type))) {
            int a = R.drawable.activity_checkin_untap;
            if ("1".equals(type) || "2".equals(type)) {
                if ("checkout".equals(from)) {
                    a = R.drawable.activity_checkout_untap;
                } else if ("reach".equals(from)) {
                    a = R.drawable.activity_reach_untap;
                } else if ("leave".equals(from)) {
                    a = R.drawable.activity_leave_untap;
                }
            } else {
                switch (type) {
                    case "3":
                        a = R.drawable.main_check_collect_untap;
                        break;
                    case "4":
                        a = R.drawable.main_askleave_untap;
                        break;
                    case "5":
                        a = R.drawable.main_info_collect_untap;
                        break;
                    case "6":
                        a = R.drawable.main_mywork_untap;
                        break;
                    case "7":
                        a = R.drawable.activity_checkquery_untap;
                        break;
                    case "8":
                        a = R.drawable.main_location_untap;
                        break;
                    case "9":
                        a = R.drawable.main_note_untap;
                        break;
                    case "11":
                        a = R.drawable.activity_visitquery_untap;
                        break;
                    case "12":
                        a = R.drawable.main_mileage_untap;
                        break;
                }
            }
            Drawable drawableTopCheckIn = context.getResources().getDrawable(a);
            button.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopCheckIn, null, null);
            button.setTextColor(0xffc8c8c8);
        }
    }
}
