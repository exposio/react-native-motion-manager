//
//  DeviceMotion.m
//
//  Created by Patrick Williams in beautiful Seattle, WA.
//

#import "React/RCTBridge.h"
#import "React/RCTEventDispatcher.h"
#import "DeviceMotion.h"

@interface DeviceMotion()

@property (nonatomic, readonly, nonnull) CMMotionManager* motionManager;

+ (NSDictionary*) dictionaryFromDeviceMotionData:(CMDeviceMotion*)deviceMotionData;

@end


@implementation DeviceMotion

@synthesize motionManager = _motionManager;

@synthesize bridge = _bridge;

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

RCT_EXPORT_MODULE();

- (id) init {
  self = [super init];
  NSLog(@"DeviceMotion");

  if (self) {
    self->_motionManager = [[CMMotionManager alloc] init];
    //DeviceMotion
    if(self.motionManager.deviceMotionAvailable)
    {
      NSLog(@"DeviceMotion available");
      /* Start the DeviceMotion if it is not active already */
      if(self.motionManager.deviceMotionActive)
      {
        NSLog(@"DeviceMotion active");
      } else {
        NSLog(@"DeviceMotion not active");
      }
    }
    else
    {
      NSLog(@"DeviceMotion not Available!");
    }
  }
  return self;
}

RCT_EXPORT_METHOD(setDeviceMotionUpdateInterval:(double) interval) {
  NSLog(@"setDeviceMotionUpdateInterval: %f", interval);

  self.motionManager.deviceMotionUpdateInterval = interval;
}

RCT_EXPORT_METHOD(getDeviceMotionUpdateInterval:(nonnull RCTResponseSenderBlock) cb) {
  double interval = self.motionManager.deviceMotionUpdateInterval;
  NSLog(@"getDeviceMotionUpdateInterval: %f", interval);
  cb(@[[NSNull null], [NSNumber numberWithDouble:interval]]);
}

RCT_EXPORT_METHOD(getDeviceMotionData:(nonnull RCTResponseSenderBlock) cb) {
  CMDeviceMotion* deviceMotionData = self.motionManager.deviceMotion;

  cb(@[ [NSNull null], [DeviceMotion dictionaryFromDeviceMotionData:deviceMotionData] ]);
}

RCT_EXPORT_METHOD(startDeviceMotionUpdates:(nonnull RCTResponseSenderBlock) cb) {
  [self.motionManager startDeviceMotionUpdates];
    
  /* Receive the deviceMotion data on this block */
  [self.motionManager startDeviceMotionUpdatesToQueue:[NSOperationQueue mainQueue]
                                          withHandler:^(CMDeviceMotion *deviceMotionData, NSError *error)
   {

     [self.bridge.eventDispatcher sendDeviceEventWithName:@"DeviceMotionData" body:[DeviceMotion dictionaryFromDeviceMotionData:deviceMotionData]];
   }];

  cb(@[[NSNull null]]);
}

RCT_EXPORT_METHOD(stopDeviceMotionUpdates) {
  NSLog(@"stopDeviceMotionUpdates");
  [self.motionManager stopDeviceMotionUpdates];
}


+ (NSDictionary*) dictionaryFromDeviceMotionData:(CMDeviceMotion*)deviceMotionData {
  return @{
    @"userAcceleration": @{
      @"x" : [NSNumber numberWithDouble:deviceMotionData.userAcceleration.x],
      @"y" : [NSNumber numberWithDouble:deviceMotionData.userAcceleration.y],
      @"z" : [NSNumber numberWithDouble:deviceMotionData.userAcceleration.z],
      @"timestamp" : [NSNumber numberWithDouble:deviceMotionData.timestamp]
    },
    @"gravity": @{
      @"x" : [NSNumber numberWithDouble:deviceMotionData.gravity.x],
      @"y" : [NSNumber numberWithDouble:deviceMotionData.gravity.y],
      @"z" : [NSNumber numberWithDouble:deviceMotionData.gravity.z],
      @"timestamp" : [NSNumber numberWithDouble:deviceMotionData.timestamp]
    },
    @"attitude": @{
      @"roll" : [NSNumber numberWithDouble:deviceMotionData.attitude.roll],
      @"pitch" : [NSNumber numberWithDouble:deviceMotionData.attitude.pitch],
      @"yaw" : [NSNumber numberWithDouble:deviceMotionData.attitude.yaw],
      @"m11" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m11],
      @"m11" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m11],
      @"m12" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m12],
      @"m13" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m13],
      @"m21" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m21],
      @"m22" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m22],
      @"m23" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m23],
      @"m31" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m31],
      @"m32" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m32],
      @"m33" : [NSNumber numberWithDouble:deviceMotionData.attitude.rotationMatrix.m33],
      @"timestamp" : [NSNumber numberWithDouble:deviceMotionData.timestamp]
    }
  };
}

@end
