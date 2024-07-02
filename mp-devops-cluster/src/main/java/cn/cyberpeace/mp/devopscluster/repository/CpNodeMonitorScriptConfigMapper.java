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
