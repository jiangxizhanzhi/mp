package cn.cyberpeace.mp.behavior.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.SimulateModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(SimulateModel.class)
public interface SimulateMapper extends BaseModelMapper<SimulateModel> {

	Integer countSimulate();

	SimulateModel selectBySceneNode(@Param("scene_id") String sceneId, @Param("node_id") String nodeId);

	List<String> selectIdByStatus(@Param("status") Integer status);

	void delete(@Param("ids") List<String> ids);

	List<String> getNameList();
}
