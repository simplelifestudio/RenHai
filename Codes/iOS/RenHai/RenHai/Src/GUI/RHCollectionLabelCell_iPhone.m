//
//  RHCollectionLabelCell_iPhone.m
//  RenHai
//
//  Created by DENG KE on 13-9-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "RHCollectionLabelCell_iPhone.h"

#import "GUIStyle.h"

static UIImage* delIconImage;
static UIImage* orderIconImage;

@interface RHCollectionLabelCell_iPhone()
{

}

@property (weak, nonatomic) IBOutlet FUIButton *delIconButton;

- (IBAction)didPressDelIconButton:(id)sender;

@end

@implementation RHCollectionLabelCell_iPhone

@synthesize textField = _textField;
@synthesize countLabel = _countLabel;

@synthesize editingDelegate = _editingDelegate;

+ (void)initialize
{
    delIconImage = [UIImage imageNamed:@"close.png"];
    orderIconImage = nil;
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self)
    {
        [self _formatFlatUI];
    }
    return self;
}

- (void)awakeFromNib
{
    [self _formatFlatUI];
    
    self.cellMode = CellMode_Normal;
    
    self.layer.borderWidth = 1.5f;
    self.layer.borderColor = MAJOR_COLOR_MID.CGColor;
    self.layer.cornerRadius = 4.0f;
    
    [super awakeFromNib];
}

-(void) setSelected:(BOOL)selected
{
    [super setSelected:selected];
    [self setNeedsDisplay];
}

-(void) drawRect:(CGRect)rect
{
    [super drawRect:rect];
    
    if (self.selected)
    {
        self.layer.borderColor = SPECIAL_COLOR_WARNING.CGColor;
    }
    else
    {
        self.layer.borderColor = MAJOR_COLOR_MID.CGColor;
    }
}

-(void) setCellMode:(CellMode)cellMode
{
    _cellMode = cellMode;
    
    switch (_cellMode)
    {
        case CellMode_Normal:
        {
            _delIconButton.hidden = YES;
            self.shakeCell = NO;
            
            break;
        }
        case CellMode_Delete:
        {
            _delIconButton.hidden = NO;
            self.shakeCell = YES;
            
            break;
        }
        case CellMode_Order:
        {
            _delIconButton.hidden = YES;
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
    
//    if (_shakeCell)
//    {
//        [self _startQuivering];
//    }
//    else
//    {
//        [self _stopQuivering];
//    }
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
    
    if (nil != _editingDelegate)
    {
        [_editingDelegate onTextFieldDoneEditing:self labelName:labelName];
    }
}

- (IBAction)didPressDelIconButton:(id)sender
{
    if (nil != _editingDelegate && [_editingDelegate respondsToSelector:@selector(didDelete)])
    {
        [_editingDelegate didDelete];
    }
}

@end
