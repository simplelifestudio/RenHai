//
//  ImpressViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ImpressViewController_iPhone : UIViewController <UICollectionViewDataSource, UICollectionViewDelegate, UIScrollViewDelegate>

@property (weak, nonatomic) IBOutlet UICollectionView *assessLabelsView;
@property (weak, nonatomic) IBOutlet UICollectionView *impressLabelsView;
@property (weak, nonatomic) IBOutlet UIPageControl *pageControl;

@end
