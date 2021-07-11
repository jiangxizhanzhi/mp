package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.bo.FlowTaskNode;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowTaskPageQueryParams;
import cn.cyberpeace.mp.flow.response.FlowTaskPageResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
@ModelClass(FlowTaskTaskModel.class)
public interface FlowTaskTaskMapper extends BaseModelMapper<FlowTaskTaskModel> {

    
    List<FlowTaskNode> nodeList(@Param("sceneId") String sceneId);

    
    List<FlowTaskPageResponse> pageBySceneId(FlowTaskPageQueryParams params);

    
    int countBySceneId(FlowTaskPageQueryParams params);
	
}
