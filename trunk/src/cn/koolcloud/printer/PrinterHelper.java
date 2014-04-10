package cn.koolcloud.printer;

import java.io.UnsupportedEncodingException;

import cn.koolcloud.constant.Constant;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.parameter.OldTrans;
import cn.koolcloud.util.AppUtil;
import cn.koolcloud.util.StringUtil;

public class PrinterHelper implements Constant {
	private static PrinterHelper _instance;

	private PrinterHelper() {
	}

	synchronized public static PrinterHelper getInstance() {
		if (null == _instance) {
			_instance = new PrinterHelper();
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

			printerWrite(PrinterCommand.init());
			printerWrite(PrinterCommand.setHeatTime(180));

			for (int i = 0; i < 2; i++) {
				printerWrite(PrinterCommand.setFontBold(1));
				printerWrite(PrinterCommand.setAlignMode(1));

				printerWrite(("通联POS签购单").getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(PrinterCommand.setFontBold(0));
				printerWrite(PrinterCommand.setAlignMode(0));

				printerWrite("--------------------------------"
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(trans.getOldMID()
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				if (i == 0) {
					printerWrite(("商户存根           MERCHANT COPY")
							.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (i == 1) {
					printerWrite(("持卡人存根       CARDHOLDER COPY")
							.getBytes("GB2312"));

					printerWrite(PrinterCommand.linefeed());
				} else if (i == 2) {
					printerWrite(("银行存根               BANK COPY")
							.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}
				printerWrite("--------------------------------"
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("商户号:" + trans.getOldMID()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("终端号:" + trans.getOldTID()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("操作员:" + trans
						.getOper()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("发卡行: " + trans.getIssuerName(trans.getOldIssuerID()))
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("收单行: " + trans.getAcquirerName(trans.getOldAcquirerID()))
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// 增大字体：高度增加1倍，宽度不变
				printerWrite(PrinterCommand.setFontEnlarge(0x01));

				printerWrite("卡号(CARD NO):"
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				String pan = "";
//				if (appState.trans.getTransType() == TRAN_AUTH) {
//					pan = appState.trans.getPAN();
//				}
				pan = trans.getOldPan();
				String tempPan = pan.substring(0, 4)+"*******" +pan.substring(pan.length() -4, pan.length()) ;
				pan = tempPan;
//				switch (appState.trans.getEntryMode()) {
//				case 0:
//					pan = pan + " N";
//					break;
//				case SWIPE_ENTRY:
//					pan = pan + " S";
//					break;
//				case INSERT_ENTRY:
//					pan = pan + " I";
//					break;
////				 case MANUAL_ENTRY:
////				 pan = pan + " M";
////				 break;
//				default:
//					pan = pan + " C";
//					break;
//				}
				printerWrite(pan.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// 结束字体放大
				printerWrite(PrinterCommand.setFontEnlarge(0));

				printerWrite("交易类型(TRANS TYPE):".getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				
				if(trans.getTransType() == TRAN_VOID){
					printerWrite("消费撤销".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}else if (trans.getTransType() == TRAN_SALE){
					printerWrite("消费".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}else{
					String str = "type =" + trans.getTransType();
					printerWrite(str.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}

				printerWrite(("国际卡组织: " + trans.getOldCardOrganization())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				String exp = "";
//				if (trans.getOldExpiry().length() == 4) {
//					exp = trans.getOldExpiry().substring(0, 2) + "/"
//							+ trans.getOldExpiry().substring(2);
//				}
				printerWrite(("批次号:"
						+ StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6)
						+ "  "/*+"有效期:" + exp*/).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("凭证号:"
						+ StringUtil.fillZero(
								Integer.toString(trans.getOldTrace()), 6)
						+ "  授权码:" + trans.getOldAcquirerCode())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("参考号:" + trans.getOldRrn())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("日期/时间:"
						+ " " + ("" + trans.getOldTransYear())
						+ "/" + ("" + trans.getOldTransDate().substring(0, 2))
						+ "/" + ("" + trans.getOldTransDate().substring(2, 4))
						+ " " + ("" + trans.getOldTransTime().substring(0, 2))
						+ ":" + ("" + trans.getOldTransTime().substring(2, 4))
						+ ":" + ("" + trans.getOldTransTime().substring(4, 6)))
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// 增大字体：高度增加1倍，宽度不变
				printerWrite(PrinterCommand.setFontEnlarge(0x01));
				
				String amt = AppUtil.formatAmount(trans
						.getOldTransAmount()) +" RMB";
				if(trans.getTransType() == TRAN_VOID){
					amt = " - " + amt;
				}
				printerWrite(("金额: " + amt).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
 
				// 结束增大字体
				printerWrite(PrinterCommand.setFontEnlarge(0));

				String ref = "备注/REFERENCE";
				printerWrite(ref.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				if (trans.getTransType() == TRAN_VOID_SALE
						||trans.getTransType() == TRAN_VOID
						|| trans.getTransType() == TRAN_VOID_OFFLINE
						|| trans.getTransType() == TRAN_VOID_COMPLETE
						|| trans.getTransType() == TRAN_RESERV_VOID_SALE
						|| trans.getTransType() == TRAN_BONUS_VOID_SALE
						|| trans.getTransType() == TRAN_MOTO_VOID_SALE
						|| trans.getTransType() == TRAN_MOTO_VOID_COMP
						|| trans.getTransType() == TRAN_INSTALLMENT_VOID) {
					printerWrite(("原凭证号:" + StringUtil.fillZero(
							Integer.toString(trans.getOldTrace()),
							6)).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
					
				} 

				

				printerWrite("--------------------------------"
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				if (i == 0) {
					String sig = "持卡人签名";
					printerWrite(sig.getBytes("GB2312"));
					printerWrite(PrinterCommand.feedLine(3));
					printerWrite("--------------------------------"
							.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}

				printerWrite("本人确认以上交易，同意将其记入本卡账户".getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(PrinterCommand.feedLine(2));
			}

		} catch (UnsupportedEncodingException e) {
			throw new PrinterException("PrinterHelper.printReceipt():"
					+ e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new PrinterException(e.getMessage(), e);
		} finally {
			PrinterInterface.end();
			PrinterInterface.close();
		}
	}

	public void printerWrite(byte[] data) {
		PrinterInterface.write(data, data.length);
	}
}