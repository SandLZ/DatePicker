/********* DatePicker.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "HooDatePicker.h"

#define DATETIME_FORMAT @"yyyy-MM-dd'T'HH:mm:ss'Z'"
#define DATE_FORMAT_Y_M_D @"yyyy-MM-dd"
#define DATE_TIME_FORMAT @"HH:mm:ss"

@interface DatePicker : CDVPlugin <HooDatePickerDelegate>{
    NSInteger dateType;
    BOOL      showSecond;
}
@property (nonatomic, strong) HooDatePicker *datePicker;
@property (nonatomic, strong) HooDatePicker *timePicker;
@property (nonatomic, strong) CDVInvokedUrlCommand *command;

- (void)show:(CDVInvokedUrlCommand*)command;
@end

@implementation DatePicker

- (void)show:(CDVInvokedUrlCommand*)command
{
    self.command = command;
    NSMutableDictionary *options = [command argumentAtIndex:0];
    dateType = [[options objectForKey:@"type"] intValue];
    showSecond = [[options objectForKey:@"showSecond"] boolValue];
    if (dateType == 5) {
        // 日期 + 时间
        [self initTimePicker:options];
    }else if(dateType == 6) {
        [self initWeekPicker:options];
    }
    if (dateType != 6) {
        [self initDatePicker:options];
    }
}

- (void)close:(CDVInvokedUrlCommand*)command {
//    if (self.datePicker) {
//        [self.datePicker dismiss];
//    }
//    if (self.timePicker) {
//        [self.timePicker dismiss];
//    }
}

// 日期
- (void)initDatePicker:(NSMutableDictionary*)options
{
    int type = [[options objectForKey:@"type"] intValue];
    int Modetype = HooDatePickerModeDate;
    if (dateType == 5) {
        Modetype = HooDatePickerModeDate;
    }else if(dateType == 6) {
        Modetype = HooDatePickerModeWeek;
    }
    else {
        Modetype = [self setDatePickerMode:type];
    }
    // 判断最大最小时间
    NSString *minDateString = [options objectForKey:@"minDate"];
    NSString *maxDateString = [options objectForKey:@"maxDate"];
    NSDate *maxDate = nil;
    NSDate *minDate = nil;
    // 最小时间
    NSDateFormatter *formatter = [self createISODateFormatter: DATETIME_FORMAT timezone:[NSTimeZone defaultTimeZone]];
    if(minDateString && minDateString.length > 0){
        minDate = [formatter dateFromString:minDateString];
    }else {
        NSDateFormatter *dateFormatter = [NSDate shareDateFormatter];
        [dateFormatter setDateFormat:DATE_FORMAT_Y_M_D];
        minDate = [dateFormatter dateFromString:@"2015-01-01"];
    }
    // 最大时间
    if(maxDateString && maxDateString.length > 0){
        maxDate = [formatter dateFromString:maxDateString];
    }else {
        maxDate = [NSDate date];
    }
    
    self.datePicker = [[HooDatePicker alloc] initWithSuperView:self.webView.superview andType:dateType andModeType:Modetype andMaxDate:maxDate andMinDate:minDate andShowSecond:(BOOL)showSecond];
    self.datePicker.delegate = self;
    [self initDatePickerWithOptions:options];
    [self.webView.superview addSubview:self.datePicker];
    [self.datePicker show];
}

// 时间(用于共存的情况)
- (void)initTimePicker:(NSMutableDictionary*)options{
    
    self.timePicker = [[HooDatePicker alloc] initWithSuperView:self.webView.superview andType:dateType andModeType:HooDatePickerModeTime andMaxDate:nil andMinDate:nil andShowSecond:(BOOL)showSecond];
    self.timePicker.delegate = self;
    [self initTimePickerWithOptions:options];
    [self.webView.superview addSubview:self.timePicker];
    [self.timePicker show];
}

// 初始化周选择器
- (void)initWeekPicker:(NSMutableDictionary*)options {
    self.timePicker = [[HooDatePicker alloc] initWithSuperView:self.webView.superview andType:dateType andModeType:HooDatePickerModeWeek andMaxDate:nil andMinDate:nil andShowSecond:(BOOL)showSecond];
    self.timePicker.delegate = self;
    [self initWeekPickerWithOptions:options];
    [self.webView.superview addSubview:self.timePicker];
    [self.timePicker show];
}

// 初始化日期选择
- (void)initDatePickerWithOptions:(NSMutableDictionary*)options
{
    // 类型
    if ([options objectForKey:@"type"]) {
    }
    NSDateFormatter *formatter = [self createISODateFormatter: DATETIME_FORMAT timezone:[NSTimeZone defaultTimeZone]];
    
    //    int type = [[options objectForKey:@"type"] intValue];
    NSString *dateString = [options objectForKey:@"date"];
    //    BOOL allowOldDates = ([[options objectForKey:@"allowOldDates"] intValue] == 0) ? NO : YES;
    //    BOOL allowFutureDates = ([[options objectForKey:@"allowFutureDates"] intValue] == 0) ? NO : YES;
    //    NSString *minDateString = [options objectForKey:@"minDate"];
    //    NSString *maxDateString = [options objectForKey:@"maxDate"];
    //    NSInteger minuteInterval = [minuteIntervalString integerValue];
    // 确认、取消按钮 文字及颜色
    NSString *sureTxt = [options objectForKey:@"doneButtonLabel"];
    NSString *sureColor = [options objectForKey:@"doneButtonColor"];
    NSString *cancelTxt = [options objectForKey:@"cancelButtonLabel"];
    NSString *cancelColor = [options objectForKey:@"cancelButtonColor"];
    // 设置高亮颜色
    NSString *highLightColor = [options objectForKey:@"highLightColor"];
    
    NSString *localId = [options objectForKey:@"locale"];
    NSLocale *locale = nil;
    
    /*************** 设置 ***************/
    
    
    // 确认、取消按钮设置
    if (sureTxt == nil || sureTxt.length == 0) {
        sureTxt = @"确定";
    }
    if (cancelTxt == nil || cancelTxt.length == 0) {
        cancelTxt = @"取消";
    }
    [self.datePicker setKSureButtonText:sureTxt];
    [self.datePicker setKCancelButtonText:cancelTxt];
    if (sureColor == nil || sureColor.length == 0) {
        sureColor = @"#0D0D0D";
    }
    if (cancelColor == nil || cancelColor.length == 0) {
        cancelColor = @"#0D0D0D";
    }
    [self.datePicker setKSureButtonTextColor:[self stringToColor:sureColor]];
    [self.datePicker setKCancelButtonTextColor:[self stringToColor:cancelColor]];
    // 高亮颜色
    if (highLightColor == nil || highLightColor.length == 0) {
        highLightColor = @"#FFB412";
    }
    //    [self.datePicker setHighlightColor:[self stringToColor:highLightColor]];
    
    // 时区设置
    if (localId && localId.length > 0) {
        locale = [[NSLocale alloc] initWithLocaleIdentifier:[options objectForKey:@"locale"]];
    }else {
        locale = [NSLocale currentLocale];
    }
    if (locale) {
        [self.datePicker setLocale:locale];
    }
    NSDate *nowDate =[formatter dateFromString:dateString];
    
    [self.datePicker setDate:nowDate animated:YES];
}

// 初始化时间选择
- (void)initTimePickerWithOptions:(NSMutableDictionary*)options{
    NSDateFormatter *formatter = [self createISODateFormatter: DATE_TIME_FORMAT timezone:[NSTimeZone localTimeZone]];
    // 确认、取消按钮 文字及颜色
    NSString *sureTxt = [options objectForKey:@"doneButtonLabel"];
    NSString *sureColor = [options objectForKey:@"doneButtonColor"];
    NSString *cancelTxt = [options objectForKey:@"cancelButtonLabel"];
    NSString *cancelColor = [options objectForKey:@"cancelButtonColor"];
    // 设置高亮颜色
    NSString *highLightColor = [options objectForKey:@"highLightColor"];
    NSString *localId = [options objectForKey:@"locale"];
    NSLocale *locale = nil;
    // 确认、取消按钮设置
    if (sureTxt == nil || sureTxt.length == 0) {
        sureTxt = @"确定";
    }
    if (cancelTxt == nil || cancelTxt.length == 0) {
        cancelTxt = @"取消";
    }
    [self.timePicker setKSureButtonText:sureTxt];
    [self.timePicker setKCancelButtonText:cancelTxt];
    if (sureColor == nil || sureColor.length == 0) {
        sureColor = @"#0D0D0D";
    }
    if (cancelColor == nil || cancelColor.length == 0) {
        cancelColor = @"#0D0D0D";
    }
    [self.timePicker setKSureButtonTextColor:[self stringToColor:sureColor]];
    [self.timePicker setKCancelButtonTextColor:[self stringToColor:cancelColor]];
    // 高亮颜色
    if (highLightColor == nil || highLightColor.length == 0) {
        highLightColor = @"#FFB412";
    }
    //    [self.timePicker setHighlightColor:[self stringToColor:highLightColor]];
    // 时区设置
    if (localId && localId.length > 0) {
        locale = [[NSLocale alloc] initWithLocaleIdentifier:[options objectForKey:@"locale"]];
    }else {
        locale = [NSLocale currentLocale];
    }
    if (locale) {
        [self.timePicker setLocale:locale];
    }
    self.timePicker.timeZone = [NSTimeZone defaultTimeZone];
    // 截取出时间
    NSString *dateString = [options objectForKey:@"date"];
    NSDateFormatter *formatterDate = [self createISODateFormatter: DATETIME_FORMAT timezone:[NSTimeZone localTimeZone]];
    NSDate *timeDate =[formatterDate dateFromString:dateString];
    [self.timePicker setDate:timeDate animated:YES];
}

- (void)initWeekPickerWithOptions:(NSMutableDictionary*)options{
//    NSDateFormatter *formatter = [self createISODateFormatter: DATE_TIME_FORMAT timezone:[NSTimeZone localTimeZone]];
    // 确认、取消按钮 文字及颜色
    NSString *sureTxt = [options objectForKey:@"doneButtonLabel"];
    NSString *sureColor = [options objectForKey:@"doneButtonColor"];
    NSString *cancelTxt = [options objectForKey:@"cancelButtonLabel"];
    NSString *cancelColor = [options objectForKey:@"cancelButtonColor"];
    NSInteger weekIndex = 0;
    NSInteger weekMaxIndex = 1;
    NSArray *weekNameArr;
    // 设置高亮颜色
    NSString *highLightColor = [options objectForKey:@"highLightColor"];
    NSString *localId = [options objectForKey:@"locale"];
    NSLocale *locale = nil;
    // 确认、取消按钮设置
    if (sureTxt == nil || sureTxt.length == 0) {
        sureTxt = @"确定";
    }
    if (cancelTxt == nil || cancelTxt.length == 0) {
        cancelTxt = @"取消";
    }
    [self.timePicker setKSureButtonText:sureTxt];
    [self.timePicker setKCancelButtonText:cancelTxt];
    if (sureColor == nil || sureColor.length == 0) {
        sureColor = @"#0D0D0D";
    }
    if (cancelColor == nil || cancelColor.length == 0) {
        cancelColor = @"#0D0D0D";
    }
    [self.timePicker setKSureButtonTextColor:[self stringToColor:sureColor]];
    [self.timePicker setKCancelButtonTextColor:[self stringToColor:cancelColor]];
    // 高亮颜色
    if (highLightColor == nil || highLightColor.length == 0) {
        highLightColor = @"#FFB412";
    }
    //    [self.timePicker setHighlightColor:[self stringToColor:highLightColor]];
    // 时区设置
    if (localId && localId.length > 0) {
        locale = [[NSLocale alloc] initWithLocaleIdentifier:[options objectForKey:@"locale"]];
    }else {
        locale = [NSLocale currentLocale];
    }
    if (locale) {
        [self.timePicker setLocale:locale];
    }
    self.timePicker.timeZone = [NSTimeZone defaultTimeZone];
    
    if ([options objectForKey:@"weekIndex"]) {
        weekIndex = [[options objectForKey:@"weekIndex"] intValue];
    }
    if ([options objectForKey:@"weeKMaxIndex"]) {
        weekMaxIndex = [[options objectForKey:@"weeKMaxIndex"] intValue];
    }
    if ([options objectForKey:@"weekNameArr"]) {
        weekNameArr = [[options objectForKey:@"weekNameArr"] mutableCopy];
    }
    
    [self.timePicker setWeek:weekIndex andWeekName:weekNameArr andMaxWeek:weekMaxIndex animated:YES];
}


// 根据约定类型设置时间格式
- (HooDatePickerMode)setDatePickerMode:(int)type
{
    HooDatePickerMode mode = HooDatePickerModeDate;
    switch (type) {
        case 0:
            // 时间
            mode = HooDatePickerModeTime;
            break;
        case 1:
            // 日期+时间
            mode = HooDatePickerModeDateAndTime;
            break;
        case 2:
            // 年-月-日
            mode = HooDatePickerModeDate;
            break;
        case 3:
            // 年-月
            mode = HooDatePickerModeYearAndMonth;
            break;
        case 4:
            // 年
            mode = HooDatePickerModeYear;
            break;
        case 5:
            // 年-月-日 时:分:秒
            mode = HooDatePickerModeFullDateAndTime;
            break;
        case 6:
            mode = HooDatePickerModeWeek;
            break;
        default:
            // 默认 年-月-日
            mode = HooDatePickerModeDate;
            break;
    }
    return mode;
}

- (NSDateFormatter *)createISODateFormatter:(NSString *)format timezone:(NSTimeZone *)timezone {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    NSLocale *loc = [NSLocale currentLocale];
    [dateFormatter setLocale: loc];
    [dateFormatter setTimeZone:[NSTimeZone timeZoneWithAbbreviation:@"UTC"]];
    [dateFormatter setDateFormat:format];
    
    return dateFormatter;
}

#pragma mark - FlatDatePicker Delegate

- (void)datePicker:(HooDatePicker *)datePicker dateDidChange:(NSDate *)date {
    
    //    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    //    [dateFormatter setLocale:[NSLocale currentLocale]];
    //
    //    if (datePicker.datePickerMode == HooDatePickerModeDate) {
    //        [dateFormatter setDateFormat:@"dd MMMM yyyy"];
    //    } else if (datePicker.datePickerMode == HooDatePickerModeTime) {
    //        [dateFormatter setDateFormat:@"HH:mm:ss"];
    //    } else if (datePicker.datePickerMode == HooDatePickerModeYearAndMonth){
    //        [dateFormatter setDateFormat:@"MM/yy"];
    //    } else if (datePicker.datePickerMode == HooDatePickerModeYear){
    //        [dateFormatter setDateFormat:@"yyyy"];
    //    } else {
    //        [dateFormatter setDateFormat:@"dd MMMM yyyy HH:mm:ss"];
    //
    //    }
    //
    //    NSString *value = [dateFormatter stringFromDate:date];
    //
    ////    self.labelDateSelected.text = value;
}

- (void)datePicker:(HooDatePicker *)datePicker didCancel:(UIButton *)sender {
    [self.datePicker dismiss];
    [self.timePicker dismiss];
    NSString *jsCallback = @"datePicker._dateSelectionCanceled();";
    [self.commandDelegate evalJs:jsCallback];
}

- (void)datePicker:(HooDatePicker *)datePicker didSelectedDate:(NSDate*)date {
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setLocale:[NSLocale currentLocale]];
    
    if (datePicker.datePickerMode == HooDatePickerModeDate) {
        [dateFormatter setDateFormat:@"yyyy-MM-dd"];
    } else if (datePicker.datePickerMode == HooDatePickerModeTime) {
        [dateFormatter setDateFormat:@"HH:mm:ss"];
    } else if (datePicker.datePickerMode == HooDatePickerModeYearAndMonth){
        [dateFormatter setDateFormat:@"yyyy-MM"];
    } else if (datePicker.datePickerMode == HooDatePickerModeYear){
        [dateFormatter setDateFormat:@"yyyy"];
    } else {
        [dateFormatter setDateFormat:@"yyyy-dd-MM HH:mm:ss"];
    }
    if (nil == date) {
        if (self.datePicker) {
            [self.datePicker dismiss];
        }
        if (self.timePicker) {
            [self.timePicker dismiss];
        }
        NSString *jsCallback = @"datePicker._dateSelectionCanceled();";
        [self.commandDelegate evalJs:jsCallback];
        return;
    }
    NSString *value = [dateFormatter stringFromDate:date];
    NSMutableString *result = [NSMutableString stringWithString:value];
    // 日期 + 时间的情况
    if (dateType == 5) {
        if (datePicker.datePickerMode == HooDatePickerModeDate) {
            NSDate *timeDate = [self.timePicker getDate];
            [dateFormatter setDateFormat:@"HH:mm:ss"];
            NSString *timeValue = [dateFormatter stringFromDate:timeDate];
            [result appendString:@" "];
            [result appendString:timeValue];
        }else if (datePicker.datePickerMode == HooDatePickerModeTime) {
            NSDate *dateDate = [self.datePicker getDate];
            [dateFormatter setDateFormat:@"yyyy-MM-dd"];
            NSString *dateValue = [dateFormatter stringFromDate:dateDate];
            result = [NSMutableString string];
            [result appendString:dateValue];
            [result appendString:@" "];
            [result appendString:value];
        }
    }
    NSLog(@"选择的日期是：%@",result);
    if (self.datePicker) {
        [self.datePicker dismiss];
    }
    if (self.timePicker) {
        [self.timePicker dismiss];
    }
    NSString *jsCallback = [NSString stringWithFormat:@"datePicker._dateSelected(\"%@\");", result];
    [self.commandDelegate evalJs:jsCallback];
}

- (void)datePicker:(HooDatePicker *)dataPicker didSelectedWeek:(NSInteger)index {
    NSLog(@"选择的周是：%d",index);
    if (self.timePicker) {
        [self.timePicker dismiss];
    }

    NSString *jsCallback = [NSString stringWithFormat:@"datePicker._dateSelected(\"%d\");", index];
    [self.commandDelegate evalJs:jsCallback];
    NSLog(@"选择的周是：%d",index);
}

- (void)datePicker:(HooDatePicker *)dataPicker dismiss:(HooDatePickerMode)pickerMode {
    if (self.datePicker) {
        [self.datePicker dismiss];
    }
    if (self.timePicker) {
        [self.timePicker dismiss];
    }
}

- (void)datePicker:(HooDatePicker *)dataPicker didSelectedDateModel:(NSDate *)date {
    [self.datePicker setHighLightModel:0];
    if (dataPicker.datePickerMode == HooDatePickerModeDate) {
        return;
    }
    [self.timePicker setHidden:true];
    [self.datePicker setHidden:false];
    [self.webView.superview bringSubviewToFront:self.datePicker];
    
}

- (void)datePicker:(HooDatePicker *)dataPicker didSelectedTimeModel:(NSDate *)date {
    [self.timePicker setHighLightModel:1];
    if (dataPicker.datePickerMode == HooDatePickerModeTime) {
        return;
    }
    [self.datePicker setHidden:true];
    [self.timePicker setHidden:false];
    [self.webView.superview bringSubviewToFront:self.timePicker];
    
}

- (UIColor *) stringToColor:(NSString *)str
{
    if (!str || [str isEqualToString:@""]) {
        return nil;
    }
    unsigned red,green,blue;
    NSRange range;
    range.length = 2;
    range.location = 1;
    [[NSScanner scannerWithString:[str substringWithRange:range]] scanHexInt:&red];
    range.location = 3;
    [[NSScanner scannerWithString:[str substringWithRange:range]] scanHexInt:&green];
    range.location = 5;
    [[NSScanner scannerWithString:[str substringWithRange:range]] scanHexInt:&blue];
    UIColor *color= [UIColor colorWithRed:red/255.0f green:green/255.0f blue:blue/255.0f alpha:1];
    return color;
}

@end
