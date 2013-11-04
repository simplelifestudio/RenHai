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

@interface InterestViewController_iPhone : UICollectionViewController <UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDataSource_Draggable>

@end
