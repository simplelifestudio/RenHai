package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Profile;
import org.apache.ibatis.annotations.Param;

public interface ProfileMapper {
    int insert(Profile record);
    Profile updateByDeviceId(Integer deviceId);
    //int updateByPrimaryKey(@Param("record") Profile record, @Param("profileId") Integer profileId);
    int updateByPrimaryKey(Profile record);
}