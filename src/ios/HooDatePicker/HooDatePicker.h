//
//  HooDatePicker.h
//  HooDatePickerDeomo
//
//  Created by hujianghua on 3/5/16.
//  Copyright © 2016 hujianghua. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NSDate+HooDatePicker.h"

typedef NS_ENUM(NSInteger,HooDatePickerMode) {
    HooDatePickerModeTime,    // Displays hour, minute, and optionally AM/PM designation depending on the locale setting (e.g. 6 | 53 | PM)
    HooDatePickerModeDate,     // Displays month, day, and year depending on the locale setting (e.g. November | 15 | 2007)
    HooDatePickerModeDateAndTime, // Displays date, hour, minute, and optionally AM/PM designation depending on the locale setting (e.g. Wed Nov 15 | 6 | 53 | PM)
    HooDatePickerModeYearAndMonth, // Displays Year, Month,  designation depending on the locale setting (e.g. November | 2007)
    HooDatePickerModeYear,
    HooDatePickerModeFullDateAndTime,
    HooDatePickerModeWeek
};

@class HooDatePicker;

@protocol HooDatePickerDelegate<NSObject>
@optional
- (void)datePicker:(HooDatePicker *)datePicker dateDidChange:(NSDate *)date;
- (void)datePicker:(HooDatePicker *)datePicker dataDidChange:(NSMutableDictionary *)dic;
- (void)datePicker:(HooDatePicker *)datePicker didCancel:(UIButton *)sender;
- (void)datePicker:(HooDatePicker *)dataPicker didSelectedDate:(NSDate *)date;
- (void)datePicker:(HooDatePicker *)dataPicker didSelectedWeek:(NSInteger)index;
- (void)datePicker:(HooDatePicker *)dataPicker didSelectedDateModel:(NSDate *)date;
- (void)datePicker:(HooDatePicker *)dataPicker didSelectedTimeModel:(NSDate *)date;
- (void)datePicker:(HooDatePicker *)dataPicker dismiss:(HooDatePickerMode)pickerMode;
@end

@interface HooDatePicker : UIControl
/**
 *  Title on the top of HooDatePicker
 */
@property (nonatomic, copy) NSString *title;

@property (nonatomic, strong) NSDate *date;
/**
 *  specify min/max date range. default is nil. When min > max, the values are ignored.
 */
@property (nonatomic, strong) NSDate *minimumDate;

@property (nonatomic, strong) NSDate *maximumDate;
/**
 * default is HooDatePickerModeDate. setting nil returns to default
 */
@property (nonatomic, assign) HooDatePickerMode datePickerMode;
/**
 *  default is [NSLocale currentLocale]. setting nil returns to default
 */
@property(nonatomic,strong) NSLocale      *locale;
/**
 *  default is [NSCalendar currentCalendar]. setting nil returns to default
 */
@property(nonatomic,copy)   NSCalendar    *calendar;
/**
 *   default is nil. use current time zone or time zone from calendar
 */
@property(nonatomic,strong) NSTimeZone    *timeZone;
/**
 *  read only property, indicate in datepicker is open.
 */
@property(nonatomic,readonly) BOOL        isOpen;
@property(nonatomic,readonly) BOOL        showSecond;

@property (nonatomic, weak) id<HooDatePickerDelegate> delegate;

- (instancetype)initWithSuperView:(UIView*)superView andType:(NSInteger)type andModeType:(HooDatePickerMode)datePickerMode andMaxDate:(NSDate *)maxDate andMinDate:(NSDate*)minDate andShowSecond:(BOOL)showSecond;

- (instancetype)initDatePickerMode:(HooDatePickerMode)datePickerMode andAddToSuperView:(UIView *)superView;

- (instancetype)initDatePickerMode:(HooDatePickerMode)datePickerMode minDate:(NSDate *)minimumDate maxMamDate:(NSDate *)maximumDate  andAddToSuperView:(UIView *)superView;

- (void)setDate:(NSDate *)date animated:(BOOL)animated;

- (void)setWeek:(NSInteger)weekIndex andWeekName:(NSArray*)weekNames andMaxWeek:(NSInteger)weekMaxIndex animated:(BOOL)animated;

- (void)setTintColor:(UIColor *)tintColor;

- (void)setHighlightColor:(UIColor *)highlightColor;

- (void)setKSureButtonText:(NSString *)sureTxt;

- (void)setKSureButtonTextColor:(UIColor *)sureTxtColor;

- (void)setKCancelButtonText:(NSString *)cancelTxt;

- (void)setKCancelButtonTextColor:(UIColor *)cancelTxtColor;

- (void)show;

- (void)dismiss;

- (NSDate*)getDate;

- (void)setHighLightModel:(int)index;

- (void)setMinimumDate:(NSDate *)min andMaxDate:(NSDate *)max;
@end
