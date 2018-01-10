package com.plugin.datepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HMDatePicker extends LinearLayout implements NumberPickerView.OnValueChangeListener,
        View.OnClickListener, NumberPickerView.OnValueChangeListenerInScrolling {

    private FakeR fakeR;
    private NumberPickerView mYearPicker;
    private NumberPickerView mMonthPicker;
    private NumberPickerView mDayOfMonthPicker;

    private NumberPickerView mWeekPicker;

    private NumberPickerView mHourPicker;
    private NumberPickerView mMinutePicker;
    private NumberPickerView mSecondPicker;

    private Calendar mCalendar;

    private Button btn_sure;
    private Button btn_cancel;
    private Button btn_date_model;
    private Button btn_time_model;

    private OnDateChangedListener mOnDateChangedListener;
    private OnClickButtonListener mOnClickButtonListener;

    private LayoutInflater mLayoutInflater;
    private boolean showSecond = true;

    private List<String> weeks;
    private int weekIndex = 0;
    private int weeKMaxIndex = -1;
    private String weekMsg = "";

    private Date maxDate;

    private int dateType = 2;

    public HMDatePicker(Context context) {
        this(context, null);
    }

    public HMDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        fakeR = new FakeR(context);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    private void init() {
        mLayoutInflater.inflate(fakeR.getId("layout", "date_picker_layout"), this, true);
        mYearPicker = (NumberPickerView) findViewById(fakeR.getId("id", "year_picker"));
        mMonthPicker = (NumberPickerView) findViewById(fakeR.getId("id", "month_picker"));
        mDayOfMonthPicker = (NumberPickerView) findViewById(fakeR.getId("id", "day_picker"));

        mWeekPicker = (NumberPickerView) findViewById(fakeR.getId("id","week_picker"));

        mHourPicker = (NumberPickerView) findViewById(fakeR.getId("id", "hours_picker"));
        mMinutePicker = (NumberPickerView) findViewById(fakeR.getId("id", "minutes_picker"));
        mSecondPicker = (NumberPickerView) findViewById(fakeR.getId("id", "second_picker"));

        btn_sure = (Button) findViewById(fakeR.getId("id", "datePicker_sure"));
        btn_cancel = (Button) findViewById(fakeR.getId("id", "datePicker_cancel"));
        btn_date_model = (Button) findViewById(fakeR.getId("id", "datePicker_type_date"));
        btn_time_model = (Button) findViewById(fakeR.getId("id", "datePicker_type_time"));

        btn_sure.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_date_model.setOnClickListener(this);
        btn_time_model.setOnClickListener(this);

        mYearPicker.setOnValueChangedListener(this);
        mYearPicker.setOnValueChangeListenerInScrolling(this);
        mMonthPicker.setOnValueChangedListener(this);
        mMonthPicker.setOnValueChangeListenerInScrolling(this);
        mDayOfMonthPicker.setOnValueChangedListener(this);
        mDayOfMonthPicker.setOnValueChangeListenerInScrolling(this);
        mWeekPicker.setOnValueChangedListener(this);
        mWeekPicker.setOnValueChangeListenerInScrolling(this);

        mHourPicker.setOnValueChangedListener(this);
        mMinutePicker.setOnValueChangedListener(this);
        mSecondPicker.setOnValueChangedListener(this);

        mCalendar = Calendar.getInstance();
    }


    public HMDatePicker setDate(Date date) {
        mWeekPicker.setVisibility(GONE);
        mCalendar.setTime(date);

        // 设置年时间
        mYearPicker.setValue(mCalendar.get(Calendar.YEAR));

        mMonthPicker.setMinValue(1);
        mMonthPicker.setMaxValue(12);
        mMonthPicker.setValue(mCalendar.get(Calendar.MONTH) + 1);

        // 日

        mDayOfMonthPicker.setMinValue(1);
        mDayOfMonthPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mDayOfMonthPicker.setValue(mCalendar.get(Calendar.DAY_OF_MONTH));

        // 设置时间
        if (dateType == 5) {
            mHourPicker.setMinValue(00);
            mHourPicker.setMaxValue(23);
            mHourPicker.setValue(mCalendar.get(Calendar.HOUR_OF_DAY));
            mMinutePicker.setMinValue(00);
            mMinutePicker.setMaxValue(59);
            mMinutePicker.setValue(mCalendar.get(Calendar.MINUTE));
            if (showSecond) {
                mSecondPicker.setVisibility(VISIBLE);
                mSecondPicker.setMinValue(00);
                mSecondPicker.setMaxValue(59);
                mSecondPicker.setValue(mCalendar.get(Calendar.SECOND));
            }else {
                mSecondPicker.setVisibility(GONE);
            }

        }

        return this;
    }

    public HMDatePicker setWeek(List<String> weeks) {
        if (null != weeks) {
            mWeekPicker.setVisibility(VISIBLE);
            this.weeks = weeks;
            // 设置显示的值
            String[] strArr = new String[weeks.size()];
            weeks.toArray(strArr);
            mWeekPicker.setDisplayedValuesOfWeek(strArr);
            // 设置最大值 最小值
            mWeekPicker.setMinValue(0);
            mWeekPicker.setMaxValue(weeks.size()-1);

        }
        return this;
    }

    public HMDatePicker setWeekMaxIndex(int weekMaxIndex) {
        this.weeKMaxIndex = weekMaxIndex;
        return this;
    }

    public HMDatePicker setWeekIndex(int weekIndex) {
        this.weekIndex = weekIndex;
        mWeekPicker.setValue(weekIndex);
        return this;
    }

    @Override
    public void onValueChange(final NumberPickerView picker, final int oldVal, final int newVal) {
        Date now = new Date();
        if (picker == mYearPicker) {
            mCalendar.roll(Calendar.YEAR,newVal-oldVal);
            int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            int lastDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (dayOfMonth > lastDayOfMonth) {
                dayOfMonth = lastDayOfMonth;
            }
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mCalendar.set(newVal, mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            // 月设置(边界问题)
            mMonthPicker.setValue(getMonth());
            // 日最大值设置
            mDayOfMonthPicker.setMaxValue(lastDayOfMonth);
            mDayOfMonthPicker.setValue(getDayOfMonth());
            if (maxDate.getTime() > (now.getTime()+1000*60*60*24*2)) {
                return;
            }
            if (now.getTime() < mCalendar.getTimeInMillis()) {
                mCalendar.setTime(now);
                mMonthPicker.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMonthPicker.smoothScrollToValue(mCalendar.get(Calendar.MONTH) + 1, false);
                        mDayOfMonthPicker.smoothScrollToValue(mCalendar.get(Calendar.DAY_OF_MONTH), false);
                    }
                }, 100);
            }
        } else if (picker == mMonthPicker) {
            mCalendar.roll(Calendar.MONTH,newVal-oldVal);
            int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            // 这个月最大天数
            int lastDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (dayOfMonth > lastDayOfMonth) {
                dayOfMonth = lastDayOfMonth;
            }
            mCalendar.set(mCalendar.get(Calendar.YEAR), newVal-1, dayOfMonth);
            mDayOfMonthPicker.setMaxValue(lastDayOfMonth);
            mDayOfMonthPicker.setValue(getDayOfMonth());
            if (null != maxDate) {
                now = maxDate;
            }
            if (now.getTime() < mCalendar.getTimeInMillis()) {
                mCalendar.setTime(now);
                mMonthPicker.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMonthPicker.smoothScrollToValue(mCalendar.get(Calendar.MONTH) + 1, false);
                        mDayOfMonthPicker.smoothScrollToValue(mCalendar.get(Calendar.DAY_OF_MONTH), false);
                    }
                }, 100);
            }
        } else if (picker == mDayOfMonthPicker) {
            mCalendar.roll(Calendar.DAY_OF_MONTH,newVal-oldVal);
//            mCalendar.set(Calendar.DAY_OF_MONTH, newVal);
            if (null != maxDate) {
                now = maxDate;
            }
            if (now.getTime() < mCalendar.getTimeInMillis()) {
                mCalendar.setTime(now);
                mDayOfMonthPicker.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDayOfMonthPicker.smoothScrollToValue(mCalendar.get(Calendar.DAY_OF_MONTH), false);
                    }
                }, 100);
            }
        } else if (picker == mWeekPicker) {
            weekIndex = newVal;
            if (newVal > weeKMaxIndex) {
                weekIndex = weeKMaxIndex;
                mDayOfMonthPicker.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWeekPicker.smoothScrollToValue(weeKMaxIndex, false);
                    }
                }, 100);
            }
            weekMsg = weeks.get(weekIndex);
        }
        else if (picker == mHourPicker) {
            mCalendar.set(Calendar.HOUR_OF_DAY, newVal);
        } else if (picker == mMinutePicker) {
            mCalendar.set(Calendar.MINUTE, newVal);
        } else if (picker == mSecondPicker) {
            mCalendar.set(Calendar.SECOND, newVal);
        }

        notifyDateChanged();
    }

    @Override
    public void onClick(View view) {
        if (null != view) {
            if (view.getId() == fakeR.getId("id", "datePicker_sure")) {
                if (null != mOnClickButtonListener) {
                    if (dateType == 6) {
                        if (weekMsg.length() == 0) {
                            weekMsg = weeks.get(weekIndex);
                        }
                        mOnClickButtonListener.onWeekSure(weekIndex, weekMsg);
                    }else {
                        int year = getYear();
                        int month = getMonth();
                        int day_of_month = getDayOfMonth();
                        if (dateType == 5) {
                            int hour = getTimeHours();
                            int min = getTimeMinutes();
                            int sec = getTimeSeconds();
                            mOnClickButtonListener.onSure(year, month, day_of_month, hour, min, sec);
                        } else {
                            mOnClickButtonListener.onSure(year, month, day_of_month, 0, 0, 0);
                        }
                    }

                }
            }
            if (view.getId() == fakeR.getId("id", "datePicker_cancel")) {
                if (null != mOnClickButtonListener) {
                    mOnClickButtonListener.onCancel("cancel");
                }
            }
            if (view.getId() == fakeR.getId("id", "datePicker_type_date")) {
                // 日期模式
                setDateModel();
            }
            if (view.getId() == fakeR.getId("id", "datePicker_type_time")) {
                // 时间模式
                setTimeModel();
            }
        }
    }

    private void setDateModel() {
        ((LinearLayout) findViewById(fakeR.getId("id", "datepicker_linear_date"))).setVisibility(VISIBLE);
        ((LinearLayout) findViewById(fakeR.getId("id", "datepicker_linear_time"))).setVisibility(GONE);
        mYearPicker.setVisibility(VISIBLE);
        mMonthPicker.setVisibility(VISIBLE);
        mDayOfMonthPicker.setVisibility(VISIBLE);
        // 设置颜色
        Drawable drawableDate = getResources().getDrawable(fakeR.getId("drawable", "date_blue_bg"));
        Drawable drawableTime = getResources().getDrawable(fakeR.getId("drawable", "date_black_bg"));
        btn_date_model.setBackground(drawableDate);
        btn_date_model.setTextColor(Color.WHITE);
        btn_time_model.setBackground(drawableTime);
        btn_time_model.setTextColor(Color.BLACK);

    }

    private void setTimeModel() {
        ((LinearLayout) findViewById(fakeR.getId("id", "datepicker_linear_date"))).setVisibility(GONE);
        ((LinearLayout) findViewById(fakeR.getId("id", "datepicker_linear_time"))).setVisibility(VISIBLE);
        mHourPicker.setVisibility(VISIBLE);
        mMinutePicker.setVisibility(VISIBLE);
        // 设置颜色
        Drawable drawableDate = getResources().getDrawable(fakeR.getId("drawable", "date_black_bg"));
        Drawable drawableTime = getResources().getDrawable(fakeR.getId("drawable", "date_blue_bg"));
        btn_date_model.setBackground(drawableDate);
        btn_date_model.setTextColor(Color.BLACK);
        btn_time_model.setBackground(drawableTime);
        btn_time_model.setTextColor(Color.WHITE);
    }

    @Override
    public void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal) {

    }

    /**
     * The callback used to indicate the user changes\d the date.
     */
    public interface OnDateChangedListener {

        /**
         * Called upon a date change.
         *
         * @param view        The view associated with this listener.
         * @param year        The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *                    with {@link Calendar}.
         * @param dayOfMonth  The day of the month that was set.
         */
        void onDateChanged(HMDatePicker view, int year, int monthOfYear, int dayOfMonth);
    }

    public interface OnClickButtonListener {

        void onSure(int year, int monthOfYear, int dayOfMonth, int hour, int min, int sec);

        void onWeekSure(int weekIndex, String weekMsg);

        void onCancel(String message);
    }

    public void setmOnClickButtonListener(OnClickButtonListener l) {
        mOnClickButtonListener = l;
    }


    public HMDatePicker setOnDateChangedListener(OnDateChangedListener l) {
        mOnDateChangedListener = l;
        return this;
    }

    private void notifyDateChanged() {
        if (null != mOnDateChangedListener) {
            mOnDateChangedListener.onDateChanged(this, getYear(), getMonth(), getDayOfMonth());
        }
    }

    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return mCalendar.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getTimeHours() {
        return mCalendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getTimeMinutes() {
        return mCalendar.get(Calendar.MINUTE);
    }

    public int getTimeSeconds() {
        return mCalendar.get(Calendar.SECOND);
    }

    public int getWeekIndex() {
        return weekIndex;
    }


    @Override
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        super.setSoundEffectsEnabled(soundEffectsEnabled);
        mYearPicker.setSoundEffectsEnabled(soundEffectsEnabled);
        mMonthPicker.setSoundEffectsEnabled(soundEffectsEnabled);
        mDayOfMonthPicker.setSoundEffectsEnabled(soundEffectsEnabled);
    }


    public HMDatePicker setBackground(int color) {
        super.setBackgroundColor(color);
//        mYearPicker.setBackground(color);
//        mMonthPicker.setBackground(color);
//        mDayOfMonthPicker.setBackground(color);
        return this;
    }

    public HMDatePicker setDatePickerType(int type) {
        dateType = type;
        switch (type) {
            case 2:
                mYearPicker.setVisibility(VISIBLE);
                mMonthPicker.setVisibility(VISIBLE);
                mDayOfMonthPicker.setVisibility(VISIBLE);
                break;
            case 3:
                mYearPicker.setVisibility(VISIBLE);
                mMonthPicker.setVisibility(VISIBLE);
                mDayOfMonthPicker.setVisibility(GONE);
                break;
            case 4:
                mYearPicker.setVisibility(VISIBLE);
                mMonthPicker.setVisibility(GONE);
                mDayOfMonthPicker.setVisibility(GONE);
                break;
            case 5:
                ((LinearLayout) findViewById(fakeR.getId("id", "datepicker_linear_date"))).setVisibility(VISIBLE);
                ((LinearLayout) findViewById(fakeR.getId("id", "datepicker_linear_time"))).setVisibility(GONE);
                ((RelativeLayout) findViewById(fakeR.getId("id", "date_picker_model"))).setVisibility(VISIBLE);
                mYearPicker.setVisibility(VISIBLE);
                mMonthPicker.setVisibility(VISIBLE);
                mDayOfMonthPicker.setVisibility(VISIBLE);
                break;
            case 6:
                ((LinearLayout) findViewById(fakeR.getId("id", "datepicker_linear_date"))).setVisibility(GONE);
                ((LinearLayout) findViewById(fakeR.getId("id", "datepicker_linear_time"))).setVisibility(GONE);
                ((LinearLayout) findViewById(fakeR.getId("id", "datepicker_linear_week"))).setVisibility(VISIBLE);
                mWeekPicker.setVisibility(VISIBLE);
                break;
            default:
                mYearPicker.setVisibility(VISIBLE);
                mMonthPicker.setVisibility(VISIBLE);
                mDayOfMonthPicker.setVisibility(VISIBLE);
                break;
        }
        return this;
    }

    public HMDatePicker setShowSecond(boolean show) {
        this.showSecond = show;
        return this;
    }

    public HMDatePicker setMaxDate() {
        // 设置本年
        String d = converToString(new Date());
        int maxYear = 0;
        if (d.length() > 4) {
            maxYear = Integer.parseInt(d.substring(0, 4));
        }
        mYearPicker.setMaxValue(maxYear);
        return this;
    }

    public HMDatePicker setMinDate(int year) {
        mYearPicker.setMinValue(year);
        return this;
    }

    public HMDatePicker setMaxDate2(Date date) {
        this.maxDate = date;
        if (date.getYear() > 0) {
            mYearPicker.setMaxValue(date.getYear()+1900);
        }
        return this;
    }

    private static String converToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static Date converToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(strDate);
    }

    public Date getCurrentDate() {
        String y = String.valueOf(getYear());
        String m = String.valueOf(getMonth());
        String d = String.valueOf(getDayOfMonth());

        if (m.length() == 1) {
            m = "0" + m;
        }
        if (d.length() == 1) {
            d = "0" + d;
        }
        String cDate = y + "-" + m + "-" + d;
        Date result = new Date();
        try {
            result = converToDate(cDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }


}
