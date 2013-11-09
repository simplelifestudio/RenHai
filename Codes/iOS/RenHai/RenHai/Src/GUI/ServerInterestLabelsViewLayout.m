//
//  ServerInterestLabelsViewLayout.m
//  RenHai
//
//  Created by DENG KE on 13-11-9.
//  Copyright (c) 2013年 Simplelife Studio. All rights reserved.
//

#import "ServerInterestLabelsViewLayout.h"

@implementation ServerInterestLabelsViewLayout

- (NSArray*) layoutAttributesForElementsInRect1:(CGRect)rect
{
    NSMutableArray* result = [[super layoutAttributesForElementsInRect:rect] mutableCopy];
    
    NSArray* attrKinds = [result valueForKeyPath:@"representedElementKind"];
    NSUInteger headerIndex = [attrKinds indexOfObject:UICollectionElementKindSectionHeader];
    if (headerIndex != NSNotFound)
    {
        [result removeObjectAtIndex:headerIndex];
    }
    
    CGPoint const contentOffset = self.collectionView.contentOffset;
    CGSize headerSize = self.headerReferenceSize;
    
    UICollectionViewLayoutAttributes *newHeaderAttributes = [UICollectionViewLayoutAttributes layoutAttributesForSupplementaryViewOfKind:UICollectionElementKindSectionHeader withIndexPath:[NSIndexPath indexPathForItem:0 inSection:0]];
    CGRect frame = CGRectMake(0, contentOffset.y, headerSize.width, headerSize.height);
    newHeaderAttributes.frame = frame;
    newHeaderAttributes.zIndex = 1024;
    
    [result addObject:newHeaderAttributes];
    
    return result;
}

- (NSArray*) layoutAttributesForElementsInRect2:(CGRect)rect
{
    NSMutableArray* result = [[super layoutAttributesForElementsInRect:rect] mutableCopy];
    
    UICollectionView* const cv = self.collectionView;
    CGPoint const contentOffset = cv.contentOffset;
    
    NSMutableIndexSet* missingSections = [NSMutableIndexSet indexSet];
    for (UICollectionViewLayoutAttributes* layoutAttributes in result)
    {
        if (layoutAttributes.representedElementCategory == UICollectionElementCategoryCell)
        {
            [missingSections removeIndex:layoutAttributes.indexPath.section];
        }
    }
    
    for (UICollectionViewLayoutAttributes* layoutAttributes in result)
    {
        if ([layoutAttributes.representedElementKind isEqualToString:UICollectionElementKindSectionFooter])
        {
            [missingSections removeIndex:layoutAttributes.indexPath.section];
        }
    }
    
    [missingSections enumerateIndexesUsingBlock:^(NSUInteger idx, BOOL *stop)
    {
        NSIndexPath* indexPath = [NSIndexPath indexPathForItem:0 inSection:idx];
        
        UICollectionViewLayoutAttributes* layoutAttributes = [self layoutAttributesForSupplementaryViewOfKind:UICollectionElementKindSectionHeader atIndexPath:indexPath];
        
        [result addObject:layoutAttributes];
    }];
    
    for (UICollectionViewLayoutAttributes* layoutAttributes in result)
    {
        if ([layoutAttributes.representedElementKind isEqualToString:UICollectionElementKindSectionHeader])
        {
            NSInteger section = layoutAttributes.indexPath.section;
            NSInteger numberOfItemsInSection = [cv numberOfItemsInSection:section];
            
            NSIndexPath* firstCellIndexPath = [NSIndexPath indexPathForItem:0 inSection:section];
            NSIndexPath* lastCellIndexPath = [NSIndexPath indexPathForItem:MAX(0, (numberOfItemsInSection - 1)) inSection:section];
            
            UICollectionViewLayoutAttributes* firstCellAttrs = [self layoutAttributesForItemAtIndexPath:firstCellIndexPath];
            UICollectionViewLayoutAttributes* lastCellAttrs = [self layoutAttributesForItemAtIndexPath:lastCellIndexPath];
            
            CGFloat headerHeight = CGRectGetHeight(layoutAttributes.frame);
            CGPoint origin = layoutAttributes.frame.origin;
            origin.y = MIN(
                           MAX(
                               contentOffset.y,
                               (CGRectGetMinY(firstCellAttrs.frame) - headerHeight)
                               ),
                           (CGRectGetMaxY(lastCellAttrs.frame) - headerHeight)
                           );
            
            layoutAttributes.zIndex = 1024;
            layoutAttributes.frame = (CGRect){
                .origin = origin,
                .size = layoutAttributes.frame.size
            };
            
        }
        
    }
    
    return result;
}

//- (BOOL) shouldInvalidateLayoutForBoundsChange:(CGRect)newBound
//{
//    return YES;
//}
//
//- (NSArray *) layoutAttributesForElementsInRect:(CGRect)rect
//{
//    NSMutableArray *answer = [[super layoutAttributesForElementsInRect:rect] mutableCopy];
//    
//    NSMutableIndexSet *missingSections = [NSMutableIndexSet indexSet];
//    for (NSUInteger idx=0; idx<[answer count]; idx++)
//    {
//        UICollectionViewLayoutAttributes *layoutAttributes = answer[idx];
//        
//        if (layoutAttributes.representedElementCategory == UICollectionElementCategoryCell)
//        {
//            [missingSections addIndex:layoutAttributes.indexPath.section];  // remember that we need to layout header for this section
//        }
//        if ([layoutAttributes.representedElementKind isEqualToString:UICollectionElementKindSectionHeader])
//        {
//            [answer removeObjectAtIndex:idx];  // remove layout of header done by our super, we will do it right later
//            idx--;
//        }
//    }
//    
//    // layout all headers needed for the rect using self code
//    [missingSections enumerateIndexesUsingBlock:^(NSUInteger idx, BOOL *stop)
//    {
//        NSIndexPath *indexPath = [NSIndexPath indexPathForItem:0 inSection:idx];
//        UICollectionViewLayoutAttributes *layoutAttributes = [self layoutAttributesForSupplementaryViewOfKind:UICollectionElementKindSectionHeader atIndexPath:indexPath];
//        [answer addObject:layoutAttributes];
//    }];
//    
//    return answer;
//}
//
//- (UICollectionViewLayoutAttributes *)layoutAttributesForSupplementaryViewOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
//{
//    UICollectionViewLayoutAttributes *attributes = [super layoutAttributesForSupplementaryViewOfKind:kind atIndexPath:indexPath];
//    if ([kind isEqualToString:UICollectionElementKindSectionHeader])
//    {
//        UICollectionView * const cv = self.collectionView;
//        CGPoint const contentOffset = cv.contentOffset;
//        CGPoint nextHeaderOrigin = CGPointMake(INFINITY, INFINITY);
//        
//        if (indexPath.section+1 < [cv numberOfSections])
//        {
//            UICollectionViewLayoutAttributes *nextHeaderAttributes = [super layoutAttributesForSupplementaryViewOfKind:kind atIndexPath:[NSIndexPath indexPathForItem:0 inSection:indexPath.section+1]];
//            nextHeaderOrigin = nextHeaderAttributes.frame.origin;
//        }
//        
//        CGRect frame = attributes.frame;
//        if (self.scrollDirection == UICollectionViewScrollDirectionVertical)
//        {
//            frame.origin.y = MIN(MAX(contentOffset.y, frame.origin.y), nextHeaderOrigin.y - CGRectGetHeight(frame));
//        }
//        else
//        {
//            // UICollectionViewScrollDirectionHorizontal
//            frame.origin.x = MIN(MAX(contentOffset.x, frame.origin.x), nextHeaderOrigin.x - CGRectGetWidth(frame));
//        }
//        attributes.zIndex = 1024;
//        attributes.frame = frame;
//    }
//    return attributes;
//}
//
//- (UICollectionViewLayoutAttributes *)initialLayoutAttributesForAppearingSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
//{
//    UICollectionViewLayoutAttributes *attributes = [self layoutAttributesForSupplementaryViewOfKind:kind atIndexPath:indexPath];
//    return attributes;
//}
//
//- (UICollectionViewLayoutAttributes *)finalLayoutAttributesForDisappearingSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath
//{
//    UICollectionViewLayoutAttributes *attributes = [self layoutAttributesForSupplementaryViewOfKind:kind atIndexPath:indexPath];
//    return attributes;
//}

- (NSArray *)layoutAttributesForElementsInRect3:(CGRect)rect {
    
    NSMutableArray *answer = [[super layoutAttributesForElementsInRect:rect] mutableCopy];
    UICollectionView * const cv = self.collectionView;
    CGPoint const contentOffset = cv.contentOffset;
    
    NSMutableIndexSet *missingSections = [NSMutableIndexSet indexSet];
    for (UICollectionViewLayoutAttributes *layoutAttributes in answer) {
        if (layoutAttributes.representedElementCategory == UICollectionElementCategoryCell) {
            [missingSections addIndex:layoutAttributes.indexPath.section];
        }
    }
    for (UICollectionViewLayoutAttributes *layoutAttributes in answer) {
        if ([layoutAttributes.representedElementKind isEqualToString:UICollectionElementKindSectionHeader]) {
            [missingSections removeIndex:layoutAttributes.indexPath.section];
        }
    }
    
    [missingSections enumerateIndexesUsingBlock:^(NSUInteger idx, BOOL *stop) {
        
        NSIndexPath *indexPath = [NSIndexPath indexPathForItem:0 inSection:idx];
        
        UICollectionViewLayoutAttributes *layoutAttributes = [self layoutAttributesForSupplementaryViewOfKind:UICollectionElementKindSectionHeader atIndexPath:indexPath];
        
        [answer addObject:layoutAttributes];
        
    }];
    
    for (UICollectionViewLayoutAttributes *layoutAttributes in answer) {
        
        if ([layoutAttributes.representedElementKind isEqualToString:UICollectionElementKindSectionHeader]) {
            
            NSInteger section = layoutAttributes.indexPath.section;
            NSInteger numberOfItemsInSection = [cv numberOfItemsInSection:section];
            
            NSIndexPath *firstObjectIndexPath = [NSIndexPath indexPathForItem:0 inSection:section];
            NSIndexPath *lastObjectIndexPath = [NSIndexPath indexPathForItem:MAX(0, (numberOfItemsInSection - 1)) inSection:section];
            
            BOOL cellsExist;
            UICollectionViewLayoutAttributes *firstObjectAttrs;
            UICollectionViewLayoutAttributes *lastObjectAttrs;
            
            if (numberOfItemsInSection > 0) { // use cell data if items exist
                cellsExist = YES;
                firstObjectAttrs = [self layoutAttributesForItemAtIndexPath:firstObjectIndexPath];
                lastObjectAttrs = [self layoutAttributesForItemAtIndexPath:lastObjectIndexPath];
            } else { // else use the header and footer
                cellsExist = NO;
                firstObjectAttrs = [self layoutAttributesForSupplementaryViewOfKind:UICollectionElementKindSectionHeader
                                                                        atIndexPath:firstObjectIndexPath];
                lastObjectAttrs = [self layoutAttributesForSupplementaryViewOfKind:UICollectionElementKindSectionFooter
                                                                       atIndexPath:lastObjectIndexPath];
                
            }
            
            CGFloat topHeaderHeight = (cellsExist) ? CGRectGetHeight(layoutAttributes.frame) : 0;
            CGFloat bottomHeaderHeight = CGRectGetHeight(layoutAttributes.frame);
            CGRect frameWithEdgeInsets = UIEdgeInsetsInsetRect(layoutAttributes.frame,
                                                               cv.contentInset);
            
            CGPoint origin = frameWithEdgeInsets.origin;
            
            origin.y = MIN(
                           MAX(
                               contentOffset.y + cv.contentInset.top,
                               (CGRectGetMinY(firstObjectAttrs.frame) - topHeaderHeight)
                               ),
                           (CGRectGetMaxY(lastObjectAttrs.frame) - bottomHeaderHeight)
                           );
            
            layoutAttributes.zIndex = 1024;
            layoutAttributes.frame = (CGRect){
                .origin = origin,
                .size = layoutAttributes.frame.size
            };
            
        }
        
    }
    
    return answer;
    
}

@end
