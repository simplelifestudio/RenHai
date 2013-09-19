//
//  HelpViewController_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "HelpViewController_iPhone.h"

#import "GUIModule.h"
#import "AppDataModule.h"

#import "GUIStyle.h"

@interface HelpViewController_iPhone ()
{
    GUIModule* _guiModule;
    AppDataModule* _appDataModule;
    
    NSArray *_imageArray;
    NSTimer *_displayTimer;
}

@end

@implementation HelpViewController_iPhone

@synthesize closeButton = _closeButton;
@synthesize pageControl = _pageControl;

@synthesize scrollView = _scrollView;
@synthesize helpView = _helpView;

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self _setupViewController];
    
    [self _setupNavigationBar];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (IBAction)onClickCloseButton:(id)sender
{
    [self _close];
}

-(IBAction)onPageTurn:(UIPageControl *)sender
{
    [self _activateDisplayTimer:FALSE];
    
    int pageNum = _pageControl.currentPage;
    CGSize pageSize = _scrollView.frame.size;
    CGRect rect = CGRectMake((pageNum) * pageSize.width, 0, pageSize.width, pageSize.height);
    [_scrollView scrollRectToVisible:rect animated:TRUE];
    
    [self _refreshPageControlButtonsStatus];
}

- (void)resetDisplayStatus
{
    _pageControl.currentPage = 0;
    
    [self onPageTurn:_pageControl];
}

#pragma mark - Private Methods

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    [self.navigationController.navigationBar setTintColor:FLATUI_COLOR_NAVIGATIONBAR];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_HELP;
}

-(void)_setupSideBarMenuButtons
{
    UIImage* sidebarMenuIcon_portrait = [GUIStyle sidebarMenuIconPortrait];
    UIImage* sidebarMenuIcon_landscape = [GUIStyle sidebarMenuIconLandscape];
    
    if (self.navigationController.revealController.type & PKRevealControllerTypeLeft)
    {
        UIBarButtonItem* leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:sidebarMenuIcon_portrait landscapeImagePhone:sidebarMenuIcon_landscape style:UIBarButtonItemStylePlain target:self action:@selector(_leftBarButtonItemClicked:)];
        
        self.navigationItem.leftBarButtonItem = leftBarButtonItem;
    }
}

-(void)_leftBarButtonItemClicked:(id)sender
{
    if (self.navigationController.revealController.focusedController == self.navigationController.revealController.leftViewController)
    {
        [self.navigationController.revealController showViewController:self.navigationController.revealController.frontViewController];
    }
    else
    {
        [self.navigationController.revealController showViewController:self.navigationController.revealController.leftViewController];
    }
}

#pragma mark - UIScrollViewDelegate

-(void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    CGFloat pageWidth = _scrollView.frame.size.width;
    int currentPage = floor((_scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    
    // repeat scrolling
    // N/A
    
    // single round scrolling
    if (currentPage < _imageArray.count)
    {
        _pageControl.currentPage = currentPage;
    }
    else
    {
        _pageControl.currentPage = _imageArray.count;
    }
    
    [self _refreshPageControlButtonsStatus];
}

-(void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
    [self _activateDisplayTimer:FALSE];
}

-(void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    [self _activateDisplayTimer:TRUE];
}

-(void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    
}

#pragma mark - Private Methods

-(void)_setupViewController
{
    _guiModule = [GUIModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    
    [self _initArray];
    [self _configHelpViewUI];
    [self _refreshPageControlButtonsStatus];
}

-(void)_initArray
{
    UIImage* helpImage1 = [UIImage imageNamed:@"help_01.JPG"];
    UIImage* helpImage2 = [UIImage imageNamed:@"help_02.JPG"];
    UIImage* helpImage3 = [UIImage imageNamed:@"help_03.JPG"];
    UIImage* helpImage4 = [UIImage imageNamed:@"help_04.JPG"];
    UIImage* helpImage5 = [UIImage imageNamed:@"help_05.JPG"];
    UIImage* helpImage6 = [UIImage imageNamed:@"help_06.JPG"];
    
    _imageArray = [NSArray arrayWithObjects: helpImage1, helpImage2, helpImage3, helpImage4, helpImage5, helpImage6, nil];
}

-(void)_configHelpViewUI
{
    _scrollView.delegate = self;
    
    CGFloat width = _scrollView.frame.size.width;
    CGFloat height = _scrollView.frame.size.height;
    
    // fill images
    for (int i = 0; i < _imageArray.count; i++)
    {
        UIImageView *subImageView = [[UIImageView alloc] initWithImage:[_imageArray objectAtIndex:i]];
        subImageView.frame = CGRectMake(width * i, 0, width, height);
        subImageView.contentMode = UIViewContentModeScaleToFill;//UIViewContentModeScaleAspectFit;
        [_scrollView addSubview: subImageView];
    }
    
    // set the whole scrollView's size
    [_scrollView setContentSize:CGSizeMake(width * _imageArray.count, height)];
    [_helpView addSubview:_scrollView];
    [_scrollView scrollRectToVisible:CGRectMake(0, 0, width, height) animated:NO];
    // set page control UI attributes
    _pageControl.backgroundColor = [UIColor clearColor];
    _pageControl.alpha = 1.0;
    _pageControl.numberOfPages = _imageArray.count;
    [_pageControl setBounds:CGRectMake(0, 0, 18 * (_pageControl.numberOfPages + 1), 18)];
    _pageControl.currentPage = 0;
    _pageControl.enabled = YES;
    [_helpView addSubview:_pageControl];
    [_pageControl addTarget:self action:@selector(onPageTurn:)forControlEvents:UIControlEventValueChanged];
    
    _closeButton.backgroundColor = FLATUI_COLOR_BUTTON;
    [_helpView addSubview:_closeButton];
    
    [self _registerGestureRecognizers];
    
    // set auto display timer
    [self _activateDisplayTimer:NO];
}

- (void) _registerGestureRecognizers
{
    UITapGestureRecognizer* tapRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_handleDoubleTap:)];
    tapRecognizer.numberOfTapsRequired = 2;
    [_helpView addGestureRecognizer:tapRecognizer];
}

- (void)_handleDoubleTap:(UIGestureRecognizer *)gestureRecognizer
{
    [self _close];
}

-(void)_scrollToNextPage:(id)sender
{
    // repeat scrolling
    //    int pageNum = _pageControl.currentPage;
    //    CGSize viewSize = _scrollView.frame.size;
    //
    //    if (pageNum == imageArray.count - 1)
    //    {
    //        CGRect newRect = CGRectMake(0, 0, viewSize.width, viewSize.height);
    //        [_scrollView scrollRectToVisible:newRect animated:NO];
    //    }
    //    else
    //    {
    //        pageNum++;
    //        CGRect rect = CGRectMake(pageNum * viewSize.width, 0, viewSize.width, viewSize.height);
    //        [_scrollView scrollRectToVisible:rect animated:TRUE];
    //    }
    
    // single round scrolling
    CGSize pageSize = _scrollView.frame.size;
    int pageNum = _pageControl.currentPage;
    if (pageNum == _imageArray.count - 1)
    {
        [_displayTimer invalidate];
    }
    else
    {
        pageNum++;
        CGRect rect = CGRectMake(pageNum * pageSize.width, 0, pageSize.width, pageSize.height);
        [_scrollView scrollRectToVisible:rect animated:TRUE];
    }
    
    [self _refreshPageControlButtonsStatus];
}

-(void) _close
{
    BOOL isAppLaunchedBefore = [_appDataModule isAppLaunchedBefore];
    if (!isAppLaunchedBefore)
    {
        [_appDataModule recordAppLaunchedBefore];

        [self.navigationController setNavigationBarHidden:NO];
        [self.navigationController popToRootViewControllerAnimated:NO];
        [self.navigationController pushViewController:_guiModule.homeViewController animated:NO];
        [_guiModule.mainViewController showViewController:self animated:YES completion:nil];
        
        [self removeFromParentViewController];        
    }
    else
    {
        [self dismissViewControllerAnimated:TRUE completion:^{}];
    }
}

-(void)_scrollToPrevPage:(id)sender
{
    // single round scrolling
    CGSize pageSize = _scrollView.frame.size;
    int pageNum = _pageControl.currentPage;
    if (pageNum == 0)
    {
        [_displayTimer invalidate];
    }
    else
    {
        pageNum--;
        CGRect rect = CGRectMake(pageNum * pageSize.width, 0, pageSize.width, pageSize.height);
        [_scrollView scrollRectToVisible:rect animated:TRUE];
    }
    
    [self _refreshPageControlButtonsStatus];
}

-(void) _activateDisplayTimer:(BOOL) activate
{
    if (activate)
    {
        _displayTimer = [NSTimer scheduledTimerWithTimeInterval:HELPSCREEN_DISPLAY_SECONDS target:self selector:@selector(_scrollToNextPage:) userInfo:nil repeats:YES];
    }
    else
    {
        if (_displayTimer.isValid)
        {
            [_displayTimer invalidate];
        }
    }
}

-(void) _refreshPageControlButtonsStatus
{
    NSInteger pageNum = _pageControl.currentPage;
    if (0 == pageNum)
    {
        [_closeButton setHidden:YES];
    }
    else if(pageNum == _imageArray.count - 1)
    {
        [_closeButton setHidden:NO];
    }
    else
    {
        [_closeButton setHidden:YES];
    }
}

@end
