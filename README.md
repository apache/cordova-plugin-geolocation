#import "CDVGeolocation.h"
@interface AppDelegate : CDVAppDelegate {}

@property (nonatomic, strong) CDVGeolocation* geolocation;

@end


    self.geolocation = [[CDVGeolocation alloc] init];
    [self.geolocation initLocationManager];
    self.geolocation.uploadUrl = @"http://test.dingdongcheguanjia.com/api/geolocation";
    
    
org.apache.cordova.geolocation.LocationService
uploadUrl = "http://test.dingdongcheguanjia.com/api/geolocation"
