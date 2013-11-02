package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Sessionrecord;

public interface SessionrecordMapper {
    int insert(Sessionrecord record);
    Sessionrecord selectByPrimaryKey(Integer sessionRecordId);
}