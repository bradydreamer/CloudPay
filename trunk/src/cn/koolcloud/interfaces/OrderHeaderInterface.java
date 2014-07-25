package cn.koolcloud.interfaces;

public interface OrderHeaderInterface {
	
	/** 
	 * List header clicking events handler
	 * @param col: column number
	 * @param sortType: Sort Type
	 */
	void clicked(int col, int sortType);
}
