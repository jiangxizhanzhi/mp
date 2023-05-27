package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.flow.bo.FlowTaskNode;
import cn.cyberpeace.mp.flow.model.FlowTaskTaskModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 流量执行任务mapper
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
@Mapper
@Repository
@ModelClass(FlowTaskTaskModel.class)
public interface FlowTaskTaskMapper extends BaseModelMapper<FlowTaskTaskModel> {

    /**
     * 根据场景id查询节点列表
     * @param sceneId
     * @return
     */
    List<FlowTaskNode> nodeList(@Param("sceneId") String sceneId);
	
}
