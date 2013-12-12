//
//  RHCollectionLabelCell_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHCollectionLabelCell_iPhone.h"

#import "GUIStyle.h"
#import "GUIModule.h"

#import "RHLabelManageViewController_iPhone.h"

#define BORDER_WIDTH 2.0f
#define CORNER_RADIUS 3.0f

static UIImage* delIconImage;
static UIImage* sortIconImage;

@interface RHCollectionLabelCell_iPhone()
{
    GUIModule* _guiModule;
}

@property (weak, nonatomic) IBOutlet FUIButton *delIconButton;
@property (weak, nonatomic) IBOutlet FUIButton *sortIconButton;

- (IBAction)didPressDelIconButton:(id)sender;
- (IBAction)didPressSortIconButton:(id)sender;

@end

@implementation RHCollectionLabelCell_iPhone

@synthesize textField = _textField;
@synthesize countLabel = _countLabel;

@synthesize shakeCell = _shakeCell;

+ (void)initialize
{
    delIconImage = [UIImage imageNamed:@"close.png"];
    sortIconImage = [UIImage imageNamed:@"order.png"];
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {

    }
    return self;
}

- (void)awakeFromNib
{
    [self _setupInstance];
    
    [super awakeFromNib];
}

-(void) setSelected:(BOOL)selected
{
  NSString* str = selected ? @"YES" : @"NO";
  DDLogVerbose(@"Label(%@)'s selected status:%@", self.labelName, str);
    
    [super setSelected:selected];
    
    if (self.selected)
    {
        self.backgroundColor = _customSelectedBackgroundColor;
    }
    else
    {
        self.backgroundColor = _customBackgroundColor;
    }
    
    [self setNeedsDisplay];
}

-(void) drawRect:(CGRect)rect
{
    [super drawRect:rect];
}

-(void) setCellMode:(CellMode)cellMode
{
    _cellMode = cellMode;
    
    switch (_cellMode)
    {
        case CellMode_Normal:
        {
            _delIconButton.hidden = YES;
            _sortIconButton.hidden = YES;
            self.shakeCell = NO;
            
            break;
        }
        case CellMode_Delete:
        {
            _delIconButton.hidden = NO;
            _sortIconButton.hidden = YES;
            self.shakeCell = YES;
            
            break;
        }
        case CellMode_Sort:
        {
            _delIconButton.hidden = YES;
            _sortIconButton.hidden = NO;
            self.shakeCell = YES;
            
            break;
        }
        default:
        {
            break;
        }
    }
}

-(void) setShakeCell:(BOOL)shakeCell
{
    _shakeCell = shakeCell;
    
    if (_shakeCell)
    {
        [self _startQuivering];
    }
    else
    {
        [self _stopQuivering];
    }
}

- (NSString*) labelName
{
    return _textField.text;
}

- (void)setHighlighted:(BOOL)highlighted
{
    [super setHighlighted:highlighted];
    if (highlighted)
    {
        [self _startQuivering];
    }
    else
    {
        [self _stopQuivering];
    }
}

#pragma mark - Private Methods

-(void) _formatFlatUI
{

}

-(void) _startQuivering
{
    CABasicAnimation *quiverAnim = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
    float startAngle = (-2) * M_PI/180.0;
    float stopAngle = -startAngle;
    quiverAnim.fromValue = [NSNumber numberWithFloat:startAngle];
    quiverAnim.toValue = [NSNumber numberWithFloat:3 * stopAngle];
    quiverAnim.autoreverses = YES;
    quiverAnim.duration = 0.2;
    quiverAnim.repeatCount = HUGE_VALF;
    float timeOffset = (float)(arc4random() % 100)/100 - 0.50;
    quiverAnim.timeOffset = timeOffset;
    CALayer *layer = self.layer;
    [layer addAnimation:quiverAnim forKey:@"quivering"];
}

-(void) _stopQuivering
{
    CALayer *layer = self.layer;
    [layer removeAnimationForKey:@"quivering"];
}

-(void) _setupInstance
{
    _guiModule = [GUIModule sharedInstance];
    
    [self _formatFlatUI];
    
    self.cellMode = CellMode_Normal;
    
//    self.layer.borderWidth = BORDER_WIDTH;
//    self.layer.borderColor = FLATUI_COLOR_COLLECTIONCELL.CGColor;
//    self.layer.cornerRadius = CORNER_RADIUS;
    
    self.backgroundColor = _customBackgroundColor;
}

#pragma mark - IBActions

- (IBAction)textFieldDoneEditing:(id)sender
{
    [sender resignFirstResponder];
    
    NSString* labelName = _textField.text;
    if (nil == labelName || 0 == labelName.length)
    {
        labelName = NSLocalizedString(@"Interest_Empty", nil);
        _textField.text = labelName;
    }
}

- (IBAction)didPressDelIconButton:(id)sender
{

}

- (IBAction)didPressSortIconButton:(id)sender
{
    
}

@end
