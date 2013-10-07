//
//  CBJSONable.h
//  RenHai
//
//  Created by Patrick Deng on 13-9-2.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol CBJSONable <NSObject>

-(void) fromJSONObject:(NSDictionary*) dic;
-(NSDictionary*) toJSONObject;

@optional
-(NSString*) toJSONString;

@end
