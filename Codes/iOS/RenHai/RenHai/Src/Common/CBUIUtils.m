//
//  CBUIUtils.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBUIUtils.h"

@implementation CBUIUtils

// Manual Codes Begin

+(id<UIApplicationDelegate>) getAppDelegate
{
    return [[UIApplication sharedApplication] delegate];
}

+(UIWindow*) getKeyWindow
{
    return [UIApplication sharedApplication].keyWindow;
}

+(UIWindow*) getWindow:(UIView *)view
{
    if (nil == view) 
    {
        DDLogWarn(@"Parameter UIView* view is nil");
        return nil;
    }
    else
    {
        for (UIView* nextView = view; nextView; nextView = nextView.superview)
        {
            if([nextView isKindOfClass:[UIWindow class]])
            {
                return (UIWindow*) nextView;
            }
        }
        DDLogWarn(@"Can't find window for this view: %@", view);
        return nil;        
    }
}

+(void)removeSubViews:(UIView *)superView
{
    if (nil != superView)
    {
        NSArray* subViews = superView.subviews;
        for (UIView* subView in subViews)
        {
            [subView removeFromSuperview];
        }
    }
}

+(UIViewController*) getViewControllerFromView:(UIView *)view
{
    if(nil == view)
    {
        DDLogWarn(@"Parameter UIView* view is nil.");
        return nil;
    }
    else
    {
        for (UIView* nextView = view; nextView; nextView = nextView.superview)
        {
            UIResponder *nextResponder = [nextView nextResponder];
            
            if([nextResponder isKindOfClass:[UIViewController class]])
            {
                return (UIViewController*) nextResponder;
            }
        }
        DDLogWarn(@"Can't find controller for this view: %@", view);
        return nil;
    }
}

+(void) showInformationAlertWindow:(id) delegate andMessage:(NSString*) message
{
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"STR_ALERT", nil) message:message delegate:delegate cancelButtonTitle:NSLocalizedString(@"STR_OK", nil) otherButtonTitles: nil];
	[alert show];	 
}

+(void) showInformationAlertWindow:(id)delegate andError:(NSError *)error
{    
    NSString *message = [error localizedDescription];
    
    [CBUIUtils showInformationAlertWindow:delegate andMessage: message];
}

+(UIAlertView*) createProgressAlertView:(NSString *) title andMessage:(NSString *) message andActivity:(BOOL) activity andDelegate:(id)delegate
{
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle: title
                                                        message: message
                                                       delegate: delegate
                                              cancelButtonTitle: nil
                                              otherButtonTitles: nil];  
    
    CGFloat x = 0;
    CGFloat y = 0;
    CGFloat width = 0;
    CGFloat height = 0;
    
    if (activity) 
    {
        x = 139.0f - 18.0f;
        y = 80.0f;
        width = 37.0f;
        height = 37.0f;
        
        if (nil == title) 
        {
            y = y - 20.0f;
        }
        
        if (nil == message)
        {
            y = y - 20.0f;
        }
        
        UIActivityIndicatorView *activityView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
        activityView.frame = CGRectMake(x, y, width, height);
        [alertView addSubview:activityView];
        [activityView startAnimating];
    } 
    else 
    {
        x = 30.0f;
        y = 80.0f;
        width = 225.0f;
        height = 90.0f;       
        
        // TODO: Need adjust x and y values if title or message is NULL.
        
        UIProgressView *progressView = [[UIProgressView alloc] initWithFrame:CGRectMake(x, y, width, height)];
        [alertView addSubview:progressView];
        [progressView setProgressViewStyle: UIProgressViewStyleBar];
    }
    
    return alertView;
}

+(id) componentFromNib:(NSString*) nibId owner:(id) owner options:(NSDictionary*) options
{
    id component = nil;
    
    if (nil != nibId && 0 < nibId.length)
    {
        NSArray* nib = [[NSBundle mainBundle] loadNibNamed:nibId owner:owner options:options];
        component = [nib objectAtIndex:0];
    }
    
    return component;
}

// Manual Codes End

@end
