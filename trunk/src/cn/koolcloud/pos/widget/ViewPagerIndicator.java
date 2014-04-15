package cn.koolcloud.pos.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.koolcloud.pos.R;

public class ViewPagerIndicator extends LinearLayout implements
		OnPageChangeListener {
	private int _pageSize;
	private int _currentPage;

	private Context context;
	private ViewPager pager;
	private OnPageChangeListener onPageChangeListener;
	private LinearLayout itemContainer;
	private List<ImageView> items;
	private OnClickListener itemClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			pager.setCurrentItem(position);
		}
	};

	public ViewPagerIndicator(Context context) {
		super(context);
		this.context = context;
		setup();
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setup();
	}

	private void setup() {
		itemContainer = this;
		items = new ArrayList<ImageView>();
		this.setPageSize(2);
		this.setCurrentPage(0);
	}

	public void setPageSize(int size) {
		itemContainer.removeAllViews();
		items.removeAll(items);

		// now create the new items.
		for (int i = 0; i < size; i++) {
			ImageView item = new ImageView(context);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					10, 10);
			layoutParams.setMargins(10, 0, 10, 0);
			item.setLayoutParams(layoutParams);
			item.setTag(i);
			item.setOnClickListener(itemClickListener);
			items.add(item);
			itemContainer.addView(item);
		}

		_pageSize = size;
	}

	public int getPageSize() {
		return _pageSize;
	}

	public void setCurrentPage(int position) {
		int numberOfItems = _pageSize;

		for (int i = 0; i < numberOfItems; i++) {
			ImageView item = items.get(i);
			if (item != null) {
				if (i == position) {
					item.setImageResource(R.drawable.shape_page_indicator_f);
				} else {
					item.setImageResource(R.drawable.shape_page_indicator_n);
				}
			}
		}
		_currentPage = position;
	}

	public int getCurrentPage() {
		return _currentPage;
	}

	public ViewPager getViewPager() {
		return pager;
	}

	public void setViewPager(ViewPager pager) {
		this.pager = pager;
		this.pager.setOnPageChangeListener(this);
	}

	public OnPageChangeListener getOnPageChangeListener() {
		return onPageChangeListener;
	}

	public void setOnPageChangeListener(
			OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (this.onPageChangeListener != null) {
			this.onPageChangeListener.onPageScrollStateChanged(state);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (this.onPageChangeListener != null) {
			this.onPageChangeListener.onPageScrolled(position, positionOffset,
					positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		setCurrentPage(position);
		if (this.onPageChangeListener != null) {
			this.onPageChangeListener.onPageSelected(position);
		}
	}
}
