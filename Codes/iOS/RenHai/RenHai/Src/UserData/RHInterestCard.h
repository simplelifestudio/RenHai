//
//  RHInterestCard.h
//  RenHai
//
//  Created by DENG KE on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

#import "RHInterestLabel.h"

@interface RHInterestCard : NSObject <CBJSONable, NSCopying, NSMutableCopying>

+(NSArray*) sortedLabelListByLabelOrder:(NSArray*) labelList;
+(NSArray*) sortedLabelListByMatchCount:(NSArray*) labelList;
+(NSArray*) sortedLabelListByGlobalMatchCount:(NSArray*) labelList;
+(NSArray*) sortedLabelListByCurrentProfileCount:(NSArray*) labelList;

@property (nonatomic) NSUInteger interestCardId;

-(NSArray*) labelList;

-(BOOL) addLabel:(NSString*) labelName;

-(BOOL) removeLabelByName:(NSString*) labelName;
-(BOOL) removeLabelByIndex:(NSUInteger) index;

-(void) removeAllLabels;

-(BOOL) isLabelExists:(NSString*) labelName;

-(BOOL) insertLabelByName:(NSString*) labelName index:(NSInteger) index;

-(RHInterestLabel*) getLabelByName:(NSString*) labelName;
-(RHInterestLabel*) getLabelByIndex:(NSUInteger) index;
-(NSInteger) getLabelIndex:(NSString*) labelName;

-(void) reorderLabel:(NSUInteger) fromIndex toIndex:(NSUInteger) toIndex;

@end
