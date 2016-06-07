package com.beessoft.dyyd.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.beessoft.dyyd.dailywork.ApproveListActivity;
import com.beessoft.dyyd.dailywork.CheckApproveListActivity;
import com.beessoft.dyyd.dailywork.ConfirmListActivity;
import com.igexin.sdk.PushConsts;

import org.json.JSONException;
import org.json.JSONObject;

public class GtPushReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
//        Logger.e("onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

//                String taskid = bundle.getString("taskid");
//                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
//                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
//                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);
//                    Logger.e("receiver payload : " + data);
                    try {
                        JSONObject object = new JSONObject(data);
                        String info = object.getString("data");
                        String title = "";
                        String content = "";
                        Intent intent1 = new Intent();
                        if ("0".equals(info)) {//签到审批
                            intent1.setClass(context.getApplicationContext(), CheckApproveListActivity.class);
//                            title="签到异常";
//                            content="有效范围外签到";
                        } else if ("1".equals(info)) {//日志审批
                            intent1.setClass(context.getApplicationContext(), ApproveListActivity.class);
//                            title="日志审批";
//                            content="下属提交日志请审批";
                        } else if ("2".equals(info)) {//日志确认
                            intent1.setClass(context.getApplicationContext(), ConfirmListActivity.class);
//                            title="日志确认";
//                            content="日志已审批请确认";
                        }
//                        intent1.setClass(context.getApplicationContext(), MyWorkActivity.class);
                        intent1.putExtra("notice", true);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.getApplicationContext().startActivity(intent1);
//					//恢复到home之间的页面
////				intent.setAction(Intent.ACTION_MAIN);
////				intent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                        intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

//					TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//// Adds the back stack stackBuilder.addParentStack(MainActivity.class);
//// Adds the Intent to the top of the stack
//					stackBuilder.addNextIntent(intent);
// Gets a PendingIntent containing the entire back stack
//					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        // Creates an explicit intent for an Activity in your app
//					Intent resultIntent = new Intent(context, ApproveListActivity.class);
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//					TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//// Adds the back stack for the Intent (but not the Intent itself)
//					stackBuilder.addParentStack(MainActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//					stackBuilder.addNextIntent(resultIntent);
                        //                  Sets the Activity to start in a new, empty task
//                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        NoticeUtil.notify(context, 0, R.drawable.sfyd_icon, content, title, content, intent1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
//                String cid = bundle.getString("clientid");
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }
}
