//
//  CDVGeolocation.m
//
//  Created by DINGXIN on 16/3/4.
//
//

#import <Foundation/Foundation.h>
#import <AFNetworking/AFNetworking.h>

#import "CDVGeolocation.h"

@implementation CDVGeolocation

@synthesize locationManager;

- (void) initLocationManager {

    self.locationManager = [[CLLocationManager alloc] init];
    self.locationManager.delegate = self;
    [self.locationManager requestAlwaysAuthorization];

    self.locationManager.allowsBackgroundLocationUpdates = YES;
    self.locationManager.pausesLocationUpdatesAutomatically = NO;

    if (CLLocationManager.significantLocationChangeMonitoringAvailable) {
        [self.locationManager startMonitoringSignificantLocationChanges];
        [self.locationManager startUpdatingLocation];
    }
}

- (void)locationManager:(CLLocationManager*)manager
     didUpdateLocations:(NSArray*)locations
{
    //NSLog(@"-------------------------------didUpdateLocations");
    CLLocation* newLocation = [locations lastObject];
    if (self.timestamp == nil || -self.timestamp.timeIntervalSinceNow > 60) {
        self.timestamp = newLocation.timestamp;
        [self uploadLocation:newLocation];
    }
}

- (void) uploadLocation: (CLLocation*) location {
    NSDictionary *parameters = [NSDictionary dictionaryWithObjectsAndKeys:
                                [NSString stringWithFormat:@"%f",location.coordinate.longitude],      @"longitude", [NSString stringWithFormat:@"%f",location.coordinate.latitude], @"latitude",
                                nil];

    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:configuration];

    NSURLRequest *request = [[AFJSONRequestSerializer serializer] requestWithMethod:@"POST" URLString:self.uploadUrl parameters:parameters error:nil];

    NSURLSessionDataTask *dataTask = [manager dataTaskWithRequest:request completionHandler:^(NSURLResponse *response, id responseObject, NSError *error) {
        //if (error) {
        //    NSLog(@"Error: %@", error);
        //} else {
        //    NSLog(@"%@ %@", response, responseObject);
        //}
    }];
    [dataTask resume];
}

@end