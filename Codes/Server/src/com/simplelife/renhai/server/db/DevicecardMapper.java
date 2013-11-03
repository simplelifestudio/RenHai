package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Devicecard;
import org.apache.ibatis.annotations.Param;

public interface DevicecardMapper {
    int insert(Devicecard record);
    Devicecard selectByDeviceId(Integer deviceId);
    //int updateByDeviceIdSelective(@Param("record") Devicecard record, @Param("deviceId") Integer deviceId);
    //int updateByPrimaryKey(@Param("record") Devicecard record, @Param("deviceCardId") Integer deviceCardId);
    int updateByPrimaryKey(Devicecard record);
}