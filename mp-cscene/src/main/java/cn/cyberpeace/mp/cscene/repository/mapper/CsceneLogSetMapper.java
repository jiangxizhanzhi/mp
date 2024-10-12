package cn.cyberpeace.mp.cscene.repository.mapper;

import cn.cyberpeace.mp.cscene.request.logset.LogSetRequest;
import cn.cyberpeace.mp.cscene.vo.SceneTypeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CsceneLogSetMapper {

    Boolean modifySceneCollectionDataCycle(@Param("param") LogSetRequest requestData);

    Integer getSceneCollectionDataCycle(String sceneId);

    List<SceneTypeVO> selectLivedSceneInstance();
}
