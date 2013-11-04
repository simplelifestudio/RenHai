//
//  ConfigViewController_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ConfigViewController_iPhone.h"

#import "CBUIUtils.h"

#import "GUIModule.h"
#import "GUIStyle.h"

#import "RHTableViewSwitcherCell_iPhone.h"

#define SECTION_ITEMCOUNT_CONFIG 2

#define SECTION_INDEX_SERVER 0
#define SECTION_ITEMCOUNT_SERVER 2
#define ITEM_INDEX_PUSHNOTIFICATION 0
#define ITEM_INDEX_BROADCAST 1

#define SECTION_INDEX_NETWORK 1
#define SECTION_ITEMCOUNT_NETWORK 1
#define ITEM_INDEX_CHATON3G 0

@interface ConfigViewController_iPhone ()
{
    GUIModule* _guiModule;
    
    RHTableViewSwitcherCell_iPhone* _pushNotificationCell;
    RHTableViewSwitcherCell_iPhone* _broadcastCell;
    RHTableViewSwitcherCell_iPhone* _chatOn3GCell;
}

@end

@implementation ConfigViewController_iPhone

#pragma mark - Public Methods

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    _guiModule = [GUIModule sharedInstance];
    
    [self _setupNavigationBar];
    
    [self _setupTableCells];
}

#pragma mark - Private Methods

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    [self.navigationController.navigationBar setTintColor:FLATUI_COLOR_NAVIGATIONBAR];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_CONFIG;
}

-(void)_setupSideBarMenuButtons
{
    UIImage* sidebarMenuIcon_portrait = [GUIStyle sidebarMenuIconPortrait];
    UIImage* sidebarMenuIcon_landscape = [GUIStyle sidebarMenuIconLandscape];
    
    if ([self.navigationController.revealController hasLeftViewController])
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

-(void)_setupTableCells
{
    if (nil == _pushNotificationCell)
    {
        _pushNotificationCell = [RHTableViewSwitcherCell_iPhone configureFlatCellWithColor:FLATUI_COLOR_TABLECELL selectedColor:FLATUI_COLOR_TABLECELL_SELECTED style:UITableViewCellStyleValue1 reuseIdentifier:TABLECELL_ID_SWITCHER];
        
        [_pushNotificationCell setSelectionStyle:UITableViewCellSelectionStyleNone];
        _pushNotificationCell.switcherLabel.text = NSLocalizedString(@"Config_PushNotification", nil);
        [_pushNotificationCell.switcher addTarget:self action:@selector(_onPushNotificationSwitched) forControlEvents:UIControlEventValueChanged];
        
        [self _refershPushNotificationCell];
    }
    
    if (nil == _broadcastCell)
    {
        _broadcastCell = [RHTableViewSwitcherCell_iPhone configureFlatCellWithColor:FLATUI_COLOR_TABLECELL selectedColor:FLATUI_COLOR_TABLECELL_SELECTED style:UITableViewCellStyleValue1 reuseIdentifier:TABLECELL_ID_SWITCHER];
        
        [_broadcastCell setSelectionStyle:UITableViewCellSelectionStyleNone];
        _broadcastCell.switcherLabel.text = NSLocalizedString(@"Config_Broadcast", nil);
        [_broadcastCell.switcher addTarget:self action:@selector(_onBroadcastSwitched) forControlEvents:UIControlEventValueChanged];
        
        [self _refreshBroadcastCell];
    }
    
    if (nil == _chatOn3GCell)
    {
        _chatOn3GCell = [RHTableViewSwitcherCell_iPhone configureFlatCellWithColor:FLATUI_COLOR_TABLECELL selectedColor:FLATUI_COLOR_TABLECELL_SELECTED style:UITableViewCellStyleValue1 reuseIdentifier:TABLECELL_ID_SWITCHER];
        
        [_chatOn3GCell setSelectionStyle:UITableViewCellSelectionStyleNone];
        _chatOn3GCell.switcherLabel.text = NSLocalizedString(@"Config_ChatOn3G", nil);
        [_chatOn3GCell.switcher addTarget:self action:@selector(_onChatOn3GSwitched) forControlEvents:UIControlEventValueChanged];
        
        [self _refreshChatOn3GCell];
    }
}

-(void)_refershPushNotificationCell
{
    
}

-(void)_refreshBroadcastCell
{
    
}

-(void)_refreshChatOn3GCell
{
    
}

-(void)_onPushNotificationSwitched
{
    
}

-(void)_onBroadcastSwitched
{
    
}

-(void)_onChatOn3GSwitched
{
    
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
        case SECTION_INDEX_SERVER:
        {
            return SECTION_ITEMCOUNT_SERVER;
        }
        case SECTION_INDEX_NETWORK:
        {
            return SECTION_ITEMCOUNT_NETWORK;
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
    UITableViewCell* cell = nil;
    
    NSInteger section = indexPath.section;
    NSInteger rowInSection = indexPath.row;
    switch (section)
    {
        case SECTION_INDEX_SERVER:
        {
            switch (rowInSection)
            {
                case ITEM_INDEX_PUSHNOTIFICATION:
                {
                    cell = _pushNotificationCell;
                    break;
                }
                case ITEM_INDEX_BROADCAST:
                {
                    cell = _broadcastCell;
                    break;
                }
            }
            
            break;
        }
        case SECTION_INDEX_NETWORK:
        {
            switch (rowInSection)
            {
                case ITEM_INDEX_CHATON3G:
                {
                    cell = _chatOn3GCell;
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
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    return cell;
}

@end
