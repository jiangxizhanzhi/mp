 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CpNodeMonitorScriptConfigMapper.java.
 // File size 997.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.CpNodeMonitorScriptConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName CpNodeMonitorScriptConfigMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(CpNodeMonitorScriptConfigModel.class)
public interface CpNodeMonitorScriptConfigMapper extends BaseModelMapper<CpNodeMonitorScriptConfigModel> {

    /**
     * listAll
     *
     * @return
     */
    List<CpNodeMonitorScriptConfigModel> listAll();

    /**
     * getByAgentName
     *
     * @param agentName
     * @return
     */
    CpNodeMonitorScriptConfigModel getByAgentName(String agentName);

}
