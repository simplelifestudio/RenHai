package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Webrtcaccount;

public interface WebrtcaccountMapper {
    int countAll();
    Webrtcaccount selectByPrimaryKey(Integer webRTCAccountId);
}