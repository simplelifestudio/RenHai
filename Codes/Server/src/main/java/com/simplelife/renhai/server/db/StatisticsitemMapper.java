package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Statisticsitem;

public interface StatisticsitemMapper {
    int insert(Statisticsitem record);
    Statisticsitem selectByPrimaryKey(Integer statisticsitemId);
}