 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ProbeParamMapper.java.
 // File size 1738.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(ProbeParamModel.class)
public interface ProbeParamMapper extends BaseModelMapper<ProbeParamModel> {

//    int deleteByPrimaryKey(Integer pk);
//
//    int insert(ProbeParamModel record);
//
//    int insertSelective(ProbeParamModel record);
//
//    ProbeParamModel selectByPrimaryKey(Integer pk);
//
//    int updateByPrimaryKeySelective(ProbeParamModel record);
//
//    int updateByPrimaryKey(ProbeParamModel record);

    /**
     * selectByProbeIdAndTemplateId
     *
     * @param probeId
     * @param templateId
     * @return
     */
    List<ProbeParamModel> selectByProbeIdAndTemplateId(@Param("probeId") String probeId, @Param("templateId") String templateId);

    /**
     * deleteByProbeIdAndTemplateId
     *
     * @param probeId
     * @param templateId
     * @return int
     */
    int deleteByProbeIdAndTemplateId(@Param("probeId") String probeId, @Param("templateId") String templateId);

    /**
     * deleteByTemplateId
     *
     * @param templateId
     * @return int
     */
    int deleteByTemplateId(@Param("templateId") String templateId);

    /**
     * deleteByTemplateIds
     *
     * @param templateIds
     * @return int
     */
    int deleteByTemplateIds(@Param("templateIds") String[] templateIds);
}
