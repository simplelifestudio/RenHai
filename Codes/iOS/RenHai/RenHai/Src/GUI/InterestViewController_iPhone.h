//
//  InterestViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "UICollectionView+Draggable.h"

#import "InterestLabelsHeaderView_iPhone.h"
#import "ServerInterestLabelsHeaderView_iPhone.h"

@interface InterestViewController_iPhone : UIViewController <UICollectionViewDataSource, UICollectionViewDelegate, UIScrollViewDelegate, UICollectionViewDataSource_Draggable>

@property (weak, nonatomic) IBOutlet UICollectionView *interestLabelsView;
@property (weak, nonatomic) IBOutlet UICollectionView *serverInterestLabelsView;
@property (weak, nonatomic) IBOutlet UIPageControl *pageControl;

-(void) setupIBOutlets;

@end
