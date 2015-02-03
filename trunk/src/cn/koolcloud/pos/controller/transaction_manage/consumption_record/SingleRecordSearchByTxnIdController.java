package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import cn.koolcloud.pos.R;

public class SingleRecordSearchByTxnIdController extends SingleRecordSearchController {

	private boolean removeJSTag = true;
	private RelativeLayout method_rl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		method_rl = (RelativeLayout)findViewById(R.id.pay_method_view);
		method_rl.setVisibility(View.GONE);
	}

	@Override
	public void onClickBtnOK(View view) {
		String id = et_id.getText().toString();
		if (id.isEmpty()) {
			return;
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put("txnId", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("TransactionManageIndex.gotoSingleRecord", msg);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_single_record_search_txnid_controller);
	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		removeJSTag = tag;

	}

	@Override
	protected boolean getRemoveJSTag() {
		// TODO Auto-generated method stub
		return removeJSTag;
	}

}
