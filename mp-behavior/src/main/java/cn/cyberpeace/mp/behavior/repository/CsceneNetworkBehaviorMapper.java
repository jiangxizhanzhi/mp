package cn.cyberpeace.mp.behavior.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.CsceneNetworkBehaviorModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CsceneNetworkBehaviorMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
import java.util.List;

@Mapper
@Repository
@ModelClass(CsceneNetworkBehaviorModel.class)
public interface CsceneNetworkBehaviorMapper extends BaseModelMapper<CsceneNetworkBehaviorModel> {

	List<CsceneNetworkBehaviorModel> getTopoAllByNodeId(@Param("nodeId") String nodeId);

	List<CsceneNetworkBehaviorModel>  getTopoAllByIds(@Param("ids") List<String> ids);

	void deleteTopoByNodeId(@Param("nodeId") String nodeId);

	void deleteBySceneId(@Param("sceneId") String sceneId);
}
