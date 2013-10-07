//
//  WarningViewController_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "WarningViewController_iPhone.h"

@interface WarningViewController_iPhone ()
{
    NSTimer* _timer;
    NSUInteger _count;
    void(^_processBlockWrapper)(BOOL(^processBlock)());
}

@property (nonatomic) NSUInteger count;

@end

@implementation WarningViewController_iPhone

@synthesize hideActionButton = _hideActionButton;
@synthesize hideCountLabel = _hideCountLabel;
@synthesize hideInfoTextView = _hideInfoTextView;
@synthesize hideInfoLabel = _hideInfoLabel;

@synthesize infoText = _infoText;
@synthesize infoDetailText = _infoDetailText;

@synthesize completeBlock = _completeBlock;
@synthesize processBlock = _processBlock;

#pragma mark - Public Methods

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        // Custom initialization
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    _infoLabel.hidden = _hideInfoLabel;
    _infoTextView.hidden = _hideInfoTextView;
    _countLabel.hidden = _hideCountLabel;
    _actionButton.hidden = _hideActionButton;
    
    _infoLabel.text = _infoText;
    _infoTextView.text = _infoDetailText;
    _actionButton.titleLabel.text = NSLocalizedString(@"GUI_Reconnect", nil);
    
    _count = 0;
    _countLabel.text = [NSString stringWithFormat:@"%d", _count];    
}

- (void) viewWillAppear:(BOOL)animated
{
    typeof(self) __weak weakSelf = self;
    _processBlockWrapper = ^(BOOL(^processBlock)()){

        sleep(1);
        
        BOOL flag = processBlock();
        flag = NO;
        if (flag)
        {
            sleep(3);
        }
        else
        {
            sleep(1);
        }
        
        [weakSelf _clockCancel];
        
        if (flag)
        {
            dispatch_async(dispatch_get_main_queue(), ^(){
                [weakSelf dismissViewControllerAnimated:NO completion:weakSelf.completeBlock];}
                           );
        }
        else
        {
            dispatch_async(dispatch_get_main_queue(), ^(){

                weakSelf.actionButton.hidden = NO;
            });
        }
    };
    
    [super viewWillAppear:animated];
}

- (void) viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self _clockStart];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^(){
        _processBlockWrapper(_processBlock);
    });
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setHideActionButton:(BOOL)hideActionButton
{
    _hideActionButton = hideActionButton;
}

- (void)setHideCountLabel:(BOOL)hideCountLabel
{
    _hideCountLabel = hideCountLabel;
}

- (void)setHideInfoTextView:(BOOL)hideInfoTextView
{
    _hideInfoTextView = hideInfoTextView;
}

- (void)setHideInfoLabel:(BOOL)hideInfoLabel
{
    _hideInfoLabel = hideInfoLabel;
}

- (void)setInfoText:(NSString *)infoText
{
    _infoText = infoText;
    dispatch_async(dispatch_get_main_queue(), ^(){
        _infoLabel.text = _infoText;
    });
}

- (void)setInfoDetailText:(NSString *)infoDetailText
{
    _infoDetailText = infoDetailText;
    dispatch_async(dispatch_get_main_queue(), ^(){
        _infoTextView.text = _infoDetailText;
    });
}

#pragma mark - IBAction Methods

- (IBAction)didPressActionButton:(id)sender
{
    
}

#pragma mark - Private Methods

- (void) _clockStart
{
    NSTimeInterval interval = 1.0;
    _timer = [NSTimer scheduledTimerWithTimeInterval:interval target:self selector:@selector(_clockClick) userInfo:nil repeats:YES];
}

- (void) _clockClick
{
    dispatch_async(dispatch_get_main_queue(), ^(){
        [_countLabel setText:[NSString stringWithFormat:@"%d", _count]];
    });
    
    _count++;
}

- (void) _clockCancel
{
    [_timer invalidate];
}

@end
