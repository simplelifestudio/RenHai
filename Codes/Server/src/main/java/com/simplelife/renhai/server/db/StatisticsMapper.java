package com.simplelife.renhai.server.db;

import java.util.LinkedList;

import com.simplelife.renhai.server.util.ComparableResult;

public interface StatisticsMapper
{
	LinkedList<ComparableResult> selectDeviceCountByModel();
	LinkedList<ComparableResult> selectDeviceCountByOS();
    LinkedList<ComparableResult> selectTopImpressLabels(Integer labelCount);
    LinkedList<ComparableResult> selectTopInterestLabels(Integer labelCount);
    int selectRegisterDeviceCount();
    long selectTotalChatCount();
    long selectTotalChatDuration();
}
