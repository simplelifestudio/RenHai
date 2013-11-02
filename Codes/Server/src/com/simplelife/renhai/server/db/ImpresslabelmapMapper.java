package com.simplelife.renhai.server.db;

import com.simplelife.renhai.server.db.Impresslabelmap;

public interface ImpresslabelmapMapper {
    int deleteByPrimaryKey(Integer impressLabelMapId);
    int insert(Impresslabelmap record);
    Impresslabelmap selectByPrimaryKey(Integer impressLabelMapId);
    int updateByPrimaryKey(Impresslabelmap record);
}