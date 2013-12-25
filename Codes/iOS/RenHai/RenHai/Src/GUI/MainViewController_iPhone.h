//
//  MainViewController_iPhone.h
//  RenHai
//
//  Created by DENG KE on 13-9-6.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "PKRevealController.h"

typedef enum
{
    MAINSCENESTATUS_HOME = 0,
    MAINSCENESTATUS_IMPRESS,
    MAINSCENESTATUS_INTEREST,
    MAINSCENESTATUS_COUNT
}
MainSceneStatus;

@interface MainViewController_iPhone : PKRevealController <UINavigationControllerDelegate>

-(void) switchToMainScene;
-(void) switchToMainScene:(MainSceneStatus) mainSceneStatus;

-(void) switchToChatScene;

-(void) enableGesturers;
-(void) disableGesturers;

@end
