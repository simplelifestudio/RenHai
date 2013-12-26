//
//  LeftBarViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "LeftBarViewController_iPhone.h"

#import "CBUIUtils.h"

#import "GUIModule.h"
#import "AppDataModule.h"

#import "LeftBarTableCell_iPhone.h"
#import "LeftBarHeaderView_iPhone.h"
#import "LeftBarFooterView_iPhone.h"

#import "MainViewController_iPhone.h"

#define NIB_LeftBarHeaderView_iPhone @"LeftBarHeaderView_iPhone"

#define NIB_LeftBarFooterView_iPhone @"LeftBarFooterView_iPhone"
#define HEIGHT_LeftBarFooterView 100

#define HEIGHT_LeftBarCell 60;

#define NIB_LEFTBARTABLECELL @"LeftBarTableCell_iPhone"

@interface LeftBarViewController_iPhone ()
{
    NSInteger _selectedRow;
    
    GUIModule* _guiModule;
    AppDataModule* _appDataModule;
}

@end

@implementation LeftBarViewController_iPhone

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self _setupInstance];
}

- (void) _setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _appDataModule = [AppDataModule sharedInstance];
    
    [self _setupHeaderView];
    [self _setupTableView];
    [self _setupFooterView];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    MainViewController_iPhone* mainVC = _guiModule.mainViewController;
    
    UINavigationController* frontVC = (UINavigationController*)mainVC.frontViewController;
    UIViewController* presentingVC = frontVC.visibleViewController;
    if (presentingVC == _guiModule.homeViewController)
    {
        _selectedRow = MAINSCENESTATUS_HOME;
    }
    else if (presentingVC == _guiModule.impressViewController)
    {
        _selectedRow = MAINSCENESTATUS_IMPRESS;
    }
    else if (presentingVC == _guiModule.interestViewController)
    {
        _selectedRow = MAINSCENESTATUS_INTEREST;
    }
    else
    {
        _selectedRow = MAINSCENESTATUS_HOME;
    }
    
    [self.tableView reloadData];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return MAINSCENESTATUS_COUNT;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{    
    LeftBarTableCell_iPhone* cell = [tableView dequeueReusableCellWithIdentifier:NIB_LEFTBARTABLECELL];
    
    NSString* cellText = nil;
    NSInteger row = indexPath.row;
    switch (row)
    {
        case MAINSCENESTATUS_HOME:
        {
            cellText = NAVIGATIONBAR_TITLE_HOME;
            break;
        }
        case MAINSCENESTATUS_IMPRESS:
        {
            cellText = NAVIGATIONBAR_TITLE_IMPRESS;
            break;
        }
        case MAINSCENESTATUS_INTEREST:
        {
            cellText = NAVIGATIONBAR_TITLE_INTEREST;
            break;
        }
        default:
        {
            cellText = @"";
            break;
        }
    }
    
    UIColor* textColor = FLATUI_COLOR_LEFTBAR_CELL_TEXT;
    UIColor* backColor = FLATUI_COLOR_LEFTBAR_CELL_BACKGROUND;
    if (row == _selectedRow)
    {
        textColor = FLATUI_COLOR_LEFTBAR_CELL_TEXT_SELECTED;
        backColor = FLATUI_COLOR_LEFTBAR_CELL_BACKGROUND_SELECTED;
    }
    else
    {
        
    }

    cell.titleLabel.text = cellText;
    cell.titleLabel.textColor = textColor;

    cell.contentView.backgroundColor = backColor;
    
    cell.selectedBackgroundView = [[UIView alloc] initWithFrame:cell.frame];
    cell.selectedBackgroundView.backgroundColor = FLATUI_COLOR_LEFTBAR_CELL_BACKGROUND_SELECTING;
    
    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UINavigationController* navigationVC = _guiModule.navigationController;
    MainViewController_iPhone* mainVC = _guiModule.mainViewController;
    
    NSInteger row = indexPath.row;

    switch (row)
    {
        case MAINSCENESTATUS_HOME:
        {
            HomeViewController_iPhone* vc = _guiModule.homeViewController;
            [navigationVC popToRootViewControllerAnimated:NO];
            if (navigationVC.visibleViewController != vc)
            {
                [navigationVC pushViewController:vc animated:NO];
            }
            [mainVC showViewController:navigationVC animated:YES completion:nil];
            
            break;
        }
        case MAINSCENESTATUS_IMPRESS:
        {
            ImpressViewController_iPhone* vc = _guiModule.impressViewController;
            [navigationVC popToRootViewControllerAnimated:NO];
            if (navigationVC.visibleViewController != vc)
            {
                [navigationVC pushViewController:vc animated:NO];
            }
            [mainVC showViewController:navigationVC animated:YES completion:nil];
            
            break;
        }
        case MAINSCENESTATUS_INTEREST:
        {
            InterestViewController_iPhone* vc = _guiModule.interestViewController;
            [navigationVC popToRootViewControllerAnimated:NO];
            if (navigationVC.visibleViewController != vc)
            {
                [navigationVC pushViewController:vc animated:NO];
            }
            [mainVC showViewController:navigationVC animated:YES completion:nil];
            
            break;
        }
        default:
        {
            
            break;
        }
    }
    
}

//-(UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
//{    
//    LeftBarHeaderView_iPhone* headerView = [CBUIUtils componentFromNib:NIB_LeftBarHeaderView_iPhone owner:self options:nil];
//    headerView.backgroundColor = FLATUI_COLOR_UIVIEW_BACKGROUND;
//    return headerView;
//}
//
//- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
//{
//    CGFloat navigationBarHeight = _guiModule.navigationController.navigationBar.frame.size.height;
//    
//    NSUInteger requireCount = 0;
//    if ([UIDevice isRunningOniOS7AndLater])
//    {
//        CGFloat statusBarHeight = [UIApplication sharedApplication].statusBarFrame.size.height;
//        requireCount = statusBarHeight + navigationBarHeight;
//    }
//    else
//    {
//        requireCount = navigationBarHeight;
//    }
//    
//    return requireCount;
//}
//
//-(UIView*)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
//{
//    LeftBarFooterView_iPhone* footerView = [CBUIUtils componentFromNib:NIB_LeftBarFooterView_iPhone owner:self options:nil];
//    footerView.backgroundColor = FLATUI_COLOR_UIVIEW_BACKGROUND;
//    return footerView;
//}
//
//- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
//{
//    return HEIGHT_LeftBarFooterView;
//}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return HEIGHT_LeftBarCell;
}

#pragma mark - Private Methods

- (void) _setupHeaderView
{
    
}

- (void) _setupTableView
{
    _tableView.backgroundColor = FLATUI_COLOR_UIVIEW_BACKGROUND;
    
    UINib* nib = [UINib nibWithNibName:NIB_LEFTBARTABLECELL bundle:nil];
    [_tableView registerNib:nib forCellReuseIdentifier:NIB_LEFTBARTABLECELL];
    
    _tableView.delegate = self;
    _tableView.dataSource = self;
}

- (void) _setupFooterView
{
    _orgLabel.text = NSLocalizedString(@"LeftBar_Organization", nil);
    _verionLabel.text = [NSString stringWithFormat:NSLocalizedString(@"LeftBar_Version", nil), _appDataModule.appFullVerion];
}

@end
