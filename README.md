## iOS ##
#import "CDVGeolocation.h"
@interface AppDelegate : CDVAppDelegate {}
@property (nonatomic, strong) CDVGeolocation* geolocation;
@end

CDVGeolocation.m
- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
增加
	self.geolocation = [[CDVGeolocation alloc] init];
    [self.geolocation initLocationManager];
    self.geolocation.uploadUrl = @"http://test.dingdongcheguanjia.com/api/geolocation";//测试
    self.geolocation.uploadUrl = @"http://api.dingdongcheguanjia.com/api/geolocation";//生产


Targets > XXX > General > Linked Frameworks and Libraries > AFNetworking.framework 
status改为optional
  
 ## Android ##  
org.apache.cordova.geolocation.LocationService
uploadUrl = "http://test.dingdongcheguanjia.com/api/geolocation"
