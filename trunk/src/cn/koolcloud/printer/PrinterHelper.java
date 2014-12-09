package cn.koolcloud.printer;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Font;

import cn.koolcloud.constant.Constant;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.parameter.OldTrans;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.entity.MisposData;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.MisposOperationUtil;
import cn.koolcloud.printer.command.FormatSettingCommand;
import cn.koolcloud.printer.control.Align;
import cn.koolcloud.printer.control.Depth;
import cn.koolcloud.printer.control.FontType;
import cn.koolcloud.printer.control.PrinterControl;
import cn.koolcloud.printer.devices.DeviceManager;
import cn.koolcloud.printer.exception.AccessException;
import cn.koolcloud.printer.util.QRcodeBitmap;
import cn.koolcloud.util.AppUtil;
import cn.koolcloud.util.DateUtil;
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
	 * print receipt
	 * 
	 * @throws PrinterException
	 */
	synchronized public void printReceipt(OldTrans trans)
			throws PrinterException {
		try {
			PrinterInterface.open();
			PrinterInterface.set(1);
			// PrinterInterface.begin();
			// PrinterInterface.end();
			// printerWrite(PrinterCommand.init());
			// printerWrite(PrinterCommand.setHeatTime(180));

			int printerStatus = PrinterInterface.begin();
			if (printerStatus == -1) {
				// close printer
				PrinterInterface.end();
				PrinterInterface.close();

				JSONObject jsObj = new JSONObject();
				try {
					jsObj.put(
							"msg",
							ctx.getResources().getString(
									R.string.msg_printer_issues));
					ClientEngine.engineInstance().showAlert(jsObj, null);
				} catch (NotFoundException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			for (int i = 0; i < 2; i++) {
				printerWrite(PrinterCommand.setFontBold(1));
				printerWrite(PrinterCommand.setAlignMode(1));
				printerWrite(PrinterCommand.setFontEnlarge(0x01));
				printerWrite(Env.getResourceString(ctx, R.string.printer_bank_card_voucher).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(PrinterCommand.setFontBold(0));
				printerWrite(PrinterCommand.setAlignMode(0));
				printerWrite(PrinterCommand.setFontEnlarge(0));

				if (i == 0) {
					printerWrite(Env.getResourceString(ctx, R.string.printer_merchant_copy).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (i == 1) {
					printerWrite(Env.getResourceString(ctx, R.string.printer_bank_card_holder).getBytes("GB2312"));

					printerWrite(PrinterCommand.linefeed());
				} else if (i == 2) {
					printerWrite(Env.getResourceString(ctx, R.string.printer_bank_holder).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}

				printerWrite("--------------------------------"
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				/*
				 * printerWrite(trans.getOldMID().getBytes("GB2312"));
				 * printerWrite(PrinterCommand.linefeed());
				 * 
				 * if (i == 0) {
				 * printerWrite(("商户存根           MERCHANT COPY").getBytes
				 * ("GB2312")); printerWrite(PrinterCommand.linefeed()); } else
				 * if (i == 1) {
				 * printerWrite(("持卡人存根       CARDHOLDER COPY").getBytes
				 * ("GB2312"));
				 * 
				 * printerWrite(PrinterCommand.linefeed()); } else if (i == 2) {
				 * printerWrite
				 * (("银行存根               BANK COPY").getBytes("GB2312"));
				 * printerWrite(PrinterCommand.linefeed()); }
				 * printerWrite("--------------------------------"
				 * .getBytes("GB2312"));
				 * printerWrite(PrinterCommand.linefeed());
				 */

				printerWrite((Env.getResourceString(ctx, R.string.printer_tag_merchant) + trans.getOldMertName())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite((Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid) + trans.getKoolCloudMID())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite((Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid) + trans.getKoolCloudTID())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite((Env.getResourceString(ctx, R.string.printer_acquirer_merchant_no) + trans.getOldMID()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite((Env.getResourceString(ctx, R.string.printer_acquirer_terminal_no) + trans.getOldTID()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite((Env.getResourceString(ctx, R.string.printer_tag_ref) + trans.getOldApOrderId())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				String localTime = convertTimeToLocal(trans);
				printerWrite((Env.getResourceString(ctx, R.string.printer_tag_date_time) + " " + localTime.substring(0,4)
						+ "/" + localTime.substring(4, 6)
						+ "/" + localTime.substring(6, 8)
						+ " " + localTime.substring(8, 10)
						+ ":" + localTime.substring(10, 12)
						+ ":" + localTime.substring(12, 14))
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// printerWrite("交易类型(TRANS TYPE):".getBytes("GB2312"));
				printerWrite(Env.getResourceString(ctx, R.string.printer_tag_pay_type).getBytes("GB2312"));
				// printerWrite(PrinterCommand.linefeed());

				// set bold font
				printerWrite(PrinterCommand.setFontBold(1));
				if (trans.getTransType() == TRAN_VOID) {
					printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_reverse).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (trans.getTransType() == TRAN_SALE) {
					printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_sale).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (trans.getTransType() == TRAN_REFUND) {
					printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_refund).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (trans.getTransType() == TRAN_AUTH) {
					printerWrite("预授权".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (trans.getTransType() == TRAN_AUTH_CANCEL) {
					printerWrite("预授权撤销".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (trans.getTransType() == TRAN_AUTH_COMPLETE) {
					printerWrite("预授权完成联机".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL) {
					printerWrite("预授权完成撤销".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (trans.getTransType() == TRAN_AUTH_SETTLEMENT) {
					printerWrite("预授权完成离线".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else {
					String str = "type =" + trans.getTransType();
					printerWrite(str.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}
				printerWrite(PrinterCommand.setFontBold(0));

				printerWrite((Env.getResourceString(ctx, R.string.printer_batch_no) + StringUtil.fillZero(
						Integer.toString(trans.getOldBatch()), 6))
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite((Env.getResourceString(ctx, R.string.printer_voucher_no)
						+ StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + " "
						+ Env.getResourceString(ctx, R.string.printer_auth_no) + trans.getOldAuthCode()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite((Env.getResourceString(ctx, R.string.printer_tag_operator) + trans.getOper()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite("--------------------------------".getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite((Env.getResourceString(ctx, R.string.printer_tag_channel) + trans.getPaymentName()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// printerWrite(("发卡行: " +
				// trans.getIssuerName(trans.getOldIssuerID())).getBytes("GB2312"));
				// printerWrite(PrinterCommand.linefeed());

				// printerWrite(("收单行: " +
				// trans.getAcquirerName(trans.getOldAcquirerID())).getBytes("GB2312"));
				// printerWrite(PrinterCommand.linefeed());

				// 增大字体：高度增加1倍，宽度不变
				printerWrite(PrinterCommand.setFontEnlarge(0x01));

				// printerWrite("卡号(CARD NO):".getBytes("GB2312"));
				printerWrite(Env.getResourceString(ctx, R.string.printer_bank_card).getBytes("GB2312"));
				// printerWrite(PrinterCommand.linefeed());

				String pan = "";
				pan = trans.getOldPan();
				String tempPan = pan.substring(0, 6) + "******"
						+ pan.substring(pan.length() - 4, pan.length());
				pan = tempPan;
				printerWrite(pan.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// 结束字体放大
				printerWrite(PrinterCommand.setFontEnlarge(0));

				// printerWrite(("国际卡组织: " +
				// trans.getOldCardOrganization()).getBytes("GB2312"));
				// printerWrite(PrinterCommand.linefeed());

				// 增大字体：高度增加1倍，宽度不变
				printerWrite(PrinterCommand.setFontEnlarge(0x01));

				String amt = AppUtil.formatAmount(trans.getOldTransAmount()) + " RMB";
				if (trans.getTransType() == TRAN_VOID
						|| trans.getTransType() == TRAN_REFUND) {
					amt = " - " + amt;
				}

				printerWrite(Env.getResourceString(ctx, R.string.printer_tag_amount).getBytes("GB2312"));
				printerWrite(PrinterCommand.setFontBold(1));
				printerWrite((amt).getBytes("GB2312"));
				printerWrite(PrinterCommand.setFontBold(0));
				printerWrite(PrinterCommand.linefeed());

				// 结束增大字体
				printerWrite(PrinterCommand.setFontEnlarge(0));

				// String ref = "备注/REFERENCE";
				String ref = Env.getResourceString(ctx, R.string.printer_reference);
				printerWrite(ref.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				if (trans.getTransType() == TRAN_VOID_SALE
						|| trans.getTransType() == TRAN_VOID
						|| trans.getTransType() == TRAN_VOID_OFFLINE
						|| trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL
						|| trans.getTransType() == TRAN_RESERV_VOID_SALE
						|| trans.getTransType() == TRAN_BONUS_VOID_SALE
						|| trans.getTransType() == TRAN_MOTO_VOID_SALE
						|| trans.getTransType() == TRAN_MOTO_VOID_COMP
						|| trans.getTransType() == TRAN_INSTALLMENT_VOID) {
					// printerWrite(("原凭证号:" + StringUtil.fillZero(
					// Integer.toString(trans.getOldTrace()),
					// 6)).getBytes("GB2312"));
					// printerWrite(PrinterCommand.linefeed());

				}

				printerWrite("--------------------------------"
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				if (i == 0) {
					String sig = Env.getResourceString(ctx, R.string.printer_tag_signature);
					printerWrite(sig.getBytes("GB2312"));
					printerWrite(PrinterCommand.feedLine(3));
					printerWrite("--------------------------------".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}

				printerWrite(Env.getResourceString(ctx, R.string.printer_msg_acknowledge_card).getBytes("GB2312"));
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

    synchronized public void printTransferReceipt(OldTrans trans) throws PrinterException {
        try {
            PrinterInterface.open();
            PrinterInterface.set(1);

            int printerStatus = PrinterInterface.begin();
            if (printerStatus == -1) {
                // close printer
                PrinterInterface.end();
                PrinterInterface.close();

                JSONObject jsObj = new JSONObject();
                try {
                    jsObj.put(
                            "msg",
                            ctx.getResources().getString(R.string.msg_printer_issues));
                    ClientEngine.engineInstance().showAlert(jsObj, null);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < 2; i++) {
                printerWrite(PrinterCommand.setFontBold(1));
                printerWrite(PrinterCommand.setAlignMode(1));
                printerWrite(PrinterCommand.setFontEnlarge(0x01));
                printerWrite(Env.getResourceString(ctx, R.string.printer_transfer_voucher).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite(PrinterCommand.setFontBold(0));
                printerWrite(PrinterCommand.setAlignMode(0));
                printerWrite(PrinterCommand.setFontEnlarge(0));

                if (i == 0) {
                    printerWrite(Env.getResourceString(ctx, R.string.printer_transfer_from_card_holder).getBytes("GB2312"));
                    printerWrite(PrinterCommand.linefeed());
                } else if (i == 1) {
                    printerWrite(Env.getResourceString(ctx, R.string.printer_transfer_to_card_holder).getBytes("GB2312"));

                    printerWrite(PrinterCommand.linefeed());
                } else if (i == 2) {
                    printerWrite(Env.getResourceString(ctx, R.string.printer_bank_holder).getBytes("GB2312"));
                    printerWrite(PrinterCommand.linefeed());
                }

                printerWrite("--------------------------------".getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_merchant) + trans.getOldMertName()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid) + trans.getKoolCloudMID()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid) + trans.getKoolCloudTID()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_acquirer_merchant_no) + trans.getOldMID()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_acquirer_terminal_no) + trans.getOldTID()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_trans_no) + trans.getTxnId()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());
	            String localTime = convertTimeToLocal(trans);
                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_date_time) + " " + localTime.substring(0,4)
		                + "/" + localTime.substring(4, 6)
		                + "/" + localTime.substring(6, 8)
		                + " " + localTime.substring(8, 10)
		                + ":" + localTime.substring(10, 12)
		                + ":" + localTime.substring(12, 14)).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite(Env.getResourceString(ctx, R.string.printer_tag_pay_type).getBytes("GB2312"));
                // printerWrite(PrinterCommand.linefeed());

                // set bold font
                printerWrite(PrinterCommand.setFontBold(1));
                if (trans.getTransType() == TRAN_SUPER_TRANSFER) {
                    printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_transfer).getBytes("GB2312"));
                    printerWrite(PrinterCommand.linefeed());
                } else {
                    String str = "type =" + trans.getTransType();
                    printerWrite(str.getBytes("GB2312"));
                    printerWrite(PrinterCommand.linefeed());
                }
                printerWrite(PrinterCommand.setFontBold(0));

                printerWrite((Env.getResourceString(ctx, R.string.printer_batch_no) + StringUtil.fillZero(
                        Integer.toString(trans.getOldBatch()), 6)).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_voucher_no)
                        + StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + " "
                        + Env.getResourceString(ctx, R.string.printer_auth_no) + trans.getOldAuthCode()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_operator) + trans.getOper()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite("--------------------------------".getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_channel) + trans.getPaymentName()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                // 增大字体：高度增加1倍，宽度不变
                printerWrite(PrinterCommand.setFontEnlarge(0x01));

                printerWrite(Env.getResourceString(ctx, R.string.printer_transfer_from_bank_card).getBytes("GB2312"));
                // printerWrite(PrinterCommand.linefeed());

                String pan = "";
                pan = trans.getOldPan();
                String tempPan = pan.substring(0, 6) + "******"
                        + pan.substring(pan.length() - 4, pan.length());
                pan = tempPan;
                printerWrite(pan.getBytes("GB2312"));

                // 结束字体放大
                printerWrite(PrinterCommand.setFontEnlarge(0));
                printerWrite(PrinterCommand.linefeed());

                printerWrite(Env.getResourceString(ctx, R.string.printer_transfer_to_bank_card).getBytes("GB2312"));
                // printerWrite(PrinterCommand.linefeed());

                //to bank card
                String toBankCard = trans.getToAccount();
                String toBankCardTempPan = toBankCard.substring(0, 6) + "******"
                        + toBankCard.substring(toBankCard.length() - 4, toBankCard.length());
                toBankCard = toBankCardTempPan;
                printerWrite(toBankCard.getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                // 增大字体：高度增加1倍，宽度不变
                printerWrite(PrinterCommand.setFontEnlarge(0x01));

                String amt = AppUtil.formatAmount(trans.getOldTransAmount()) + " RMB";
                if (trans.getTransType() == TRAN_VOID
                        || trans.getTransType() == TRAN_REFUND) {
                    amt = " - " + amt;
                }

                printerWrite(Env.getResourceString(ctx, R.string.printer_tag_trans_amount).getBytes("GB2312"));
                printerWrite(PrinterCommand.setFontBold(1));
                printerWrite((amt).getBytes("GB2312"));
                printerWrite(PrinterCommand.setFontBold(0));
                printerWrite(PrinterCommand.linefeed());

                // 结束增大字体
                printerWrite(PrinterCommand.setFontEnlarge(0));

                // String ref = "备注/REFERENCE";
                String ref = Env.getResourceString(ctx, R.string.printer_reference);
                printerWrite(ref.getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());
                if (trans.getTransType() == TRAN_VOID_SALE
                        || trans.getTransType() == TRAN_VOID
                        || trans.getTransType() == TRAN_VOID_OFFLINE
                        || trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL
                        || trans.getTransType() == TRAN_RESERV_VOID_SALE
                        || trans.getTransType() == TRAN_BONUS_VOID_SALE
                        || trans.getTransType() == TRAN_MOTO_VOID_SALE
                        || trans.getTransType() == TRAN_MOTO_VOID_COMP
                        || trans.getTransType() == TRAN_INSTALLMENT_VOID) {

                }

                printerWrite("--------------------------------".getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                if (i == 0) {
                    String sig = Env.getResourceString(ctx, R.string.printer_tag_signature);
                    printerWrite(sig.getBytes("GB2312"));
                    printerWrite(PrinterCommand.feedLine(3));
                    printerWrite("--------------------------------".getBytes("GB2312"));
                    printerWrite(PrinterCommand.linefeed());
                }

                printerWrite(Env.getResourceString(ctx, R.string.printer_msg_acknowledge_card).getBytes("GB2312"));
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

	synchronized public void printMisposReceipt(MisposData misposBean)
			throws PrinterException {
		try {
			PrinterInterface.open();
			PrinterInterface.set(1);
			PrinterInterface.begin();
			PrinterInterface.end();
			// printerWrite(PrinterCommand.init());
			// printerWrite(PrinterCommand.setHeatTime(180));

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

				printerWrite("--------------------------------"
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("商户名:" + misposBean.getMerchantName())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// printerWrite(("酷云客户号:" + misposBean.getKoolCloudMID())
				// .getBytes("GB2312"));
				// printerWrite(PrinterCommand.linefeed());
				//
				// printerWrite(("酷云设备号:" + misposBean.getKoolCloudTID())
				// .getBytes("GB2312"));
				// printerWrite(PrinterCommand.linefeed());

				printerWrite(("收单商户号:" + misposBean.getMerchantId())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("收单终端号:" + misposBean.getTerminalId())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("参考号:" + misposBean.getRefNo())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("日期/时间:" + " "
						+ ("" + misposBean.getTranDate().substring(0, 4)) + "/"
						+ ("" + misposBean.getTranDate().substring(4, 6)) + "/"
						+ ("" + misposBean.getTranDate().substring(6, 8)) + " "
						+ ("" + misposBean.getTranTime().substring(0, 2)) + ":"
						+ ("" + misposBean.getTranTime().substring(2, 4)) + ":" + ("" + misposBean
						.getTranTime().substring(4, 6))).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// printerWrite("交易类型(TRANS TYPE):".getBytes("GB2312"));
				printerWrite("交易类型:".getBytes("GB2312"));
				// printerWrite(PrinterCommand.linefeed());

				// set bold font
				printerWrite(PrinterCommand.setFontBold(1));
				if (misposBean.getTransType().equals(
						MisposOperationUtil.TRAN_TYPE_CONSUMPTION_REVERSE)) {
					printerWrite("消费撤销".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (misposBean.getTransType().equals(
						MisposOperationUtil.TRAN_TYPE_CONSUMPTION)) {
					printerWrite("消费".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else if (misposBean.getTransType().equals(
						MisposOperationUtil.TRAN_TYPE_PRE_AUTHORIZATION)) {
					printerWrite("预授权".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				} else {
					String str = "type =" + misposBean.getTransType();
					printerWrite(str.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}
				printerWrite(PrinterCommand.setFontBold(0));

				printerWrite(("批次号:" + misposBean.getBatchNo())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("凭证号:" + misposBean.getVoucherNo() + "  授权码:" + misposBean
						.getAuthNo()).getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite(("操作员:" + misposBean.getOperatorId())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite("--------------------------------"
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				printerWrite((TAG_CHANNEL + misposBean.getPaymentName())
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// 增大字体：高度增加1倍，宽度不变
				printerWrite(PrinterCommand.setFontEnlarge(0x01));

				printerWrite("卡号:".getBytes("GB2312"));

				String pan = "";
				pan = misposBean.getCardNo();
				String tempPan = pan.substring(0, 6) + "******"
						+ pan.substring(pan.length() - 4, pan.length());
				pan = tempPan;
				printerWrite(pan.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());

				// 结束字体放大
				printerWrite(PrinterCommand.setFontEnlarge(0));

				// 增大字体：高度增加1倍，宽度不变
				printerWrite(PrinterCommand.setFontEnlarge(0x01));

				String amt = AppUtil.formatAmount(Long.parseLong(misposBean
						.getAmount())) + " RMB";
				if (misposBean.getTransType().equals(
						MisposOperationUtil.TRAN_TYPE_CONSUMPTION_REVERSE)) {
					amt = " - " + amt;
				}

				printerWrite(("金额: ").getBytes("GB2312"));
				printerWrite(PrinterCommand.setFontBold(1));
				printerWrite((amt).getBytes("GB2312"));
				printerWrite(PrinterCommand.setFontBold(0));
				printerWrite(PrinterCommand.linefeed());

				// 结束增大字体
				printerWrite(PrinterCommand.setFontEnlarge(0));

				// String ref = "备注/REFERENCE";
				String ref = "备注";
				printerWrite(ref.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				// if (misposBean.getTransType() == TRAN_VOID_SALE
				// || misposBean.getTransType() == TRAN_VOID
				// || misposBean.getTransType() == TRAN_VOID_OFFLINE
				// || misposBean.getTransType() == TRAN_AUTH_COMPLETE_CANCEL
				// || misposBean.getTransType() == TRAN_RESERV_VOID_SALE
				// || misposBean.getTransType() == TRAN_BONUS_VOID_SALE
				// || misposBean.getTransType() == TRAN_MOTO_VOID_SALE
				// || misposBean.getTransType() == TRAN_MOTO_VOID_COMP
				// || misposBean.getTransType() == TRAN_INSTALLMENT_VOID) {
				// // printerWrite(("原凭证号:" + StringUtil.fillZero(
				// // Integer.toString(trans.getOldTrace()),
				// 6)).getBytes("GB2312"));
				// // printerWrite(PrinterCommand.linefeed());
				//
				// }

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

	synchronized public void printTransSummary(JSONObject printData)
			throws PrinterException {
		try {
			PrinterInterface.open();
			PrinterInterface.set(1);
			PrinterInterface.begin();
			PrinterInterface.end();
			// printerWrite(PrinterCommand.init());
			// printerWrite(PrinterCommand.setHeatTime(180));

			PrinterInterface.begin();

			printerWrite(PrinterCommand.setFontBold(1));
			printerWrite(PrinterCommand.setAlignMode(1));
			printerWrite(PrinterCommand.setFontEnlarge(0x01));
			printerWrite(("当日消费汇总").getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite(PrinterCommand.setFontBold(0));
			printerWrite(PrinterCommand.setAlignMode(0));
			printerWrite(PrinterCommand.setFontEnlarge(0));

			printerWrite("--------------------------------".getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite(("商户名称:" + printData
					.optString(ConstantUtils.FOR_PRINT_MERCHANT_NAME))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite(("酷云客户号:" + printData
					.optString(ConstantUtils.FOR_PRINT_MERCHANT_ID))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite(("酷云设备号:" + printData
					.optString(ConstantUtils.FOR_PRINT_MECHINE_ID))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite(("汇总时间:" + getCurrentTime()).getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite("--------------------------------".getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite(("交易类型     " + "笔数  " + "金额（元） ").getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite("--------------------------------".getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite((getTransType(printData,
					ConstantUtils.APMP_TRAN_TYPE_CONSUME)
					+ "            "
					+ getTransCount(printData,
							ConstantUtils.APMP_TRAN_TYPE_CONSUME) + "  " + getTransAmount(
						printData, ConstantUtils.APMP_TRAN_TYPE_CONSUME))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite((getTransType(printData,
					ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE)
					+ "        "
					+ getTransCount(printData,
							ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE)
					+ "  " + getTransAmount(printData,
						ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite((getTransType(printData,
					ConstantUtils.APMP_TRAN_TYPE_PREAUTH)
					+ "          "
					+ getTransCount(printData,
							ConstantUtils.APMP_TRAN_TYPE_PREAUTH) + "  " + getTransAmount(
						printData, ConstantUtils.APMP_TRAN_TYPE_PREAUTH))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite((getTransType(printData,
					ConstantUtils.APMP_TRAN_TYPE_PRAUTHCOMPLETE)
					+ "  "
					+ getTransCount(printData,
							ConstantUtils.APMP_TRAN_TYPE_PRAUTHCOMPLETE)
					+ "  " + getTransAmount(printData,
						ConstantUtils.APMP_TRAN_TYPE_PRAUTHCOMPLETE))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite((getTransType(printData,
					ConstantUtils.APMP_TRAN_TYPE_PRAUTHSETTLEMENT)
					+ "  "
					+ getTransCount(printData,
							ConstantUtils.APMP_TRAN_TYPE_PRAUTHSETTLEMENT)
					+ "  " + getTransAmount(printData,
						ConstantUtils.APMP_TRAN_TYPE_PRAUTHSETTLEMENT))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite((getTransType(printData,
					ConstantUtils.APMP_TRAN_TYPE_PRAUTHCANCEL)
					+ "      "
					+ getTransCount(printData,
							ConstantUtils.APMP_TRAN_TYPE_PRAUTHCANCEL)
					+ "  " + getTransAmount(printData,
						ConstantUtils.APMP_TRAN_TYPE_PRAUTHCANCEL))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite((getTransType(printData,
					ConstantUtils.APMP_TRAN_TYPE_PREAUTHCOMPLETECANCEL)
					+ "  "
					+ getTransCount(
							printData,
							ConstantUtils.APMP_TRAN_TYPE_PREAUTHCOMPLETECANCEL)
					+ "  " + getTransAmount(printData,
						ConstantUtils.APMP_TRAN_TYPE_PREAUTHCOMPLETECANCEL))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite("--------------------------------".getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite(PrinterCommand.linefeed());
			printerWrite(PrinterCommand.linefeed());

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

	private String getTransType(JSONObject summary, String transType) {
		String value = summary.optString(transType);
		String[] strs = value.split("-");
		return strs[0];
	}

	private String getTransCount(JSONObject summary, String transType) {
		String value = summary.optString(transType);
		String[] strs = value.split("-");
		return strs[1];
	}

	private String getTransAmount(JSONObject summary, String transType) {
		String value = summary.optString(transType);
		String[] strs = value.split("-");
		return strs[2];
	}

	private String getCurrentTime() {
		String transTime;
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
		System.setProperty("user.timezone", "GMT+8");
		transTime = dateFormat.format(now);
		return transTime;
	}

	public void printerWrite(byte[] data) {
		PrinterInterface.write(data, data.length);
	}

    public void printQRCodeReceipt(OldTrans trans) {
        PrinterControl control = DeviceManager.getInstance()
                .getPrinterControlEx();
        try {

            control.open();

            for (int i = 0; i < 2; i++) {

                // control.sendESC(FormatSettingCommand.getESCan(Align.CENTER));
                control.sendESC(PrinterCommand.setAlignMode(1));

                Drawable mDrawable = ctx.getResources().getDrawable(
                        R.drawable.alipay);
                Bitmap mBitMap = ((BitmapDrawable) mDrawable).getBitmap();
                control.printImage(Bitmap.createScaledBitmap(mBitMap, 250, 80,
                        false));

                // control.printText(TAG_DTITAL + "\n", FontType.DOUBLE_WH,
                // Align.CENTER);

                control.sendESC(FormatSettingCommand.getESCan(Align.LEFT));

                if (i == 0) {
                    control.printText(Env.getResourceString(ctx, R.string.printer_merchant_copy));
                    printerWrite(PrinterCommand.linefeed());
                } else if (i == 1) {
                    control.printText(Env.getResourceString(ctx, R.string.printer_cardhoder_copy));

                    printerWrite(PrinterCommand.linefeed());
                }

                control.printText(TAG_LINE2 + "\n");

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_merchant));

                control.printText(trans.getOldMertName() + "\n",
                        FontType.NORMAL, Align.LEFT, Depth.DEEP);

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid) + trans.getKoolCloudMID()
                        + "\n");
                control.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid) + trans.getKoolCloudTID()
                        + "\n");
                // control.printText(TAG_AP_NAME + trans.getPaymentName() +
                // "\n");
                // control.printText(TAG_TERMINAL + trans.getOldTID() + "\n");

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_ref) + trans.getOldApOrderId() + "\n");

//				control.printText(TAG_DATE_TIME + Utils.getCurrentDate() + " ");

//				control.printText(/* TAG_TIME + */Utils.getCurrentTime() + "\n");
	            String localTime = convertTimeToLocal(trans);
                control.printText(Env.getResourceString(ctx, R.string.printer_tag_date_time) + " " + localTime.substring(0,4)
		                + "/" + localTime.substring(4, 6)
		                + "/" + localTime.substring(6, 8)
		                + " " + localTime.substring(8, 10)
		                + ":" + localTime.substring(10, 12)
		                + ":" + localTime.substring(12, 14));
                control.printText("\n");
                control.printText(Env.getResourceString(ctx, R.string.printer_tag_pay_type));

                String transType = "";
                if (trans.getTransType() == TRAN_VOID) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_reverse);
                } else if (trans.getTransType() == TRAN_SALE) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_sale);
                } else if (trans.getTransType() == TRAN_REFUND) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_refund);
                } else {
                    transType = trans.getTransType() + "";
                }

                control.printText(transType + "\n", FontType.NORMAL,
                        Align.LEFT, Depth.DEEP);

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_trace)
                        + StringUtil.fillZero(
                        Integer.toString(trans.getOldTrace()), 6)
                        + "\n");

                //print batch no
                control.printText(Env.getResourceString(ctx, R.string.printer_batch_no), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
                control.printText(StringUtil.fillZero(Integer.toString(trans.getOldBatch()), 6) + "\n", FontType.NORMAL);

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_operator) + trans.getOper() + "\n");

                control.printText(TAG_LINE2 + "\n");

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_channel) + trans.getPaymentName() + "\n");

                if (trans.getTransType() == TRAN_SALE) {
                    control.printText(Env.getResourceString(ctx, R.string.printer_tag_account) + trans.getAlipayAccount()
                            + "\n");
                }

                String amt = AppUtil.formatAmount(trans.getOldTransAmount());
                if (trans.getTransType() == TRAN_VOID
                        || trans.getTransType() == TRAN_REFUND) {
                    amt = " - " + amt;
                }

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_amount));

                control.sendESC(PrinterCommand.setFontEnlarge(0x01));
                control.sendESC(PrinterCommand.setFontBold(1));
                control.printText(amt);
                control.sendESC(PrinterCommand.setFontBold(0));
                control.sendESC(PrinterCommand.setFontEnlarge(0));
                control.printText("\n", FontType.NORMAL, Align.LEFT);

                control.printText(
                        Env.getResourceString(ctx, R.string.printer_transaction_id) + "\n" + trans.getAlipayTransactionID()
                                + "\n", FontType.NORMAL, Align.LEFT);
                control.printText(Env.getResourceString(ctx, R.string.printer_partner_id) + "\n", FontType.NORMAL,
                        Align.LEFT);
                control.printText(trans.getAlipayPId() + "\n", FontType.NORMAL,
                        Align.LEFT, Depth.DEEP);

                String number = trans.getOldApOrderId();

                control.printText(Env.getResourceString(ctx, R.string.printer_merchant_receipt_id) + "\n", FontType.NORMAL,
                        Align.LEFT);
                control.printText(number + "\n", FontType.NORMAL, Align.LEFT,
                        Depth.DEEP);

                // not record the setting state, resetting it after finishing
                // control.sendESC(FormatSettingCommand.getESCan(Align.CENTER));
                control.sendESC(PrinterCommand.setAlignMode(1));
                Bitmap mQrcode = QRcodeBitmap.create(number, 250, 250);
                control.printImage(mQrcode);

                control.sendESC(FormatSettingCommand.getESCan(Align.LEFT));

                control.printText(TAG_LINE2 + "\n");

                if (i == 0) {
                    control.printText(Env.getResourceString(ctx, R.string.printer_tag_signature) + "\n\n\n");
                    control.printText(TAG_LINE2 + "\n");
                    control.printText(Env.getResourceString(ctx, R.string.printer_msg_acknowledge));
                    control.printText("\n\n\n\n\n");

                    Thread.currentThread().sleep(8000);
                } else {
                    control.printText(Env.getResourceString(ctx, R.string.printer_msg_acknowledge));
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
	// QRCode print

	public void printQRCodeOverSeaReceipt(OldTrans trans) {
		PrinterControl control = DeviceManager.getInstance()
				.getPrinterControlEx();
		try {

			control.open();

			for (int i = 0; i < 2; i++) {

				// control.sendESC(FormatSettingCommand.getESCan(Align.CENTER));
				control.sendESC(PrinterCommand.setAlignMode(1));

				Drawable mDrawable = ctx.getResources().getDrawable(
						R.drawable.alipay);
				Bitmap mBitMap = ((BitmapDrawable) mDrawable).getBitmap();
				control.printImage(Bitmap.createScaledBitmap(mBitMap, 250, 80, false));

				control.sendESC(FormatSettingCommand.getESCan(Align.LEFT));

				if (i == 0) {
					control.printText(Env.getResourceString(ctx, R.string.printer_merchant_copy), FontType.NORMAL);
					printerWrite(PrinterCommand.linefeed());
				} else if (i == 1) {
					control.printText(Env.getResourceString(ctx, R.string.printer_cardhoder_copy), FontType.NORMAL);
					printerWrite(PrinterCommand.linefeed());
				}

				control.printText(TAG_LINE2 + "\n", FontType.NORMAL);

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_merchant), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
                control.printText(trans.getOldMertName() + "\n", FontType.NORMAL, Align.LEFT, Depth.DEEP);

				control.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
                control.printText(trans.getKoolCloudMID()+ "\n", FontType.NORMAL);

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
                control.printText(trans.getKoolCloudTID() + "\n", FontType.NORMAL);

                //print reference
				control.printText(Env.getResourceString(ctx, R.string.printer_tag_trans_no), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
                control.printText(trans.getTxnId() + "\n", FontType.NORMAL);

                //print tran date and time
				control.printText(Env.getResourceString(ctx, R.string.printer_tag_date_time), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
				String localTime = convertTimeToLocal(trans);
                control.printText(localTime.substring(0,4)
		                + "/" + localTime.substring(4, 6)
		                + "/" + localTime.substring(6, 8)
		                + " " + localTime.substring(8, 10)
		                + ":" + localTime.substring(10, 12)
		                + ":" + localTime.substring(12, 14), FontType.NORMAL);
                control.printText("\n");

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_pay_type), FontType.NORMAL);

                //print tran type
                String transType = "";
                if (trans.getTransType() == TRAN_VOID) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_reverse);
                } else if (trans.getTransType() == TRAN_SALE) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_sale);
                } else if (trans.getTransType() == TRAN_REFUND) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_refund);
                } else {
                    transType = trans.getTransType() + "";
                }

                printerWrite(PrinterCommand.linefeed());
                control.printText(transType, FontType.DOUBLE_WH, Align.LEFT, Depth.DEEP);
                control.printText("\n", FontType.NORMAL, Align.LEFT);

                //print trace no.
				control.printText(Env.getResourceString(ctx, R.string.printer_tag_trace), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
                control.printText(StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + "\n", FontType.NORMAL);

                //print batch no
                control.printText(Env.getResourceString(ctx, R.string.printer_batch_no), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
                control.printText(StringUtil.fillZero(Integer.toString(trans.getOldBatch()), 6) + "\n", FontType.NORMAL);

                //print operator
				control.printText(Env.getResourceString(ctx, R.string.printer_tag_operator), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
                control.printText(trans.getOper() + "\n", FontType.NORMAL);

				control.printText(TAG_LINE2 + "\n", FontType.NORMAL);

                //print payment name
				control.printText(Env.getResourceString(ctx, R.string.printer_tag_channel), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());
                control.printText(trans.getPaymentName() + "\n", FontType.NORMAL);

				if (trans.getTransType() == TRAN_SALE) {
					control.printText(Env.getResourceString(ctx, R.string.printer_tag_account), FontType.NORMAL);
                    printerWrite(PrinterCommand.linefeed());
                    control.printText(trans.getAlipayAccount() + "\n", FontType.NORMAL);
                }

				String amt = AppUtil.formatAmount(trans.getOldTransAmount());
				if (trans.getTransType() == TRAN_VOID
						|| trans.getTransType() == TRAN_REFUND) {
					amt = " - " + amt;
				}

                //print amount
                String currency = Env.getCurrencyResource(ctx);
				control.printText(Env.getResourceString(ctx, R.string.printer_tag_amount), FontType.NORMAL);
                printerWrite(PrinterCommand.linefeed());

                control.sendESC(PrinterCommand.setFontEnlarge(0x01));
                control.sendESC(PrinterCommand.setFontBold(1));
                control.printText(amt + "(" + currency + ")", FontType.DOUBLE_WH, Align.CENTER);
                control.sendESC(PrinterCommand.setFontBold(0));
                control.sendESC(PrinterCommand.setFontEnlarge(0));
                control.printText("\n", FontType.NORMAL, Align.LEFT);

                if (trans.getTransType() == TRAN_SALE) {

                    //print exchange rate
                    control.printText(Env.getResourceString(ctx, R.string.printer_tag_exchange_rate), FontType.NORMAL);
                    printerWrite(PrinterCommand.linefeed());
                    control.printText(trans.getExchangeRate());
                    printerWrite(PrinterCommand.linefeed());

                    //print real amount
                    control.printText(Env.getResourceString(ctx, R.string.printer_tag_real_amount), FontType.NORMAL);
                    printerWrite(PrinterCommand.linefeed());
                    control.printText(trans.getRealAmount());
                    printerWrite(PrinterCommand.linefeed());
                }

                //print alipay params
				control.printText(Env.getResourceString(ctx, R.string.printer_transaction_id) + "\n", FontType.NORMAL, Align.LEFT);
                control.printText(trans.getAlipayTransactionID() + "\n\n", FontType.NORMAL);
				control.printText(Env.getResourceString(ctx, R.string.printer_partner_id) + "\n", FontType.NORMAL, Align.LEFT);
				control.printText(trans.getAlipayPId() + "\n", FontType.NORMAL, Align.LEFT, Depth.DEEP);

				String number = trans.getOldApOrderId();

				control.printText(Env.getResourceString(ctx, R.string.printer_merchant_receipt_id) + "\n", FontType.NORMAL, Align.LEFT);
				control.printText(number + "\n", FontType.NORMAL, Align.LEFT, Depth.DEEP);

				// not record the setting state, resetting it after finishing
				// control.sendESC(FormatSettingCommand.getESCan(Align.CENTER));
				control.sendESC(PrinterCommand.setAlignMode(1));
				Bitmap mQrcode = QRcodeBitmap.create(number, 250, 250);
				control.printImage(mQrcode);

				control.sendESC(FormatSettingCommand.getESCan(Align.LEFT));

				control.printText(TAG_LINE2 + "\n", FontType.NORMAL);

                control.printText(Env.getResourceString(ctx, R.string.printer_tag_signature) + "\n\n\n", FontType.NORMAL);
                control.printText(TAG_LINE2 + "\n", FontType.NORMAL);
                control.printText(Env.getResourceString(ctx, R.string.printer_msg_acknowledge));

                printerWrite(PrinterCommand.linefeed());
                control.sendESC(PrinterCommand.setAlignMode(1));
                Drawable mKingPowerDrawable = ctx.getResources().getDrawable(R.drawable.kingpower);
                Bitmap mKingPowerBitMap = ((BitmapDrawable) mKingPowerDrawable).getBitmap();
                control.printImage(Bitmap.createScaledBitmap(mKingPowerBitMap, 250, 80, false));
                control.sendESC(FormatSettingCommand.getESCan(Align.LEFT));
                control.printText("\n\n\n");
                if (i == 0) {
                    Thread.currentThread().sleep(8000);
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
	private String convertTimeToLocal(OldTrans trans){
		/*
			 * 这里是为了适应本地化设置的，所以把time date转成当地时间。
			 * 需要注意的是：这里的年只能是当年，由于8583报文中没有年份，只有日期、时分秒。如果
			 * 出现今年打印去年的交易记录时，其显示的年份依然是今年。如果使用的是我们的posp,我们可以
			 * 在40域中记录交易年份，如果是直连银联或银行的话，就没有办法了，除非他们提示年份。
			 */
		String localTime = trans.getOldTransYear() + "-"
				+ trans.getOldTransDate().substring(0,2) + "-"
				+ trans.getOldTransDate().substring(2,4) + " "
				+ trans.getOldTransTime().substring(0,2) + ":"
				+ trans.getOldTransTime().substring(2,4) + ":"
				+ trans.getOldTransTime().substring(4,6);
		Date dt = DateUtil.parseData(localTime, "yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("GMT+08:00"));
		localTime = DateUtil.formatDate(dt, "yyyyMMddHHmmss",TimeZone.getDefault());
		return localTime;
	}
}
