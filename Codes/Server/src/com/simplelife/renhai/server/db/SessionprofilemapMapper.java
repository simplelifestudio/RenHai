package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Sessionprofilemap;

public interface SessionprofilemapMapper {
    int deleteByPrimaryKey(Integer sessionImpressMapId);
    int insert(Sessionprofilemap record);
    Sessionprofilemap selectByPrimaryKey(Integer sessionImpressMapId);
    int updateByPrimaryKey(Sessionprofilemap record);
}