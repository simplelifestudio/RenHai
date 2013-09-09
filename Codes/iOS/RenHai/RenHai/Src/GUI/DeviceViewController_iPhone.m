//
//  DeviceViewController_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "DeviceViewController_iPhone.h"

#import "FlatUIKit.h"

#import "GUIModule.h"
#import "GUIStyle.h"
#import "UIDevice+CBDeviceExtends.h"

#import "TableViewLabelCell.h"

#define SECTION_ITEMCOUNT_CONFIG 2

#define SECTION_INDEX_ENV 0
#define SECTION_ITEMCOUNT_ENV 4
#define ITEM_INDEX_DEVICEMODEL 0
#define ITEM_INDEX_OSVER 1
#define ITEM_INDEX_ISJAILED 2
#define ITEM_INDEX_DEVICESN 3

#define SECTION_INDEX_APP 1
#define SECTION_ITEMCOUNT_APP 4
#define ITEM_INDEX_APPVER 0
#define ITEM_INDEX_REGTIME 1
#define ITEM_INDEX_SERVICESTATUS 2
#define ITEM_INDEX_FORBIDDENEXPIREDDATE 3

#define CELL_ID_DEVICEITEM @"TableViewLabelCell"

#define NIB_TABLEVIEWLABELCELL @"TableViewLabelCell"

@interface DeviceViewController_iPhone ()
{
    GUIModule* _guiModule;
}

@end

@implementation DeviceViewController_iPhone

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    _guiModule = [GUIModule sharedInstance];
    
    [self _setupNavigationBar];
    
    [self _setupTableView];
}

#pragma mark - UITableViewDataDelegate

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return SECTION_ITEMCOUNT_CONFIG;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    switch (section)
    {
        case SECTION_INDEX_ENV:
        {
            return SECTION_ITEMCOUNT_ENV;
        }
        case SECTION_INDEX_APP:
        {
            return SECTION_ITEMCOUNT_APP;
        }
        default:
        {
            break;
        }
    }
    return 0;
}

- (UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    TableViewLabelCell* cell = (TableViewLabelCell*)[tableView dequeueReusableCellWithIdentifier:CELL_ID_DEVICEITEM forIndexPath:indexPath];
    
    NSString* itemName = @"";
    NSString* itemVal = @"";
    
    NSInteger section = indexPath.section;
    NSInteger rowInSection = indexPath.row;
    switch (section)
    {
        case SECTION_INDEX_ENV:
        {
            switch (rowInSection)
            {
                case ITEM_INDEX_DEVICEMODEL:
                {
                    itemName = NSLocalizedString(@"DeviceCard_DeviceModel", nil);
                    itemVal = [UIDevice deviceSimpleModel];
                    break;
                }
                case ITEM_INDEX_OSVER:
                {
                    itemName = NSLocalizedString(@"DeviceCard_OSVersion", nil);
                    itemVal = [UIDevice osVersion];
                    break;
                }
                case ITEM_INDEX_ISJAILED:
                {
                    itemName = NSLocalizedString(@"DeviceCard_IsJailed", nil);
                    itemVal = ([UIDevice isJailed]) ? NSLocalizedString(@"Common_Yes", nil) : NSLocalizedString(@"Common_No", nil);
                    break;
                }
                case ITEM_INDEX_DEVICESN:
                {
                    itemName = NSLocalizedString(@"DeviceCard_DeviceSN", nil);
                    break;
                }
                default:
                {
                    break;
                }
            }
            
            break;
        }
        case SECTION_INDEX_APP:
        {
            switch (rowInSection)
            {
                case ITEM_INDEX_APPVER:
                {
                    itemName = NSLocalizedString(@"DeviceCard_AppVersion", nil);                    
                    break;
                }
                case ITEM_INDEX_REGTIME:
                {
                    itemName = NSLocalizedString(@"DeviceCard_RegisterTime", nil);                    
                    break;
                }
                case ITEM_INDEX_SERVICESTATUS:
                {
                    itemName = NSLocalizedString(@"DeviceCard_ServiceStatus", nil);
                    break;
                }
                case ITEM_INDEX_FORBIDDENEXPIREDDATE:
                {
                    itemName = NSLocalizedString(@"DeviceCard_ForbiddenExpiredDate", nil);                    
                    break;
                }
                default:
                {
                    break;
                }
            }
            
            break;
        }
        default:
        {
            break;
        }
    }
    
    cell = (TableViewLabelCell*)cell;
    
    cell.majorLabel.text = itemName;
    cell.minorLabel.text = itemVal;
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;

    return cell;
}

#pragma mark - Private Methods

-(void)_setupTableView
{
    UINib* nib = [UINib nibWithNibName:NIB_TABLEVIEWLABELCELL bundle:nil];
    [self.tableView registerNib:nib forCellReuseIdentifier:CELL_ID_DEVICEITEM];
}

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    [self.navigationController.navigationBar setTintColor:FLATUI_COLOR_NAVIGATIONBAR];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_DEVICE;
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

@end
