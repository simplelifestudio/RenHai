//
//  DeviceViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "DeviceViewController_iPhone.h"

#import "FlatUIKit.h"

#import "UIDevice+CBDeviceExtends.h"
#import "CBDateUtils.h"

#import "GUIModule.h"
#import "GUIStyle.h"
#import "RHTableViewLabelCell_iPhone.h"

#import "UserDataModule.h"

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

@interface DeviceViewController_iPhone ()
{
    GUIModule* _guiModule;
    UserDataModule* _userDataModule;
    RHDevice* _device;
    RHDeviceCard* _deviceCard;
    RHProfile* _profile;
}

@end

@implementation DeviceViewController_iPhone

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    [self _setupInstance];
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
//    RHTableViewLabelCell_iPhone* cell = (RHTableViewLabelCell_iPhone*)[tableView dequeueReusableCellWithIdentifier:TABLECELL_ID_DEVICEITEM forIndexPath:indexPath];
    
    RHTableViewLabelCell_iPhone* cell = [RHTableViewLabelCell_iPhone configureFlatCellWithColor:FLATUI_COLOR_TABLECELL selectedColor:FLATUI_COLOR_TABLECELL_SELECTED style:UITableViewCellStyleValue1 reuseIdentifier:TABLECELL_ID_LABELER];
    
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
                    itemName = NSLocalizedString(@"Device_DeviceModel", nil);
                    itemVal = _deviceCard.deviceModel;
                    break;
                }
                case ITEM_INDEX_OSVER:
                {
                    itemName = NSLocalizedString(@"Device_OSVersion", nil);
                    itemVal = _deviceCard.osVersion;
                    break;
                }
                case ITEM_INDEX_ISJAILED:
                {
                    itemName = NSLocalizedString(@"Device_IsJailed", nil);
                    itemVal = (_deviceCard.isJailed) ? NSLocalizedString(@"Common_Yes", nil) : NSLocalizedString(@"Common_No", nil);
                    break;
                }
                case ITEM_INDEX_DEVICESN:
                {
                    itemName = NSLocalizedString(@"Device_DeviceSN", nil);
                    itemVal = _device.shortDeviceSn;
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
                    itemName = NSLocalizedString(@"Device_AppVersion", nil);
                    itemVal = _deviceCard.appVersion;
                    break;
                }
                case ITEM_INDEX_REGTIME:
                {
                    itemName = NSLocalizedString(@"Device_RegisterTime", nil);
                    itemVal = [CBDateUtils dateStringInLocalTimeZoneWithFormat:STANDARD_DATE_TIME_FORMAT andDate:_deviceCard.registerTime];
                    break;
                }
                case ITEM_INDEX_SERVICESTATUS:
                {
                    itemName = NSLocalizedString(@"Device_ServiceStatus", nil);
                    itemVal = [RHProfile serviceStatusString:_profile.serviceStatus];
                    break;
                }
                case ITEM_INDEX_FORBIDDENEXPIREDDATE:
                {
                    itemName = NSLocalizedString(@"Device_UnbanDate", nil);
                    itemVal = [CBDateUtils dateStringInLocalTimeZoneWithFormat:STANDARD_DATE_TIME_FORMAT andDate:_profile.unbanDate];
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
    
    cell.majorLabel.text = itemName;
    cell.minorLabel.text = itemVal;
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;

    return cell;
}

#pragma mark - Private Methods

-(void)_setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    _userDataModule = [UserDataModule sharedInstance];
    _device = _userDataModule.device;
    _deviceCard = _device.deviceCard;
    _profile = _device.profile;
    
    [self _setupNavigationBar];

    [self _setupTableView];
}

-(void)_setupTableView
{
    UINib* nib = [UINib nibWithNibName:NIB_TABLECELL_LABELER bundle:nil];
    [self.tableView registerNib:nib forCellReuseIdentifier:TABLECELL_ID_DEVICEITEM];
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
