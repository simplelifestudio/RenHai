//
//  RHInterestCard.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

@interface RHInterestCard : NSObject <CBJSONable>

@property (nonatomic) NSUInteger cardId;

-(BOOL) addLabel:(NSString*) labelName;
-(BOOL) removeLabel:(NSString*) labelName;
-(BOOL) isLabelExists:(NSString*) labelName;
-(NSArray*) getLabelList;

@end
