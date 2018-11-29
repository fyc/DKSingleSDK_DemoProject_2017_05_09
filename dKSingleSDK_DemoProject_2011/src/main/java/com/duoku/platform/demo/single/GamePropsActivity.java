package com.duoku.platform.demo.single;


import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duoku.demo.single.data.DemoRecordData;
import com.duoku.demo.single.data.PropsData;
import com.duoku.demo.single.data.DemoDBDao;
import com.duoku.platform.demo.single.R;
import com.duoku.platform.single.DKPlatform;
import com.duoku.platform.single.DKPlatformSettings;
import com.duoku.platform.single.DkErrorCode;
import com.duoku.platform.single.DkProtocolKeys;
import com.duoku.platform.single.callback.IDKSDKCallBack;
import com.duoku.platform.single.item.DKCMGBData;
import com.duoku.platform.single.item.DKCMMMData;
import com.duoku.platform.single.item.DKCMMdoData;
import com.duoku.platform.single.item.DKCMYBKData;
import com.duoku.platform.single.item.GamePropsInfo;
import com.duoku.platform.single.util.SharedUtil;
import com.duoku.platform.single.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * function    : 模拟游戏的道具商店模块
 * description : 显示游戏内道具的已购买记录
 * @date       : 2014-07-09 
 * */
public class GamePropsActivity extends BaseActivity{

	private Activity activity;
	Handler mHandler = new Handler();
	
	private Button demoNonPropsFirst;
	private Button demoNonPropsSecond;
	
	private GridView demoGridView;
	private PropsAdapter demoAdapter;
	
	private EditText demoEditText;
	private Button demoBtnConfirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.game_props_activity);
		
		activity = this;
		
		initView();
	}
	
	private void initView(){
		demoNonPropsFirst = (Button) findViewById(R.id.demoNonPropsFirst);
		demoNonPropsSecond = (Button) findViewById(R.id.demoNonPropsSecond);
		
		demoGridView = (GridView) findViewById(R.id.demoGridView);
		demoGridView.setOnItemClickListener(new PropsItemClickListener());
		demoAdapter = new PropsAdapter(this);
		demoGridView.setAdapter(demoAdapter);
		
		demoEditText = (EditText) findViewById(R.id.demoInputMoney);
		demoBtnConfirm = (Button) findViewById(R.id.demoBtnConfirm);
		
		demoNonPropsFirst.setOnClickListener(new BtnClickListener());
		demoNonPropsSecond.setOnClickListener(new BtnClickListener());
		demoBtnConfirm.setOnClickListener(new BtnClickListener());
	}
	
	/**
	 * 按钮的点击事件监听器
	 * */
	class BtnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			try {
				switch(v.getId()){
					case R.id.demoNonPropsFirst:
						GamePropsInfo propsFirst = new GamePropsInfo("1063", "1", "少司命卡牌-超长名称测试啊测试啊测试啊测试啊测试啊测试啊测试啊","transparent");
						DKPlatform.getInstance().invokePayCenterActivity(activity, 
								propsFirst, 
								null,null, null,null,RechargeCallback);
						break;
					case R.id.demoNonPropsSecond :
						GamePropsInfo propsSecond = new GamePropsInfo("1064", "2", "大司命卡牌","");
						//DKCMMdoData mdoData = new DKCMMdoData("1064","2","YX,253436,1,e8ad,1822026","10658077016622","622001");
//						DKCMMMData mmData = new DKCMMMData("300008875644","90CF26C5D3F5428D42CBA4679E537426");
//						mmData.setPaycode("30000887564401");
						DKPlatform.getInstance().invokePayCenterActivity(activity, 
								propsSecond, 
								null,null, null,null,RechargeCallback);
						break;
					case R.id.demoBtnConfirm :
						String priceEdit = demoEditText.getText().toString();
						String price = StringUtils.inputPriceFormatter(priceEdit);
						if("0".equals(price)){
							return;
						}
//						if("".equals(price)){
//							return ;
//						}
						if("0.01".equals(price)){							
							GamePropsInfo propsThird = new GamePropsInfo("1065", price, 
									 "测试","");
							DKPlatform.getInstance().invokePayCenterActivity(activity, 
									propsThird, 
									null,null,null,null, RechargeCallback);
						}else{
							GamePropsInfo propsThird = new GamePropsInfo("1065", price, 
//									Integer.valueOf(price) * 10 + "血瓶",""
									(int)(Float.valueOf(price) * 10) + "血瓶",""
									);
							DKPlatform.getInstance().invokePayCenterActivity(activity, 
									propsThird, 
									null,null,null, null,RechargeCallback);
						}
						break;
					default:
						break;
				}
			} catch (NumberFormatException e){
				e.printStackTrace();
				Toast.makeText(GamePropsActivity.this,"金额非法", Toast.LENGTH_LONG).show();
			}
		}		
	}
	
	/**
	 * 消费品专栏的道具列表点击事件监听器
	 * */
	class PropsItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position,
				long id) {
			PropsData propsData = (PropsData) demoAdapter.getItem(position);
			String propsId = propsData.getmPropsId();
			int price = propsData.getmPrice();
			GamePropsInfo gamePropsInfo = new GamePropsInfo(propsId, String.valueOf(price), 
					getString(propsData.getmTitle()).split("-")[0],
					"");
			
			//棋牌类游戏此处设为true,静态变量只需设置一次即可，否则会导致mdo计费失败。
			//DKCMMdoData.setCardgameFlag(true);
			
			DKCMMdoData mdoData = null;
			mdoData = new DKCMMdoData();
			mdoData.setPropsId(propsId);
			mdoData.setPrice(String.valueOf(price));
			mdoData.setDest("10658077016622");
			mdoData.setChannelNum("622001");
			
			DKCMMMData mmData = new DKCMMMData("000000000000","0000000000000000");
//			//mm皮肤选择，共三种。如果不设置走默认皮肤
			mmData.setSKIN(DKCMMMData.SKIN_SYSTEM_ONE);
			mmData.setPaycode("00000000000000");
			
			DKCMGBData gbData = new DKCMGBData("006");
			DKCMYBKData ybkData= new DKCMYBKData("ca0c4ff0d57b3344285e9187", "");
			switch(position){
				case 0 :
//					mdoData = null;
//					mdoData.setCode("YX,252737,1,02d6,1822026");
//					mmData = null;
					ybkData.setPayCode("022114000");
					break;
				case 1 :
					mdoData.setCode("YX,252737,1,02d6,1822026");
//					mmData.setSKIN(DKCMMMData.SKIN_SYSTEM_TWO);
					ybkData.setPayCode("022115000");
					break;
				case 2 :
					mdoData.setPrice("0.01");
					mdoData.setCode("YX,252737,1,02d6,1822026");
//					mmData.setSKIN(DKCMMMData.SKIN_SYSTEM_THREE);
					ybkData.setPayCode("022116000");
					break;
				case 3 :
					mdoData.setCode("YX,253436,3,328e,1822026");
					ybkData.setPayCode("022117000");
					break;
				case 4 :
					mdoData.setCode("YX,253436,4,9bae,1822026");
					ybkData.setPayCode("022113000");
					break;
				case 5 :
					mdoData = null;
					ybkData = null;
					break;
				case 6 :
					ybkData = null;
					mdoData.setCode("YX,253436,5,dcbe,1822026");
					break;
				case 7 :
					ybkData = null;
					mdoData.setCode("YX,253436,6,79a9,1822026");
					break;
				case 8 :
					ybkData = null;
					mdoData.setCode("YX,253436,6,79a9,1822026");
					//直接调用支付宝
					DKPlatform.getInstance().invokePayCenterActivity(activity,gamePropsInfo,RechargeCallback,DKPlatformSettings.PAY_ALIPAY);
					break;
				case 9 :
					ybkData = null;
					mdoData.setCode("YX,253436,6,79a9,1822026");
					//直接调用微信
					DKPlatform.getInstance().invokePayCenterActivity(activity,gamePropsInfo,RechargeCallback,DKPlatformSettings.PAY_TENCENTMM);
					break;
				case 10 :
					ybkData = null;
					mdoData.setCode("YX,253436,6,79a9,1822026");
					//直接调用话费支付
					DKPlatform.getInstance().invokePayCenterActivity(activity,gamePropsInfo, mdoData, mmData, gbData,null,RechargeCallback,DKPlatformSettings.PAY_HUAFEI);
					break;
			}
			if(position<=7){
				DKPlatform.getInstance().invokePayCenterActivity(activity,gamePropsInfo, mdoData, mmData, gbData,null,RechargeCallback);
			} 
		}
	}
	
	/**
	 * 支付处理过程的结果回调函数
	 * */
	IDKSDKCallBack RechargeCallback = new IDKSDKCallBack(){
		@Override
		public void onResponse(String paramString) {
			// TODO Auto-generated method stub
			Log.d("GamePropsActivity", paramString);
			try {
				JSONObject jsonObject = new JSONObject(paramString);
				// 支付状态码
				int mStatusCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_STATUS_CODE);
				
				if(mStatusCode == DkErrorCode.BDG_RECHARGE_SUCCESS){
					// 返回支付成功的状态码，开发者可以在此处理相应的逻辑
					
					// 订单ID
					String mOrderId = null;
					// 订单状态
					String mOrderStatus = null;
					// 道具ID
					String mOrderProductId = null;
					// 道具实际支付的价格
					String mOrderPrice = null;
					// 支付通道
					String mOrderPayChannel = null;
					//道具原始价格，若微信、支付宝未配置打折该值为空，
					String mOrderPriceOriginal = null;
					
					if(jsonObject.has(DkProtocolKeys.BD_ORDER_ID)){						
						mOrderId = jsonObject.getString(DkProtocolKeys.BD_ORDER_ID);	
						SharedUtil.getInstance(GamePropsActivity.this).saveString("payment_orderid", mOrderId);
					}
					if(jsonObject.has(DkProtocolKeys.BD_ORDER_STATUS)){
						mOrderStatus = jsonObject.getString(DkProtocolKeys.BD_ORDER_STATUS);
					}
					if(jsonObject.has(DkProtocolKeys.BD_ORDER_PRODUCT_ID)){			
						mOrderProductId = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRODUCT_ID);
					}
					if(jsonObject.has(DkProtocolKeys.BD_ORDER_PAY_CHANNEL)){						
						mOrderPayChannel = jsonObject.getString(DkProtocolKeys.BD_ORDER_PAY_CHANNEL);
					}
					if(jsonObject.has(DkProtocolKeys.BD_ORDER_PRICE)){						
						mOrderPrice = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRICE);
					}
					//int mNum = Integer.valueOf(mOrderPrice) * 10;
					if(jsonObject.has(DkProtocolKeys.BD_ORDER_PAY_ORIGINAL)){						
						mOrderPriceOriginal = jsonObject.getString(DkProtocolKeys.BD_ORDER_PAY_ORIGINAL);
					}
					int mNum = 0;
					if( "".equals(mOrderPriceOriginal) ||null==mOrderPriceOriginal){
						mNum = Integer.valueOf(mOrderPrice) * 10;
					}else{
						mNum = Integer.valueOf(mOrderPriceOriginal) * 10;
					}
					String propsType = "1";
					Toast.makeText(activity, "道具购买成功!\n金额:"+mOrderPrice+"元", Toast.LENGTH_LONG).show();
					
					DemoRecordData data = new DemoRecordData(mOrderProductId, mOrderPrice, propsType, String.valueOf(mNum));
					DemoDBDao.getInstance(activity).updateRechargeRecord(data);
					
				}else if(mStatusCode == DkErrorCode.BDG_RECHARGE_USRERDATA_ERROR){
					
					Toast.makeText(activity, "用户透传数据不合法", Toast.LENGTH_LONG).show();
					
				}else if(mStatusCode == DkErrorCode.BDG_RECHARGE_ACTIVITY_CLOSED){
					
					// 返回玩家手动关闭支付中心的状态码，开发者可以在此处理相应的逻辑
					Toast.makeText(activity, "玩家关闭支付中心", Toast.LENGTH_LONG).show();
					
				}else if(mStatusCode == DkErrorCode.BDG_RECHARGE_FAIL){ 
					if(jsonObject.has(DkProtocolKeys.BD_ORDER_ID)){			
						SharedUtil.getInstance(GamePropsActivity.this).saveString("payment_orderid", jsonObject.getString(DkProtocolKeys.BD_ORDER_ID));
					}
					// 返回支付失败的状态码，开发者可以在此处理相应的逻辑
					Toast.makeText(activity, "购买失败", Toast.LENGTH_LONG).show();
					
				} else if(mStatusCode == DkErrorCode.BDG_RECHARGE_EXCEPTION){ 
					
					// 返回支付出现异常的状态码，开发者可以在此处理相应的逻辑
					Toast.makeText(activity, "购买出现异常", Toast.LENGTH_LONG).show();
					
				} else if(mStatusCode == DkErrorCode.BDG_RECHARGE_CANCEL){ 
					
					// 返回取消支付的状态码，开发者可以在此处理相应的逻辑
					Toast.makeText(activity, "玩家取消支付", Toast.LENGTH_LONG).show();
					
				} else {
					Toast.makeText(activity, "未知情况", Toast.LENGTH_LONG).show();
					
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};

	class PropsAdapter extends BaseAdapter {

		private int[] drawableIds = {R.drawable.daoju_1, R.drawable.daoju_2, R.drawable.daoju_3,
				R.drawable.daoju_4, R.drawable.daoju_5, R.drawable.daoju_6,
				R.drawable.daoju_7, R.drawable.daoju_8,R.drawable.daoju_7, R.drawable.daoju_8,R.drawable.daoju_7};

		private int[] txtIds = {R.string.demo_txt_daoju1, R.string.demo_txt_daoju2, R.string.demo_txt_daoju3,
				R.string.demo_txt_daoju4, R.string.demo_txt_daoju5, R.string.demo_txt_daoju6,
				R.string.demo_txt_daoju7, R.string.demo_txt_daoju8,R.string.demo_txt_daoju9, R.string.demo_txt_daoju10, R.string.demo_txt_daoju11};


		private String[] propsIds = {"1055", "1056", "1057", "1058", "1059", "1060", "1061", "1062", "1055", "1055", "1055"};

		private int[] money = {1, 2, 4, 6, 10, 15, 20, 30, 1, 1, 1};

		private List<PropsData> propsList = new ArrayList<PropsData>();

		private LayoutInflater inflater;

		public PropsAdapter(Context context){
			inflater = LayoutInflater.from(context);
			initData();
		}

		private void initData(){
			for(int index = 0; index < money.length; index++){
				PropsData data = new PropsData(propsIds[index], drawableIds[index], txtIds[index], money[index]);
				propsList.add(data);
			}
		}

		@Override
		public int getCount() {
			return propsList.size();
		}

		@Override
		public Object getItem(int position) {
			return propsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.game_props_item, null);
				holder = new Holder();
				holder.propsImg = (ImageView) convertView.findViewById(R.id.demoDaojuImg);
				holder.propsTxt = (TextView) convertView.findViewById(R.id.demoDaojuTxt);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.propsImg.setBackgroundResource(drawableIds[position]);
			holder.propsTxt.setText(txtIds[position]);

			return convertView;
		}

		class Holder{
			ImageView propsImg;
			TextView propsTxt;
		}
	}
}
