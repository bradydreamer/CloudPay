package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.util.DateUtil;

public class ConsumptionRecordSearchController extends BaseController {

	private EditText et_startDate;
	private EditText et_endDate;
	private Calendar calendar_startDate;
	private Calendar calendar_endDate;
	private SimpleDateFormat dateFormat;
	private DatePickerDialog startDateDialog;
	private DatePickerDialog endDateDialog;
	private TimeZone timeZone;
	private Date maxDate;
	private Date minDate;
	private boolean removeJSTag = true;

	private final int FLAG_CALENDAR_STARTDATE = 0;
	private final int FLAG_CALENDAR_ENDDATE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		et_startDate = (EditText) findViewById(R.id.consumption_record_search_et_startDate);
		et_endDate = (EditText) findViewById(R.id.consumption_record_search_et_endDate);
		initParams();
		initDatePickerDialog();
	}

	private void initParams() {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		timeZone = TimeZone.getTimeZone("GMT+8");
		dateFormat.setTimeZone(timeZone);
		calendar_endDate = Calendar.getInstance(timeZone);
		calendar_endDate.add(Calendar.DAY_OF_MONTH, -1);
		calendar_startDate = Calendar.getInstance(timeZone);
		calendar_startDate.setTime(calendar_endDate.getTime());
		calendar_startDate.set(Calendar.DAY_OF_MONTH, 1);
		et_startDate.setText(dateFormat.format(calendar_startDate.getTime()));
		Calendar calendar_minDate = Calendar.getInstance(timeZone);
		calendar_minDate.add(Calendar.MONTH, -6);
		try {
			minDate = dateFormat.parse(dateFormat.format(calendar_minDate
					.getTime()));
			maxDate = dateFormat.parse(dateFormat.format(calendar_endDate
					.getTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		et_endDate.setText(dateFormat.format(calendar_endDate.getTime()));
	}

	private void onDatePickerDialogDateSet(int calendarFlag, int year,
			int monthOfYear, int dayOfMonth) {
		Calendar tempCalendar = Calendar.getInstance(timeZone);
		tempCalendar.set(year, monthOfYear, dayOfMonth);
		Date tempDate = tempCalendar.getTime();
		try {
			tempDate = dateFormat.parse(dateFormat.format(tempDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (tempDate.before(minDate) || tempDate.after(maxDate)) {
			showDateRangeNotice();
		} else {
			switch (calendarFlag) {
			case FLAG_CALENDAR_STARTDATE:
				calendar_startDate.set(year, monthOfYear, dayOfMonth);
				et_startDate.setText(dateFormat.format(calendar_startDate
						.getTime()));

				break;
			case FLAG_CALENDAR_ENDDATE:
				calendar_endDate.set(year, monthOfYear, dayOfMonth);
				et_endDate
						.setText(dateFormat.format(calendar_endDate.getTime()));

				break;

			default:
				break;
			}
		}
	}

	private void initDatePickerDialog() {
		startDateDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						onDatePickerDialogDateSet(FLAG_CALENDAR_STARTDATE,
								year, monthOfYear, dayOfMonth);
					}
				}, calendar_startDate.get(Calendar.YEAR),
				calendar_startDate.get(Calendar.MONTH),
				calendar_startDate.get(Calendar.DAY_OF_MONTH));

		endDateDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						onDatePickerDialogDateSet(FLAG_CALENDAR_ENDDATE, year,
								monthOfYear, dayOfMonth);
					}
				}, calendar_endDate.get(Calendar.YEAR),
				calendar_endDate.get(Calendar.MONTH),
				calendar_endDate.get(Calendar.DAY_OF_MONTH));
	}

	private void showDateRangeNotice() {
		Toast.makeText(
				this,
				getString(R.string.consumption_record_search_java_dateRangeNotice),
				Toast.LENGTH_LONG).show();
	}

	public void showStartDatePicker(View view) {
		try {
			Date date_startDate = dateFormat.parse(et_startDate.getText()
					.toString());
			calendar_startDate.setTime(date_startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		startDateDialog.updateDate(calendar_startDate.get(Calendar.YEAR),
				calendar_startDate.get(Calendar.MONTH),
				calendar_startDate.get(Calendar.DAY_OF_MONTH));
		startDateDialog.show();
	}

	public void showEndDatePicker(View view) {
		try {
			Date date_endDate = dateFormat.parse(et_endDate.getText()
					.toString());
			calendar_endDate.setTime(date_endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		endDateDialog.updateDate(calendar_endDate.get(Calendar.YEAR),
				calendar_endDate.get(Calendar.MONTH),
				calendar_endDate.get(Calendar.DAY_OF_MONTH));
		endDateDialog.show();
	}

	public void onConfirm(View view) {
		String startDate = et_startDate.getText().toString() + " 00:00:00";
		startDate = DateUtil.formatDate(DateUtil.parseDate(startDate), "yyyyMMddHHmmss", TimeZone.getTimeZone("GMT+08:00")); //转换成G8时区的起始时间
		String endDate = et_endDate.getText().toString() + " 23:59:59";
		endDate = DateUtil.formatDate(DateUtil.parseDate(endDate),"yyyyMMddHHmmss",TimeZone.getTimeZone("GMT+08:00")); //转换成G8时区的最终时间
		JSONObject msg = new JSONObject();
		try {
			msg.put("startDate", startDate);
			msg.put("endDate", endDate);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finish();
		onCall("ConsumptionRecordSearch.onConfirm", msg);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_consumption_record_search_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_consumption_record_search_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_ConsumptionRecordSearch);
	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		removeJSTag = tag;

	}

	@Override
	protected boolean getRemoveJSTag() {
		// TODO Auto-generated method stub
		return removeJSTag;
	}

}
