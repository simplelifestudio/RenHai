package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Globalimpresslabel;

public interface GlobalimpresslabelMapper {
    int insert(Globalimpresslabel record);
    Globalimpresslabel selectByPrimaryKey(Integer globalImpressLabelId);
    Globalimpresslabel selectByLabelName(String labelName);
    int updateByPrimaryKey(Globalimpresslabel record);
}