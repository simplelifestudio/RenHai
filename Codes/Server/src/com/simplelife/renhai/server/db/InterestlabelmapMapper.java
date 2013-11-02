package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Interestlabelmap;

public interface InterestlabelmapMapper {
    int deleteByPrimaryKey(Integer interestLabelMapId);
    int insert(Interestlabelmap record);
    Interestlabelmap selectByPrimaryKey(Integer interestLabelMapId);
    int updateByPrimaryKey(Interestlabelmap record);
}