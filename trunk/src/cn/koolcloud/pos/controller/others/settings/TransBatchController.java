package cn.koolcloud.pos.controller.others.settings;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

public class TransBatchController extends BaseController {

	private boolean removeJSTag = true;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		onCall("TransBatch.gotoTransBatch", null);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_set_trans_batch_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_set_trans_batch_controller);
	}

	@Override
	protected String getControllerName() {
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_TransBatch);
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
