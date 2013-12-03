//
//  SplashViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "SplashViewController_iPhone.h"

#import "CBModuleManager.h"
#import "CBAppUtils.h"
#import "CBUIUtils.h"

#import "AppDataModule.h"
#import "GUIModule.h"

#import "PKRevealController.h"
#import "MainViewController_iPhone.h"

#define STUDIOLABEL_DURATION 0.75
#define LABELS_DURATION 0.75
#define LABELS_DURATION_OFFSET 0.5
#define SPLASH_DURATION 0.25

@interface SplashViewController_iPhone ()
{
    GUIModule* _guiModule;
}

@property (nonatomic, strong) NSThread *loadStuffThread;

@end

@implementation SplashViewController_iPhone

@synthesize label1 = _label1;
@synthesize label2 = _label2;
@synthesize label3 = _label3;
@synthesize studioLabel = _studioLabel;

@synthesize loadStuffThread = _loadStuffThread;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {

    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    [self _setupViewController];
    
    _guiModule = [GUIModule sharedInstance];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

#pragma mark - Public Methods

- (void) loadAnyNecessaryStuff
{
    // All CBModules should start here
    
    CBModuleManager* moduleManager = [CBModuleManager sharedInstance];
    NSArray* moduleList = [moduleManager moduleList];
    float percents = 0;
    for (id<CBModule> module in moduleList)
    {
        percents = percents + module.moduleWeightFactor;
        percents = (1 < percents) ? 1 : percents;
        [module initModule];

        [module startService];
    }
    
    [self performSelectorOnMainThread:@selector(_startShowingStudioLabel) withObject:self waitUntilDone:YES];
}

- (void)startFadingSplashScreen
{
	[UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveLinear];    
	[UIView setAnimationDuration:SPLASH_DURATION];
	[UIView setAnimationDelegate:self];
	[UIView setAnimationDidStopSelector:@selector(finishFadingSplashScreen)];
	self.view.alpha = 0.0;
	[UIView commitAnimations];   
}

- (void) finishFadingSplashScreen
{
    [self _enterInApp];
}

#pragma mark - Private Methods

- (void) _startShowingStudioLabel
{
    if (SPLASHVIEW_HIDE)
    {
        [self _enterInApp];
    }
    else
    {
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
        [UIView setAnimationDuration:STUDIOLABEL_DURATION];
        [UIView setAnimationDelegate:self];
        
        _studioLabel.alpha = 0.0;
        
        [UIView setAnimationDidStopSelector:@selector(_finishShowingStudioLabel)];
        
        [UIView commitAnimations];
    }
}

- (void) _finishShowingStudioLabel
{    
    [self performSelectorOnMainThread:@selector(_startShowingLabels) withObject:self waitUntilDone:YES];
}

static NSInteger s_labelShowingIndex = 1;
static NSTimeInterval s_labelDuration = LABELS_DURATION;
- (void) _startShowingLabels
{
	[UIView beginAnimations:nil context:nil];
    [UIView setAnimationCurve:UIViewAnimationCurveEaseIn];
	[UIView setAnimationDuration:s_labelDuration];
    if (s_labelShowingIndex < 2)
    {
        s_labelDuration += LABELS_DURATION_OFFSET;
    }
    else
    {
        s_labelDuration += LABELS_DURATION_OFFSET * 2;
    }
	[UIView setAnimationDelegate:self];
	[UIView setAnimationDidStopSelector:@selector(_finishShowingLabels)];

    switch (s_labelShowingIndex)
    {
        case 1:
        {
            _label1.alpha = 1.0;
            break;
        }
        case 2:
        {
            _label2.alpha = 1.0;
            break;
        }
        case 3:
        {
            _label3.alpha = 1.0;
            break;
        }
        default:
        {
            break;
        }
    }
    
	[UIView commitAnimations];
}

- (void) _finishShowingLabels
{
    if (s_labelShowingIndex < 3)
    {
        s_labelShowingIndex++;        
        [self _startShowingLabels];
    }
    else
    {
        [self performSelectorOnMainThread:@selector(startFadingSplashScreen) withObject:self waitUntilDone:YES];
    }
}

- (void) _enterInApp
{
    [self dismissViewControllerAnimated:NO completion:nil];
    
    MainViewController_iPhone* mainViewController = _guiModule.mainViewController;
    
    [CBUIUtils setRootController:mainViewController];
}

- (void) _setupViewController
{
    [self _setupLabels];
    
    _loadStuffThread = [[NSThread alloc] initWithTarget:self selector:@selector(loadAnyNecessaryStuff)  object:nil];
    [_loadStuffThread start];
}

- (void) _setupLabels
{
    _label1.alpha = 0.0;
    _label2.alpha = 0.0;
    _label3.alpha = 0.0;
    
    _studioLabel.alpha = 1.0;
    
    _label2.textColor = FLATUI_COLOR_TOOLBAR;
}

#pragma mark - ScreenOrientation Methods

- (BOOL) shouldAutorotate
{
	return NO;
}

- (NSUInteger)supportedInterfaceOrientations
{
	return UIInterfaceOrientationMaskPortrait;
}

- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation
{
	return UIInterfaceOrientationPortrait;
}

@end
