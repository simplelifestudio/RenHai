package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Systemstatistics;

public interface SystemstatisticsMapper {
    int insert(Systemstatistics record);
    Systemstatistics selectByPrimaryKey(Integer systemStatisticsId);
}