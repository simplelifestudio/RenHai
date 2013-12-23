package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Device;
import com.simplelife.renhai.server.util.ICachableMapper;

public interface DeviceMapper extends ICachableMapper{
    int insert(Device record);
    //Device selectByDeviceSn(String deviceSn);
    Device selectByStringKey(String deviceSn);
}