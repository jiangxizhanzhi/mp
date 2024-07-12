 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowClassificationsMapper.java.
 // File size 1024.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wanggang
 */
@Mapper
@Repository
@ModelClass(FlowClassificationModel.class)
public interface FlowClassificationsMapper extends BaseModelMapper<FlowClassificationModel> {

    /**
     * 查询所有
     * @return
     */
    List<FlowClassificationModel> findAll();

    /**
     * 删除所有
     * @return
     */
    int deleteAll();

    /**
     * 新增
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<FlowClassificationModel> list, @Param("currentTime")LocalDateTime currentTime);
}
