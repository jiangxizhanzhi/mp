package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowScriptDemoModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(FlowScriptDemoModel.class)
public interface FlowScriptDemoMapper extends BaseModelMapper<FlowScriptDemoModel> {

    List<FlowScriptDemoModel> listAll();

}
