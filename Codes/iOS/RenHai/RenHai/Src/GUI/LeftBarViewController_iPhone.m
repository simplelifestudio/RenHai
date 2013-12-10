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

#import "LeftBarTableCell_iPhone.h"
#import "LeftBarHeaderView_iPhone.h"
#import "LeftBarFooterView_iPhone.h"

#define NIB_LeftBarHeaderView_iPhone @"LeftBarHeaderView_iPhone"

#define NIB_LeftBarFooterView_iPhone @"LeftBarFooterView_iPhone"
#define HEIGHT_LeftBarFooterView 100

#define HEIGHT_LeftBarCell 60;

#define NIB_LEFTBARTABLECELL @"LeftBarTableCell_iPhone"

@interface LeftBarViewController_iPhone ()
{
    NSInteger _selectedRow;
    
    GUIModule* _guiModule;
}

@end

@implementation LeftBarViewController_iPhone

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self)
    {

    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self _setupInstance];
}

- (void) _setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    
    self.tableView.backgroundColor = FLATUI_COLOR_UIVIEW_BACKGROUND;
    
    UINib* nib = [UINib nibWithNibName:NIB_LEFTBARTABLECELL bundle:nil];
    [self.tableView registerNib:nib forCellReuseIdentifier:NIB_LEFTBARTABLECELL];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    MainViewController_iPhone* mainVC = _guiModule.mainViewController;
    
    UINavigationController* frontVC = (UINavigationController*)mainVC.frontViewController;
    UIViewController* presentingVC = frontVC.visibleViewController;
    if (presentingVC == _guiModule.homeViewController)
    {
        _selectedRow = LEFTBAR_CELL_HOME;
    }
//    else if (presentingVC == _guiModule.deviceViewController)
//    {
//        _selectedRow = LEFTBAR_CELL_DEVICE;
//    }
    else if (presentingVC == _guiModule.impressViewController)
    {
        _selectedRow = LEFTBAR_CELL_IMPRESS;
    }
    else if (presentingVC == _guiModule.interestViewController)
    {
        _selectedRow = LEFTBAR_CELL_INTEREST;
    }
//    else if (presentingVC == _guiModule.configViewController)
//    {
//        _selectedRow = LEFTBAR_CELL_CONFIG;
//    }
    else
    {
        _selectedRow = LEFTBAR_CELL_HOME;
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
    return LEFTBAR_CELL_COUNT;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{    
    LeftBarTableCell_iPhone* cell = [tableView dequeueReusableCellWithIdentifier:NIB_LEFTBARTABLECELL];
    
    NSString* cellText = nil;
    NSInteger row = indexPath.row;
    switch (row)
    {
        case LEFTBAR_CELL_HOME:
        {
            cellText = NAVIGATIONBAR_TITLE_HOME;
            break;
        }
//        case LEFTBAR_CELL_DEVICE:
//        {
//            cellText = NAVIGATIONBAR_TITLE_DEVICE;
//            break;
//        }
        case LEFTBAR_CELL_IMPRESS:
        {
            cellText = NAVIGATIONBAR_TITLE_IMPRESS;
            break;
        }
        case LEFTBAR_CELL_INTEREST:
        {
            cellText = NAVIGATIONBAR_TITLE_INTEREST;
            break;
        }
//        case LEFTBAR_CELL_CONFIG:
//        {
//            cellText = NAVIGATIONBAR_TITLE_CONFIG;
//            break;
//        }
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
        case LEFTBAR_CELL_HOME:
        {
            [navigationVC popToRootViewControllerAnimated:NO];
            if (navigationVC.visibleViewController != _guiModule.homeViewController)
            {
                [navigationVC pushViewController:_guiModule.homeViewController animated:NO];
            }
            [mainVC showViewController:navigationVC animated:YES completion:nil];
            
            break;
        }
//        case LEFTBAR_CELL_DEVICE:
//        {
//            [navigationVC popToRootViewControllerAnimated:NO];
//            [navigationVC pushViewController:_guiModule.deviceViewController animated:NO];
//            [mainVC showViewController:navigationVC animated:YES completion:nil];
//            
//            break;
//        }
        case LEFTBAR_CELL_IMPRESS:
        {
            [navigationVC popToRootViewControllerAnimated:NO];
            [navigationVC pushViewController:_guiModule.impressViewController animated:NO];
            [mainVC showViewController:navigationVC animated:YES completion:nil];
            
            break;
        }
        case LEFTBAR_CELL_INTEREST:
        {
            InterestViewController_iPhone* interestVC = _guiModule.interestViewController;
            [navigationVC popToRootViewControllerAnimated:NO];
            [navigationVC pushViewController:interestVC animated:NO];
            [mainVC showViewController:navigationVC animated:YES completion:nil];
            
            break;
        }
//        case LEFTBAR_CELL_CONFIG:
//        {
//            [navigationVC popToRootViewControllerAnimated:NO];
//            [navigationVC pushViewController:_guiModule.configViewController animated:NO];
//            [mainVC showViewController:navigationVC animated:YES completion:nil];
//            
//            break;
//        }
        default:
        {
            
            break;
        }
    }
    
}

-(UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{    
    LeftBarHeaderView_iPhone* headerView = [CBUIUtils componentFromNib:NIB_LeftBarHeaderView_iPhone owner:self options:nil];
    headerView.backgroundColor = FLATUI_COLOR_UIVIEW_BACKGROUND;
    return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
#warning Should be replaced with API but not hard code.
    NSUInteger requireCount = 0;
    if ([UIDevice isRunningOniOS7AndLater])
    {
        requireCount = 64;
    }
    else
    {
        requireCount = 44;
    }
    
    return requireCount;
}

-(UIView*)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    LeftBarFooterView_iPhone* footerView = [CBUIUtils componentFromNib:NIB_LeftBarFooterView_iPhone owner:self options:nil];
    footerView.backgroundColor = FLATUI_COLOR_UIVIEW_BACKGROUND;
    return footerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return HEIGHT_LeftBarFooterView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return HEIGHT_LeftBarCell;
}

@end
