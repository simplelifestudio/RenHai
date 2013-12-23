package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Profileoperationlog;

public interface ProfileoperationlogMapper {
    int insert(Profileoperationlog record);
    Profileoperationlog selectByPrimaryKey(Integer profileOperationLogId);
}