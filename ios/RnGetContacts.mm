#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(RnGetContacts, NSObject)

RCT_EXTERN_METHOD(getContacts:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
