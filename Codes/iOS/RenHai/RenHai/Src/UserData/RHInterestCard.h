//
//  RHInterestCard.h
//  RenHai
//
//  Created by DENG KE on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CBJSONable.h"

@interface RHInterestCard : NSObject <CBJSONable, NSCopying, NSMutableCopying>

@property (nonatomic) NSUInteger interestCardId;

-(BOOL) addLabel:(NSString*) labelName;
-(BOOL) removeLabel:(NSString*) labelName;
-(BOOL) isLabelExists:(NSString*) labelName;
-(NSArray*) labelList;

@end
