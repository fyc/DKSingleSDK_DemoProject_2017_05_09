package com.duoku.platform.demo.single;

import com.duoku.platform.single.DKPlatform;
import com.duoku.platform.single.setting.DKSingleSDKSettings;

import android.app.Activity;

public class BaseActivity extends Activity
{
	@Override
    protected void onResume(){
        super.onResume();
        DKPlatform.getInstance().resumeBaiduMobileStatistic(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        DKPlatform.getInstance().pauseBaiduMobileStatistic(this);
    }
}