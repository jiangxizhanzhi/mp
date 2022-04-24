package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.model.ProbeScriptModel;

public interface ProbeScriptMapper {
    int deleteByPrimaryKey(Integer pk);

    int insert(ProbeScriptModel record);

    int insertSelective(ProbeScriptModel record);

    ProbeScriptModel selectByPrimaryKey(Integer pk);

    int updateByPrimaryKeySelective(ProbeScriptModel record);

    int updateByPrimaryKeyWithBLOBs(ProbeScriptModel record);

    int updateByPrimaryKey(ProbeScriptModel record);
}