package cn.cyberpeace.mp.behavior.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.CsceneUserBehaviorModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName CsceneUserBehaviorMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */

@Mapper
@Repository
@ModelClass(CsceneUserBehaviorModel.class)
public interface CsceneUserBehaviorMapper extends BaseModelMapper<CsceneUserBehaviorModel> {

	List<CsceneUserBehaviorModel> getTopoAllByNodeId(@Param("nodeId") String nodeId);

	List<CsceneUserBehaviorModel> getTopoAllByNodeIds(@Param("nodeIds") List<String> nodeIds);

	void deleteTopoByNodeId(@Param("nodeId") String nodeId);

	void deleteBySceneId(@Param("sceneId") String sceneId);
}
