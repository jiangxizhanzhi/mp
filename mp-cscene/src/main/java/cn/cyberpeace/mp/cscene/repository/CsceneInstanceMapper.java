 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneInstanceMapper.java.
 // File size 1921.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountActivitySceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountByReferenceTypeModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName CsceneInstanceMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneMapper
 */
@Mapper
@Repository
@ModelClass(CsceneInstanceModel.class)
public interface CsceneInstanceMapper extends BaseCsceneMapper<CsceneInstanceModel> {

    /**
     * 根据引用类型分组统计
     * @param events 排除的event
     * @return
     */
    List<CsceneInstanceCountByReferenceTypeModel> countGroupByReferenceType(@Param("events") List<Integer> events);

    /**
     * 统计活动场景总数和场景总数
     * @return
     */
    CsceneInstanceCountActivitySceneModel countActivityScene();

    /**
     * 统计一段时间区间内活跃过的场景数(活跃数 = 区间内启动过的(不管有没有关闭) + 之前启动的而且未关闭的)
     * @param startTime
     * @param endTime
     * @return
     */
    Integer countActivitySceneByTime(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * getVlanUsedInstance
     *
     * @param vlanId
     * @return
     */
    List<CloudPluginSceneElementModel> getVlanUsedInstance(List<Integer> vlanId);
}
