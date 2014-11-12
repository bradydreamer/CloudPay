package cn.koolcloud.pos.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import cn.koolcloud.pos.entity.AcquireInstituteBean;
import cn.koolcloud.pos.entity.BatchTaskBean;
import cn.koolcloud.pos.service.PaymentInfo;
import cn.koolcloud.pos.util.Logger;
import cn.koolcloud.pos.util.UtilForDataStorage;

/**
 * <p>Title: CacheDB.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: KoolCloud</p>
 * @author 		Teddy
 * @date 		2014-7-11
 * @version 	
 */
public class CacheDB extends BaseSqlAdapter {

	private final static String DATABASE_NAME = "Cache.db";
	private final static int DATABASE_VERSION = 3;
    private final static String BATCH_PROCESSING_TABLE_NAME = "batch_processing_table";
    private final static String ACQUIRE_INSTITUTE_TABLE_NAME = "acquire_institute_table";
    private final static String PAYMENT_ACTIVITY_TABLE_NAME = "payment_activity_table";
    
    private Context context;
    private String dbName;
    
    private static CacheDB cacheDB;
    
    //TODO:batch processing table columns
    private final static String BATCH_TXN_ID = "txnId";
    private final static String BATCH_ORI_TXN_ID = "oriTxnId";
    private final static String BATCH_REF_NUMBER = "refNo";									//RRN
    private final static String BATCH_AUTH_CODE = "authNo";									//Authorization Identification Response
    private final static String BATCH_RESPONSE_CODE = "resCode";
    private final static String BATCH_RESPONSE_MESSAGE = "resMsg";
    private final static String BATCH_ISSUER_ID = "issuerId";
    private final static String BATCH_EXP_DATE = "dateExpr";								//format YYMM
    private final static String BATCH_SETTLEMENT_DATE = "stlmDate";							//settlement date format MMDD
    
    private final static String BATCH_CARD_NUM = "cardNo";
    private final static String BATCH_PAYMENT_ID = "paymentId";
    private final static String BATCH_TRANS_TYPE = "transType";
    private final static String BATCH_BATCH_NO = "batchNo";
    private final static String BATCH_TRACE_NO = "traceNo";
    private final static String BATCH_TRANS_TIME = "transTime";
    private final static String BATCH_TRANS_AMOUNT = "transAmount";
    
    private final static String BATCH_ACTION = "action";
    private final static String BATCH_PRIMARY_KEY_ID = "pk_id";
    
    
    //TODO:acquire institute table columns
    private final static String ACQUIRE_MERCHANT_ID = "brhMchtId";
    private final static String ACQUIRE_TERMINAL_ID = "brhTermId";
    private final static String ACQUIRE_DEVICE_NUM_OF_MERCH = "openBrh";
    private final static String ACQUIRE_IMG_NAME = "imgName";
    private final static String ACQUIRE_INSTITUTE_NAME = "openBrhName";
    private final static String ACQUIRE_MERCH_NUM_OF_MERCH = "mch_no";
    private final static String ACQUIRE_PAYMENT_ID = "paymentId";
    private final static String ACQUIRE_PAYMENT_NAME = "paymentName";
    private final static String ACQUIRE_PRINT_TYPE = "printType";
    private final static String ACQUIRE_PRODUCT_DESC = "prdtDesc";
    private final static String ACQUIRE_PRODUCT_NO = "prdtNo";
    private final static String ACQUIRE_PRODUCT_TITLE = "prdtTitle";
    private final static String ACQUIRE_PRODUCT_TYPE = "prdtType";
    private final static String ACQUIRE_TYPE_ID = "typeId";
    private final static String ACQUIRE_TYPE_NAME = "typeName";
    private final static String ACQUIRE_BRH_KEY_INDEX = "brhKeyIndex";
    private final static String ACQUIRE_BRH_MSG_TYPE = "brhMsgType";
    private final static String ACQUIRE_BRH_MCHT_MCC = "brhMchtMcc";
    private final static String ACQUIRE_TAB_TYPE_ID = "tabType";
    
    private final static String PAYMENT_ACTIVITY_JSON = "payment_json";
  
    private CacheDB(Context ctx, int version) {
    	this.context = ctx;
    	if (dbName == null) {
    		dbName = context.getFileStreamPath(DATABASE_NAME).getAbsolutePath();
    	}
    	
		mDbHelper = new CacheHelper(ctx, DATABASE_NAME, null, version);
    } 
    
    public static CacheDB getInstance(Context ctx) {
    	if (cacheDB == null) {
    		cacheDB = new CacheDB(ctx, DATABASE_VERSION);
    	}
    	return cacheDB;
    }
  
    /**
     * @Title: insertBatchTask
     * @Description: insert batch record
     * @param batchTask
     * @return: void
     */
    public void insertBatchTask(JSONObject batchTaskObj) {
    	
    	String pkStr = batchTaskObj.optString("pk_id");
    	//avoid duplicated data while dealing batch data tasks
    	if (TextUtils.isEmpty(pkStr)) {
    		return;
    	}
        
        ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
        Cursor cursor = null;
		try {
			
			String sql = "INSERT INTO "+ BATCH_PROCESSING_TABLE_NAME +"(" +
					BATCH_PRIMARY_KEY_ID + ", " + 
					BATCH_TXN_ID + ", " + 
					BATCH_ORI_TXN_ID + ", " + 
					BATCH_REF_NUMBER + ", " + 
					BATCH_AUTH_CODE + ", " + 
					BATCH_RESPONSE_CODE + ", " + 
					BATCH_RESPONSE_MESSAGE + ", " + 
					BATCH_ISSUER_ID + ", " + 
					BATCH_EXP_DATE + ", " +
					BATCH_SETTLEMENT_DATE + ", " +
					BATCH_CARD_NUM + ", " +
					BATCH_PAYMENT_ID + ", " +
					BATCH_TRANS_TYPE + ", " +
					BATCH_BATCH_NO + ", " +
					BATCH_TRACE_NO + ", " +
					BATCH_TRANS_TIME + ", " +
					BATCH_TRANS_AMOUNT + ", " +
					BATCH_ACTION + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			String[] params = new String[] {batchTaskObj.optString("pk_id"), batchTaskObj.optString("txnId"), batchTaskObj.optString("oriTxnId"),
					batchTaskObj.optString("refNo"), batchTaskObj.optString("authNo"), batchTaskObj.optString("resCode"),
					batchTaskObj.optString("resMsg"), batchTaskObj.optString("issuerId"), batchTaskObj.optString("dateExpr"),
					batchTaskObj.optString("stlmDate"), batchTaskObj.optString("cardNo"), batchTaskObj.optString("paymentId"),
					batchTaskObj.optString("transType"), batchTaskObj.optString("batchNo"), batchTaskObj.optString("traceNo"),
					batchTaskObj.optString("transTime"), batchTaskObj.optString("transAmount"), batchTaskObj.optString("action")
					};
			sqlList.add(new SQLEntity(sql, params));
			excuteSql(sqlList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				
				cursor.close();
			}
			closeDB();
		}
    }
    
    /**
     * @Title: insertAcquireInstitute
     * @Description: insert acquire institute
     * @param acquireInstituteBean
     * @return: void
     */
    public void insertAcquireInstitute(List<AcquireInstituteBean> acquireInstituteList) {
    	
    	ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
    	Cursor cursor = null;
    	boolean isExist = false;
    	try {
    		String sql = "INSERT INTO "+ ACQUIRE_INSTITUTE_TABLE_NAME +"(" +
    				ACQUIRE_MERCHANT_ID + ", " +
    				ACQUIRE_TERMINAL_ID + ", " +
    				ACQUIRE_DEVICE_NUM_OF_MERCH + ", " +
    				ACQUIRE_IMG_NAME + ", " +
    				ACQUIRE_INSTITUTE_NAME + ", " +
    				ACQUIRE_MERCH_NUM_OF_MERCH + ", " +
    				ACQUIRE_PAYMENT_ID + ", " +
    				ACQUIRE_PAYMENT_NAME + ", " +
    				ACQUIRE_PRINT_TYPE + ", " +
    				ACQUIRE_PRODUCT_DESC + ", " +
    				ACQUIRE_PRODUCT_NO + ", " +
    				ACQUIRE_PRODUCT_TITLE + ", " +
    				ACQUIRE_PRODUCT_TYPE + ", " +
    				ACQUIRE_TYPE_ID + ", " +
    				ACQUIRE_TYPE_NAME + ", " +
    				ACQUIRE_BRH_KEY_INDEX + ", " +
    				ACQUIRE_BRH_MSG_TYPE + ", " +
    				ACQUIRE_BRH_MCHT_MCC + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    		
    		/*if (acquireInstituteBean != null) {
    			
    			cursor = selectAcquireInstituteByMerchantId(acquireInstituteBean.getBrhMchtId());
    			if (cursor.getCount() > 0) {
    				isExist = true;
    			}
    			cursor.close();
    			if (isExist) {
    				return;
    			} else {
    				
    				String[] params = new String[] { acquireInstituteBean.getBrhMchtId(), acquireInstituteBean.getBrhTermId(),
    						acquireInstituteBean.getDeviceNumOfMerch(), acquireInstituteBean.getImgName(),
    						acquireInstituteBean.getInstituteName(), acquireInstituteBean.getMerchNumOfMerch(),
    						acquireInstituteBean.getPaymentId(), acquireInstituteBean.getPaymentName(),
    						acquireInstituteBean.getPrintType(), acquireInstituteBean.getProductDesc(),
    						acquireInstituteBean.getProductNo(), acquireInstituteBean.getProductTitle(),
    						acquireInstituteBean.getProductType(), acquireInstituteBean.getTypeId(),
    						acquireInstituteBean.getTypeName(), acquireInstituteBean.getBrhKeyIndex(),
    						acquireInstituteBean.getBrhMsgType(), acquireInstituteBean.getBrhMchtMcc()
    					};
    				sqlList.add(new SQLEntity(sql, params));
    			}
    		}
    		excuteSql(sqlList);*/
    		
    		if (acquireInstituteList != null && acquireInstituteList.size() > 0) {
				for (int i = 0; i < acquireInstituteList.size(); i++) {
					AcquireInstituteBean acquireInstituteBean = acquireInstituteList.get(i);
					cursor = selectAcquireInstituteByMerchantId(acquireInstituteBean.getBrhMchtId(), acquireInstituteBean.getBrhTermId());
					if (cursor.getCount() > 0) {
						continue;
					}
					cursor.close();
					String[] params = new String[] { acquireInstituteBean.getBrhMchtId(), acquireInstituteBean.getBrhTermId(),
    						acquireInstituteBean.getDeviceNumOfMerch(), acquireInstituteBean.getImgName(),
    						acquireInstituteBean.getInstituteName(), acquireInstituteBean.getMerchNumOfMerch(),
    						acquireInstituteBean.getPaymentId(), acquireInstituteBean.getPaymentName(),
    						acquireInstituteBean.getPrintType(), acquireInstituteBean.getProductDesc(),
    						acquireInstituteBean.getProductNo(), acquireInstituteBean.getProductTitle(),
    						acquireInstituteBean.getProductType(), acquireInstituteBean.getTypeId(),
    						acquireInstituteBean.getTypeName(), acquireInstituteBean.getBrhKeyIndex(),
    						acquireInstituteBean.getBrhMsgType(), acquireInstituteBean.getBrhMchtMcc()
    					};
					sqlList.add(new SQLEntity(sql, params));
				}
				excuteSql(sqlList);
			}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (cursor != null) {
    			
    			cursor.close();
    		}
    		closeDB();
    	}
    }
    
    /**
     * @Title: insertPayment
     * @Description: TODO
     * @param paymentActivity
     * @return: void
     */
    public void insertPayment(List<AcquireInstituteBean> paymentActivity) {
    	
    	ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
    	Cursor cursor = null;
    	try {
    		String sql = "INSERT INTO "+ PAYMENT_ACTIVITY_TABLE_NAME +"(" +
    				ACQUIRE_PAYMENT_ID + ", " +
    				ACQUIRE_PAYMENT_NAME + ", " +
    				ACQUIRE_BRH_KEY_INDEX + ", " +
    				ACQUIRE_MERCHANT_ID + ", " +
    				ACQUIRE_TERMINAL_ID + ", " +
    				ACQUIRE_PRINT_TYPE + ", " +
    				ACQUIRE_PRODUCT_NO + ", " +
    				ACQUIRE_PRODUCT_TITLE + ", " +
    				ACQUIRE_PRODUCT_DESC + ", " +
    				ACQUIRE_DEVICE_NUM_OF_MERCH + ", " +
    				ACQUIRE_INSTITUTE_NAME + ", " +
    				ACQUIRE_TAB_TYPE_ID + ", " +
    				PAYMENT_ACTIVITY_JSON + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    		
    		if (paymentActivity != null && paymentActivity.size() > 0) {
    			for (int i = 0; i < paymentActivity.size(); i++) {
    				AcquireInstituteBean acquireInstituteBean = paymentActivity.get(i);
    				cursor = selectPaymentByPaymentId(acquireInstituteBean.getPaymentId());
    				if (cursor.getCount() > 0) {
    					continue;
    				}
    				cursor.close();
    				String[] params = new String[] { acquireInstituteBean.getPaymentId(), acquireInstituteBean.getPaymentName(),
    						acquireInstituteBean.getBrhKeyIndex(), acquireInstituteBean.getBrhMchtId(),
    						acquireInstituteBean.getBrhTermId(), acquireInstituteBean.getPrintType(), 
    						acquireInstituteBean.getProductNo(), acquireInstituteBean.getProductTitle(), 
    						acquireInstituteBean.getProductDesc(), acquireInstituteBean.getDeviceNumOfMerch(),
    						acquireInstituteBean.getInstituteName(), acquireInstituteBean.getTypeId(), acquireInstituteBean.getJsonItem()
    						
    				};
    				sqlList.add(new SQLEntity(sql, params));
    			}
    			excuteSql(sqlList);
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (cursor != null) {
    			
    			cursor.close();
    		}
    		closeDB();
    	}
    }
    
    public void clearBatchTableData() {
    	String sql = "delete from " + BATCH_PROCESSING_TABLE_NAME;
    	
    	try {
			excuteWriteAbleSql(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		closeDB();
    }
    public void clearAcquireInstituteTableData() {
    	String sql = "delete from " + ACQUIRE_INSTITUTE_TABLE_NAME;
    	
    	try {
    		excuteWriteAbleSql(sql);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	closeDB();
    }
    
    public void clearPaymentActivityTableData() {
    	String sql = "delete from " + PAYMENT_ACTIVITY_TABLE_NAME;
    	
    	try {
    		excuteWriteAbleSql(sql);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	closeDB();
    }
    
    public void deleteBatchTaskByPKId(String pkId) { 
        String sql = "delete from " + BATCH_PROCESSING_TABLE_NAME + " where " + BATCH_PRIMARY_KEY_ID + "='" + pkId + "'";
		try {
			excuteSql(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		closeDB();
    }
    
    public Cursor selectBatchTaskByTxnId(String txnId) { 
    	String sql = "select * from " + BATCH_PROCESSING_TABLE_NAME + " where " + BATCH_TXN_ID + " = " + txnId;
    	Cursor cursor = getCursor(sql, null);
    	return cursor; 
    }
    
    public boolean isExistMerchantIdTermId(String merchantId, String terminalId) {
    	boolean result = false;
    	String sql = "select * from " + ACQUIRE_INSTITUTE_TABLE_NAME + 
    				" where " + ACQUIRE_MERCHANT_ID + " = '" + merchantId + "'" + " and " +
    				ACQUIRE_TERMINAL_ID + " = '" + terminalId + "'";
    	
    	Cursor cursor = getCursor(sql, null);
    	if (cursor.getCount() > 0) {
    		result = true;
    	}
    	cursor.close();
    	return result;
    }
    
    public boolean isMisposConfiged() {
    	boolean result = false;
    	String sql = "select * from " + ACQUIRE_INSTITUTE_TABLE_NAME + " where " + ACQUIRE_BRH_KEY_INDEX + " = '90'";
    	
    	Cursor cursor = getCursor(sql, null);
    	if (cursor.getCount() > 0) {
    		result = true;
    	}
    	cursor.close();
    	return result;
    }
    
    public Cursor selectAcquireInstituteByMerchantId(String merchantId, String termId) { 
    	String sql = "select * from " + ACQUIRE_INSTITUTE_TABLE_NAME + " where " + ACQUIRE_MERCHANT_ID + " = '" + merchantId + "'" + " and " + ACQUIRE_TERMINAL_ID + " = '" + termId + "'";
//    	String sql = "select * from " + ACQUIRE_INSTITUTE_TABLE_NAME + " where " + ACQUIRE_MERCHANT_ID + " = ?";
    	
    	Cursor cursor = getCursor(sql, null);
//    	Cursor cursor = getCursor(sql, new String[] { merchantId });
    	return cursor;
    }
    
    private Cursor selectPaymentByPaymentId(String paymentId) { 
    	String sql = "select * from " + PAYMENT_ACTIVITY_TABLE_NAME + " where " + ACQUIRE_PAYMENT_ID + " = '" + paymentId + "'";
    	
    	Cursor cursor = getCursor(sql, null);
    	return cursor;
    }
    
    public int getPaymentsCount() { 
    	String sql = "select count(*) from " + PAYMENT_ACTIVITY_TABLE_NAME;
    	Cursor cursor = getCursor(sql, null);
    	cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
    	return count;
    }
    
    public PaymentInfo getPaymentByPaymentId(String paymentId) { 
    	String sql = "select * from " + PAYMENT_ACTIVITY_TABLE_NAME + " where " + ACQUIRE_PAYMENT_ID + " = '" + paymentId + "'";
    	PaymentInfo paymentInfo = null;
    	Cursor cursor = getCursor(sql, null);
    	if (cursor.getCount() > 0) {
    		cursor.moveToNext();
    		String paymentName = cursor.getString(cursor.getColumnIndex(ACQUIRE_PAYMENT_NAME));
    		String brhKeyIndex = cursor.getString(cursor.getColumnIndex(ACQUIRE_BRH_KEY_INDEX));
    		String prodtNo = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_NO));
    		String prdtTitle = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_TITLE));
    		String prdtDesc = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_DESC));
    		String openBrh = cursor.getString(cursor.getColumnIndex(ACQUIRE_DEVICE_NUM_OF_MERCH));
    		String openBrhName = cursor.getString(cursor.getColumnIndex(ACQUIRE_INSTITUTE_NAME));
    		paymentInfo = new PaymentInfo(paymentId, paymentName, brhKeyIndex, prodtNo, prdtTitle, prdtDesc, openBrh, openBrhName);
    	}
    	
    	cursor.close();
    	return paymentInfo;
    }
    
    public AcquireInstituteBean getAcquireByPaymentId(String paymentId) { 
    	String sql = "select * from " + PAYMENT_ACTIVITY_TABLE_NAME + " where " + ACQUIRE_PAYMENT_ID + " = '" + paymentId + "'";
    	AcquireInstituteBean acquireInfo = null;
    	Cursor cursor = getCursor(sql, null);
    	if (cursor.getCount() > 0) {
    		cursor.moveToNext();
    		String paymentName = cursor.getString(cursor.getColumnIndex(ACQUIRE_PAYMENT_NAME));
    		String brhKeyIndex = cursor.getString(cursor.getColumnIndex(ACQUIRE_BRH_KEY_INDEX));
    		String productNo = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_NO));
    		String prdtTitle = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_TITLE));
    		String prdtDesc = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_DESC));
    		String openBrh = cursor.getString(cursor.getColumnIndex(ACQUIRE_DEVICE_NUM_OF_MERCH));
    		String tabType = cursor.getString(cursor.getColumnIndex(ACQUIRE_TAB_TYPE_ID));
    		String openBrhName = cursor.getString(cursor.getColumnIndex(ACQUIRE_INSTITUTE_NAME));
    		acquireInfo = new AcquireInstituteBean();
    		acquireInfo.setPaymentId(paymentId);
    		acquireInfo.setPaymentName(paymentName);
    		acquireInfo.setBrhKeyIndex(brhKeyIndex);
    		acquireInfo.setProductNo(productNo);
    		acquireInfo.setProductTitle(prdtTitle);
    		acquireInfo.setProductDesc(prdtDesc);
    		acquireInfo.setOpenBrh(openBrh);
    		acquireInfo.setOpenBrhName(openBrhName);
    		acquireInfo.setTypeId(tabType);
    		
    	}
    	
    	cursor.close();
    	return acquireInfo;
    }
    
    public JSONArray selectAllBatchStack() {
    	String sql = "select * from " + BATCH_PROCESSING_TABLE_NAME;
    	JSONArray jsArray = null;
    	Cursor cursor = null;
		try {
			cursor = getCursor(sql, null);
			jsArray = new JSONArray();
			while (cursor.moveToNext()) {
				String pkId = cursor.getString(cursor.getColumnIndex(BATCH_PRIMARY_KEY_ID));
				String txnId = cursor.getString(cursor.getColumnIndex(BATCH_TXN_ID));
				String oriTxnId = cursor.getString(cursor.getColumnIndex(BATCH_ORI_TXN_ID));
				String refNo = cursor.getString(cursor.getColumnIndex(BATCH_REF_NUMBER));
				String authNo = cursor.getString(cursor.getColumnIndex(BATCH_AUTH_CODE));
				String resCode = cursor.getString(cursor.getColumnIndex(BATCH_RESPONSE_CODE));
				String resMsg = cursor.getString(cursor.getColumnIndex(BATCH_RESPONSE_MESSAGE));
				String issuerId = cursor.getString(cursor.getColumnIndex(BATCH_ISSUER_ID));
				String dateExpr = cursor.getString(cursor.getColumnIndex(BATCH_EXP_DATE));
				String stlmDate = cursor.getString(cursor.getColumnIndex(BATCH_SETTLEMENT_DATE));
				String cardNo = cursor.getString(cursor.getColumnIndex(BATCH_CARD_NUM));
				String paymentId = cursor.getString(cursor.getColumnIndex(BATCH_PAYMENT_ID));
				String transType = cursor.getString(cursor.getColumnIndex(BATCH_TRANS_TYPE));
				String batchNo = cursor.getString(cursor.getColumnIndex(BATCH_BATCH_NO));
				String traceNo = cursor.getString(cursor.getColumnIndex(BATCH_TRACE_NO));
				String transTime = cursor.getString(cursor.getColumnIndex(BATCH_TRANS_TIME));
				String transAmount = cursor.getString(cursor.getColumnIndex(BATCH_TRANS_AMOUNT));
				String action = cursor.getString(cursor.getColumnIndex(BATCH_ACTION));
				
				JSONObject jsObj = new JSONObject();
				jsObj.put("pk_id", pkId);
				jsObj.put("txnId", txnId);
				jsObj.put("oriTxnId", oriTxnId);
				jsObj.put("refNo", refNo);
				jsObj.put("authNo", authNo);
				jsObj.put("resCode", resCode);
				jsObj.put("resMsg", resMsg);
				jsObj.put("issuerId", issuerId);
				jsObj.put("dateExpr", dateExpr);
				jsObj.put("stlmDate", stlmDate);
				jsObj.put("cardNo", cardNo);
				jsObj.put("paymentId", paymentId);
				jsObj.put("transType", transType);
				jsObj.put("batchNo", batchNo);
				jsObj.put("traceNo", traceNo);
				jsObj.put("transTime", transTime);
				jsObj.put("transAmount", transAmount);
				jsObj.put("action", action);
				
				jsArray.put(jsObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (cursor != null) {
				
				cursor.close();
			}
		}
    	return jsArray;
    }
    
   /* public List<BatchTaskBean> selectAllBatchList() {
    	String sql = "select * from " + BATCH_PROCESSING_TABLE_NAME;
    	List<BatchTaskBean> batchTaskList = null;
    	Cursor cursor = null;
    	try {
    		cursor = getCursor(sql, null);
    		batchTaskList = new ArrayList<BatchTaskBean>();
    		while (cursor.moveToNext()) {
    			int pkId = cursor.getInt(cursor.getColumnIndex(BATCH_PRIMERY_KEY_ID));
				String txnId = cursor.getString(cursor.getColumnIndex(BATCH_TXN_ID));
				String oriTxnId = cursor.getString(cursor.getColumnIndex(BATCH_ORI_TXN_ID));
				String refNo = cursor.getString(cursor.getColumnIndex(BATCH_REF_NUMBER));
				String authNo = cursor.getString(cursor.getColumnIndex(BATCH_AUTH_CODE));
				String resCode = cursor.getString(cursor.getColumnIndex(BATCH_RESPONSE_CODE));
				String resMsg = cursor.getString(cursor.getColumnIndex(BATCH_RESPONSE_MESSAGE));
				String issuerId = cursor.getString(cursor.getColumnIndex(BATCH_ISSUER_ID));
				String dateExpr = cursor.getString(cursor.getColumnIndex(BATCH_EXP_DATE));
				String stlmDate = cursor.getString(cursor.getColumnIndex(BATCH_SETTLEMENT_DATE));
				String cardNo = cursor.getString(cursor.getColumnIndex(BATCH_CARD_NUM));
				String paymentId = cursor.getString(cursor.getColumnIndex(BATCH_PAYMENT_ID));
				String transType = cursor.getString(cursor.getColumnIndex(BATCH_TRANS_TYPE));
				String batchNo = cursor.getString(cursor.getColumnIndex(BATCH_BATCH_NO));
				String traceNo = cursor.getString(cursor.getColumnIndex(BATCH_TRACE_NO));
				String transTime = cursor.getString(cursor.getColumnIndex(BATCH_TRANS_TIME));
				String transAmount = cursor.getString(cursor.getColumnIndex(BATCH_TRANS_AMOUNT));
				String action = cursor.getString(cursor.getColumnIndex(BATCH_ACTION));
    			
    			BatchTaskBean batchTask = new BatchTaskBean();
    			batchTask.setTxnId(txnId);
    			batchTask.setAuthCode(authCode);
    			batchTask.setExpDate(expDate);
    			batchTask.setIssuerId(issuerId);
    			batchTask.setRefrenceRetrievalNumber(retrievalReferenceNum);
    			batchTask.setResponseCode(responseCode);
    			batchTask.setResponseMsg(responseMsg);
    			batchTask.setSettlementDate(settlementDate);
    			
    			batchTaskList.add(batchTask);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		if (cursor != null) {
    			
    			cursor.close();
    		}
    	}
    	return batchTaskList;
    }*/
    
    public List<PaymentInfo> selectAllPaymentInfo() {
    	String sql = "select * from " + PAYMENT_ACTIVITY_TABLE_NAME + " order by " + ACQUIRE_PAYMENT_ID + " ASC";
    	List<PaymentInfo> paymentList = null;
    	Cursor cursor = null;
    	try {
    		cursor = getCursor(sql, null);
    		paymentList = new ArrayList<PaymentInfo>();
    		while (cursor.moveToNext()) {
    			String paymentId = cursor.getString(cursor.getColumnIndex(ACQUIRE_PAYMENT_ID));
    			String paymentName = cursor.getString(cursor.getColumnIndex(ACQUIRE_PAYMENT_NAME));
    			String brhKeyIndex = cursor.getString(cursor.getColumnIndex(ACQUIRE_BRH_KEY_INDEX));
    			String prdtNo = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_NO));
    			String prdtDesc = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_DESC));
    			String prdtTitle = cursor.getString(cursor.getColumnIndex(ACQUIRE_PRODUCT_TITLE));
    			String openBrh = cursor.getString(cursor.getColumnIndex(ACQUIRE_DEVICE_NUM_OF_MERCH));
    			String openBrhName = cursor.getString(cursor.getColumnIndex(ACQUIRE_INSTITUTE_NAME));
    			
    			PaymentInfo paymentInfo = new PaymentInfo(paymentId, paymentName, brhKeyIndex, prdtNo,
    					prdtTitle, prdtDesc, openBrh, openBrhName);
    			paymentList.add(paymentInfo);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		if (cursor != null) {
    			
    			cursor.close();
    		}
    	}
    	return paymentList;
    }
    
    /**
     * <p>Title: CacheDB.java </p>
     * <p>Description: </p>
     * <p>Copyright: Copyright (c) 2014</p>
     * <p>Company: KoolCloud</p>
     * @author 		Teddy
     * @date 		2014-7-11
     * @version 	
     */
    class CacheHelper extends SQLiteOpenHelper {
    	Context ctx;
		public CacheHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
			ctx = context;
		}

		@Override
		
		public void onCreate(SQLiteDatabase db) {
			createTables(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion == 3) {
				// Drop tables  
		        db.execSQL("DROP TABLE IF EXISTS " + BATCH_PROCESSING_TABLE_NAME);
		        db.execSQL("DROP TABLE IF EXISTS " + ACQUIRE_INSTITUTE_TABLE_NAME);
		        db.execSQL("DROP TABLE IF EXISTS " + PAYMENT_ACTIVITY_TABLE_NAME);
		        // Create tables  
		        onCreate(db);
		        
		        //clear saved parameter preference
		        Map<String, Object> map = new HashMap<String, Object>();
		        map.put("payParamVersion", "UPD");
		        map.put("pwd", "0");
		        UtilForDataStorage.savePropertyBySharedPreferences(context,	"merchant", map);
		        UtilForDataStorage.saveDate(context, "");
			}
		}
		
		private void createTables(SQLiteDatabase db) {
			String createBatchProcessSql = "CREATE TABLE IF NOT EXISTS " + BATCH_PROCESSING_TABLE_NAME + " ("
					+ BATCH_PRIMARY_KEY_ID + " varchar PRIMARY KEY, " 
					+ BATCH_TXN_ID + " varchar, " 
			        + BATCH_ORI_TXN_ID + " varchar, " 
			        + BATCH_REF_NUMBER + " varchar, " 
			        + BATCH_AUTH_CODE + " varchar, " 
			        + BATCH_RESPONSE_CODE + " varchar, " 
			        + BATCH_RESPONSE_MESSAGE + " varchar, " 
			        + BATCH_ISSUER_ID + " varchar, " 
			        + BATCH_EXP_DATE + " varchar, "
			        + BATCH_SETTLEMENT_DATE + " varchar, "
			        + BATCH_CARD_NUM + " varchar, "
			        + BATCH_PAYMENT_ID + " varchar, "
			        + BATCH_TRANS_TYPE + " varchar, "
			        + BATCH_BATCH_NO + " varchar, "
			        + BATCH_TRACE_NO + " varchar, "
			        + BATCH_TRANS_TIME + " varchar, "
			        + BATCH_TRANS_AMOUNT + " varchar, "
			        + BATCH_ACTION + " varchar "
			        + 
			        ");";
			
			/*String createAcquireInstituteSql = "CREATE TABLE IF NOT EXISTS " + ACQUIRE_INSTITUTE_TABLE_NAME + " ("
					+ ACQUIRE_MERCHANT_ID + " varchar primary key, " 
					+ ACQUIRE_TERMINAL_ID + " varchar, " 
					+ ACQUIRE_DEVICE_NUM_OF_MERCH + " varchar, " 
					+ ACQUIRE_IMG_NAME + " varchar, " 
					+ ACQUIRE_INSTITUTE_NAME + " varchar, " 
					+ ACQUIRE_MERCH_NUM_OF_MERCH + " varchar, " 
					+ ACQUIRE_PAYMENT_ID + " varchar, " 
					+ ACQUIRE_PAYMENT_NAME + " varchar, " 
					+ ACQUIRE_PRINT_TYPE + " varchar, " 
					+ ACQUIRE_PRODUCT_DESC + " varchar, " 
					+ ACQUIRE_PRODUCT_NO + " varchar, " 
					+ ACQUIRE_PRODUCT_TITLE + " varchar, " 
					+ ACQUIRE_PRODUCT_TYPE + " varchar, " 
					+ ACQUIRE_TYPE_ID + " varchar, " 
					+ ACQUIRE_TYPE_NAME + " varchar, " 
					+ ACQUIRE_BRH_KEY_INDEX + " varchar, " 
					+ ACQUIRE_BRH_MSG_TYPE + " varchar, " 
					+ ACQUIRE_BRH_MCHT_MCC + " varchar);";*/
			String createAcquireInstituteSql = "CREATE TABLE IF NOT EXISTS " + ACQUIRE_INSTITUTE_TABLE_NAME + " ("
					+ ACQUIRE_MERCHANT_ID + " varchar, " 
					+ ACQUIRE_TERMINAL_ID + " varchar, " 
					+ ACQUIRE_DEVICE_NUM_OF_MERCH + " varchar, " 
					+ ACQUIRE_IMG_NAME + " varchar, " 
					+ ACQUIRE_INSTITUTE_NAME + " varchar, " 
					+ ACQUIRE_MERCH_NUM_OF_MERCH + " varchar, " 
					+ ACQUIRE_PAYMENT_ID + " varchar, " 
					+ ACQUIRE_PAYMENT_NAME + " varchar, " 
					+ ACQUIRE_PRINT_TYPE + " varchar, " 
					+ ACQUIRE_PRODUCT_DESC + " varchar, " 
					+ ACQUIRE_PRODUCT_NO + " varchar, " 
					+ ACQUIRE_PRODUCT_TITLE + " varchar, " 
					+ ACQUIRE_PRODUCT_TYPE + " varchar, " 
					+ ACQUIRE_TYPE_ID + " varchar, " 
					+ ACQUIRE_TYPE_NAME + " varchar, " 
					+ ACQUIRE_BRH_KEY_INDEX + " varchar, " 
					+ ACQUIRE_BRH_MSG_TYPE + " varchar, " 
					+ ACQUIRE_BRH_MCHT_MCC + " varchar, "
					+ " CONSTRAINT PK_ACQUIRE_INSTITUTE PRIMARY KEY (" + ACQUIRE_MERCHANT_ID + ", " + ACQUIRE_TERMINAL_ID + ", " + ACQUIRE_DEVICE_NUM_OF_MERCH + ")" +
					");";
			
			//FIXME: payment activity
			String paymentActivitySql = "CREATE TABLE IF NOT EXISTS " + PAYMENT_ACTIVITY_TABLE_NAME + " ("
					+ ACQUIRE_PAYMENT_ID + " varchar, " 
					+ ACQUIRE_PAYMENT_NAME + " varchar, " 
					+ ACQUIRE_BRH_KEY_INDEX + " varchar, " 
					+ ACQUIRE_MERCHANT_ID + " varchar, " 
					+ ACQUIRE_TERMINAL_ID + " varchar, " 
					+ ACQUIRE_PRINT_TYPE + " varchar, " 
					+ ACQUIRE_PRODUCT_NO + " varchar, " 
					+ ACQUIRE_PRODUCT_TITLE + " varchar, " 
					+ ACQUIRE_PRODUCT_DESC + " varchar, " 
					+ ACQUIRE_DEVICE_NUM_OF_MERCH + " varchar, " 
					+ ACQUIRE_INSTITUTE_NAME + " varchar, " 
					+ ACQUIRE_TAB_TYPE_ID + " varchar, " 
					+ PAYMENT_ACTIVITY_JSON + " nvarchar, " 
					+ " CONSTRAINT PK_PAYMENT_ACTIVITY PRIMARY KEY (" + ACQUIRE_PAYMENT_ID + ")" +
					");";
					
	        
	        db.execSQL(createBatchProcessSql);
	        db.execSQL(createAcquireInstituteSql);
	        db.execSQL(paymentActivitySql);
			setmDb(db);
		}
	}
}
