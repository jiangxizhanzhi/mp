 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPortMapper.java.
 // File size 850.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName FlowPortMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(FlowPortModel.class)
public interface FlowPortMapper extends BaseModelMapper<FlowPortModel> {

    /**
     * 批量查询网口信息
     * @param flowPortIds
     * @return
     */
    List<FlowPortModel> selectByPortIds(@Param("flowPortIds") List<String> flowPortIds);

}
