//
//  CBNetworkUtils.m
//  RenHai
//
//  Created by DENG KE on 13-9-1.
//  Copyright (c) 2013年 SimpleLife Studio. All rights reserved.
//

#import "CBNetworkUtils.h"

#include <netdb.h>
#include <arpa/inet.h>
#include <ifaddrs.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <net/if.h>
#include <net/if_dl.h>
#include <arpa/inet.h>
#include <ifaddrs.h>

#import "Reachability.h"

@implementation CBNetworkUtils

+ (NSString*)hostNameInWiFi
{
	struct ifaddrs *addrs;
	const struct ifaddrs *cursor;
	int error;
	error = getifaddrs(&addrs);
	NSString *hostname = nil;
	
	if (error)
	{
		DDLogError(@"Failed to obtain IP address with error: %d", error);
	}
	for (cursor = addrs; cursor; cursor = cursor->ifa_next)
	{
        if (cursor->ifa_addr->sa_family == AF_INET && (cursor->ifa_flags & IFF_LOOPBACK) == 0)
		{
            NSString *ifa_name = [NSString stringWithUTF8String:cursor->ifa_name];
			if([@"en0" isEqualToString:ifa_name] ||
               [@"en1" isEqualToString:ifa_name])
			{
				hostname = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)cursor->ifa_addr)->sin_addr)];
				DDLogVerbose(@"hostname:%@",hostname);
				break;
			}
		}
	}
	freeifaddrs(addrs);
	return hostname;
}

+(BOOL) isInternetConnectable:(NSString*) hostName
{
    [CBAppUtils assert:(nil != hostName && 0 < hostName.length) logFormatString:@"Illegal hostName"];
    
    BOOL flag = NO;
    
    Reachability *r = [Reachability reachabilityWithHostname:hostName];
    switch ([r currentReachabilityStatus])
    {
        case NotReachable: // No Connection
            flag = NO;
            break;
        case ReachableViaWWAN: // 3G
            flag = YES;
            break;
        case ReachableViaWiFi: // WiFi
            flag = YES;
            break;
    }
    
    return flag;
}

+ (BOOL) isWiFiEnabled
{
    return ([[Reachability reachabilityForLocalWiFi] currentReachabilityStatus] == ReachableViaWiFi);
}

+ (BOOL) is3GEnabled
{
    return ([[Reachability reachabilityForInternetConnection] currentReachabilityStatus] == ReachableViaWWAN);
}

@end
