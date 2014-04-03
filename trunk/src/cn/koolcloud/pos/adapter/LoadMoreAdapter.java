package cn.koolcloud.pos.adapter;

import java.util.List;

import org.json.JSONObject;

import cn.koolcloud.postest.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.UtilForMoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class LoadMoreAdapter extends BaseAdapter {

	protected Context context;
	protected LayoutInflater inflater;
	protected List<JSONObject> list;
	protected boolean hasMore;
	
	private final int TYPE_ORDER = 0;
	private final int TYPE_LOADMORE = 1;
	private final int TYPECOUNT = TYPE_LOADMORE + 1;
	
	public LoadMoreAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(this.context);
	}
	
	public void setList(List<JSONObject> customlist) {
		this.list = customlist;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (null != this.list) {
			count = this.list.size();
		}
		if (0 != count && hasMore) {
			count ++;
		}
		return count;
	}
	
	@Override
	public int getItemViewType(int position) {
		return (position < this.list.size()) ? TYPE_ORDER : TYPE_LOADMORE;
	}

	@Override
	public int getViewTypeCount() {
		return TYPECOUNT;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
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
			convertView = this.getNormalView(position, convertView, parent);
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
			loadMoreHolder.notice.setText(this.context.getString(R.string.lv_loadmore_row_lv_notice_show));
			break;
			
		default:
			break;
		}
		
		return convertView;
	}
	
	abstract protected View getNormalView(int position, View convertView, ViewGroup parent);

	
	private final class LoadMoreHolder {
		public TextView notice;
	}

}
