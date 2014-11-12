package cn.koolcloud.pos.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.wd.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class DelVoucherRecordAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<JSONObject> recordDataList;
	private BaseController controller;
	
	private boolean hasMore;
	
	private final int TYPE_ORDER = 0;
	private final int TYPE_LOADMORE = 1;
	private final int TYPECOUNT = TYPE_LOADMORE + 1;
	
	public DelVoucherRecordAdapter(BaseController controller, List<JSONObject> recordDataList) {
		inflater = LayoutInflater.from(controller);
		this.recordDataList = recordDataList;
		this.controller = controller;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (null != recordDataList) {
			count = recordDataList.size();
		}
		if (0 != count && hasMore) {
			count ++;
		}
		return count;
	}
	
	@Override
	public int getItemViewType(int position) {
		return (position < recordDataList.size()) ? TYPE_ORDER : TYPE_LOADMORE;
	}

	@Override
	public int getViewTypeCount() {
		return TYPECOUNT;
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
	
	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		switch (type) {
		case TYPE_ORDER:
			RecordViewHolder recordViewHolder = null;
			if (null == convertView) {
				convertView = inflater.inflate(R.layout.lv_del_voucher_record, parent, false);
				recordViewHolder = new RecordViewHolder();
				recordViewHolder.tv_voucherName = (TextView) convertView.findViewById(R.id.lv_del_voucher_record_tv_voucherName);
				recordViewHolder.tv_delDate = (TextView) convertView.findViewById(R.id.lv_del_voucher_record_tv_delDate);
				recordViewHolder.tv_voucherId = (TextView) convertView.findViewById(R.id.lv_del_voucher_record_tv_voucherId);
				recordViewHolder.tv_voucherNum = (TextView) convertView.findViewById(R.id.lv_del_voucher_record_tv_voucherNum);
				recordViewHolder.tv_status = (TextView) convertView.findViewById(R.id.lv_del_voucher_record_tv_status);
				recordViewHolder.btn_operation = (Button) convertView.findViewById(R.id.lv_del_voucher_record_btn_operation);
				convertView.setTag(recordViewHolder);
			} else {
				recordViewHolder = (RecordViewHolder) convertView.getTag();
			}
			recordViewHolder.btn_operation.setTag((Integer)position);
			recordViewHolder.btn_operation.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final int index = ((Integer)v.getTag()).intValue();
					new AlertDialog.Builder(controller)
					.setMessage(controller.getString(R.string.del_voucher_record_notice_cancelConfirm_prefix)
							+ recordDataList.get(index).optString("consumePwd")
							+ controller.getString(R.string.del_voucher_record_notice_cancelConfirm_suffix))
					.setPositiveButton(controller.getString(R.string.alert_btn_positive), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							JSONObject msg = new JSONObject();
							JSONObject recordData = recordDataList.get(index);
							try {
								msg.put("index", index);
								msg.put("seqId", recordData.optString("seqId"));
								msg.put("consumePwd", recordData.optString("consumePwd"));
								msg.put("consumeQty", recordData.optString("consumeQty"));
								msg.put("brhId", recordData.optString("brhId"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
							controller.onCall("DelVoucherRecord.onCancel", msg);
						}
					})
					.setNegativeButton("取消", null)
					.show();
				}
			});
			JSONObject recordData = recordDataList.get(position);
			recordViewHolder.tv_voucherName.setText(recordData.optString("promotionName"));
			recordViewHolder.tv_delDate.setText(recordData.optString("delDate"));
			recordViewHolder.tv_voucherId.setText(recordData.optString("consumePwd"));
			recordViewHolder.tv_voucherNum.setText(recordData.optString("consumeQty"));
			recordViewHolder.tv_status.setText(recordData.optString("txn"));

			if(recordData.optBoolean("cancelEnable")){
				recordViewHolder.tv_status.setVisibility(View.GONE);
				recordViewHolder.btn_operation.setVisibility(View.VISIBLE);
			}else{
				recordViewHolder.btn_operation.setVisibility(View.GONE);
				recordViewHolder.tv_status.setVisibility(View.VISIBLE);
			}
			if (position%2 == 0) {
				convertView.setBackgroundResource(R.drawable.lv_row_bg_white);
			} else {
				convertView.setBackgroundResource(R.drawable.lv_row_bg_gray);
			}
			break;
			
		case TYPE_LOADMORE:
			LoadMoreHolder loadMoreHolder = null;
			if (null == convertView) {
				convertView = inflater.inflate(R.layout.lv_loadmore_row, parent, false);
				loadMoreHolder = new LoadMoreHolder();
				loadMoreHolder.notice = (TextView) convertView.findViewById(R.id.loadmore_tv_notice);
				convertView.setTag(loadMoreHolder);
			} else {
				loadMoreHolder = (LoadMoreHolder) convertView.getTag();
			}
			loadMoreHolder.notice.setText(controller.getString(R.string.lv_loadmore_row_lv_notice_show));
			break;
			
		default:
			break;
		}
		
		return convertView;
	}
	
	private class RecordViewHolder {
		public TextView tv_voucherName;
		public TextView tv_delDate;
		public TextView tv_voucherId;
		public TextView tv_voucherNum;
		public TextView tv_status;
		public Button btn_operation;
	}

	private final class LoadMoreHolder {
		public TextView notice;
	}
}
