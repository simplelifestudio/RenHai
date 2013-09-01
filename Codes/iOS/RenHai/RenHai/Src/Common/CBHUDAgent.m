//
//  CBHUDAgent.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBHUDAgent.h"

#import "MBProgressHUD.h"

#import "CBUIUtils.h"

@interface CBHUDAgent()
{
    MBProgressHUD* _HUD;
    
    UIView* _HUDView;
}

@end

@implementation CBHUDAgent

-(id) initWithUIView:(UIView*) view
{
    self = [super init];
    if (self)
    {
        [self _setupInstance];
        [self attachToView:view];
    }
    return self;
}

-(void) showHUD:(NSString*) majorStauts minorStatus:(NSString*) minorStatus delay:(NSInteger)seconds;
{
    MBProgressHUD* HUD = [self sharedHUD];

    HUD.labelText = majorStauts;
    if (minorStatus)
    {
        HUD.detailsLabelText = minorStatus;
    }
    else
    {
        HUD.detailsLabelText = nil;
    }
    
    [HUD showAnimated:YES whileExecutingBlock:^(){
        [NSThread sleepForTimeInterval:seconds];
    }];

}

-(void) attachToView:(UIView*) view
{
    _HUDView = view;
}

-(MBProgressHUD*) sharedHUD
{
    _HUD = [MBProgressHUD showHUDAddedTo:_HUDView animated:YES];
    
    _HUD.minSize = HUD_CENTER_SIZE;
    _HUD.mode = MBProgressHUDModeText;
    _HUD.labelText = nil;
    _HUD.detailsLabelText = nil;
    
    return _HUD;
}

- (void) releaseResources
{
}

#pragma mark - Private Methods

-(void) _setupInstance
{
    [self _setupHUD];
}

-(void) _setupHUD
{
    UIWindow* keyWindow = [CBUIUtils getKeyWindow];
    _HUD = [[MBProgressHUD alloc] initWithWindow:keyWindow];
    _HUD.mode = MBProgressHUDModeText;
    _HUD.minSize = HUD_CENTER_SIZE;
}

@end
