//
//  LeftBarViewController_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "LeftBarViewController_iPhone.h"

#import "LeftBarHeaderView_iPhone.h"
#import "LeftBarFooterView_iPhone.h"
#import "CBUIUtils.h"

@interface LeftBarViewController_iPhone ()
{
    NSInteger _selectedRow;    
}

@end

@implementation LeftBarViewController_iPhone

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self)
    {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    
}

- (void)viewWillAppear:(BOOL)animated
{
//    AppDelegate* app = [UIApplication sharedApplication].delegate;
//    PKRevealController* revealVC = app.revealController;
//    UINavigationController* frontVC = (UINavigationController*)revealVC.frontViewController;
//    UIViewController* presentingVC = frontVC.visibleViewController;
//    if (presentingVC == app.homeViewController)
//    {
//        _selectedRow = 0;
//    }
//    else if (presentingVC == app.deviceCardViewController)
//    {
//        _selectedRow = 1;
//    }
//    else if (presentingVC == app.impressCardViewController)
//    {
//        _selectedRow = 2;
//    }
//    else if (presentingVC == app.interestCardViewController)
//    {
//        _selectedRow = 3;
//    }
//    else if (presentingVC == app.configViewController)
//    {
//        _selectedRow = 4;
//    }
//    else if (presentingVC == app.helpViewController)
//    {
//        _selectedRow = 5;
//    }
    
    [self.tableView reloadData];
    
    [super viewWillAppear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return 6;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    float version = [[UIDevice currentDevice] systemVersion].floatValue;
    BOOL iOS6Flag = (6.0 <= version) ? YES : NO;
    
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = nil;
    if (iOS6Flag)
    {
        cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    }
    else
    {
        cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    }
    
    // Configure the cell...
    NSString* cellText = nil;
    NSInteger row = indexPath.row;
    switch (row)
    {
        case 0:
        {
            cellText = @"主屏";
            break;
        }
        case 1:
        {
            cellText = @"设备";
            break;
        }
        case 2:
        {
            cellText = @"印象";
            break;
        }
        case 3:
        {
            cellText = @"兴趣";
            break;
        }
        case 4:
        {
            cellText = @"配置";
            break;
        }
        case 5:
        {
            cellText = @"帮助";
            break;
        }
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
        //        cell.selected = YES;
        
        
    }
    else
    {
        //        cell.selected = NO;
    }
    
    cell.textLabel.text = cellText;
    cell.textLabel.textColor = textColor;
    
    cell.backgroundColor = backColor;
    
    return cell;
}

/*
 // Override to support conditional editing of the table view.
 - (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
 {
 // Return NO if you do not want the specified item to be editable.
 return YES;
 }
 */

/*
 // Override to support editing the table view.
 - (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
 {
 if (editingStyle == UITableViewCellEditingStyleDelete) {
 // Delete the row from the data source
 [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
 }
 else if (editingStyle == UITableViewCellEditingStyleInsert) {
 // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
 }
 }
 */

/*
 // Override to support rearranging the table view.
 - (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
 {
 }
 */

/*
 // Override to support conditional rearranging of the table view.
 - (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
 {
 // Return NO if you do not want the item to be re-orderable.
 return YES;
 }
 */

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     */
    
//    AppDelegate* app = [UIApplication sharedApplication].delegate;
//    UINavigationController* navigationVC = app.navigationController;
//    PKRevealController* revealVC = app.revealController;
//    NSInteger row = indexPath.row;
//    switch (row)
//    {
//        case 0:
//        {
//            [navigationVC popToRootViewControllerAnimated:NO];
//            [navigationVC pushViewController:app.homeViewController animated:NO];
//            [revealVC showViewController:navigationVC animated:YES completion:nil];
//            
//            break;
//        }
//        case 1:
//        {
//            [navigationVC popToRootViewControllerAnimated:NO];
//            [navigationVC pushViewController:app.deviceCardViewController animated:NO];
//            [revealVC showViewController:navigationVC animated:YES completion:nil];
//            
//            break;
//        }
//        case 2:
//        {
//            [navigationVC popToRootViewControllerAnimated:NO];
//            [navigationVC pushViewController:app.impressCardViewController animated:NO];
//            [revealVC showViewController:navigationVC animated:YES completion:nil];
//            
//            break;
//        }
//        case 3:
//        {
//            [navigationVC popToRootViewControllerAnimated:NO];
//            [navigationVC pushViewController:app.interestCardViewController animated:NO];
//            [revealVC showViewController:navigationVC animated:YES completion:nil];
//            
//            break;
//        }
//        case 4:
//        {
//            [navigationVC popToRootViewControllerAnimated:NO];
//            [navigationVC pushViewController:app.configViewController animated:NO];
//            [revealVC showViewController:navigationVC animated:YES completion:nil];
//            
//            break;
//        }
//        case 5:
//        {
//            [navigationVC popToRootViewControllerAnimated:NO];
//            [navigationVC pushViewController:app.helpViewController animated:NO];
//            [revealVC showViewController:navigationVC animated:YES completion:nil];
//            
//            break;
//        }
//        default:
//        {
//            
//            break;
//        }
//    }
    
}

-(UIView*)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{    
    LeftBarHeaderView_iPhone* headerView = [CBUIUtils componentFromNib:@"LeftBarHeaderView_iPhone" owner:self options:nil];
    
    return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 44;
}

-(UIView*)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section
{
    LeftBarFooterView_iPhone* footerView = [CBUIUtils componentFromNib:@"LeftBarFooterView_iPhone" owner:self options:nil];
    
    return footerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 44;
}

@end
