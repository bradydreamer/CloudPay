package cn.koolcloud.pos.util;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;

public class UtilForGraghic {

	/**
	 * setBackgroundDrawable(Drawable background) was deprecated in API level 16
	 */
	public static void setBackground(View view, Drawable drawable){
		view.setBackgroundDrawable(drawable);
	}
	
	/**
	 * display.getWidth() and display.getHeight() were deprecated in API level 13.
	 */
	public static Point getDisplayPoint(Display display) {
		Point point = new Point();
		point.set(display.getWidth(), display.getHeight());
		return point;
	}
}
