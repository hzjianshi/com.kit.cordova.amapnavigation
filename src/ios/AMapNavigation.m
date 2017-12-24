#import <UIKit/UIKit.h>
#import <Cordova/CDV.h>
#import <AMapFoundationKit/AMapFoundationKit.h>
#import <MAMapKit/MAMapKit.h>
#import <AMapNaviKit/AMapNaviKit.h>


@interface AMapNavigation : CDVPlugin <AMapNaviRideManagerDelegate,AMapNaviRideViewDelegate>{
    // Member variables go here.
    NSString* callbackId;
    CGFloat opType;
}

@property (nonatomic, strong)NSString*                  amapApiKey;
@property (nonatomic, strong)AMapNaviRideManager*       rideManager;
@property (nonatomic, strong)AMapNaviRideView*          rideview;
@property (nonatomic, strong)AMapNaviPoint*             startPoint;
@property (nonatomic, strong)AMapNaviPoint*             endPoint;
@property (nonatomic, strong)MAMapView*                 mapview;

- (void)navigation:(CDVInvokedUrlCommand*)command;
@end

@implementation AMapNavigation

- (void)navigation:(CDVInvokedUrlCommand*)command
{
    callbackId = command.callbackId;

    [AMapServices sharedServices].apiKey = @"b0cbac6fc683b46332628e2a19a54106";


    CGFloat startLng = [[command.arguments objectAtIndex:0] doubleValue];
    CGFloat startLat = [[command.arguments objectAtIndex:1] doubleValue];
    CGFloat endLng = [[command.arguments objectAtIndex:2] doubleValue];
    CGFloat endLat = [[command.arguments objectAtIndex:3] doubleValue];
    opType = [[command.arguments objectAtIndex:4] doubleValue];
    navWay = [[command.arguments objectAtIndex:5] doubleValue];


    [self initRideManager];
    [self initRideView];
    [self initMapView];
    [self ConnectRideView];
    AMapNaviPoint* startPoint = [AMapNaviPoint locationWithLatitude:startLat longitude:startLng];
    AMapNaviPoint* endPoint = [AMapNaviPoint locationWithLatitude:endLat longitude:endLng];
    [self caculate:startPoint endPoint:endPoint];

}

- (void)rideManagerOnCalculateRouteSuccess:(AMapNaviDriveManager *)rideManager
{
    if(opType== 0)
    {
        [self.rideManager startEmulatorNavi];
    }
    else
    {
        [self.rideManager startGPSNavi];
    }
}

- (void)rideViewCloseButtonClicked:(AMapNaviDriveView *)rideview
{
    //停止导航
    [self.rideManager stopNavi];
    [self.rideManager removeDataRepresentative:self.rideview];
        //停止语音
    [self.rideview removeFromSuperview];
}


- (void)initMapView
{
    if (self.mapview == nil)
    {
        self.mapview = [[MAMapView alloc] initWithFrame:self.webView.bounds];
        self.mapview.frame = self.webView.bounds;
        self.mapview.delegate = self;
    }
}


- (void)driveManagerOnCalculateRouteSuccess:(AMapNaviDriveManager *)rideManager
{
    [self.rideManager startEmulatorNavi];
}

- (NSString*)amapApiKey{
    if(!_amapApiKey){
        CDVViewController* viewController = (CDVViewController*)self.viewController;
        _amapApiKey = [viewController.settings objectForKey:@"amapapikey"];
    }
    return _amapApiKey;
}


- (void)initRideManager{
    if(self.rideManager == nil){
        self.rideManager = [[AMapNaviRideManager alloc] init];
        [self.rideManager setDelegate:self];
    }
}


- (void)initRideView {
    if (self.rideview == nil)
    {
        self.rideview = [[AMapNaviRideView alloc] initWithFrame:self.webView.bounds];
        [self.rideview setDelegate:self];
    }
}
-(void)ConnectRideView{
    [self.rideManager addDataRepresentative:self.rideview];
    [self.webView.superview addSubview:self.rideview];
}

-(void)caculate:(AMapNaviPoint*)startPoint endPoint:(AMapNaviPoint*)endPoint{
    [self.rideManager calculateRideRouteWithStartPoint:startPoint
                                              endPoint:endPoint];
}



@end
