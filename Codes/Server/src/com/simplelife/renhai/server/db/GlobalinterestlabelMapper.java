package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Globalinterestlabel;

public interface GlobalinterestlabelMapper {
    int insert(Globalinterestlabel record);
    Globalinterestlabel selectByPrimaryKey(Integer globalInterestLabelId);
    Globalinterestlabel selectByLabelName(String labelName);
    int updateByPrimaryKey(Globalinterestlabel record);
}