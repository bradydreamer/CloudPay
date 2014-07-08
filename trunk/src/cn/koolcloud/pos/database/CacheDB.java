package cn.koolcloud.pos.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import cn.koolcloud.pos.entity.BatchTaskBean;

/**
 * <p>Title: CacheDB.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: All In Pay</p>
 * @author 		Teddy
 * @date 		2014-6-23
 * @version 	
 */
public class CacheDB extends BaseSqlAdapter {

	private final static String DATABASE_NAME = "Cache.db";
    private final static int DATABASE_VERSION = 0;
    private final static String BATCH_PROCESSING_TABLE_NAME = "batch_processing_table";
    
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
  
   /* private void createCacheDB() {
    	try {
    		File file = new File(dbName);
    		
    		//create file
			if (!file.exists()) {
				file.createNewFile();
			}
    		
			SQLiteDatabase sdbVersion = SQLiteDatabase.openOrCreateDatabase(file, null);
			String createBatchTableSql = "CREATE TABLE IF NOT EXISTS " + BATCH_PROCESSING_TABLE_NAME + " (" 
				+ TXN_ID + " varchar primary key, " 
		        + RETRIEVAL_REFERENCE_NUMBER + " varchar, " 
		        + AUTH_CODE + " varchar, " 
		        + RESPONSE_CODE + " varchar, " 
		        + RESPONSE_MESSAGE + " varchar, " 
		        + EXP_DATE + " varchar, " 
		        + SETTLEMENT_DATE + " varchar, " 
		        + ISSUER_ID + " varchar);";
	        
			sdbVersion.execSQL(createBatchTableSql);
			sdbVersion.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }*/
    
    /**
     * @Title: insertBatchTask
     * @Description: TODO insert batch record
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
    
    public void clearBatchTableData() {
    	String sql = "delete from " + BATCH_PROCESSING_TABLE_NAME;
    	
    	try {
			excuteWriteAbleSql(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		closeDB();
    }
    
  
    // delete operations by id
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
    
    /**
     * <p>Title: CacheDB.java </p>
     * <p>Description: </p>
     * <p>Copyright: Copyright (c) 2014</p>
     * <p>Company: All In Pay</p>
     * @author 		Teddy
     * @date 		2014-6-24
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
	        
	        db.execSQL(createBatchProcessSql);
			setmDb(db);
		}
	}
}
