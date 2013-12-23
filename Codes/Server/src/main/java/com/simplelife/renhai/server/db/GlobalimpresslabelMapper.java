package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Globalimpresslabel;
import com.simplelife.renhai.server.util.ICachableMapper;

public interface GlobalimpresslabelMapper extends ICachableMapper{
    int insert(Globalimpresslabel record);
    Globalimpresslabel selectByPrimaryKey(Integer globalImpressLabelId);
    //Globalimpresslabel selectByLabelName(String impressLabelName);
    int updateByPrimaryKey(Globalimpresslabel record);
}