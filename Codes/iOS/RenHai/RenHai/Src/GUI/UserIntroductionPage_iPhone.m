//
//  UserIntroductionPage_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-12-22.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "UserIntroductionPage_iPhone.h"

#import "CBStringUtils.h"

#import "GUIStyle.h"

@implementation UserIntroductionPage_iPhone

#pragma mark - Public Methods

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self _setupInstance];
}

-(void) installTitle:(NSString*) title
{
    _titleLabel.text = title;
}

-(void) installImage:(NSString*) imageFileName
{
    UIImage* image = [UIImage imageNamed:imageFileName];
    _imageView.image = image;
}

-(void) installText:(NSString*) textFileName
{
    NSString* text = [CBStringUtils textFromTextFileNamed:textFileName];
    _textView.text = text;

    FTCoreTextStyle* style = [FTCoreTextStyle styleWithName:FTCoreTextTagDefault];
    style.font = [UIFont systemFontOfSize:FLATUI_FONT_NORMAL];
    
    FTCoreTextStyle *liStyle = [FTCoreTextStyle styleWithName:FTCoreTextTagBullet];
    liStyle.name = @"li";
    liStyle.paragraphInset = UIEdgeInsetsMake(0, 14.0f, 0, 0);
    
    [_textView changeDefaultTag:FTCoreTextTagBullet toTag:@"li"];
    
    [_textView addStyles:@[style, liStyle]];
}

#pragma mark - Private Methods

-(void) _setupInstance
{

}

@end
