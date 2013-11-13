package com.simplelife.renhai.server.db;

import java.util.List;

import com.simplelife.renhai.server.db.Webrtcsession;

public interface WebrtcsessionMapper {
    int deleteByPrimaryKey(Integer webRtcSessionId);
    int insert(Webrtcsession record);
    Webrtcsession selectByPrimaryKey(Integer webRtcSessionId);
    int updateByPrimaryKey(Webrtcsession record);
    List<Webrtcsession> selectAll();
}