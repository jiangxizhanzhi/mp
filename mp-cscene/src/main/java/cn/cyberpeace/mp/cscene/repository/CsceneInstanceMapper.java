package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountActivitySceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceCountByReferenceTypeModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneMapper;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.model.CloudPluginSceneModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
@Repository
@ModelClass(CsceneInstanceModel.class)
public interface CsceneInstanceMapper extends BaseCsceneMapper<CsceneInstanceModel> {

    
    List<CsceneInstanceCountByReferenceTypeModel> countGroupByReferenceType(@Param("events") List<Integer> events);

    
    CsceneInstanceCountActivitySceneModel countActivityScene();

    
    Integer countActivitySceneByTime(LocalDateTime startTime, LocalDateTime endTime);

    List<CloudPluginSceneElementModel> getVlanUsedInstance(List<Integer> vlanId);
}
