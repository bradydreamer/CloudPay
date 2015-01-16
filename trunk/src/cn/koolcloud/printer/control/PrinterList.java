package cn.koolcloud.printer.control;

import java.util.HashMap;
import java.util.Map;

public class PrinterList {
	public static Map<String, BasePrinterInterface> mPrinterMap = new HashMap<String, BasePrinterInterface>();
	static {
		SprtPrinter mSprtPrinter = new SprtPrinter();
		mPrinterMap.put(mSprtPrinter.getType(), mSprtPrinter);

	}

}
