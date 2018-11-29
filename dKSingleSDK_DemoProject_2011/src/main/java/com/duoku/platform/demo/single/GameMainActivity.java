package com.duoku.platform.demo.single;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

//import cn.cmgame.billing.api.GameInterface;

import com.duoku.demo.single.data.DemoRecordData;
import com.duoku.demo.single.data.DemoDBDao;
import com.duoku.platform.demo.single.R;
import com.duoku.platform.single.DKPlatform;
import com.duoku.platform.single.DKPlatformSettings;
import com.duoku.platform.single.DkErrorCode;
import com.duoku.platform.single.DkProtocolKeys;
import com.duoku.platform.single.callback.IDKSDKCallBack;
import com.duoku.platform.single.item.DKCMGBData;
import com.duoku.platform.single.item.DKCMMMData;
import com.duoku.platform.single.item.DKOrderPayChannelData;
import com.duoku.platform.single.item.DKOrderStatus;
import com.duoku.platform.single.util.SharedUtil;


/**
 * @desc 模拟游戏的入口Activity,也可以是游戏的主界面
 * */
public class GameMainActivity extends BaseActivity{

	private Activity activity;
	private Button btnRecharge;
	private Button btnStartGame;
	private Button btnMyProps;
	private Button btnExitGame;
	private Button btnMyPause;
	private Button btnMyLogin;
	private Button btnMyModify;
	private Button btnMySwitchAccount;
	
	private IDKSDKCallBack loginlistener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.game_main_activity);
		activity = this;

		initView();
		initSDK();
	}

	private void initView(){
		btnRecharge = (Button) findViewById(R.id.demoBtnRecharge);
		btnStartGame = (Button) findViewById(R.id.demoBtnStartGame);
		btnMyProps = (Button) findViewById(R.id.demoBtnMyProps);
		btnExitGame = (Button) findViewById(R.id.demoBtnExitGame);
		btnMyPause = (Button) findViewById(R.id.demoBtnMyPause);
		btnMyLogin = (Button) findViewById(R.id.demoBtnLogin);
		btnMyModify = (Button) findViewById(R.id.demoBtnModfiyPwd);
		btnMySwitchAccount = (Button) findViewById(R.id.demoBtnSwitch);

		btnRecharge.setOnClickListener(new BtnClickListener());
		btnStartGame.setOnClickListener(new BtnClickListener());
		btnMyProps.setOnClickListener(new BtnClickListener());
		btnExitGame.setOnClickListener(new BtnClickListener());
		btnMyPause.setOnClickListener(new BtnClickListener());
		btnMyLogin.setOnClickListener(new BtnClickListener());
		btnMyModify.setOnClickListener(new BtnClickListener());
		btnMySwitchAccount.setOnClickListener(new BtnClickListener());
	}
	
	// 初始化SDK
	private void initSDK(){
		//回调函数
		IDKSDKCallBack initcompletelistener = new IDKSDKCallBack(){
			@Override
			public void onResponse(String paramString) {
				Log.d("GameMainActivity", paramString);
				try {
					JSONObject jsonObject = new JSONObject(paramString);
					// 返回的操作状态码
					int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);
					
					//初始化完成
					if(mFunctionCode == DkErrorCode.BDG_CROSSRECOMMEND_INIT_FINSIH){
						initLogin();
						initAds();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		//参数为测试数据，接入时请填入你真实数据
		DKCMMMData mmData = new DKCMMMData("000000000000","0000000000000000");
		DKCMGBData gbData = new DKCMGBData();
		//初始化函数
		DKPlatform.getInstance().init(this, false, DKPlatformSettings.SdkMode.SDK_PAY,mmData,gbData,initcompletelistener);
	}
	
	//登陆初始化,接入百度账号功能的需要调用此接口
	private void initLogin(){
		//回调函数
		 loginlistener = new IDKSDKCallBack(){
			@Override
			public void onResponse(String paramString) {
				try {
					JSONObject jsonObject = new JSONObject(paramString);
					// 返回的操作状态码
					int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);
					// 返回的百度uid，供cp绑定使用
					String bduid = jsonObject.getString(DkProtocolKeys.BD_UID);
					
					//登陆成功
					if(mFunctionCode == DkErrorCode.DK_ACCOUNT_LOGIN_SUCCESS){
						//隐藏登陆按钮，显示修改密码和切换账号按钮
						btnMyLogin.setVisibility(View.GONE);
						btnMyModify.setVisibility(View.VISIBLE);
						btnMySwitchAccount.setVisibility(View.VISIBLE);
					//登陆失败
					}else if(mFunctionCode == DkErrorCode.DK_ACCOUNT_LOGIN_FAIL){
						//显示登陆按钮，隐藏修改密码和切换账号按钮
						btnMyLogin.setVisibility(View.VISIBLE);
						btnMyModify.setVisibility(View.GONE);
						btnMySwitchAccount.setVisibility(View.GONE);
					//快速注册成功
					}else if(mFunctionCode == DkErrorCode.DK_ACCOUNT_QUICK_REG_SUCCESS){
						//快速试玩登陆成功，都需要隐藏
						btnMyLogin.setVisibility(View.GONE);
						btnMyModify.setVisibility(View.VISIBLE);
						btnMySwitchAccount.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		DKPlatform.getInstance().invokeBDInit(this, loginlistener);
	}

	/**
	 * 品宣接口初始化
	 */
	private void initAds(){
		DKPlatform.getInstance().bdgameInit(this, new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				Log.d("GameMainActivity","bggameInit success");
			}
		});
	}
	
	private void callSupplement(){
		//回调函数
		IDKSDKCallBack supplementlistener = new IDKSDKCallBack(){
			@Override
			public void onResponse(String paramString) {
				Log.d("GameMainActivity", paramString);
				try {
					JSONObject jsonObject = new JSONObject(paramString);
					// 返回的操作状态码
					int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);
					
					if(mFunctionCode == DkErrorCode.BDG_REORDER_CHECK){
						// 订单ID 返回补单成功的状态码，可以补发道具
						String mOrderId = jsonObject.getString(DkProtocolKeys.BD_ORDER_ID);		
						// 道具ID
						String mOrderProductId = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRODUCT_ID);
						// 道具价格
						String mOrderPrice = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRICE);
						int mNum = Integer.valueOf(mOrderPrice) * 10;
						String propsType = "1";
						DemoRecordData data = new DemoRecordData(mOrderProductId, mOrderPrice, propsType, String.valueOf(mNum));
						DemoDBDao.getInstance(activity).updateRechargeRecord(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		DKPlatform.getInstance().invokeSupplementDKOrderStatus(this,supplementlistener);
	}
	
	/**
	 * 按钮的点击事件监听器
	 * */
	class BtnClickListener implements View.OnClickListener{
		Intent intent ;
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.demoBtnRecharge:
					Intent intent = new Intent(activity, GamePropsActivity.class);
					startActivity(intent);
					break;
					
				case R.id.demoBtnStartGame :
					String orderId = SharedUtil.getInstance(GameMainActivity.this).getString("payment_orderid");
					if(!"".equals(orderId)){
						DialogBuilder.showGetOrderidDialog(GameMainActivity.this, orderId);
					}
					Toast.makeText(activity, R.string.demo_txt_game_not_open, Toast.LENGTH_SHORT).show();
					break;
					
				case R.id.demoBtnMyProps :
					Intent intentMyProps = new Intent(activity, GameRechargeActivity.class);
					startActivity(intentMyProps);
					break;
				
				case R.id.demoBtnMyPause :
					DKPlatform.getInstance().bdgamePause(activity, new IDKSDKCallBack() {
						@Override
						public void onResponse(String paramString) {
							Log.d("GameMainActivity","bggamePause success");
						}
					});
					break;
					
				case R.id.demoBtnExitGame :
					DKPlatform.getInstance().bdgameExit(activity, new IDKSDKCallBack() {
						@Override
						public void onResponse(String paramString) {
							exitWithMigu();
//							exitGameDirectly();
						}
					});
					
					break;	
				case R.id.demoBtnLogin:
					DKPlatform.getInstance().invokeBDLogin(GameMainActivity.this, loginlistener);
					break;
				case R.id.demoBtnSwitch:	
					btnMyLogin.setVisibility(View.VISIBLE);
					btnMyModify.setVisibility(View.GONE);
					btnMySwitchAccount.setVisibility(View.GONE);
					DKPlatform.getInstance().invokeBDLogin(GameMainActivity.this, loginlistener);
					break;
				case R.id.demoBtnModfiyPwd:
					
					DKPlatform.getInstance().invokeBDModifyPwd(GameMainActivity.this, loginlistener);
					break;
				default:
					break;
			}
		}		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				DKPlatform.getInstance().bdgameExit(this, new IDKSDKCallBack() {
					@Override
					public void onResponse(String paramString) {
						exitWithMigu();
//						exitGameDirectly();
					}
				});
				break;
			default:
				break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 咪咕版本调用
	 */
	private void exitWithMigu() {
		//咪咕基地退出接口 start
//		try {
//			GameInterface.exit(activity, new GameInterface.GameExitCallback() {
//				@Override
//				public void onConfirmExit() {
//					exitGameDirectly();
//				}
//				@Override
//				public void onCancelExit() {
//				}
//			});
//		}catch (Exception e){
//			e.printStackTrace();
//		}
		//咪咕基地退出接口 end
	}
	
	/**
	 * 非咪咕版本调用
	 */
	private void exitGameDirectly() {
		activity.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}


	static class DialogBuilder {
		public static void showGetOrderidDialog(final Context context, final String orerId){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			AlertDialog dialog =
					builder.setCancelable(false).setTitle("获取订单号").setMessage(orerId).setPositiveButton(
							"关闭", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).setNegativeButton("查询订单状态", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							DKPlatform.getInstance().invokeQueryDKOrderStatus(context, orerId, DKOrderPayChannelData.DK_ORDER_CHANNEL_YEEPAY.getValue(),new IDKSDKCallBack() {
								@Override
								public void onResponse(String paramString) {
									// TODO Auto-generated method stub
									try {
										JSONObject jsonObject = new JSONObject(paramString);
										// 返回的操作状态码
										int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);
										if(DkErrorCode.BDG_QUERY_ORDER_STATUS_SUCCESS == mFunctionCode){
											StringBuilder sb = new StringBuilder("查询成功\n");
											int orderStatus = jsonObject.getInt(DkProtocolKeys.BD_ORDER_STATUS);
											if(DKOrderStatus.DK_ORDER_STATUS_DEALING.getValue() == orderStatus){
												sb.append("订单处理中");
											}else
											if(DKOrderStatus.DK_ORDER_STATUS_FAIL.getValue() == orderStatus){
												sb.append("订单交易失败");
											}else
											if(DKOrderStatus.DK_ORDER_STATUS_SMS_SEND.getValue() == orderStatus){
												sb.append("短信已发出");
											}else
											if(DKOrderStatus.DK_ORDER_STATUS_SUCCESS.getValue() == orderStatus){
												sb.append("订单交易成功");
											}else
											if(DKOrderStatus.DK_ORDER_STATUS_UNKNOWN.getValue() == orderStatus){
												sb.append("订单状态未知");
											}
											Toast.makeText(context,sb.toString(),Toast.LENGTH_LONG).show();
										}else
										if(DkErrorCode.BDG_QUERY_ORDER_STATUS_FAIL == mFunctionCode){
											if(!isNetworkConnected(context)){
												Toast.makeText(context,"无网络连接",Toast.LENGTH_LONG).show();
											}else{
												Toast.makeText(context,"查询失败",Toast.LENGTH_LONG).show();
											}
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
						}
					}).create();
			dialog.show();
		}

		public static boolean isNetworkConnected(Context context)
		{
			ConnectivityManager connectivitymanager = (ConnectivityManager)context.getApplicationContext().getSystemService("connectivity");
			NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
			if (networkinfo != null)
				return networkinfo.isConnectedOrConnecting();
			else
				return false;
		}
	}
}
