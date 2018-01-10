#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>

@interface DatePicker : CDVPlugin {
    
}

- (void)show:(CDVInvokedUrlCommand*)command;
- (void)close:(CDVInvokedUrlCommand*)command;
@end
