 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ProbeScriptMapper.java.
 // File size 1383.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.ProbeScriptModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ProbeScriptModel.class)
public interface ProbeScriptMapper extends BaseModelMapper<ProbeScriptModel> {
    //    int deleteByPrimaryKey(Integer pk);
//
//    int insert(ProbeScriptModel record);
//
//    int insertSelective(ProbeScriptModel record);
//
//    ProbeScriptModel selectByPrimaryKey(Integer pk);
//
//    int updateByPrimaryKeySelective(ProbeScriptModel record);
//
//    int updateByPrimaryKeyWithBLOBs(ProbeScriptModel record);
//
//    int updateByPrimaryKey(ProbeScriptModel record);

    /**
     * deleteByProbeId
     *
     * @param probeId
     * @return int
     */
    int deleteByProbeId(String probeId);

    /**
     * getChecksumByProbeId
     *
     * @param probeId
     * @return
     */
    String getChecksumByProbeId(String probeId);

    /**
     * getScriptModelByProbeId
     *
     * @param probeId
     * @return 探针脚本实体，跟探针一对一关系
     */
    ProbeScriptModel getScriptModelByProbeId(String probeId);
}
