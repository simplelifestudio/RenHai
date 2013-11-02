package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Impresscard;

public interface ImpresscardMapper {
    int insert(Impresscard record);
    Impresscard selectByPrimaryKey(Integer impressCardId);
    int updateByPrimaryKey(Impresscard record);
}