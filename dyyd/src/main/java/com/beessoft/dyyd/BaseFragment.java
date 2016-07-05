package com.beessoft.dyyd;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import com.beessoft.dyyd.utils.GetInfo;

public class BaseFragment extends Fragment {

    public Context context;
    public String mac;
    public String username;
    public String ifSf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mac = GetInfo.getIMEI(context);
        username = GetInfo.getUserName(context);
        ifSf = GetInfo.getIfSf(context) ? "0" : "1";
    }
}
