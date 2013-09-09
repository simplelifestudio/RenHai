//
//  ImpressViewController_iPhone.m
//  RenHai
//
//  Created by Patrick Deng on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ImpressViewController_iPhone.h"

#import "GUIModule.h"
#import "GUIStyle.h"

#import "RHCollectionLabelCell_iPhone.h"

#define SECTION_COUNT 3

#define SECTION_INDEX_ASSESSES 0
#define SECTION_ASSESSES_ITEMCOUNT 3

#define SECTION_INDEX_CHATS 1
#define SECTION_CHATS_ITEMCOUNT 3

#define SECTION_INDEX_LABELS 2

#define CELL_ID_IMPRESSCARDLABEL @"RHCollectionLabelCell_iPhone"

@interface ImpressViewController_iPhone ()
{
    GUIModule* _guiModule;
}

@end

@implementation ImpressViewController_iPhone

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    _guiModule = [GUIModule sharedInstance];
    
    [self _setupNavigationBar];
    
    [self _setupCollectionView];
}

#pragma mark - UICollectionViewDataDelegate

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    switch (section)
    {
        case SECTION_INDEX_ASSESSES:
        {
            return SECTION_ASSESSES_ITEMCOUNT;
        }
        case SECTION_INDEX_CHATS:
        {
            return SECTION_CHATS_ITEMCOUNT;
        }
        case SECTION_INDEX_LABELS:
        {
            return 9;
        }
        default:
        {
            return 0;
        }
    }
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    RHCollectionLabelCell_iPhone* cell = (RHCollectionLabelCell_iPhone*)[collectionView dequeueReusableCellWithReuseIdentifier:CELL_ID_IMPRESSCARDLABEL forIndexPath:indexPath];

    cell.textField.text = @"标签";
    cell.countLabel.text = @"9";
    
    return cell;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return SECTION_COUNT;
}

//- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
//{
//    
//}

#pragma mark - Private Methods

-(void)_setupCollectionView
{
    UINib* nib = [UINib nibWithNibName:CELL_ID_IMPRESSCARDLABEL bundle:nil];
    [self.collectionView registerNib:nib forCellWithReuseIdentifier:CELL_ID_IMPRESSCARDLABEL];
}

-(void)_setupNavigationBar
{
    [self _setupSideBarMenuButtons];
    
    [self.navigationController.navigationBar setTintColor:FLATUI_COLOR_NAVIGATIONBAR];
    
    self.navigationItem.title = NAVIGATIONBAR_TITLE_IMPRESS;
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
