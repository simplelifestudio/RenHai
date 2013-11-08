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

#import "LeftBarHeaderView_iPhone.h"
#import "LeftBarFooterView_iPhone.h"

#define NIB_LeftBarHeaderView_iPhone @"LeftBarHeaderView_iPhone"
#define HEIGHT_LeftBarHeaderView 44

#define NIB_LeftBarFooterView_iPhone @"LeftBarFooterView_iPhone"
#define HEIGHT_LeftBarFooterView 44

#define CELL_ID_LEFTBAR @"LeftBarCell"

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
    
    _guiModule = [GUIModule sharedInstance];
}

- (void)viewWillAppear:(BOOL)animated
{
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
    
    [self.tableView reloadData];
    
    [super viewWillAppear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
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
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CELL_ID_LEFTBAR];

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
    
    UIColor* textColor = [UIColor grayColor];
    UIColor* backColor = [UIColor darkGrayColor];
    if (row == _selectedRow)
    {
        textColor = [UIColor whiteColor];
    }
    else
    {

    }
    
    cell.textLabel.text = cellText;
    cell.textLabel.textColor = textColor;
    
    cell.backgroundColor = backColor;
    
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
            [interestVC setupIBOutlets];
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
    
    return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return HEIGHT_LeftBarHeaderView;
}

-(UIView*)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    LeftBarFooterView_iPhone* footerView = [CBUIUtils componentFromNib:NIB_LeftBarFooterView_iPhone owner:self options:nil];
    
    return footerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return HEIGHT_LeftBarFooterView;
}

@end
