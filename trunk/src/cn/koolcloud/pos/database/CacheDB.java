package cn.koolcloud.pos.database;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import cn.koolcloud.pos.entity.AcquireInstituteBean;
import cn.koolcloud.pos.entity.BatchTaskBean;
import cn.koolcloud.pos.service.PaymentInfo;

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
	private final static int DATABASE_VERSION = 1;
    private final static String BATCH_PROCESSING_TABLE_NAME = "batch_processing_table";
    private final static String ACQUIRE_INSTITUTE_TABLE_NAME = "acquire_institute_table";
    private final static String PAYMENT_ACTIVITY_TABLE_NAME = "payment_activity_table";
    
    private Context context;
    private String dbName;
    
    private static CacheDB cacheDB;
    
//    private final static String TRANS_TYPE = "trans_type";
//    private final static String BATCH_NUMBER = "batch_number";
//    private final static String PRIMARY_ACCOUNT_NUMBER = "primary_account_number";
//    private final static String TRANS_AMOUNT = "trans_amount";
//    private final static String TRACE_NUMBER = "trace_number";
//    private final static String TRANS_TIME = "trans_time";
//    private final static String TRANS_DATE = "trans_date";
    //TODO:batch processing table columns
    private final static String TXN_ID = "txn_id";
    private final static String RETRIEVAL_REFERENCE_NUMBER = "retrieval_reference_number";	//RRN
    private final static String AUTH_CODE = "auth_code";									//Authorization Identification Response
    private final static String RESPONSE_CODE = "response_code";
    private final static String RESPONSE_MESSAGE = "response_message";
    private final static String ISSUER_ID = "issuer_id";
    private final static String EXP_DATE = "exp_date";										//format YYMM
    private final static String SETTLEMENT_DATE = "settlement_date";						//settlement date format MMDD
    
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
    public void insertBatchTask(BatchTaskBean batchTask) {
        
        ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
        Cursor cursor = null;
        boolean isExist = false;
		try {
			
			String sql = "INSERT INTO "+ BATCH_PROCESSING_TABLE_NAME +"(" +
					TXN_ID + ", " +
					RETRIEVAL_REFERENCE_NUMBER + ", " +
					AUTH_CODE + ", " +
					RESPONSE_CODE + ", " +
					RESPONSE_MESSAGE + ", " +
					ISSUER_ID + ", " +
					EXP_DATE + ", " +
					SETTLEMENT_DATE + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
			
			if (batchTask != null) {
				
				cursor = selectBatchTaskByTxnId(batchTask.getTxnId());
				if (cursor.getCount() > 0) {
					isExist = true;
				}
				cursor.close();
				if (isExist) {
					return;
				} else {
					
					String[] params = new String[] { batchTask.getTxnId(), batchTask.getRefrenceRetrievalNumber(),
							batchTask.getAuthCode(), batchTask.getResponseCode(), batchTask.getResponseMsg(),
							batchTask.getIssuerId(), batchTask.getExpDate(), batchTask.getSettlementDate() };
					sqlList.add(new SQLEntity(sql, params));
				}
			}
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
					cursor = selectAcquireInstituteByMerchantId(acquireInstituteBean.getBrhMchtId());
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
    				ACQUIRE_INSTITUTE_NAME + ", " +
    				PAYMENT_ACTIVITY_JSON + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    		
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
    						acquireInstituteBean.getBrhTermId(),
    						acquireInstituteBean.getPrintType(), acquireInstituteBean.getProductNo(),
    						acquireInstituteBean.getProductTitle(), acquireInstituteBean.getProductDesc(),
    						acquireInstituteBean.getInstituteName(), acquireInstituteBean.getJsonItem(),
    						
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
    
    public void clearPaymentActivityTableData() {
    	String sql = "delete from " + PAYMENT_ACTIVITY_TABLE_NAME;
    	
    	try {
    		excuteWriteAbleSql(sql);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	closeDB();
    }
    
    public void deleteBatchTaskByTxnId(String txnId) { 
        
        String sql = "delete from " + BATCH_PROCESSING_TABLE_NAME + " where " + TXN_ID + "='" + txnId + "'";
		try {
			excuteSql(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		closeDB();
    }
    
    public Cursor selectBatchTaskByTxnId(String txnId) { 
    	String sql = "select * from " + BATCH_PROCESSING_TABLE_NAME + " where " + TXN_ID + " = " + txnId;
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
    
    public Cursor selectAcquireInstituteByMerchantId(String merchantId) { 
    	String sql = "select * from " + ACQUIRE_INSTITUTE_TABLE_NAME + " where " + ACQUIRE_MERCHANT_ID + " = '" + merchantId + "'";
//    	String sql = "select * from " + ACQUIRE_INSTITUTE_TABLE_NAME + " where " + ACQUIRE_MERCHANT_ID + " = ?";
    	
    	Cursor cursor = getCursor(sql, null);
//    	Cursor cursor = getCursor(sql, new String[] { merchantId });
    	return cursor;
    }
    
    public Cursor selectPaymentByPaymentId(String paymentId) { 
    	String sql = "select * from " + PAYMENT_ACTIVITY_TABLE_NAME + " where " + ACQUIRE_PAYMENT_ID + " = '" + paymentId + "'";
    	
    	Cursor cursor = getCursor(sql, null);
    	return cursor;
    }
    
    public JSONArray selectAllBatchStack() {
    	String sql = "select * from " + BATCH_PROCESSING_TABLE_NAME;
    	JSONArray jsArray = null;
    	Cursor cursor = null;
		try {
			cursor = getCursor(sql, null);
			jsArray = new JSONArray();
			while (cursor.moveToNext()) {
				String txnId = cursor.getString(cursor.getColumnIndex(TXN_ID));
				String retrievalReferenceNum = cursor.getString(cursor.getColumnIndex(RETRIEVAL_REFERENCE_NUMBER));
				String authCode = cursor.getString(cursor.getColumnIndex(AUTH_CODE));
				String responseCode = cursor.getString(cursor.getColumnIndex(RESPONSE_CODE));
				String responseMsg = cursor.getString(cursor.getColumnIndex(RESPONSE_MESSAGE));
				String issuerId = cursor.getString(cursor.getColumnIndex(ISSUER_ID));
				String expDate = cursor.getString(cursor.getColumnIndex(EXP_DATE));
				String settlementDate = cursor.getString(cursor.getColumnIndex(SETTLEMENT_DATE));
				
				/*BatchTaskBean batchTask = new BatchTaskBean();
				batchTask.setTxnId(txnId);
				batchTask.setAuthCode(authCode);
				batchTask.setExpDate(expDate);
				batchTask.setIssuerId(issuerId);
				batchTask.setRefrenceRetrievalNumber(retrievalReferenceNum);
				batchTask.setResponseCode(responseCode);
				batchTask.setResponseMsg(responseMsg);
				batchTask.setSettlementDate(settlementDate);*/
				JSONObject jsObj = new JSONObject();
				jsObj.put("txnId", txnId);
				jsObj.put("resCode", responseCode);
				jsObj.put("resMsg", responseMsg);
				jsObj.put("refNo", retrievalReferenceNum);
				jsObj.put("authNo", authCode);
				jsObj.put("issuerId", issuerId);
				jsObj.put("dateExpr", expDate);
				jsObj.put("stlmDate", settlementDate);
				
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
    
    public List<BatchTaskBean> selectAllBatchList() {
    	String sql = "select * from " + BATCH_PROCESSING_TABLE_NAME;
    	List<BatchTaskBean> batchTaskList = null;
    	Cursor cursor = null;
    	try {
    		cursor = getCursor(sql, null);
    		batchTaskList = new ArrayList<BatchTaskBean>();
    		while (cursor.moveToNext()) {
    			String txnId = cursor.getString(cursor.getColumnIndex(TXN_ID));
    			String retrievalReferenceNum = cursor.getString(cursor.getColumnIndex(RETRIEVAL_REFERENCE_NUMBER));
    			String authCode = cursor.getString(cursor.getColumnIndex(AUTH_CODE));
    			String responseCode = cursor.getString(cursor.getColumnIndex(RESPONSE_CODE));
    			String responseMsg = cursor.getString(cursor.getColumnIndex(RESPONSE_MESSAGE));
    			String issuerId = cursor.getString(cursor.getColumnIndex(ISSUER_ID));
    			String expDate = cursor.getString(cursor.getColumnIndex(EXP_DATE));
    			String settlementDate = cursor.getString(cursor.getColumnIndex(SETTLEMENT_DATE));
    			
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
    }
    
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
    			String openBrhName = cursor.getString(cursor.getColumnIndex(ACQUIRE_INSTITUTE_NAME));
    			
    			PaymentInfo batchTask = new PaymentInfo(paymentId, paymentName, brhKeyIndex, prdtNo,
    					prdtDesc, prdtTitle, openBrhName);
    			
    			paymentList.add(batchTask);
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
			if (oldVersion == 1 && newVersion == 2) {
				// Drop tables  
		        db.execSQL("DROP TABLE IF EXISTS " + BATCH_PROCESSING_TABLE_NAME);
		        db.execSQL("DROP TABLE IF EXISTS " + ACQUIRE_INSTITUTE_TABLE_NAME);
		        // Create tables  
		        onCreate(db); 
			}
		}
		
		private void createTables(SQLiteDatabase db) {
			String createBatchProcessSql = "CREATE TABLE IF NOT EXISTS " + BATCH_PROCESSING_TABLE_NAME + " ("
					+ TXN_ID + " varchar, " 
			        + RETRIEVAL_REFERENCE_NUMBER + " varchar, " 
			        + AUTH_CODE + " varchar, " 
			        + RESPONSE_CODE + " varchar, " 
			        + RESPONSE_MESSAGE + " varchar, " 
			        + EXP_DATE + " varchar, " 
			        + SETTLEMENT_DATE + " varchar, " 
			        + ISSUER_ID + " varchar);";
			
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
					+ ACQUIRE_INSTITUTE_NAME + " varchar, " 
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
