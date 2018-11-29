package com.duoku.platform.demo.single;

import com.duoku.platform.single.DKPlatform;

import android.app.Application;
import android.content.Context;


public class DemoApplication extends Application {

    @Override
    protected void attachBaseContext(Context ctx){
        super.attachBaseContext(ctx);

        //移动MM支付初始化接口,仅在需要接入移动MM支付功能时调用
        DKPlatform.getInstance().invokeMMHelperInstall(this);
    }
    
    @Override
	public void onCreate() {
		super.onCreate();

		 //百度账号
		DKPlatform.getInstance().invokeBDInitApplication(this);
//
//        //移动基地初始化
//		DKPlatform.getInstance().invokeGBInit(this);
	}
}