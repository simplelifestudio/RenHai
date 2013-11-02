package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Systemoperationlog;

public interface SystemoperationlogMapper {
    int insert(Systemoperationlog record);
    Systemoperationlog selectByPrimaryKey(Integer systemOperationLogId);
}