package cn.koolcloud.pos.adapter;

import java.util.List;

import org.json.JSONObject;

import cn.koolcloud.pos.util.UtilForMoney;
import cn.koolcloud.pos.wd.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TradeStatisticsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<JSONObject> infoDataList;
	
	public TradeStatisticsAdapter(Context context, List<JSONObject> infoDataList) {
		inflater = LayoutInflater.from(context);
		this.infoDataList = infoDataList;
	}

	@Override
	public int getCount() {
		if (null != infoDataList) {
			return infoDataList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoViewHolder infoViewHolder = null;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.lv_trade_statistics_row, parent, false);
			infoViewHolder = new InfoViewHolder();
			infoViewHolder.tv_payType = (TextView) convertView.findViewById(R.id.lv_trade_statistics_tv_payType);
			infoViewHolder.tv_payCount = (TextView) convertView.findViewById(R.id.lv_trade_statistics_tv_payCount);
			infoViewHolder.tv_payAmount = (TextView) convertView.findViewById(R.id.lv_trade_statistics_tv_payAmount);
			convertView.setTag(infoViewHolder);
		} else {
			infoViewHolder = (InfoViewHolder) convertView.getTag();
		}
		
		JSONObject infoData = infoDataList.get(position);
		infoViewHolder.tv_payType.setText(infoData.optString("payType"));
		infoViewHolder.tv_payCount.setText(infoData.optString("totalCount"));
		String totalAmount = UtilForMoney.fen2yuan(infoData.optString("totalAmount"));
		infoViewHolder.tv_payAmount.setText(totalAmount);
		
		return convertView;
	}
	
	private class InfoViewHolder {
		public TextView tv_payType;
		public TextView tv_payCount;
		public TextView tv_payAmount;
	}

}
