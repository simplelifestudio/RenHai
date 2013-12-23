package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Globalinterestlabel;
import com.simplelife.renhai.server.util.ICachableMapper;

public interface GlobalinterestlabelMapper extends ICachableMapper{
    int insert(Globalinterestlabel record);
    Globalinterestlabel selectByPrimaryKey(Integer globalInterestLabelId);
    //Globalinterestlabel selectByLabelName(String interestLabelName);
    int updateByPrimaryKey(Globalinterestlabel record);
}