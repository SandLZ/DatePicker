package com.plugin.datepicker;

import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class echoes a string called from JavaScript.
 */
public class DatePicker extends CordovaPlugin {

    // 显示的日期类型
    private DatePickerDialog.DateType dateType;
    // 将来时间
    private boolean allowFutrueDate = false;
    // 最小时间
    private Date minDate = null;
    // 最大时间
    private Date maxDate = null;
    // 设置控件显示的日期
    private Date currentDate = null;
    // 是否显示秒
    private boolean showSecond = true;
    // 周下标
    private int weekIndex = 0;
    private int weeKMaxIndex = -1;
    // 周数组
    private List<String> weeks = new ArrayList<String>();
    // 确认、取消按钮文字
    private String sureTxt, cancelTxt = "";

    private FakeR fakeR;


    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {
            JSONObject options = data.optJSONObject(0);
            this.show(options, callbackContext);
            return true;
        }
        return false;
    }

    private void show(JSONObject options, CallbackContext callbackContext) {
        if (null != options) {
            try {
                praseJsonOptions(options);
                showDatePickerDialog(callbackContext);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDatePickerDialog(final CallbackContext callbackContext) {
        fakeR = new FakeR(cordova.getActivity());
        DatePickerDialog datePickerDialog = new DatePickerDialog(cordova.getActivity(),
                fakeR.getId("style","DatePickerDialogTheme"),dateType,currentDate,maxDate,showSecond,weekIndex,weeKMaxIndex,weeks);
        Window window = datePickerDialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        datePickerDialog.regisDatePickerListener(new DatePickerDialog.OnGetDatePickerListener() {
            @Override
            public void onSure(int year, int month, int day, int hour, int min, int sec) {
                String result = "";
                switch (dateType) {
                    case DateTimeType_yyyy:
                        result += year;
                        break;
                    case DateTimeType_yyyy_MM:
                        if (month < 10) {
                            result += year + "-0" + month;
                        } else {
                            result += year + "-" + month;
                        }
                        break;
                    case DateTimeType_yyyy_MM_dd:
                        if (month < 10) {
                            if (day < 10) {
                                result += year + "-0" + month + "-0" + day;
                            } else {
                                result += year + "-0" + month + "-" + day;
                            }
                        } else {
                            if (day < 10) {
                                result += year + "-" + month + "-0" + day;
                            } else {
                                result += year + "-" + month + "-" + day;
                            }
                        }
                        break;
                    case DateTimeType_yyyy_MM_dd_HH_mm_ss:
                        if (month < 10) {
                            if (day < 10) {
                                result += year + "-0" + month + "-0" + day;
                            } else {
                                result += year + "-0" + month + "-" + day;
                            }
                        } else {
                            if (day < 10) {
                                result += year + "-" + month + "-0" + day;
                            } else {
                                result += year + "-" + month + "-" + day;
                            }
                        }
                        result += " ";
                        if (hour< 10) {
                            result += "0"+hour;
                        }else {
                            result += hour;
                        }
                        result += ":";
                        if (min< 10) {
                            result += "0"+min;
                        }else {
                            result += min;
                        }
                        result += ":";
                        if (sec< 10) {
                            result += "0"+sec;
                        }else {
                            result += sec;
                        }
                        break;
                    case DateTimeType_WEEK:
                        // todo
                        break;
                }
                callbackContext.success(result);
            }

            @Override
            public void onWeekSure(int weekIndex, String weekMsg) {
                String result = "";
//                System.out.println("选择下标 ：" + weekIndex +" - "+weekMsg);
                result += weekIndex;
                callbackContext.success(result);
            }

            @Override
            public void onCancel(String message) {
                callbackContext.error(message);
            }
        });
        datePickerDialog.show();
    }

    /**
     * 日期控件要显示的类型
     *
     * @param type
     */
    private void getDatePickerType(int type) {
        switch (type) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                // 年-月-日
                dateType = DatePickerDialog.DateType.DateTimeType_yyyy_MM_dd;
                break;
            case 3:
                // 年-月
                dateType = DatePickerDialog.DateType.DateTimeType_yyyy_MM;
                break;
            case 4:
                // 年
                dateType = DatePickerDialog.DateType.DateTimeType_yyyy;
                break;
            case 5:
                // 年月日 时分秒
                dateType = DatePickerDialog.DateType.DateTimeType_yyyy_MM_dd_HH_mm_ss;
                break;
            case 6:
                // 年月日 时分秒
                dateType = DatePickerDialog.DateType.DateTimeType_WEEK;
                break;
            default:
                // 年-月-日
                dateType = DatePickerDialog.DateType.DateTimeType_yyyy_MM_dd;
                break;
        }
    }

    /**
     * 解析配置
     *
     * @param options
     * @throws JSONException
     */
    private void praseJsonOptions(JSONObject options) throws JSONException {
        int type = options.getInt("type");
        getDatePickerType(type);
        if (null != options.getString("date")) {
            // 设置显示的时间
            String date = options.getString("date");
            showSecond = options.getBoolean("showSecond");
            try {
                if (type == 5) {
                    Date currentDate = ConverToDate2(date);
                    this.currentDate = currentDate;
                }else if (type == 6) {
                    // 周
                    weekIndex = options.getInt("weekIndex");
                    weeKMaxIndex = options.getInt("weeKMaxIndex");
                    JSONArray array = options.getJSONArray("weekNameArr");
                    weeks = new ArrayList<String>();
                    for (int i=0; i<array.length(); i++) {
                        weeks.add( array.getString(i) );
                    }
                }
                else {
                    Date currentDate = ConverToDate(date);
                    this.currentDate = currentDate;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != options.getString("okText")) {
            // 设置 确认
            sureTxt = options.getString("okText");
        } else {
            sureTxt = "确认";
        }
        if (null != options.getString("cancelText")) {
            // 设置 取消
            cancelTxt = options.getString("cancelText");
        } else {
            cancelTxt = "取消";
        }
        if (options.has("maxDate")) {
            // 设置最大时间
            if (options.getString("maxDate") == "0") {
                return;
            }
            try {
                maxDate = ConverToDate2(options.getString("maxDate"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != options.getString("minDate")) {
            // 设置最小时间

        }

    }

    private static String ConverToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    //把字符串转为日期
    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.parse(strDate);
    }

    public static Date ConverToDate2(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy/HH/mm/ss");
        return df.parse(strDate);
    }
}
