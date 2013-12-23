package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Interestcard;

public interface InterestcardMapper {
    int deleteByPrimaryKey(Integer interestCardId);
    int insert(Interestcard record);
    Interestcard selectByPrimaryKey(Integer interestCardId);
    int updateByPrimaryKey(Interestcard record);
}