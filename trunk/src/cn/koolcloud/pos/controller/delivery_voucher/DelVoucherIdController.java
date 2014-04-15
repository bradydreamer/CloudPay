package cn.koolcloud.pos.controller.delivery_voucher;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.pay.PayAccountController;

public class DelVoucherIdController extends PayAccountController {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}

		TextView tv_amount = (TextView) findViewById(R.id.pay_account_tv_amount);
		((ViewGroup) tv_amount.getParent()).setVisibility(View.GONE);
	}
}
