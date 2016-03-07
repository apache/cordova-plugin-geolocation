//
//  CDVGeoloction.h
//
//  Created by DINGXIN on 16/3/4.
//
//

#import <CoreLocation/CoreLocation.h>

@interface CDVGeolocation: NSObject<CLLocationManagerDelegate>

@property (nonatomic, strong) CLLocationManager* locationManager;

@property (nonatomic, strong) NSString* uploadUrl;

@property (nonatomic, strong) NSDate *timestamp;

- (void) initLocationManager;

- (void) uploadLocation: (CLLocation*) location;

@end
