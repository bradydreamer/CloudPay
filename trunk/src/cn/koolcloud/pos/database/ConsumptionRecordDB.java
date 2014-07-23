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
import android.text.TextUtils;
import cn.koolcloud.pos.entity.AcquireInstituteBean;
import cn.koolcloud.pos.entity.BatchTaskBean;

/**
 * <p>Title: CacheDB.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: KoolCloud</p>
 * @author 		Teddy
 * @date 		2014-7-11
 * @version 	
 */
public class ConsumptionRecordDB extends BaseSqlAdapter {

	private final static String DATABASE_NAME = "RecordCache.db";
	private final static int DATABASE_VERSION = 2;
    private final static String CONSUMPTION_RECORD_TABLE_NAME = "consumption_record_table";
    
    private Context context;
    private String dbName;
    
    private static ConsumptionRecordDB consumptionDB;
    
    //TODO:acquire institute table columns
    private final static String TXN_ID_RECORD = "txnId";
    private final static String TRANS_TYPE_RECORD = "transType";
    private final static String TRANS_TYPE_DESC_RECORD = "transTypeDesc";
    private final static String PAYMENT_ID_RECORD = "paymentId";
    private final static String PAYMENT_NAME_RECORD = "paymentName";
    private final static String REF_NO_RECORD = "refNo";
    private final static String TRANS_AMOUNT_RECORD = "transAmount";
    private final static String PAY_KEY_INDEX_RECORD = "payKeyIndex";
    private final static String PAY_TYPE_DESC_RECORD = "payTypeDesc";
    private final static String BATCH_NO_RECORD = "batchNo";
    private final static String TRACE_NO_RECORD = "traceNo";
    private final static String ORDER_STATE_RECORD = "orderState";
    private final static String ORDER_STATE_DESC_RECORD = "orderStateDesc";
    private final static String CARD_NO_RECORD = "cardNo";
    private final static String OPEN_BRH_RECORD = "openBrh";
    private final static String OPERATOR_RECORD = "operator";
    private final static String TRANS_DATE_TIME_RECORD = "transDateTime";
    private final static String TRANS_DATE_RECORD = "transDate";
    private final static String TRANS_TIME_RECORD = "transTime";
    
    private ConsumptionRecordDB(Context ctx, int version) {
    	this.context = ctx;
    	if (dbName == null) {
    		dbName = context.getFileStreamPath(DATABASE_NAME).getAbsolutePath();
    	}
    	
		mDbHelper = new RecordHelper(ctx, DATABASE_NAME, null, version);
    } 
    
    public static ConsumptionRecordDB getInstance(Context ctx) {
    	if (consumptionDB == null) {
    		consumptionDB = new ConsumptionRecordDB(ctx, DATABASE_VERSION);
    	}
    	return consumptionDB;
    }
    
    /**
     * @Title: insertConsumptionRecord
     * @Description: TODO
     * @param recordDataList
     * @return: void
     */
    public void insertConsumptionRecord(List<JSONObject> recordDataList) {
    	
    	ArrayList<SQLEntity> sqlList = new ArrayList<SQLEntity>();
    	Cursor cursor = null;
    	try {
    		String sql = "INSERT INTO "+ CONSUMPTION_RECORD_TABLE_NAME +"(" +
    				TXN_ID_RECORD + ", " +
    				TRANS_TYPE_RECORD + ", " +
    				TRANS_TYPE_DESC_RECORD + ", " +
    				PAYMENT_ID_RECORD + ", " +
    				PAYMENT_NAME_RECORD + ", " +
    				REF_NO_RECORD + ", " +
    				TRANS_AMOUNT_RECORD + ", " +
    				PAY_KEY_INDEX_RECORD + ", " +
    				PAY_TYPE_DESC_RECORD + ", " +
    				BATCH_NO_RECORD + ", " +
    				TRACE_NO_RECORD + ", " +
    				ORDER_STATE_RECORD + ", " +
    				ORDER_STATE_DESC_RECORD + ", " +
    				CARD_NO_RECORD + ", " +
    				OPEN_BRH_RECORD + ", " +
    				OPERATOR_RECORD + ", " +
    				TRANS_DATE_TIME_RECORD + ", " +
    				TRANS_DATE_RECORD + ", " +
    				TRANS_TIME_RECORD + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    		
    		if (recordDataList != null && recordDataList.size() > 0) {
				for (int i = 0; i < recordDataList.size(); i++) {
					JSONObject jsonObj = recordDataList.get(i);
					cursor = selectRecordByTxnId(jsonObj.getString(TXN_ID_RECORD));
					if (cursor.getCount() > 0) {
						continue;
					}
					cursor.close();
					String[] params = new String[] { jsonObj.optString(TXN_ID_RECORD), jsonObj.optString(TRANS_TYPE_RECORD),
    						jsonObj.optString(TRANS_TYPE_DESC_RECORD), jsonObj.optString(PAYMENT_ID_RECORD),
    						jsonObj.optString(PAYMENT_NAME_RECORD), jsonObj.optString(REF_NO_RECORD),
    						jsonObj.optString(TRANS_AMOUNT_RECORD), jsonObj.optString(PAY_KEY_INDEX_RECORD),
    						jsonObj.optString(PAY_TYPE_DESC_RECORD), jsonObj.optString(BATCH_NO_RECORD),
    						jsonObj.optString(TRACE_NO_RECORD), jsonObj.optString(ORDER_STATE_RECORD),
    						jsonObj.optString(ORDER_STATE_DESC_RECORD), jsonObj.optString(CARD_NO_RECORD),
    						jsonObj.optString(OPEN_BRH_RECORD), jsonObj.optString(OPERATOR_RECORD),
    						jsonObj.optString("transTime"), jsonObj.optString("tDate"), jsonObj.optString("tTime")
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
     * @Title: getSortedRecords
     * @Description: get consumption records with conditions
     * @param startDate from date YYYYMMDD
     * @param endDate	end date YYYYMMDD
     * @param sortColumn	order by this column
     * @param orderOption   order option ("DESC" or "ASC"), true (default) is DESC.
     * @return return current day results if startDate and endDate are all null
     * @return: List<JSONObject>
     */
    public List<JSONObject> getSortedRecords(String startDate, String endDate, String sortColumn, boolean isDesc) {
    	List<JSONObject> resultList = null;
    	StringBuffer sqlBuffer = new StringBuffer("select * from " + CONSUMPTION_RECORD_TABLE_NAME + " where ");
    	if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
    		sqlBuffer.append("transDate = strftime('%Y%m%d', 'now', 'localtime') ");
    	} else {
    		sqlBuffer.append("transDate between '" + startDate +"' and '" + endDate + "' ");
    	}
    	
    	if (TextUtils.isEmpty(sortColumn)) {
    		return null;
    	} else {
    		sqlBuffer.append("order by " + sortColumn);
    	}
    	
    	if (isDesc) {
    		sqlBuffer.append(" desc");
    	} else {
    		sqlBuffer.append(" asc");
    	}
    	
    	Cursor cursor = null;
		try {
			cursor = getCursor(sqlBuffer.toString(), null);
			resultList = new ArrayList<JSONObject>();
			while (cursor.moveToNext()) {
				String txnId = cursor.getString(cursor.getColumnIndex(TXN_ID_RECORD));
				String transType = cursor.getString(cursor.getColumnIndex(TRANS_TYPE_RECORD));
				String transTypeDesc = cursor.getString(cursor.getColumnIndex(TRANS_TYPE_DESC_RECORD));
				String paymentId = cursor.getString(cursor.getColumnIndex(PAYMENT_ID_RECORD));
				String paymentName = cursor.getString(cursor.getColumnIndex(PAYMENT_NAME_RECORD));
				String refNo = cursor.getString(cursor.getColumnIndex(REF_NO_RECORD));
				String transAmount = cursor.getString(cursor.getColumnIndex(TRANS_AMOUNT_RECORD));
				String payKeyIndex = cursor.getString(cursor.getColumnIndex(PAY_KEY_INDEX_RECORD));
				String payTypeDesc = cursor.getString(cursor.getColumnIndex(PAY_TYPE_DESC_RECORD));
				String batchNo = cursor.getString(cursor.getColumnIndex(BATCH_NO_RECORD));
				String traceNo = cursor.getString(cursor.getColumnIndex(TRACE_NO_RECORD));
				String orderState = cursor.getString(cursor.getColumnIndex(ORDER_STATE_RECORD));
				String orderStateDesc = cursor.getString(cursor.getColumnIndex(ORDER_STATE_DESC_RECORD));
				String cardNo = cursor.getString(cursor.getColumnIndex(CARD_NO_RECORD));
				String openBrh = cursor.getString(cursor.getColumnIndex(OPEN_BRH_RECORD));
				String operator = cursor.getString(cursor.getColumnIndex(OPERATOR_RECORD));
				String transDateTime = cursor.getString(cursor.getColumnIndex(TRANS_DATE_TIME_RECORD));
				String transDate = cursor.getString(cursor.getColumnIndex(TRANS_DATE_RECORD));
				String transTime = cursor.getString(cursor.getColumnIndex(TRANS_TIME_RECORD));
				
				JSONObject jsObj = new JSONObject();
				jsObj.put(TXN_ID_RECORD, txnId);
				jsObj.put(TRANS_TYPE_RECORD, transType);
				jsObj.put(TRANS_TYPE_DESC_RECORD, transTypeDesc);
				jsObj.put(PAYMENT_ID_RECORD, paymentId);
				jsObj.put(PAYMENT_NAME_RECORD, paymentName);
				jsObj.put(REF_NO_RECORD, refNo);
				jsObj.put(TRANS_AMOUNT_RECORD, transAmount);
				jsObj.put(PAY_KEY_INDEX_RECORD, payKeyIndex);
				jsObj.put(PAY_TYPE_DESC_RECORD, payTypeDesc);
				jsObj.put(BATCH_NO_RECORD, batchNo);
				jsObj.put(TRACE_NO_RECORD, traceNo);
				jsObj.put(ORDER_STATE_RECORD, orderState);
				jsObj.put(ORDER_STATE_DESC_RECORD, orderStateDesc);
				jsObj.put(CARD_NO_RECORD, cardNo);
				jsObj.put(OPEN_BRH_RECORD, openBrh);
				jsObj.put(OPERATOR_RECORD, operator);
				jsObj.put("transTime", transDateTime);
				jsObj.put("tDate", transDate);
				jsObj.put("tTime", transTime);
				
				resultList.add(jsObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (cursor != null) {
				
				cursor.close();
			}
		}
    	
    	return resultList;
    }
    
    public void deleteRecordByTxnId(String txnId) { 
        
        String sql = "delete from " + CONSUMPTION_RECORD_TABLE_NAME + " where " + TXN_ID_RECORD + "='" + txnId + "'";
		try {
			excuteSql(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		closeDB();
    }
    
    public Cursor selectRecordByTxnId(String txnId) { 
    	String sql = "select * from " + CONSUMPTION_RECORD_TABLE_NAME + " where " + TXN_ID_RECORD + " = '" + txnId + "'";
    	Cursor cursor = getCursor(sql, null);
    	return cursor; 
    }
    
    public void clearRecordTableData() {
    	String sql = "delete from " + CONSUMPTION_RECORD_TABLE_NAME;
    	
    	try {
			excuteWriteAbleSql(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		closeDB();
    }
    
    
    /**
     * <p>Title: ConsumptionRecordDB.java </p>
     * <p>Description: </p>
     * <p>Copyright: Copyright (c) 2014</p>
     * <p>Company: KoolCloud</p>
     * @author 		Teddy
     * @date 		2014-7-17
     * @version 	
     */
    class RecordHelper extends SQLiteOpenHelper {
    	Context ctx;
		public RecordHelper(Context context, String name, CursorFactory factory, int version) {
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
		        db.execSQL("DROP TABLE IF EXISTS " + CONSUMPTION_RECORD_TABLE_NAME);
		        // Create tables
		        onCreate(db); 
			}
		}
		
		private void createTables(SQLiteDatabase db) {
			//FIXME: create table
			String createConsumptionRecordSql = "CREATE TABLE IF NOT EXISTS " + CONSUMPTION_RECORD_TABLE_NAME + " ("
					+ TXN_ID_RECORD + " varchar, " 
					+ TRANS_TYPE_RECORD + " varchar, " 
					+ TRANS_TYPE_DESC_RECORD + " varchar, " 
					+ PAYMENT_ID_RECORD + " varchar, " 
					+ PAYMENT_NAME_RECORD + " varchar, " 
					+ REF_NO_RECORD + " varchar, " 
					+ TRANS_AMOUNT_RECORD + " double, " 
					+ PAY_KEY_INDEX_RECORD + " varchar, " 
					+ PAY_TYPE_DESC_RECORD + " varchar, " 
					+ BATCH_NO_RECORD + " varchar, " 
					+ TRACE_NO_RECORD + " varchar, " 
					+ ORDER_STATE_RECORD + " varchar, " 
					+ ORDER_STATE_DESC_RECORD + " varchar, " 
					+ CARD_NO_RECORD + " varchar, "
					+ OPEN_BRH_RECORD + " varchar, " 
					+ OPERATOR_RECORD + " varchar, "
					+ TRANS_DATE_TIME_RECORD + " varchar, "
					+ TRANS_DATE_RECORD + " varchar, "
					+ TRANS_TIME_RECORD + " varchar, "
					+ " CONSTRAINT PK_CONSUMTION_RECORD PRIMARY KEY (" + TXN_ID_RECORD + ")" +
					");";
	        
	        db.execSQL(createConsumptionRecordSql);
			setmDb(db);
		}
	}
}
