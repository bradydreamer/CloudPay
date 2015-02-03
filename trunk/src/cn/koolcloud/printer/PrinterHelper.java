package cn.koolcloud.printer;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import cn.koolcloud.constant.Constant;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.parameter.OldTrans;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.database.BankDB;
import cn.koolcloud.pos.entity.ConsumptionRecordBean;
import cn.koolcloud.pos.entity.MisposData;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.MisposOperationUtil;
import cn.koolcloud.printer.command.FormatSettingCommand;
import cn.koolcloud.printer.command.PrinterCommand;
import cn.koolcloud.printer.control.SettingConstants.Align;
import cn.koolcloud.printer.control.BasePrinterInterface;
import cn.koolcloud.printer.control.SettingConstants.Depth;
import cn.koolcloud.printer.control.SettingConstants.FontType;
import cn.koolcloud.printer.control.PrinterImpl;
import cn.koolcloud.printer.exception.AccessException;
import cn.koolcloud.printer.exception.PrinterException;
import cn.koolcloud.util.AppUtil;
import cn.koolcloud.util.DateUtil;
import cn.koolcloud.util.StringUtil;

public class PrinterHelper implements Constant {
	// private ClientEngine client

	private static PrinterHelper _instance;
	private static Context ctx;
    private final static int PRINTER_DELAY_TIME = 4000;
    private static boolean isPrinting = false;
    private final static int QRCODE_WIDTH = 300;        //qrcode width
    private final static int QRCODE_HEIGHT = 300;       //qrcode height

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
	 * @throws cn.koolcloud.printer.exception.PrinterException
	 */
	synchronized public void printReceipt(OldTrans trans)
			throws PrinterException {
        if (isPrinting) {
            return;
        }
		try {
			PrinterInterface.open();
			PrinterInterface.set(1);
			int printerStatus = PrinterInterface.begin();

            isPrinting = true;

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

            BankDB bankDB = BankDB.getInstance(ctx);
            String bankName = bankDB.getBankNameByIssuerId(trans.getOldIssuerID());
            bankDB.closeDB();
			String language = Locale.getDefault().getLanguage();
			if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) { //中文的排版
				for (int i = 0; i < 2; i++) {
					printerWrite(PrinterCommand.setFontBold(1));
					printerWrite(PrinterCommand.setAlignMode(1));
					printerWrite(PrinterCommand.setFontEnlarge(0x01));
					printerWrite(Env.getResourceString(ctx, R.string.printer_bank_card_voucher).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite(PrinterCommand.setFontBold(0));
					printerWrite(PrinterCommand.setAlignMode(0));
					printerWrite(PrinterCommand.setFontEnlarge(0));

					//set line space
					printerWrite(FormatSettingCommand.getESC3n((byte) 3));
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

					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_operator) + trans.getOper()).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite(Env.getResourceString(ctx, R.string.printer_tag_pay_type).getBytes("GB2312"));
					// printerWrite(PrinterCommand.linefeed());

					// set bold font
					printerWrite(PrinterCommand.setFontBold(1));
					if (trans.getTransType() == TRAN_VOID) {
						printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_reverse).getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_SALE) {
						printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_sale).getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_REFUND) {
						printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_refund).getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH) {
						printerWrite("预授权".getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH_CANCEL) {
						printerWrite("预授权撤销".getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH_COMPLETE) {
						printerWrite("预授权完成联机".getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL) {
						printerWrite("预授权完成撤销".getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH_SETTLEMENT) {
						printerWrite("预授权完成离线".getBytes("GB2312"));
					} else {
						String str = "type =" + trans.getTransType();
						printerWrite(str.getBytes("GB2312"));
					}
					printerWrite(PrinterCommand.setFontBold(0));
					printerWrite(PrinterCommand.linefeed());

					printerWrite((Env.getResourceString(ctx, R.string.printer_batch_no) + StringUtil.fillZero(
							Integer.toString(trans.getOldBatch()), 6)).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite((Env.getResourceString(ctx, R.string.printer_voucher_no)
							+ StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + " "
							+ Env.getResourceString(ctx, R.string.printer_auth_no) + trans.getOldAuthCode()).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					String localTime = convertTimeToLocal(trans);
					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_date_time) + " " + localTime.substring(0, 4)
							+ "/" + localTime.substring(4, 6)
							+ "/" + localTime.substring(6, 8)
							+ " " + localTime.substring(8, 10)
							+ ":" + localTime.substring(10, 12)
							+ ":" + localTime.substring(12, 14)).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite("--------------------------------".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_channel) + trans.getPaymentName()).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					//ref no.
					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_ref) + trans.getOldApOrderId()).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					//Issuer name
					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_issuer_name)).getBytes("GB2312"));
					printerWrite(bankName.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite(Env.getResourceString(ctx, R.string.printer_bank_card).getBytes("GB2312"));
					// 增大字体：高度增加1倍，宽度不变
					printerWrite(PrinterCommand.setFontEnlarge(0x01));
					String pan = "";
					pan = trans.getOldPan();
					String tempPan = pan.substring(0, 6) + "******"
							+ pan.substring(pan.length() - 4, pan.length());
					pan = tempPan;
					printerWrite(pan.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					// 结束字体放大
					printerWrite(PrinterCommand.setFontEnlarge(0));

					String amt = AppUtil.formatAmount(trans.getOldTransAmount()) + " RMB";
					if (trans.getTransType() == TRAN_VOID
							|| trans.getTransType() == TRAN_REFUND) {
						amt = " - " + amt;
					}

					printerWrite(Env.getResourceString(ctx, R.string.printer_tag_amount).getBytes("GB2312"));
					// 增大字体：高度增加1倍，宽度不变
					printerWrite(PrinterCommand.setFontEnlarge(0x01));
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

					printerWrite("--------------------------------".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					if (i == 0) {
						String sig = Env.getResourceString(ctx, R.string.printer_tag_signature);
						printerWrite(sig.getBytes("GB2312"));
						printerWrite(PrinterCommand.linefeed());
						printerWrite(PrinterCommand.linefeed());
						printerWrite(PrinterCommand.linefeed());
						printerWrite("--------------------------------".getBytes("GB2312"));
						printerWrite(PrinterCommand.linefeed());
					}

					printerWrite(Env.getResourceString(ctx, R.string.printer_msg_acknowledge_card).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());

					if (i == 0) {
						Thread.currentThread().sleep(PRINTER_DELAY_TIME);
					}
				}
			}else{//英文版本
				for (int i = 0; i < 2; i++) {
					printerWrite(PrinterCommand.setFontBold(1));
					printerWrite(PrinterCommand.setAlignMode(1));
					printerWrite(PrinterCommand.setFontEnlarge(0x01));
					printerWrite(Env.getResourceString(ctx, R.string.printer_bank_card_voucher).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite(PrinterCommand.setFontBold(0));
					printerWrite(PrinterCommand.setAlignMode(0));
					printerWrite(PrinterCommand.setFontEnlarge(0));

					//set line space
					printerWrite(FormatSettingCommand.getESC3n((byte) 3));
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

					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_trans_no) + "\n" + trans.getTxnId()).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_operator) + trans.getOper()).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite(Env.getResourceString(ctx, R.string.printer_tag_pay_type).getBytes("GB2312"));
					// printerWrite(PrinterCommand.linefeed());
					// set bold font
					printerWrite(PrinterCommand.setFontBold(1));
					if (trans.getTransType() == TRAN_VOID) {
						printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_reverse).getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_SALE) {
						printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_sale).getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_REFUND) {
						printerWrite(Env.getResourceString(ctx, R.string.printer_tran_type_refund).getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH) {
						printerWrite("预授权".getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH_CANCEL) {
						printerWrite("预授权撤销".getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH_COMPLETE) {
						printerWrite("预授权完成联机".getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH_COMPLETE_CANCEL) {
						printerWrite("预授权完成撤销".getBytes("GB2312"));
					} else if (trans.getTransType() == TRAN_AUTH_SETTLEMENT) {
						printerWrite("预授权完成离线".getBytes("GB2312"));
					} else {
						String str = "type =" + trans.getTransType();
						printerWrite(str.getBytes("GB2312"));
					}
					printerWrite(PrinterCommand.setFontBold(0));
					printerWrite(PrinterCommand.linefeed());

					printerWrite((Env.getResourceString(ctx, R.string.printer_batch_no) + StringUtil.fillZero(
							Integer.toString(trans.getOldBatch()), 6)).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite((Env.getResourceString(ctx, R.string.printer_voucher_no)
							+ StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + "\n"
							+ Env.getResourceString(ctx, R.string.printer_auth_no) + trans.getOldAuthCode()).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					String localTime = convertTimeToLocal(trans);
					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_date_time) + "\n" + localTime.substring(0, 4)
							+ "/" + localTime.substring(4, 6)
							+ "/" + localTime.substring(6, 8)
							+ " " + localTime.substring(8, 10)
							+ ":" + localTime.substring(10, 12)
							+ ":" + localTime.substring(12, 14)).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite("--------------------------------".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_channel) + trans.getPaymentName()).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					//ref no.
					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_ref) + trans.getOldApOrderId()).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					//Issuer name
					printerWrite((Env.getResourceString(ctx, R.string.printer_tag_issuer_name)).getBytes("GB2312"));
					printerWrite(bankName.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					printerWrite(Env.getResourceString(ctx, R.string.printer_bank_card).getBytes("GB2312"));
					// 增大字体：高度增加1倍，宽度不变
					printerWrite(PrinterCommand.setFontEnlarge(0x01));
					String pan = "";
					pan = trans.getOldPan();
					String tempPan = pan.substring(0, 6) + "******"
							+ pan.substring(pan.length() - 4, pan.length());
					pan = tempPan;
					printerWrite(pan.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					// 结束字体放大
					printerWrite(PrinterCommand.setFontEnlarge(0));

					String amt = AppUtil.formatAmount(trans.getOldTransAmount()) + " RMB";
					if (trans.getTransType() == TRAN_VOID
							|| trans.getTransType() == TRAN_REFUND) {
						amt = " - " + amt;
					}

					printerWrite(Env.getResourceString(ctx, R.string.printer_tag_amount).getBytes("GB2312"));
					// 增大字体：高度增加1倍，宽度不变
					printerWrite(PrinterCommand.setFontEnlarge(0x01));

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

					printerWrite("--------------------------------".getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());

					if (i == 0) {
						String sig = Env.getResourceString(ctx, R.string.printer_tag_signature);
						printerWrite(sig.getBytes("GB2312"));
						printerWrite(PrinterCommand.linefeed());
						printerWrite(PrinterCommand.linefeed());
						printerWrite(PrinterCommand.linefeed());
						printerWrite("--------------------------------".getBytes("GB2312"));
						printerWrite(PrinterCommand.linefeed());
					}

					printerWrite(Env.getResourceString(ctx, R.string.printer_msg_acknowledge_card).getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());
					printerWrite(PrinterCommand.linefeed());

					if (i == 0) {
						Thread.currentThread().sleep(PRINTER_DELAY_TIME);
					}
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
            isPrinting = false;
		}
	}

    synchronized public void printTransferReceipt(OldTrans trans) throws PrinterException {
        if (isPrinting) {
            return;
        }
        try {
            PrinterInterface.open();
            PrinterInterface.set(1);

            isPrinting = true;
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

            //get issuer name
            BankDB bankDB = BankDB.getInstance(ctx);
            String bankName = bankDB.getBankNameByIssuerId(trans.getOldIssuerID());
            bankDB.closeDB();
            //set line space
            printerWrite(FormatSettingCommand.getESC3n((byte) 3));
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

                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_ref) + trans.getOldApOrderId()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

	            printerWrite((Env.getResourceString(ctx, R.string.printer_tag_operator) + trans.getOper()).getBytes("GB2312"));
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
                        + StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + "\n"
                        + Env.getResourceString(ctx, R.string.printer_auth_no) + trans.getOldAuthCode()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

	            String localTime = convertTimeToLocal(trans);
	            printerWrite((Env.getResourceString(ctx, R.string.printer_tag_date_time) + " " + localTime.substring(0,4)
			            + "/" + localTime.substring(4, 6)
			            + "/" + localTime.substring(6, 8)
			            + " " + localTime.substring(8, 10)
			            + ":" + localTime.substring(10, 12)
			            + ":" + localTime.substring(12, 14)).getBytes("GB2312"));
	            printerWrite(PrinterCommand.linefeed());

                printerWrite("--------------------------------".getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_channel) + trans.getPaymentName()).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());

                //Issuer name
                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_issuer_name)).getBytes("GB2312"));

                printerWrite(bankName.getBytes("GB2312"));
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

                String amt = AppUtil.formatAmount(trans.getOldTransAmount() - 1000) + " RMB";
                if (trans.getTransType() == TRAN_VOID
                        || trans.getTransType() == TRAN_REFUND) {
                    amt = " - " + amt;
                }

                printerWrite(Env.getResourceString(ctx, R.string.printer_tag_trans_amount).getBytes("GB2312"));
                printerWrite(PrinterCommand.setFontBold(1));
                printerWrite((amt).getBytes("GB2312"));
                printerWrite(PrinterCommand.setFontBold(0));
                printerWrite(PrinterCommand.linefeed());

                printerWrite((Env.getResourceString(ctx, R.string.printer_tag_trans_fee) + 10.00 + " RMB").getBytes("GB2312"));
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
                    printerWrite(PrinterCommand.linefeed());
                    printerWrite(PrinterCommand.linefeed());
                    printerWrite(PrinterCommand.linefeed());
                    printerWrite("--------------------------------".getBytes("GB2312"));
                    printerWrite(PrinterCommand.linefeed());
                }

                printerWrite(Env.getResourceString(ctx, R.string.printer_msg_acknowledge_card).getBytes("GB2312"));
                printerWrite(PrinterCommand.linefeed());
                printerWrite(PrinterCommand.linefeed());
                printerWrite(PrinterCommand.linefeed());
                printerWrite(PrinterCommand.linefeed());
                printerWrite(PrinterCommand.linefeed());
                printerWrite(PrinterCommand.linefeed());

                if (i == 0) {
                    Thread.currentThread().sleep(PRINTER_DELAY_TIME);
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
            isPrinting = false;
        }
    }

	synchronized public void printMisposReceipt(MisposData misposBean)
			throws PrinterException {
        if (isPrinting) {
            return;
        }
		try {
			PrinterInterface.open();
			PrinterInterface.set(1);
			PrinterInterface.begin();
			PrinterInterface.end();
			// printerWrite(PrinterCommand.init());
			// printerWrite(PrinterCommand.setHeatTime(180));

			PrinterInterface.begin();

            isPrinting = true;

            //set line space
            printerWrite(FormatSettingCommand.getESC3n((byte) 3));
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


				printerWrite(("操作员:" + misposBean.getOperatorId())
						.getBytes("GB2312"));
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
				} else if (misposBean.getTransType().equals(
                        MisposOperationUtil.TRAN_TYPE_CONSUMPTION_TRANSFER)) {
                    printerWrite("助农取款".getBytes("GB2312"));
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

				printerWrite(("日期/时间:" + " "
						+ ("" + misposBean.getTranDate().substring(0, 4)) + "/"
						+ ("" + misposBean.getTranDate().substring(4, 6)) + "/"
						+ ("" + misposBean.getTranDate().substring(6, 8)) + " "
						+ ("" + misposBean.getTranTime().substring(0, 2)) + ":"
						+ ("" + misposBean.getTranTime().substring(2, 4)) + ":" + ("" + misposBean
						.getTranTime().substring(4, 6))).getBytes("GB2312"));
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
                    printerWrite(PrinterCommand.linefeed());
                    printerWrite(PrinterCommand.linefeed());
                    printerWrite(PrinterCommand.linefeed());
					printerWrite("--------------------------------"
							.getBytes("GB2312"));
					printerWrite(PrinterCommand.linefeed());
				}

				printerWrite("本人确认以上交易，同意将其记入本卡账户".getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				printerWrite(PrinterCommand.linefeed());
				printerWrite(PrinterCommand.linefeed());
				printerWrite(PrinterCommand.linefeed());
				printerWrite(PrinterCommand.linefeed());
				printerWrite(PrinterCommand.linefeed());

				if (i == 0) {
					Thread.currentThread().sleep(PRINTER_DELAY_TIME);
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
            isPrinting = false;
		}
	}

	synchronized public void printTransSummary(JSONObject printData)
			throws PrinterException {
        if (isPrinting) {
            return;
        }
		try {
			PrinterInterface.open();
			PrinterInterface.set(1);
			PrinterInterface.begin();
			PrinterInterface.end();
			// printerWrite(PrinterCommand.init());
			// printerWrite(PrinterCommand.setHeatTime(180));

			PrinterInterface.begin();

            isPrinting = true;
			printerWrite(FormatSettingCommand.getESC3n((byte) 3));
            //set line space
            printerWrite(FormatSettingCommand.getESC3n((byte) 3));
			printerWrite(PrinterCommand.setFontBold(1));
			printerWrite(PrinterCommand.setAlignMode(1));
			printerWrite(PrinterCommand.setFontEnlarge(0x01));
			printerWrite(Env.getResourceString(ctx, R.string.printer_summary_title).getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite(PrinterCommand.setFontBold(0));
			printerWrite(PrinterCommand.setAlignMode(0));
			printerWrite(PrinterCommand.setFontEnlarge(0));
			printerWrite(FormatSettingCommand.getESC3n((byte) 3));
            //set line space
            printerWrite(FormatSettingCommand.getESC3n((byte) 3));
			printerWrite("--------------------------------".getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite((Env.getResourceString(ctx, R.string.printer_tag_merchant) + printData
					.optString(ConstantUtils.FOR_PRINT_MERCHANT_NAME))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite((Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid) + printData
					.optString(ConstantUtils.FOR_PRINT_MERCHANT_ID))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite((Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid) + printData
					.optString(ConstantUtils.FOR_PRINT_MECHINE_ID))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite((Env.getResourceString(ctx, R.string.printer_tag_operator) + printData
					.optString(ConstantUtils.FOR_PRINT_OPERATOR))
					.getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite((Env.getResourceString(ctx, R.string.printer_tag_date_time)).getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite(getCurrentTime().getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			printerWrite("--------------------------------".getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			String amount = Env.getResourceString(ctx, R.string.printer_summary_amount) + "(" + Env.getCurrencyResource(ctx) + ")";
			printerWrite((Env.getResourceString(ctx, R.string.printer_summary_transType) + "    " + Env.getResourceString(ctx, R.string.printer_summary_count) + "  " + amount).getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite("--------------------------------".getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());

			String language = Locale.getDefault().getLanguage();
			if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) { //中文的排版
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
			} else { //英文的排版
				printerWrite((getTransType(printData,
						ConstantUtils.APMP_TRAN_TYPE_CONSUME)
						+ "             "
						+ getTransCount(printData,
						ConstantUtils.APMP_TRAN_TYPE_CONSUME) + "  " + getTransAmount(
						printData, ConstantUtils.APMP_TRAN_TYPE_CONSUME))
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				printerWrite((getTransType(printData,
						ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE)
						+ "             "
						+ getTransCount(printData,
						ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE)
						+ "  " + getTransAmount(printData,
						ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE))
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				printerWrite((getTransType(printData,
						ConstantUtils.APMP_TRAN_TYPE_PREAUTH)
						+ "             "
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
						+ " "
						+ getTransCount(printData,
						ConstantUtils.APMP_TRAN_TYPE_PRAUTHSETTLEMENT)
						+ "  " + getTransAmount(printData,
						ConstantUtils.APMP_TRAN_TYPE_PRAUTHSETTLEMENT))
						.getBytes("GB2312"));
				printerWrite(PrinterCommand.linefeed());
				printerWrite((getTransType(printData,
						ConstantUtils.APMP_TRAN_TYPE_PRAUTHCANCEL)
						+ "        "
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

			}
			printerWrite("--------------------------------".getBytes("GB2312"));
			printerWrite((Env.getResourceString(ctx, R.string.printer_summary_totalAmount)).getBytes("GB2312"));
			printerWrite(PrinterCommand.setFontEnlarge(0x01));
			printerWrite((printData.optString(ConstantUtils.FOR_TYPE_TOTALAMOUNT)).getBytes("GB2312"));
			printerWrite(PrinterCommand.setFontEnlarge(0));
			printerWrite(("  (" + Env.getCurrencyResource(ctx) + ")").getBytes("GB2312"));
			printerWrite(PrinterCommand.linefeed());
			printerWrite(PrinterCommand.linefeed());
			printerWrite(PrinterCommand.linefeed());
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
            isPrinting = false;
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
        PrinterImpl mPrinterImpl = new PrinterImpl();
        if (isPrinting) {
            return;
        }
        try {
            String language = Locale.getDefault().getLanguage();
            mPrinterImpl.open();
            isPrinting = true;

            //set line space
            mPrinterImpl.setLineSpacing(20);
            for (int i = 0; i < 2; i++) {

                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_CENTER);

                Drawable mDrawable = ctx.getResources().getDrawable(R.drawable.alipay);
                Bitmap mBitMap = ((BitmapDrawable) mDrawable).getBitmap();
                mPrinterImpl.printerBitmap(Bitmap.createScaledBitmap(mBitMap, 250, 80, false));

                mPrinterImpl.setLineSpacing(20);
                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_LEFT);

                if (i == 0) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_merchant_copy));
                    printerWrite(PrinterCommand.linefeed());
                } else if (i == 1) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_cardhoder_copy));

                    printerWrite(PrinterCommand.linefeed());
                }

                mPrinterImpl.printText(TAG_LINE2 + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_merchant));

                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.printText(trans.getOldMertName() + "\n");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);


                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid) + trans.getKoolCloudMID() + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid) + trans.getKoolCloudTID() + "\n");
                // control.printText(TAG_AP_NAME + trans.getPaymentName() +
                // "\n");
                // control.printText(TAG_TERMINAL + trans.getOldTID() + "\n");

                String transStr = "";
                if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) {
                    transStr = Env.getResourceString(ctx, R.string.printer_tag_trans_no) + trans.getTxnId() + "\n";
                } else {
                    transStr = Env.getResourceString(ctx, R.string.printer_tag_trans_no) + "\n" + trans.getTxnId() + "\n";
                }
                mPrinterImpl.printText(transStr);

	            mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_operator) + trans.getOper() + "\n");

//				control.printText(TAG_DATE_TIME + Utils.getCurrentDate() + " ");

//				control.printText(/* TAG_TIME + */Utils.getCurrentTime() + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_pay_type));

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

                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.setAlign(Align.LEFT);
                mPrinterImpl.printText(transType + "\n");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);
	            //print batch no
	            mPrinterImpl.setFontSize(FontType.NORMAL);
	            mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_batch_no));
	            //                printerWrite(PrinterCommand.linefeed());
	            mPrinterImpl.printText(StringUtil.fillZero(Integer.toString(trans.getOldBatch()), 6) + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_trace) + StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + "\n");

	            String localTime = convertTimeToLocal(trans);

                String tranTimeStr = "";
                if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) {
                    tranTimeStr = Env.getResourceString(ctx, R.string.printer_tag_date_time) + " " + localTime.substring(0,4)
                            + "/" + localTime.substring(4, 6)
                            + "/" + localTime.substring(6, 8)
                            + " " + localTime.substring(8, 10)
                            + ":" + localTime.substring(10, 12)
                            + ":" + localTime.substring(12, 14);
                } else {
                    tranTimeStr = Env.getResourceString(ctx, R.string.printer_tag_date_time) + "\n" + localTime.substring(0,4)
                            + "/" + localTime.substring(4, 6)
                            + "/" + localTime.substring(6, 8)
                            + " " + localTime.substring(8, 10)
                            + ":" + localTime.substring(10, 12)
                            + ":" + localTime.substring(12, 14);
                }

	            mPrinterImpl.printText(tranTimeStr);
	            mPrinterImpl.printText("\n");

                mPrinterImpl.printText(TAG_LINE2 + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_channel) + trans.getPaymentName() + "\n");

                if (trans.getTransType() == TRAN_SALE) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_account) + trans.getAlipayAccount()
                            + "\n");
                }

                String amt = AppUtil.formatAmount(trans.getOldTransAmount());
                if (trans.getTransType() == TRAN_VOID
                        || trans.getTransType() == TRAN_REFUND) {
                    amt = " - " + amt;
                }

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_amount));
                mPrinterImpl.setDoubleFormat(BasePrinterInterface.DOUBLE_WIDTH_HEIGHT);
                mPrinterImpl.setPrinterMode(FontType.BOLD);
                mPrinterImpl.printText(amt);
                mPrinterImpl.setPrinterMode(0);
                mPrinterImpl.setDoubleFormat(0);
                mPrinterImpl.printText("\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_transaction_id) + "\n");
                mPrinterImpl.printText(trans.getAlipayTransactionID() + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_partner_id) + "\n");
                mPrinterImpl.printText(trans.getAlipayPId() + "\n");

                String number = trans.getOldApOrderId();

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_merchant_receipt_id) + "\n");
                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.printText(number + "\n");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);

                // not record the setting state, resetting it after finishing
                // control.sendESC(FormatSettingCommand.getESCan(Align.CENTER));
                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_CENTER);
                mPrinterImpl.printTwoDBarCode(number, QRCODE_WIDTH, QRCODE_HEIGHT);

                mPrinterImpl.setAlign(Align.LEFT);
                mPrinterImpl.printText(TAG_LINE2 + "\n");

                if (i == 0) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_signature) + "\n\n\n");
                    mPrinterImpl.printText(TAG_LINE2 + "\n");
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_msg_acknowledge));
                    mPrinterImpl.printText("\n\n\n\n\n");

                    Thread.currentThread().sleep(PRINTER_DELAY_TIME);
                } else {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_msg_acknowledge));
                    mPrinterImpl.printText("\n\n\n\n\n");
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
                mPrinterImpl.close();
                isPrinting = false;
            } catch (AccessException e) {
                isPrinting = false;
                e.printStackTrace();
            }

        }
    }

    public void printBaiduReceipt(OldTrans trans) {
        PrinterImpl mPrinterImpl = new PrinterImpl();
        if (isPrinting) {
            return;
        }
        try {
            String language = Locale.getDefault().getLanguage();
            mPrinterImpl.open();
            isPrinting = true;

            mPrinterImpl.setLineSpacing(20);
            for (int i = 0; i < 2; i++) {

                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_CENTER);

                Drawable mDrawable = ctx.getResources().getDrawable(R.drawable.baidu_print_logo);
                Bitmap mBitMap = ((BitmapDrawable) mDrawable).getBitmap();
                mPrinterImpl.printerBitmap(Bitmap.createScaledBitmap(mBitMap, 280, 80, false));

                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_LEFT);

                mPrinterImpl.setLineSpacing(20);
                if (i == 0) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_merchant_copy));
                    printerWrite(PrinterCommand.linefeed());
                } else if (i == 1) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_cardhoder_copy));

                    printerWrite(PrinterCommand.linefeed());
                }

                mPrinterImpl.printText(TAG_LINE2 + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_merchant));

                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.printText(trans.getOldMertName() + "\n");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid) + trans.getKoolCloudMID() + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid) + trans.getKoolCloudTID() + "\n");
                // control.printText(TAG_AP_NAME + trans.getPaymentName() +
                // "\n");
                // control.printText(TAG_TERMINAL + trans.getOldTID() + "\n");

                String transStr = "";
                if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) {
                    transStr = Env.getResourceString(ctx, R.string.printer_tag_trans_no) + trans.getTxnId() + "\n";
                } else {
                    transStr = Env.getResourceString(ctx, R.string.printer_tag_trans_no) + "\n" + trans.getTxnId() + "\n";
                }

                mPrinterImpl.printText(transStr);

//				control.printText(TAG_DATE_TIME + Utils.getCurrentDate() + " ");

//				control.printText(/* TAG_TIME + */Utils.getCurrentTime() + "\n");
	            mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_operator) + trans.getOper() + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_pay_type));

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

                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.setAlign(Align.LEFT);
                mPrinterImpl.printText(transType + "\n");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);

	            //print batch no
	            mPrinterImpl.setFontSize(FontType.NORMAL);
	            mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_batch_no));
	            //                printerWrite(PrinterCommand.linefeed());
	            mPrinterImpl.printText(StringUtil.fillZero(Integer.toString(trans.getOldBatch()), 6) + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_trace) + StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + "\n");

	            String localTime = convertTimeToLocal(trans);

                String tranTimeStr = "";
                if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) {
                    tranTimeStr = Env.getResourceString(ctx, R.string.printer_tag_date_time) + " " + localTime.substring(0,4)
                            + "/" + localTime.substring(4, 6)
                            + "/" + localTime.substring(6, 8)
                            + " " + localTime.substring(8, 10)
                            + ":" + localTime.substring(10, 12)
                            + ":" + localTime.substring(12, 14);
                } else {
                    tranTimeStr = Env.getResourceString(ctx, R.string.printer_tag_date_time) + "\n" + localTime.substring(0,4)
                            + "/" + localTime.substring(4, 6)
                            + "/" + localTime.substring(6, 8)
                            + " " + localTime.substring(8, 10)
                            + ":" + localTime.substring(10, 12)
                            + ":" + localTime.substring(12, 14);
                }


	            mPrinterImpl.printText(tranTimeStr);
	            mPrinterImpl.printText("\n");

                mPrinterImpl.printText(TAG_LINE2 + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_channel) + trans.getPaymentName() + "\n");

                String amt = AppUtil.formatAmount(trans.getOldTransAmount());
                if (trans.getTransType() == TRAN_VOID
                        || trans.getTransType() == TRAN_REFUND) {
                    amt = " - " + amt;
                }

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_amount));
                mPrinterImpl.setDoubleFormat(BasePrinterInterface.DOUBLE_WIDTH_HEIGHT);
                mPrinterImpl.setPrinterMode(FontType.BOLD);
                mPrinterImpl.printText(amt);
                mPrinterImpl.setPrinterMode(0);
                mPrinterImpl.setDoubleFormat(0);
                mPrinterImpl.printText("\n");

//                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_baidu_transaction_id) + "\n");
//                mPrinterImpl.printText(trans.getAlipayTransactionID() + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_baidu_partner_id) + "\n");
                mPrinterImpl.printText(trans.getAlipayPId() + "\n");

                String number = trans.getOldApOrderId();

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_merchant_receipt_id) + "\n");
                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.printText(number + "\n");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);

                mPrinterImpl.setAlign(Align.LEFT);
                mPrinterImpl.printText(TAG_LINE2 + "\n");

                if (i == 0) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_signature) + "\n\n\n");
                    mPrinterImpl.printText(TAG_LINE2 + "\n");
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_baidu_msg_acknowledge));
                    mPrinterImpl.printText("\n\n\n\n\n");

                    Thread.currentThread().sleep(PRINTER_DELAY_TIME);
                } else {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_baidu_msg_acknowledge));
                    mPrinterImpl.printText("\n\n\n\n\n");
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
                mPrinterImpl.close();
                isPrinting = false;
            } catch (AccessException e) {
                isPrinting = false;
                e.printStackTrace();
            }

        }
    }

    public void printWeChatReceipt(OldTrans trans) {
        PrinterImpl mPrinterImpl = new PrinterImpl();
        if (isPrinting) {
            return;
        }
        try {
            String language = Locale.getDefault().getLanguage();
            mPrinterImpl.open();
            isPrinting = true;

            mPrinterImpl.setLineSpacing(20);
            for (int i = 0; i < 2; i++) {

                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_CENTER);

                Drawable mDrawable = ctx.getResources().getDrawable(R.drawable.wechat_print_light);
                Bitmap mBitMap = ((BitmapDrawable) mDrawable).getBitmap();
                mPrinterImpl.printerBitmap(Bitmap.createScaledBitmap(mBitMap, 280, 80, false));

                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_LEFT);

                mPrinterImpl.setLineSpacing(20);
                if (i == 0) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_merchant_copy));
                    printerWrite(PrinterCommand.linefeed());
                } else if (i == 1) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_cardhoder_copy));

                    printerWrite(PrinterCommand.linefeed());
                }

                mPrinterImpl.printText(TAG_LINE2 + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_merchant));

                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.printText(trans.getOldMertName() + "\n");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);


                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid) + trans.getKoolCloudMID() + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid) + trans.getKoolCloudTID() + "\n");
                // control.printText(TAG_AP_NAME + trans.getPaymentName() +
                // "\n");
                // control.printText(TAG_TERMINAL + trans.getOldTID() + "\n");

                String transStr = "";
                if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) {
                    transStr = Env.getResourceString(ctx, R.string.printer_tag_trans_no) + trans.getTxnId() + "\n";
                } else {
                    transStr = Env.getResourceString(ctx, R.string.printer_tag_trans_no) + "\n" + trans.getTxnId() + "\n";
                }
                mPrinterImpl.printText(transStr);

//				control.printText(TAG_DATE_TIME + Utils.getCurrentDate() + " ");

//				control.printText(/* TAG_TIME + */Utils.getCurrentTime() + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_operator) + trans.getOper() + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_pay_type));

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

                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.setAlign(Align.LEFT);
                mPrinterImpl.printText(transType + "\n");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);
	            //print batch no
	            mPrinterImpl.setFontSize(FontType.NORMAL);
	            mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_batch_no));
	            //                printerWrite(PrinterCommand.linefeed());
	            mPrinterImpl.printText(StringUtil.fillZero(Integer.toString(trans.getOldBatch()), 6) + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_trace) + StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + "\n");

                String localTime = convertTimeToLocal(trans);

                String tranTimeStr = "";
                if (!TextUtils.isEmpty(language) && language.equals(ConstantUtils.LANGUAGE_CHINESE)) {
                    tranTimeStr = Env.getResourceString(ctx, R.string.printer_tag_date_time) + " " + localTime.substring(0,4)
                            + "/" + localTime.substring(4, 6)
                            + "/" + localTime.substring(6, 8)
                            + " " + localTime.substring(8, 10)
                            + ":" + localTime.substring(10, 12)
                            + ":" + localTime.substring(12, 14);
                } else {
                    tranTimeStr = Env.getResourceString(ctx, R.string.printer_tag_date_time) + "\n" + localTime.substring(0,4)
                            + "/" + localTime.substring(4, 6)
                            + "/" + localTime.substring(6, 8)
                            + " " + localTime.substring(8, 10)
                            + ":" + localTime.substring(10, 12)
                            + ":" + localTime.substring(12, 14);
                }

                mPrinterImpl.printText(tranTimeStr);
                mPrinterImpl.printText("\n");

                mPrinterImpl.printText(TAG_LINE2 + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_channel) + trans.getPaymentName() + "\n");

                String amt = AppUtil.formatAmount(trans.getOldTransAmount());
                if (trans.getTransType() == TRAN_VOID
                        || trans.getTransType() == TRAN_REFUND) {
                    amt = " - " + amt;
                }

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_amount));
                mPrinterImpl.setDoubleFormat(BasePrinterInterface.DOUBLE_WIDTH_HEIGHT);
                mPrinterImpl.setPrinterMode(FontType.BOLD);
                mPrinterImpl.printText(amt);
                mPrinterImpl.setPrinterMode(0);
                mPrinterImpl.setDoubleFormat(0);
                mPrinterImpl.printText("\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_wechat_transaction_id) + "\n");
                mPrinterImpl.printText(trans.getAlipayTransactionID() + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_wechat_partner_id) + "\n");
                mPrinterImpl.printText(trans.getAlipayPId() + "\n");

                String number = trans.getOldApOrderId();

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_merchant_receipt_id) + "\n");
                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.printText(number + "\n");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);

                mPrinterImpl.setAlign(Align.LEFT);
                mPrinterImpl.printText(TAG_LINE2 + "\n");

                if (i == 0) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_signature) + "\n\n\n");
                    mPrinterImpl.printText(TAG_LINE2 + "\n");
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_wechat_msg_acknowledge));
                    mPrinterImpl.printText("\n\n\n\n\n");

                    Thread.currentThread().sleep(PRINTER_DELAY_TIME);
                } else {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_wechat_msg_acknowledge));
                    mPrinterImpl.printText("\n\n\n\n\n");
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
                mPrinterImpl.close();
                isPrinting = false;
            } catch (AccessException e) {
                isPrinting = false;
                e.printStackTrace();
            }

        }
    }

	// QRCode print
	public void printQRCodeOverSeaReceipt(OldTrans trans) {
        PrinterImpl mPrinterImpl = new PrinterImpl();
        if (isPrinting) {
            return;
        }
		try {

            mPrinterImpl.open();
            isPrinting = true;

			for (int i = 0; i < 2; i++) {

				// control.sendESC(FormatSettingCommand.getESCan(Align.CENTER));
                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_CENTER);

                Drawable mKingPowerDrawable = ctx.getResources().getDrawable(R.drawable.kingpower);
                Bitmap mKingPowerBitMap = ((BitmapDrawable) mKingPowerDrawable).getBitmap();
                mPrinterImpl.printerBitmap(Bitmap.createScaledBitmap(mKingPowerBitMap, 250, 80, false));

                mPrinterImpl.setFontSize(FontType.NORMAL);
                mPrinterImpl.setLineSpacing(20);
                printerWrite(PrinterCommand.linefeed());

//				Drawable mDrawable = ctx.getResources().getDrawable(R.drawable.alipay);
//				Bitmap mBitMap = ((BitmapDrawable) mDrawable).getBitmap();
//                mPrinterImpl.printerBitmap(Bitmap.createScaledBitmap(mBitMap, 250, 80, false));

                mPrinterImpl.setAlign(Align.LEFT);

				if (i == 0) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_merchant_copy));
					printerWrite(PrinterCommand.linefeed());
				} else if (i == 1) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_cardhoder_copy));
					printerWrite(PrinterCommand.linefeed());
				}

                mPrinterImpl.printText(TAG_LINE2 + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_merchant));
//                printerWrite(PrinterCommand.linefeed());

                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.printText(trans.getOldMertName() + "\n");

                mPrinterImpl.setPrinterMode(Depth.NORMAL);
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid));
//                printerWrite(PrinterCommand.linefeed());
                mPrinterImpl.printText(trans.getKoolCloudMID()+ "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid));
//                printerWrite(PrinterCommand.linefeed());
                mPrinterImpl.printText(trans.getKoolCloudTID() + "\n");

                //print reference
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_trans_no));
                printerWrite(PrinterCommand.linefeed());
                mPrinterImpl.printText(trans.getTxnId() + "\n");

				//print operator
				mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_operator));
				//                printerWrite(PrinterCommand.linefeed());
				mPrinterImpl.printText(trans.getOper() + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_pay_type));

                //print tran type
                String transType = "";
//                String bakTransType = "";
                if (trans.getTransType() == TRAN_VOID) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_reverse);
//                    bakTransType = Env.getResourceString(ctx, R.string.printer_tran_type_reverse);
                } else if (trans.getTransType() == TRAN_SALE) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_sale);
//                    bakTransType = Env.getResourceString(ctx, R.string.printer_tran_type_sale);
                } else if (trans.getTransType() == TRAN_REFUND) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_refund);
//                    bakTransType = Env.getResourceString(ctx, R.string.printer_tran_type_refund);
                } else {
                    transType = trans.getTransType() + "";
                }

                if (!TextUtils.isEmpty(trans.getPrintFromTag()) && !trans.getPrintFromTag().equals("undefined")) {
                    transType = Env.getResourceString(ctx, R.string.printer_tran_type_duplicate);
                }

                printerWrite(PrinterCommand.linefeed());
                mPrinterImpl.setFontSize(1);
                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.printText(transType);
                mPrinterImpl.setPrinterMode(Depth.NORMAL);
                mPrinterImpl.setFontSize(0);
                /*mPrinterImpl.setFontSize(0);
                if (!TextUtils.isEmpty(trans.getPrintFromTag()) && !trans.getPrintFromTag().equals("undefined")) {
                    mPrinterImpl.printText("(" + bakTransType + ")");
                }*/
                mPrinterImpl.printText("\n");

				//print batch no
				mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_batch_no));
				//                printerWrite(PrinterCommand.linefeed());
				mPrinterImpl.printText(StringUtil.fillZero(Integer.toString(trans.getOldBatch()), 6) + "\n");
                //print trace no.
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_trace));
//                printerWrite(PrinterCommand.linefeed());
                mPrinterImpl.printText(StringUtil.fillZero(Integer.toString(trans.getOldTrace()), 6) + "\n");

                //print tran date and time
				mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_date_time));
				printerWrite(PrinterCommand.linefeed());
				String localTime = convertTimeToLocal(trans);
				mPrinterImpl.printText(localTime.substring(0,4)
						+ "/" + localTime.substring(4, 6)
						+ "/" + localTime.substring(6, 8)
						+ " " + localTime.substring(8, 10)
						+ ":" + localTime.substring(10, 12)
						+ ":" + localTime.substring(12, 14));
				mPrinterImpl.printText("\n");

                mPrinterImpl.printText(TAG_LINE2 + "\n");

                //print payment name
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_channel));
//                printerWrite(PrinterCommand.linefeed());
                mPrinterImpl.printText(trans.getPaymentName() + "\n");

				if (trans.getTransType() == TRAN_SALE) {
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_account));
                    printerWrite(PrinterCommand.linefeed());
                    mPrinterImpl.printText(trans.getAlipayAccount() + "\n");
                }

				String amt = AppUtil.formatAmount(trans.getOldTransAmount());
				if (trans.getTransType() == TRAN_VOID
						|| trans.getTransType() == TRAN_REFUND) {
					amt = " - " + amt;
				}

                //print amount
                String currency = Env.getCurrencyResource(ctx);
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_amount));
//                printerWrite(PrinterCommand.linefeed());

                mPrinterImpl.setPrinterMode(Depth.DEEP);
                mPrinterImpl.setFontSize(1);
                mPrinterImpl.printText(amt);
                mPrinterImpl.setFontSize(0);
                mPrinterImpl.printText("(" + currency + ")");
                mPrinterImpl.setPrinterMode(Depth.NORMAL);
                mPrinterImpl.printText("\n");

                if (trans.getTransType() == TRAN_SALE) {

                    //print exchange rate
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_exchange_rate));
//                    printerWrite(PrinterCommand.linefeed());
                    mPrinterImpl.printText(trans.getExchangeRate());
                    printerWrite(PrinterCommand.linefeed());

                    //print real amount
                    mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_real_amount));
//                    printerWrite(PrinterCommand.linefeed());
                    mPrinterImpl.printText(trans.getRealAmount());
                    printerWrite(PrinterCommand.linefeed());
                }

                //print alipay params
//                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_transaction_id) + "\n");
//                mPrinterImpl.printText(trans.getAlipayTransactionID() + "\n");
//                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_partner_id) + "\n");
//                mPrinterImpl.printText(trans.getAlipayPId() + "\n");

				String number = trans.getOldApOrderId();

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_merchant_receipt_id) + "\n");

                mPrinterImpl.setPrinterMode(Depth.DEEP);
                if (!TextUtils.isEmpty(number)) {
                    mPrinterImpl.printText(number.substring(0, number.length() - 5));
                    mPrinterImpl.setFontSize(1);
                    mPrinterImpl.printText(number.substring(number.length() - 5));
                    mPrinterImpl.setFontSize(0);
                }
                mPrinterImpl.setPrinterMode(Depth.NORMAL);

				// not record the setting state, resetting it after finishing
                mPrinterImpl.setLineSpacing(20);
                mPrinterImpl.printText("\n");
                mPrinterImpl.setLineSpacing(20);
                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_CENTER);
                mPrinterImpl.printTwoDBarCode(number, 250, 250);

                mPrinterImpl.setAlign(Align.LEFT);
                mPrinterImpl.setLineSpacing(20);
                mPrinterImpl.printText(TAG_LINE2 + "\n");

                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_tag_signature) + "\n\n");
                mPrinterImpl.printText(TAG_LINE2 + "\n");
                mPrinterImpl.printText(Env.getResourceString(ctx, R.string.printer_msg_acknowledge));

                printerWrite(PrinterCommand.linefeed());

                mPrinterImpl.setAlign(BasePrinterInterface.ALIGN_CENTER);
                Drawable mOcgDrawable = ctx.getResources().getDrawable(R.drawable.carrotpay);
                Bitmap mOcgBitMap = ((BitmapDrawable) mOcgDrawable).getBitmap();
                mPrinterImpl.printerBitmap(Bitmap.createScaledBitmap(mOcgBitMap, 255, 80, false));
                mPrinterImpl.setAlign(Align.LEFT);
                mPrinterImpl.printText("\n\n\n");
				if (i == 0) {
                    Thread.currentThread().sleep(PRINTER_DELAY_TIME);
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
                mPrinterImpl.close();
                isPrinting = false;
			} catch (AccessException e) {
                isPrinting = false;
				e.printStackTrace();
			}

		}
	}

    synchronized public void printRecord(ConsumptionRecordBean record, JSONObject printData) throws PrinterException {
        if (isPrinting) {
            return;
        }
        try {
            PrinterInterface.open();
            PrinterInterface.set(1);
            PrinterInterface.begin();
            PrinterInterface.end();

            PrinterInterface.begin();
            isPrinting = true;

            //set line space
            printerWrite(FormatSettingCommand.getESC3n((byte) 3));

            printerWrite(PrinterCommand.setFontBold(1));
            printerWrite(PrinterCommand.setAlignMode(1));
            printerWrite(PrinterCommand.setFontEnlarge(0x01));
            printerWrite(Env.getResourceString(ctx, R.string.printer_pos_consumption_record).getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());

            printerWrite(PrinterCommand.setFontBold(0));
            printerWrite(PrinterCommand.setAlignMode(0));
            printerWrite(PrinterCommand.setFontEnlarge(0));

            //set line space
            printerWrite(FormatSettingCommand.getESC3n((byte) 3));
            printerWrite("--------------------------------".getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());

            printerWrite((Env.getResourceString(ctx, R.string.printer_tag_merchant) + record.getMerchName()).getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());

            printerWrite((Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_mid) + record.getMerchNo()).getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());

            printerWrite((Env.getResourceString(ctx, R.string.printer_tag_kool_cloud_tid) + record.getTermId()).getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());

//            printerWrite((Env.getResourceString(ctx, R.string.printer_batch_no) + StringUtil.fillZero(record.getBatchNo(), 6)).getBytes("GB2312"));
//            printerWrite(PrinterCommand.linefeed());
	        printerWrite((Env.getResourceString(ctx, R.string.printer_tag_operator)).getBytes("GB2312"));
            String operatorStr = record.getOperator();
            if (TextUtils.isEmpty(operatorStr)) {
                operatorStr = Env.getResourceString(ctx, R.string.printer_value_operator);
            }
            printerWrite(operatorStr.getBytes("GB2312"));
	        printerWrite(PrinterCommand.linefeed());
            printerWrite((Env.getResourceString(ctx, R.string.printer_tag_date_time)).getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());
            printerWrite((record.getPrintTime() + "\n").getBytes("GB2312"));

            printerWrite("--------------------------------".getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());
            JSONArray recordItems = printData.optJSONArray("recordList");
            String currency = "(" + Env.getCurrencyResource(ctx) + ")";
            if (recordItems != null && recordItems.length() > 0) {
                for (int i = 0; i < recordItems.length(); i++) {
                    JSONObject item = recordItems.optJSONObject(i);
                    String tranType = item.optString("transType");
                    String tranTypeDesc = "";
                    if (!TextUtils.isEmpty(tranType) && tranType.equals(APMP_TRAN_CONSUME)) {
                        tranTypeDesc = Env.getResourceString(ctx, R.string.printer_tran_type_sale);
                    } else if (!TextUtils.isEmpty(tranType) && tranType.equals(APMP_TRAN_CONSUMECANCE)) {
                        tranTypeDesc = Env.getResourceString(ctx, R.string.printer_tran_type_reverse);
                    } else if (!TextUtils.isEmpty(tranType) && tranType.equals(APMP_TRAN_PRAUTHCOMPLETE)) {
                        tranTypeDesc = Env.getResourceString(ctx, R.string.printer_tran_type_prepaid_complete);
                    } else if (!TextUtils.isEmpty(tranType) && tranType.equals(APMP_TRAN_PRAUTHSETTLEMENT)) {
                        tranTypeDesc = Env.getResourceString(ctx, R.string.printer_tran_type_prepaid_complete_offline);
                    } else if (!TextUtils.isEmpty(tranType) && tranType.equals(APMP_TRAN_PREAUTHCOMPLETECANCEL)) {
                        tranTypeDesc = Env.getResourceString(ctx, R.string.printer_tran_type_prepaid_complete_void);
                    } else {
                        tranTypeDesc = "type = " + tranType;
                    }
                    //set line space
                    printerWrite(FormatSettingCommand.getESC3n((byte) 3));
                    printerWrite((item.optString("refNo")).getBytes("GB2312"));
                    // set bold font
                    printerWrite(PrinterCommand.setFontBold(1));
                    //set double height
                    printerWrite(PrinterCommand.setFontEnlarge(0x01));
                    printerWrite((" " + tranTypeDesc).getBytes("GB2312"));
                    printerWrite(PrinterCommand.setFontEnlarge(0));
                    printerWrite(PrinterCommand.setFontBold(0));
                    printerWrite(PrinterCommand.linefeed());
                    String tempPan = item.optString("cardNo");
                    String cardNo = "";
                    if (!TextUtils.isEmpty(tempPan) && TextUtils.isDigitsOnly(tempPan) && !tempPan.equals("9999999999999999")) {
                        cardNo = tempPan.substring(0, 6) + "*" + tempPan.substring(tempPan.length() - 4, tempPan.length());
                    } else if (!TextUtils.isEmpty(tempPan) && tempPan.equals("9999999999999999")) {
                        cardNo = "";
                    } else {
                        cardNo = tempPan;
                    }

                    String amt = AppUtil.formatAmount(item.optLong("transAmount"));

                    printerWrite((" " + cardNo).getBytes("GB2312"));
                    printerWrite(PrinterCommand.setFontBold(1));
                    printerWrite(PrinterCommand.setFontEnlarge(0x01));
                    printerWrite((" " + amt).getBytes("GB2312"));
                    printerWrite(PrinterCommand.setFontEnlarge(0));
                    printerWrite(PrinterCommand.setFontBold(0));
                    printerWrite((currency + " " + item.optString("authNo")).getBytes("GB2312"));

                    printerWrite(PrinterCommand.linefeed());

                    //TODO:print tran date and time
                    String tranDateTime = item.optString("transTime");
                    String tmpTranTime = "";
                    if (!TextUtils.isEmpty(tranDateTime)) {
                        tmpTranTime = tranDateTime.substring(0, 4) + "/" + tranDateTime.substring(4, 6) + "/"
                                + tranDateTime.substring(6, 8) + " " + tranDateTime.substring(8, 10) + ":"
                                + tranDateTime.substring(10, 12) + ":" + tranDateTime.substring(12);
                    }
                    printerWrite((" " + tmpTranTime + " ").getBytes("GB2312"));
                    printerWrite(item.optString("paymentName").getBytes("GB2312"));
                    if (i != recordItems.length()-1) {
                        printerWrite(PrinterCommand.linefeed());
                        printerWrite(PrinterCommand.linefeed());
                    } else {
                        printerWrite(PrinterCommand.linefeed());
                    }
                }
            }

            printerWrite("--------------------------------".getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());
//            printerWrite(PrinterCommand.linefeed());
//            printerWrite(PrinterCommand.linefeed());

            //TODO print summary

            printerWrite(TAB_CMDS);

            //print title
            printerWrite(Env.getResourceString(ctx, R.string.printer_summary_transType).getBytes("GB2312"));
            printerWrite(PrinterCommand.getCmdHt());
            printerWrite(Env.getResourceString(ctx, R.string.printer_summary_count).getBytes("GB2312"));
            printerWrite(PrinterCommand.getCmdHt());
            printerWrite((Env.getResourceString(ctx, R.string.printer_summary_amount) + currency).getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());
            printerWrite("--------------------------------".getBytes("GB2312"));
            printerWrite(PrinterCommand.linefeed());

            Map<String, String> summaryMap = record.getSummaryMap();

            Iterator keys = summaryMap.entrySet().iterator();
            while (keys.hasNext()) {
                Map.Entry entry = (Map.Entry) keys.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                if (key.equals("total")) {
                    continue;
                } else {
                    String[] itemValue = value.split("-");
                    String tranTypeStr = "";
                    if (key.equals(ConstantUtils.APMP_TRAN_TYPE_CONSUME)) {
                        tranTypeStr = Env.getResourceString(ctx, R.string.printer_tran_type_sale);
                    } else if (key.equals(ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE)) {
                        tranTypeStr = Env.getResourceString(ctx, R.string.printer_tran_type_reverse);
                    } else if (key.equals(ConstantUtils.APMP_TRAN_TYPE_PRAUTHCOMPLETE)) {
                        tranTypeStr = Env.getResourceString(ctx, R.string.printer_tran_type_prepaid_complete);
                    } else if (key.equals(ConstantUtils.APMP_TRAN_TYPE_PRAUTHSETTLEMENT)) {
                        tranTypeStr = Env.getResourceString(ctx, R.string.printer_tran_type_prepaid_complete_offline);
                    } else if (key.equals(ConstantUtils.APMP_TRAN_TYPE_PREAUTHCOMPLETECANCEL)) {
                        tranTypeStr = Env.getResourceString(ctx, R.string.printer_tran_type_prepaid_complete_void);
                    }
                    printerWrite(tranTypeStr.getBytes("GB2312"));
                    printerWrite(PrinterCommand.getCmdHt());
                    printerWrite(itemValue[1].getBytes("GB2312"));

                    printerWrite(PrinterCommand.getCmdHt());
                    printerWrite(FormatSettingCommand.getESCan((byte) 0x50));
//                    printerWrite(PrinterCommand.setFontEnlarge(0x01));
                    printerWrite(AppUtil.formatAmount(Long.parseLong(itemValue[2])).getBytes("GB2312"));
//                    printerWrite(PrinterCommand.setFontEnlarge(0));
                    printerWrite(FormatSettingCommand.getESCan((byte) 0x48));
                }
                printerWrite(PrinterCommand.linefeed());

            }
            printerWrite("--------------------------------".getBytes("GB2312"));
            printerWrite(FormatSettingCommand.getESCan((byte) 0x50));
            if (summaryMap.get("total").equals("0")) {
                printerWrite((Env.getResourceString(ctx, R.string.printer_total_amount)).getBytes("GB2312"));
                printerWrite(PrinterCommand.setFontEnlarge(0x01));
                printerWrite("0.00".getBytes("GB2312"));
                printerWrite(PrinterCommand.setFontEnlarge(0));
                printerWrite(currency.getBytes("GB2312"));
            } else {
                String totalAmount = Env.getResourceString(ctx, R.string.printer_total_amount);
                printerWrite(totalAmount.getBytes("GB2312"));
                printerWrite(PrinterCommand.setFontEnlarge(0x01));
                Long total = Long.parseLong(summaryMap.get("total"));
                String printTotal = "";
                if (total < 0) {
                    printTotal = "-" + AppUtil.formatAmount(Math.abs(total));
                } else {
                    printTotal = AppUtil.formatAmount(total);
                }
                printerWrite(printTotal.getBytes("GB2312"));
                printerWrite(PrinterCommand.setFontEnlarge(0));
                printerWrite(currency.getBytes("GB2312"));
            }
            printerWrite(FormatSettingCommand.getESCan((byte) 0x48));
            printerWrite(PrinterCommand.linefeed());
            printerWrite(PrinterCommand.linefeed());
            printerWrite(PrinterCommand.linefeed());
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
            isPrinting = false;
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
