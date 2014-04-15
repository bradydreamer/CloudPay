package cn.koolcloud.printer.devices;

import cn.koolcloud.printer.control.PrinterControl;
import cn.koolcloud.printer.control.impl.PrinterControlImpl;
import cn.koolcloud.printer.control.impl.PrinterControlImplEx;


public class DeviceManager {
	private static DeviceManager self = null;

	public static synchronized DeviceManager getInstance() {
		if (self == null) {
			self = new DeviceManager();
		}
		return self;
	}

	public PrinterControl getPrinterControl() {
		return new PrinterControlImpl();
	}
	
	public PrinterControl getPrinterControlEx() {
		return new PrinterControlImplEx();
	}
}
