package com.plugin.datepicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Date;
import java.util.List;

/**
 * Created by liuzhu on 16/10/18.
 * Description :
 * Usage :
 */
public class DatePickerDialog extends Dialog implements HMDatePicker.OnClickButtonListener{

    private OnGetDatePickerListener listener;
    private DateType dateType;
    private Context pContext;
    private FakeR fakeR;
    private Date currentDate = new Date();
    private Date maxDate;
    private boolean showSecond = true;
    private int weekIndex = 0;
    private int weeKMaxIndex = -1;
    private List<String> weeks;

    public DatePickerDialog(Context context, DateType dateType, Date date) {
        super(context);

    }

    public DatePickerDialog(Context context, int theme, DateType dateType, Date date,Date maxDate,
                            boolean showSecond, int weekIndex, int weeKMaxIndex,List<String> weeks) {
        super(context, theme);
        this.dateType = dateType;
        this.pContext = context;
        this.currentDate = date;
        this.maxDate = maxDate;
        this.showSecond = showSecond;
        this.weekIndex = weekIndex;
        this.weeKMaxIndex = weeKMaxIndex;
        this.weeks = weeks;
        fakeR = new FakeR(this.pContext);
        initDateTimeType(dateType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        showDialog();
    }

    private void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(pContext);
        View view = inflater.inflate(fakeR.getId("layout", "date_picker_dialog"), null);
        setContentView(view);
        HMDatePicker datePicker = (HMDatePicker)findViewById(fakeR.getId("id","date_picker_dialog"));
        datePicker.setDatePickerType(dateType.getIndex());
        datePicker.setShowSecond(showSecond);
        if (dateType.getIndex() == 6) {
            datePicker.setWeek(weeks);
            datePicker.setWeekIndex(weekIndex);
            datePicker.setWeekMaxIndex(weeKMaxIndex);
        }else {
            datePicker.setMinDate(2015);
            datePicker.setMaxDate();
            if (null != maxDate) {
                datePicker.setMaxDate2(maxDate);
            }
            datePicker.setDate(currentDate);
        }

        datePicker.setmOnClickButtonListener(this);
    }

    private void initDateTimeType(DateType dateTimeType) {
        String selectTitle = "日期选择";
        this.dateType = dateTimeType;
        switch (dateTimeType) {
            case DateTimeType_yyyy:
                selectTitle = "请选择年份";
                break;
            case DateTimeType_yyyy_MM:
                selectTitle = "请选择年月";
                break;
            case DateTimeType_yyyy_MM_dd:
                selectTitle = "请选择年月日";
                break;
            case DateTimeType_WEEK:
                selectTitle = "请选择周";
                break;
        }
        this.setTitle(selectTitle);
    }

    @Override
    public void onSure(int year, int monthOfYear, int dayOfMonth, int hour, int min, int sec) {
        if (null != listener) {
            listener.onSure(year, monthOfYear, dayOfMonth,hour,min,sec);
        }
        DatePickerDialog.this.dismiss();
    }

    @Override
    public void onWeekSure(int weekIndex, String weekMsg) {
        if (null != listener) {
            listener.onWeekSure(weekIndex, weekMsg);
        }
        DatePickerDialog.this.dismiss();
    }

    @Override
    public void onCancel(String message) {
        if (null != listener) {
            listener.onCancel(message);
        }
        DatePickerDialog.this.dismiss();
    }

    public interface OnGetDatePickerListener {

        void onSure(int year, int monthOfYear, int dayOfMonth,int hour, int min, int sec);

        void onWeekSure(int weekIndex, String weekMsg);

        void onCancel(String message);
    }

    public void regisDatePickerListener (OnGetDatePickerListener l) {
        this.listener = l;
    }

    public enum DateType {
        DateTimeType_MM(1),
        DateTimeType_yyyy_MM_dd(2),
        DateTimeType_yyyy_MM(3),
        DateTimeType_yyyy(4),
        DateTimeType_yyyy_MM_dd_HH_mm_ss(5),
        DateTimeType_WEEK(6);

        private int index;

        private DateType(int _index) {
            this.index = _index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
