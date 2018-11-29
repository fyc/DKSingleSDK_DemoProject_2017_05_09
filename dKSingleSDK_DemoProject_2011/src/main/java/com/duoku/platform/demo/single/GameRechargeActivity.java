package com.duoku.platform.demo.single;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.duoku.demo.single.data.DemoRecordData;
import com.duoku.demo.single.data.DemoDBDao;
import com.duoku.platform.demo.single.R;

/**
 * @desc 显示玩家已经购买过的所有商品
 * */
public class GameRechargeActivity extends BaseActivity{

	private Activity activity;
	Handler mHandler = new Handler();
	
	private TextView demoNonPropsFirst;
	private TextView demoNonPropsSecond;
	
	private ListView demoListView;
	private GameRechargeRecordAdapter demoAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.game_record_activity);
		
		activity = this;
		initView();
	}

	private void initView(){
		demoNonPropsFirst = (TextView) findViewById(R.id.demoNonPropsFirst);
		demoNonPropsSecond = (TextView) findViewById(R.id.demoNonPropsSecond);
		
		if(DemoDBDao.getInstance(activity).queryNonRechargeRecord("1063")){
			demoNonPropsFirst.setVisibility(View.VISIBLE);
		} else {
			demoNonPropsFirst.setVisibility(View.GONE);
		}
		
		if(DemoDBDao.getInstance(activity).queryNonRechargeRecord("1064")){
			demoNonPropsSecond.setVisibility(View.VISIBLE);
		} else {
			demoNonPropsSecond.setVisibility(View.GONE);
		}
		
		demoListView = (ListView) findViewById(R.id.demoListView);
		demoAdapter = new GameRechargeRecordAdapter(this);
		demoListView.setAdapter(demoAdapter);
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				final List<DemoRecordData> dataList = DemoDBDao.getInstance(activity).getAllRechargeRecords();
				activity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						if(demoAdapter != null)
							demoAdapter.updateData(dataList);
					}					
				});
			}			
		}).start();
	}

	class GameRechargeRecordAdapter extends BaseAdapter {

		private int[] drawableIds = {R.drawable.daoju_1, R.drawable.daoju_2, R.drawable.daoju_3,
				R.drawable.daoju_4, R.drawable.daoju_5, R.drawable.daoju_6,
				R.drawable.daoju_7, R.drawable.daoju_8};
		private int[] txtIds = {R.string.demo_txt_daoju1, R.string.demo_txt_daoju2, R.string.demo_txt_daoju3,
				R.string.demo_txt_daoju4, R.string.demo_txt_daoju5, R.string.demo_txt_daoju6,
				R.string.demo_txt_daoju7, R.string.demo_txt_daoju8};
		private String[] propsIds = {"1055", "1056", "1057", "1058", "1059", "1060", "1061", "1062"};

		private List<DemoRecordData> propsList = new ArrayList<DemoRecordData>();

		private LayoutInflater inflater;

		public GameRechargeRecordAdapter(Context context){
			inflater = LayoutInflater.from(context);
		}

		public void updateData(List<DemoRecordData> mPropsList){
			propsList.clear();
			propsList.addAll(mPropsList);
			notifyDataSetChanged();
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
				convertView = inflater.inflate(R.layout.game_recharge_item, null);
				holder = new Holder();
				holder.propsImg = (ImageView) convertView.findViewById(R.id.demoDaojuImg);
				holder.propsTxt = (TextView) convertView.findViewById(R.id.demoDaojuTxt);
				holder.propsNum = (TextView) convertView.findViewById(R.id.demoDaojuNum);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			DemoRecordData data = propsList.get(position);
			int index = 0;
			String propsId = data.getPropsId();
			for(int i = 0; i < propsIds.length; i++){
				if(propsId.equals(propsIds[i])){
					index = i;
					break;
				}
			}
			holder.propsImg.setBackgroundResource(drawableIds[index]);
			holder.propsTxt.setText(txtIds[index]);
			holder.propsNum.setText(data.getRecordNum());

			return convertView;
		}

		class Holder{
			ImageView propsImg;
			TextView propsTxt;
			TextView propsNum;
		}
	}
}