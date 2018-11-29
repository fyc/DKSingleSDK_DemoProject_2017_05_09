package com.duoku.platform.demo.single;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.duoku.platform.single.DKPlatform;

import cn.cmgame.billing.api.GameOpenActivity;

/**
 * Created by WuYuanhong on 17/3/1.
 */
public class MiguActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //移动基地初始化
        DKPlatform.getInstance().invokeGBInit(getApplication());
        new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				    Intent intent = new Intent();
			        intent.setClass(MiguActivity.this, GameOpenActivity.class);
			        startActivity(intent);
			        finish();
			}
		},3000);
     
    }
}