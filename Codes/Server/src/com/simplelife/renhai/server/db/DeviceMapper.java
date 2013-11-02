package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Device;

public interface DeviceMapper {
    int insert(Device record);
    Device selectByDeviceSn(String deviceSn);
    Device selectWholeDeviceByDeviceSn(String deviceSn);
}