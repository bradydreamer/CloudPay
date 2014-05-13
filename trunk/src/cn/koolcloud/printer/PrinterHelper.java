package cn.koolcloud.printer;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import cn.koolcloud.constant.Constant;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.parameter.OldTrans;
import cn.koolcloud.pos.R;
import cn.koolcloud.printer.command.FormatSettingCommand;
import cn.koolcloud.printer.control.Align;
import cn.koolcloud.printer.control.Depth;
import cn.koolcloud.printer.control.FontType;
import cn.koolcloud.printer.control.PrinterControl;
import cn.koolcloud.printer.devices.DeviceManager;
import cn.koolcloud.printer.exception.AccessException;
import cn.koolcloud.printer.util.QRcodeBitmap;
import cn.koolcloud.printer.util.Utils;
import cn.koolcloud.util.AppUtil;
import cn.koolcloud.util.StringUtil;

public class PrinterHelper implements Constant {
	// private ClientEngine client

	private static PrinterHelper _instance;
	private static Context ctx;

	private PrinterHelper() {
	}

	synchronized public static PrinterHelper getInstance(Context context) {
		if (null == _instance) {
			_instance = new PrinterHelper();
			ctx = context;
		}
		return _instance;
	}

	/**
	 * 打印签购单
	 * 
	 * @throws PrinterException
	 */
	synchronized public void printReceipt(OldTrans trans)
			throws PrinterException {
		try {
			PrinterInterface.open();
			PrinterInterface.set(1);
			PrinterInterface.begin();
			PrinterInterface.end();
//			printerWrite(PrinterCommand.init());
//			printerWrite(PrinterCommand.setHeatTime(180));
			
			PrinterInterface.begin();
			for (int i = 0; i < 2; i++) {
				printerWrite(PrinterCommand.setFontBold(1));
				printerWrite(PrinterCommand.setAlignMode(1));
				printerWrite(PrinterCommand.setFontEnlarge(0x01));
				printerWrite(("银行卡签购单").getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(PrinterCommand.setFontBold(0));
				printerWrite(PrinterCommand.setAlignMode(0));
				printerWrite(PrinterCommand.setFontEnlarge(0));
				
				if (i == 0) {
					printerWrite(("商户存根").getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (i == 1) {
					printerWrite(("持卡人存根").getBytes("GB2312"));

					printerWrite(PrinterCommand.linefeed());
				} else if (i == 2) {
					printerWrite(("银行存根").getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}
				
				printerWrite("--------------------------------".getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				/*printerWrite(trans.getOldMID().getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				if (i == 0) {
					printerWrite(("商户存根           MERCHANT COPY").getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (i == 1) {
					printerWrite(("持卡人存根       CARDHOLDER COPY").getBytes("GB2312"));

					printerWrite(PrinterCommand.linefeed());
				} else if (i == 2) {
					printerWrite(("银行存根               BANK COPY").getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}
				printerWrite("--------------------------------".getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());*/

				printerWrite(("商户名:" + trans.getOldMertName()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("酷云客户号:" + trans.getKoolCloudMID())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("酷云设备号:" + trans.getKoolCloudTID())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("收单商户号:" + trans.getOldMID()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("收单终端号:" + trans.getOldTID()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				
				printerWrite(("参考号:" + trans.getOldRrn()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				
				printerWrite(("日期/时间:" + " " + ("" + trans.getOldTransYear())
						+ "/" + ("" + trans.getOldTransDate().substring(0, 2))
						+ "/" + ("" + trans.getOldTransDate().substring(2, 4))
						+ " " + ("" + trans.getOldTransTime().substring(0, 2))
						+ ":" + ("" + trans.getOldTransTime().substring(2, 4))
						+ ":" + ("" + trans.getOldTransTime().substring(4, 6))).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				
//				printerWrite("交易类型(TRANS TYPE):".getBytes("GB2312"));
				printerWrite("交易类型:".getBytes("GB2312"));
//				printerWrite(PrinterCommand.linefeed());
				
				//set bold font
				printerWrite(PrinterCommand.setFontBold(1));
				if (trans.getTransType() == TRAN_VOID) {
					printerWrite("消费撤销".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (trans.getTransType() == TRAN_SALE) {
					printerWrite("消费".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (trans.getTransType() == TRAN_REFUND) {
					printerWrite("退货".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else {
					String str = "type =" + trans.getTransType();
					printerWrite(str.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}
				printerWrite(PrinterCommand.setFontBold(0));
				
				printerWrite(("批次号:" + StringUtil.fillZero(
						Integer.toString(trans.getOldBatch()), 6)).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("凭证号:" + StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6)
						+ "  授权码:" + trans.getOldAuthCode()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("操作员:" + trans.getOper()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				
				printerWrite("--------------------------------".getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				
				printerWrite((TAG_CHANNEL + trans.getPaymentName()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

//				printerWrite(("发卡行: " + trans.getIssuerName(trans.getOldIssuerID())).getBytes("GB2312"));
//				printerWrite(PrinterCommand.linefeed());

//				printerWrite(("收单行: " + trans.getAcquirerName(trans.getOldAcquirerID())).getBytes("GB2312"));
//				printerWrite(PrinterCommand.linefeed());

				// 增大字体：高度增加1倍，宽度不变
				printerWrite(PrinterCommand.setFontEnlarge(0x01));

//				printerWrite("卡号(CARD NO):".getBytes("GB2312"));
				printerWrite("卡号:".getBytes("GB2312"));
//				printerWrite(PrinterCommand.linefeed());

				String pan = "";
				pan = trans.getOldPan();
				String tempPan = pan.substring(0, 6) + "******" + pan.substring(pan.length() - 4, pan.length());
				pan = tempPan;
				printerWrite(pan.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// 结束字体放大
				printerWrite(PrinterCommand.setFontEnlarge(0));

//				printerWrite(("国际卡组织: " + trans.getOldCardOrganization()).getBytes("GB2312"));
//				printerWrite(PrinterCommand.linefeed());

				

				// 增大字体：高度增加1倍，宽度不变
				printerWrite(PrinterCommand.setFontEnlarge(0x01));

				String amt = AppUtil.formatAmount(trans.getOldTransAmount()) + " RMB";
				if (trans.getTransType() == TRAN_VOID || trans.getTransType() == TRAN_REFUND) {
					amt = " - " + amt;
				}
				
				printerWrite(("金额: ").getBytes("GB2312"));
				printerWrite(PrinterCommand.setFontBold(1));
				printerWrite((amt).getBytes("GB2312"));
				printerWrite(PrinterCommand.setFontBold(0));
				printerWrite(PrinterCommand.linefeed());

				// 结束增大字体
				printerWrite(PrinterCommand.setFontEnlarge(0));

//				String ref = "备注/REFERENCE";
				String ref = "备注";
				printerWrite(ref.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				if (trans.getTransType() == TRAN_VOID_SALE
						|| trans.getTransType() == TRAN_VOID
						|| trans.getTransType() == TRAN_VOID_OFFLINE
						|| trans.getTransType() == TRAN_VOID_COMPLETE
						|| trans.getTransType() == TRAN_RESERV_VOID_SALE
						|| trans.getTransType() == TRAN_BONUS_VOID_SALE
						|| trans.getTransType() == TRAN_MOTO_VOID_SALE
						|| trans.getTransType() == TRAN_MOTO_VOID_COMP
						|| trans.getTransType() == TRAN_INSTALLMENT_VOID) {
//					printerWrite(("原凭证号:" + StringUtil.fillZero(
//							Integer.toString(trans.getOldTrace()), 6)).getBytes("GB2312"));
//					printerWrite(PrinterCommand.linefeed());

				}

				printerWrite("--------------------------------".getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				if (i == 0) {
					String sig = "持卡人签名";
					printerWrite(sig.getBytes("GB2312"));
					printerWrite(PrinterCommand.feedLine(3));
					printerWrite("--------------------------------".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}

				printerWrite("本人确认以上交易，同意将其记入本卡账户".getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(PrinterCommand.feedLine(2));
				if (i == 0) {
					Thread.currentThread().sleep(8000);
				}
			}

		} catch (UnsupportedEncodingException e) {
			throw new PrinterException("PrinterHelper.printReceipt():"
					+ e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new PrinterException(e.getMessage(), e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PrinterInterface.end();
			PrinterInterface.close();
		}
	}

	public void printerWrite(byte[] data) {
		PrinterInterface.write(data, data.length);
	}

	// QRCode print

	public void printQRCodeReceipt(OldTrans trans) {
		PrinterControl control = DeviceManager.getInstance().getPrinterControlEx();
		try {

			control.open();
			
			for (int i = 0; i < 2; i++) {
				
//				control.sendESC(FormatSettingCommand.getESCan(Align.CENTER));
				control.sendESC(PrinterCommand.setAlignMode(1));
				
				Drawable mDrawable = ctx.getResources().getDrawable(R.drawable.alipay);
				Bitmap mBitMap = ((BitmapDrawable) mDrawable).getBitmap();
				control.printImage(Bitmap.createScaledBitmap(mBitMap, 250, 80, false));
				
//			control.printText(TAG_DTITAL + "\n", FontType.DOUBLE_WH, Align.CENTER);
				
				control.sendESC(FormatSettingCommand.getESCan(Align.LEFT));
				
				if (i == 0) {
					control.printText("商户存根");
					printerWrite(PrinterCommand.linefeed());
				} else if (i == 1) {
					control.printText("买家存根");

					printerWrite(PrinterCommand.linefeed());
				}
				
				control.printText(TAG_LINE2 + "\n");
				
				control.printText(TAG_MERCHANT);

				control.printText(trans.getOldMertName() + "\n",
						FontType.NORMAL, Align.LEFT, Depth.DEEP);

				control.printText(TAG_KOOL_CLOUD_MID + trans.getKoolCloudMID()
						+ "\n");
				control.printText(TAG_KOOL_CLOUD_TID + trans.getKoolCloudTID()
						+ "\n");
				// control.printText(TAG_AP_NAME + trans.getPaymentName() +
				// "\n");
				// control.printText(TAG_TERMINAL + trans.getOldTID() + "\n");

				control.printText(TAG_REF + trans.getOldRrn() + "\n");
				
				control.printText(TAG_DATE_TIME + Utils.getCurrentDate() + " ");
				
				control.printText(/*TAG_TIME +*/ Utils.getCurrentTime() + "\n");
				
				control.printText(TAG_PAYTYPE);
				
				String transType = "";
				if (trans.getTransType() == TRAN_VOID) {
					transType = "消费撤销";
				} else if (trans.getTransType() == TRAN_SALE) {
					transType = "消费";
				} else if (trans.getTransType() == TRAN_REFUND) {
					transType = "退货";
				} else {
					transType = trans.getTransType() + "";
				}
				
				control.printText(transType + "\n", FontType.NORMAL, Align.LEFT, Depth.DEEP);
				
				control.printText(TAG_TRACE + StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + "\n");
				
				control.printText(TAG_TELLERNO + trans.getOper() + "\n");
				
				control.printText(TAG_LINE2 + "\n");
				
				control.printText(TAG_CHANNEL + trans.getPaymentName() + "\n");
				
				control.printText(TAG_ACCOUNT + trans.getAlipayAccount() + "\n");
				
				String amt = AppUtil.formatAmount(trans.getOldTransAmount());
				if (trans.getTransType() == TRAN_VOID || trans.getTransType() == TRAN_REFUND) {
					amt = " - " + amt;
				}
				
				control.printText(TAG_AMOUNT);
				
				control.sendESC(PrinterCommand.setFontEnlarge(0x01));
				control.sendESC(PrinterCommand.setFontBold(1));
				control.printText(amt);
				control.sendESC(PrinterCommand.setFontBold(0));
				control.sendESC(PrinterCommand.setFontEnlarge(0));
				control.printText("\n", FontType.NORMAL, Align.LEFT);
				
				control.printText(TRANSACTION_ID + "\n" + trans.getAlipayTransactionID() + "\n", FontType.NORMAL, Align.LEFT);
				control.printText(PARTNER_ID + "\n", FontType.NORMAL, Align.LEFT);
				control.printText(trans.getAlipayPId() + "\n", FontType.NORMAL, Align.LEFT, Depth.DEEP);
				
				String number = trans.getOldApOrderId();
				
				control.printText(MERCHANT_RECEIPT_ID + "\n", FontType.NORMAL, Align.LEFT);
				control.printText(number + "\n", FontType.NORMAL, Align.LEFT, Depth.DEEP);
				
				// not record the setting state, resetting it after finishing
//				control.sendESC(FormatSettingCommand.getESCan(Align.CENTER));
				control.sendESC(PrinterCommand.setAlignMode(1));
				Bitmap mQrcode = QRcodeBitmap.create(number, 250, 250);
				control.printImage(mQrcode);
				
				control.sendESC(FormatSettingCommand.getESCan(Align.LEFT));
				
//				control.printText("POS退货时，请扫上方二维码" + "\n");
				
				control.printText(TAG_LINE2 + "\n");
				
				if (i == 0) {
					control.printText(TAG_SIGNATURE + "\n\n\n");
					control.printText(TAG_LINE2 + "\n");
					control.printText("本人确认以上交易，同意将其记入支付宝账户");
					control.printText("\n\n\n\n\n");
					
					Thread.currentThread().sleep(8000);
				} else {
					control.printText("本人确认以上交易，同意将其记入支付宝账户");
					control.printText("\n\n\n\n\n");
				}
				
			}
			
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				control.close();
			} catch (AccessException e) {
				e.printStackTrace();
			}
			
		}
	}
}
