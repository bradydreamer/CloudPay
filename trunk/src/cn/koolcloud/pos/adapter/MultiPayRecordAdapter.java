package cn.koolcloud.pos.adapter;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.others.settings.PaymentMechanismController;
import cn.koolcloud.pos.database.CacheDB;

public class MultiPayRecordAdapter extends LoadMoreAdapter {
    private Context ctx;
	public MultiPayRecordAdapter(Context context) {
		super(context);
        ctx = context;
	}

	@Override
	protected View getNormalView(int position, View convertView,
			ViewGroup parent) {
		RecordViewHolder recordViewHolder = null;
		// var order = {
		// "ref" : ConsumptionData.dataForPayment.rrn,
		// "result" : ConsumptionData.dataForPayment.result,
		// "orderStateDesc" : ConsumptionData.dataForPayment.result == 0 ? "完成"
		// : "失败",
		// "payTypeDesc" : "" + ConsumptionData.dataForPayment.paymentName,
		// "transAmount" : ConsumptionData.dataForPayment.transAmount,
		// "showAmount" :
		// util.formatAmountStr(ConsumptionData.dataForPayment.transAmount),
		// };

		if (null == convertView) {
			convertView = inflater.inflate(R.layout.lv_multipay_record_row,
					parent, false);
			recordViewHolder = new RecordViewHolder();
			recordViewHolder.tv_no = (TextView) convertView
					.findViewById(R.id.lv_multipay_record_tv_no);
			recordViewHolder.tv_transType = (TextView) convertView
					.findViewById(R.id.lv_multipay_record_tv_transType);
			recordViewHolder.tv_rrn = (TextView) convertView
					.findViewById(R.id.lv_multipay_record_tv_rrn);
			recordViewHolder.tv_transDate = (TextView) convertView
					.findViewById(R.id.lv_multipay_record_tv_transDate);
			recordViewHolder.tv_transTime = (TextView) convertView
					.findViewById(R.id.lv_multipay_record_tv_transTime);
			recordViewHolder.tv_transAmount = (TextView) convertView
					.findViewById(R.id.lv_multipay_record_tv_transAmount);
			recordViewHolder.tv_orderStatus = (TextView) convertView
					.findViewById(R.id.lv_multipay_record_tv_orderStatus);
			convertView.setTag(recordViewHolder);
		} else {
			recordViewHolder = (RecordViewHolder) convertView.getTag();
		}

		JSONObject recordData = this.list.get(position);
		recordViewHolder.tv_no.setText("" + (position + 1));
        String paymentId = recordData.optString("paymentId");
        String paymentName = recordData.optString("paymentIdDesc");
        if (!TextUtils.isEmpty(paymentName) && !paymentName.equals("undefined")) {
            recordViewHolder.tv_transType.setText(recordData.optString("paymentIdDesc"));
        } else {
            String paymentDesc = CacheDB.getInstance(ctx).getPaymentByPaymentId(paymentId).getPaymentName();
            recordViewHolder.tv_transType.setText(paymentDesc);
        }
		recordViewHolder.tv_rrn.setText(recordData.optString("refNo"));

        //fix bug SMTPS-262 by Teddy --start on 8th January
        String transTime = recordData.optString("transTime");
        if (!TextUtils.isEmpty(transTime)) {

            recordViewHolder.tv_transDate.setText(recordData.optString("transTime").substring(0,8));
            recordViewHolder.tv_transTime.setText(
                    recordData.optString("transTime").substring(8,10) + ":" +
                            recordData.optString("transTime").substring(10,12) + ":" +
                            recordData.optString("transTime").substring(12,14));
        }
        //fix bug SMTPS-262 by Teddy --end on 8th January

        recordViewHolder.tv_transAmount.setText(formatAmountStr(recordData
				.optString("transAmount")));
        recordViewHolder.tv_orderStatus.setText(recordData
				.optString("orderStateDesc"));

		// convertView.setBackgroundResource(R.drawable.lv_row_bg_white);

		return convertView;
	}

	private class RecordViewHolder {
		public TextView tv_no;
		public TextView tv_rrn;
		public TextView tv_transType;
		public TextView tv_transAmount;
		public TextView tv_orderStatus;
		public TextView tv_transDate;
		public TextView tv_transTime;
	}

	private String formatAmountStr(String amount) {
		String floatAmount = null;
		int length = amount.length();
		if (length == 0) {
			floatAmount = "";
		} else if (length == 1) {
			floatAmount = "0.0" + amount;
		} else if (length == 2) {
			floatAmount = "0." + amount;
		} else {
			floatAmount = amount.substring(0, length - 2) + "."
					+ amount.substring(length - 2, length);
		}
		return floatAmount;
	}

}
