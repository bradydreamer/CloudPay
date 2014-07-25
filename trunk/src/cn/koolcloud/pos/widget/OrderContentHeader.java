package cn.koolcloud.pos.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.koolcloud.interfaces.OrderHeaderInterface;
import cn.koolcloud.pos.R;

public class OrderContentHeader extends LinearLayout implements OnClickListener {
	/** Column Titles */
	private TextView[] colTitles = new TextView[7];
	private OrderHeaderInterface orderHeaderInterface;
	private Drawable upArrow;
	private Drawable downArrow;
	private int[] resIds = { R.id.transTypeHeader, R.id.payTypeHeader, R.id.refNoHeader, R.id.transDateHeader, R.id.transTimeHeader, R.id.transAmountHeader, R.id.orderStatusHeader };

	/**
	 * Default Order
	 */
	public static final int SORT_DEFAULT = 0;
	
	/**
	 * ASC
	 */
	public static final int SORT_ASCENDING = 1;
	
	/**
	 * DESC
	 */
	public static final int SORT_DESCENDING = 2;
	
	private int lastSortCol = COL_1;
	private int sortType = SORT_DEFAULT;
	
	/**
	 * The First Column
	 */
	public static final int COL_1 = 0;
	
	/**
	 * The Second Column
	 */
	public static final int COL_2 = 1;
	
	/**
	 * The Third Column
	 */
	public static final int COL_3 = 2;
	
	/**
	 * The Forth Column
	 */
	public static final int COL_4 = 3;
	
	/**
	 * The Fifth Column
	 */
	public static final int COL_5 = 4;
	
	/**
	 * The Sixth Column
	 */
	public static final int COL_6 = 5;
	
	/**
	 * The Seventh Column
	 */
	public static final int COL_7 = 6;

	public OrderContentHeader(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.order_content_header, this);

		for (int i = 0; i < colTitles.length; i++) {
			colTitles[i] = (TextView) findViewById(resIds[i]);
			colTitles[i].setOnClickListener(this);
		}
		
		upArrow = context.getResources().getDrawable(R.drawable.form_navigation_bar_arrow_up);
		upArrow.setBounds(0, 0, upArrow.getMinimumWidth(), upArrow.getMinimumHeight());
		
		downArrow = context.getResources().getDrawable(R.drawable.form_navigation_bar_arrow_down);
		downArrow.setBounds(0, 0, downArrow.getMinimumWidth(), downArrow.getMinimumHeight());
	}

	public void setTitles(String[] titles) {
		for (int i = 0; i < colTitles.length; i++) {
			colTitles[i].setText(titles[i]);
		}
	}

	public void setOrderHeaderInterface(OrderHeaderInterface headerInterface) {
		orderHeaderInterface = headerInterface;
	}
	
	public void setSortType(int sortType, int lastSortCol) {
		this.sortType = sortType;
		this.lastSortCol = lastSortCol;
		if(sortType == SORT_DESCENDING)
			colTitles[lastSortCol].setCompoundDrawables(null, null, downArrow, null);
		else if(sortType == SORT_ASCENDING) {
			colTitles[lastSortCol].setCompoundDrawables(null, null, upArrow, null);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.transTypeHeader:
			changeSortType(COL_1);
			orderHeaderInterface.clicked(COL_1, sortType);
			break;
		case R.id.payTypeHeader:
			changeSortType(COL_2);
			orderHeaderInterface.clicked(COL_2, sortType);
			break;
		case R.id.refNoHeader:
			changeSortType(COL_3);
			orderHeaderInterface.clicked(COL_3, sortType);
			break;
		case R.id.transDateHeader:
			changeSortType(COL_4);
			orderHeaderInterface.clicked(COL_4, sortType);
			break;
		case R.id.transTimeHeader:
			changeSortType(COL_5);
			orderHeaderInterface.clicked(COL_5, sortType);
			break;
		case R.id.transAmountHeader:
			changeSortType(COL_6);
			orderHeaderInterface.clicked(COL_6, sortType);
			break;
		case R.id.orderStatusHeader:
			changeSortType(COL_7);
			orderHeaderInterface.clicked(COL_7, sortType);
			break;
		default:
			changeSortType(COL_1);
			orderHeaderInterface.clicked(COL_1, sortType);
		}
	}
	
	/**
	 * Change sort type
	 * @param currentCol: Current column
	 */
	private void changeSortType(int currentCol) {
		for(TextView tv : colTitles) {
			tv.setCompoundDrawables(null, null, null, null);
		}
		
		if(currentCol != lastSortCol) {
			sortType = SORT_DESCENDING;
			lastSortCol = currentCol;
			colTitles[currentCol].setCompoundDrawables(null, null, downArrow, null);
		} else {
			if(sortType == SORT_DEFAULT) {
				sortType = SORT_DESCENDING;
				colTitles[currentCol].setCompoundDrawables(null, null, downArrow, null);
			} else if(sortType == SORT_DESCENDING) {
				sortType = SORT_ASCENDING;
				colTitles[currentCol].setCompoundDrawables(null, null, upArrow, null);
			} else {
				sortType = SORT_DEFAULT;
			}
		}
	}

}
