package com.beessoft.dyyd.material;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beessoft.dyyd.R;
import com.beessoft.dyyd.WebViewActivity;
import com.beessoft.dyyd.utils.GetInfo;
import com.beessoft.dyyd.utils.ToastUtil;
import com.beessoft.dyyd.utils.User;

public class MaterialFragment extends Fragment {
    private Button terminalBtn, salesBtn, bossBtn, workBtn, companyBtn, branchBtn, targetQueryBtn;
    private String role;
    private Context context;
    private Button researchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_material, container, false);
        context = getActivity();
        role = GetInfo.getRole(context);
        initView(view);
        return view;
    }

    private void initView(View view) {
        terminalBtn = (Button) view.findViewById(R.id.terminal_button);
        salesBtn = (Button) view.findViewById(R.id.sales_button);
        bossBtn = (Button) view.findViewById(R.id.boss_button);
        workBtn = (Button) view.findViewById(R.id.workbook_button);
        companyBtn = (Button) view.findViewById(R.id.companytarget_button);
        branchBtn = (Button) view.findViewById(R.id.branchtarget_button);
        targetQueryBtn = (Button) view.findViewById(R.id.targetquery_button);

        researchButton = (Button) view.findViewById(R.id.research_button);

        Drawable drawableTopTerminal = getResources().getDrawable(R.drawable.terminal_untap);
        Drawable drawableTopSales = getResources().getDrawable(R.drawable.sales_untap);
        Drawable drawableTopBoss = getResources().getDrawable(R.drawable.boss_untap);
        Drawable drawableTopWorkBook = getResources().getDrawable(R.drawable.workbook_untap);
        Drawable drawableTopCompany = getResources().getDrawable(R.drawable.company_untap);
        Drawable drawableTopBranch = getResources().getDrawable(R.drawable.branch_untap);
        Drawable drawableTopTarget = getResources().getDrawable(R.drawable.target_untap_icon);

//        if (role.equals("3")) {
////			terminalBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTerminal,
////					null, null);
////			terminalBtn.setTextColor(0xffc8c8c8);
//            bossBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBoss,
//                    null, null);
//            bossBtn.setTextColor(0xffc8c8c8);
//            workBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopWorkBook,
//                    null, null);
//            workBtn.setTextColor(0xffc8c8c8);
//            companyBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopCompany,
//                    null, null);
//            companyBtn.setTextColor(0xffc8c8c8);
//            branchBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBranch,
//                    null, null);
//            branchBtn.setTextColor(0xffc8c8c8);
////			targetQueryBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTarget,
////					null, null);
////			targetQueryBtn.setTextColor(0xffc8c8c8);
//        }
//        if (role.equals("2")) {
////			terminalBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTerminal,
////					null, null);
////			terminalBtn.setTextColor(0xffc8c8c8);
//            workBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopWorkBook,
//                    null, null);
//            workBtn.setTextColor(0xffc8c8c8);
//            companyBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopCompany,
//                    null, null);
//            companyBtn.setTextColor(0xffc8c8c8);
////			branchBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBranch,
////					null, null);
////			branchBtn.setTextColor(0xffc8c8c8);
////			targetQueryBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTarget,
////					null, null);
////			targetQueryBtn.setTextColor(0xffc8c8c8);
//        }
//        if (role.equals("4")) {
//            terminalBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTerminal,
//                    null, null);
//            terminalBtn.setTextColor(0xffc8c8c8);
//            salesBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopSales,
//                    null, null);
//            salesBtn.setTextColor(0xffc8c8c8);
//            bossBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBoss,
//                    null, null);
//            bossBtn.setTextColor(0xffc8c8c8);
//            workBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopWorkBook,
//                    null, null);
//            workBtn.setTextColor(0xffc8c8c8);
//            companyBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopCompany,
//                    null, null);
//            companyBtn.setTextColor(0xffc8c8c8);
//            branchBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopBranch,
//                    null, null);
//            branchBtn.setTextColor(0xffc8c8c8);
//            targetQueryBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTopTarget,
//                    null, null);
//            targetQueryBtn.setTextColor(0xffc8c8c8);
//        }

        terminalBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if (!"4".equals(role)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WebViewActivity.class);
                    intent.putExtra("title", "终端管理");
                    intent.putExtra("url", "http://cmerp.dyg3.net/");
                    startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
            }
        });
        //店员手册
        salesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if (!"4".equals(role)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WebViewActivity.class);
                    intent.putExtra("title", "店员手册");
                    intent.putExtra("url", User.mainurl + "books.jsp?booktype=2&mac="
                            + GetInfo.getIMEI(context) + "&usercode=" + GetInfo.getUserName(context));
                    startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
            }
        });
        //老板手册
        bossBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if ("3".equals(role) || "4".equals(role)) {
//                    ToastUtil.toast(context, "无权限");
//                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WebViewActivity.class);
                    intent.putExtra("title", "老板手册");
                    intent.putExtra("url", User.mainurl + "books.jsp?booktype=3&mac="
                            + GetInfo.getIMEI(context) + "&usercode=" + GetInfo.getUserName(context));
                    startActivity(intent);
//                }
            }
        });
        //员工手册
        workBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if ("0".equals(role) || "1".equals(role)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WebViewActivity.class);
                    intent.putExtra("title", "员工手册");
                    intent.putExtra("url", User.mainurl + "books.jsp?booktype=1&mac="
                            + GetInfo.getIMEI(context) + "&usercode=" + GetInfo.getUserName(context));
                    startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
            }
        });
        //公司日报
        companyBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ("0".equals(role) || "1".equals(role)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WebViewActivity.class);
                    intent.putExtra("title", "公司日报");
                    intent.putExtra("url", User.mainurl + "books.jsp?booktype=4&mac="
                            + GetInfo.getIMEI(context) + "&usercode=" + GetInfo.getUserName(context));
                    startActivity(intent);
                } else {
                    ToastUtil.toast(context, "无权限");
                }
            }
        });
        //分局日报
        branchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if ("3".equals(role) || "4".equals(role)) {
//                    ToastUtil.toast(context, "无权限");
//                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WebViewActivity.class);
                    intent.putExtra("title", "分局日报");
                    intent.putExtra("url", User.mainurl + "books.jsp?booktype=5&mac="
                            + GetInfo.getIMEI(context) + "&usercode=" + GetInfo.getUserName(context));
                    startActivity(intent);
//                }
            }
        });
        //目标查询
        targetQueryBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if (!"4".equals(role)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), TargetQueryListActivity.class);
                    startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
            }
        });

        researchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if ("1".equals(role) || "0".equals(role)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ResearchActivity.class);
                    startActivity(intent);
//                } else {
//                    ToastUtil.toast(context, "无权限");
//                }
            }
        });
    }

}