package cn.cyberpeace.mp.devopscluster.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.CpNodeMonitorScriptConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(CpNodeMonitorScriptConfigModel.class)
public interface CpNodeMonitorScriptConfigMapper extends BaseModelMapper<CpNodeMonitorScriptConfigModel> {

    List<CpNodeMonitorScriptConfigModel> listAll();

    CpNodeMonitorScriptConfigModel getByAgentName(String agentName);

}
