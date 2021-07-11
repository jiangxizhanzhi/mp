package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
@ModelClass(CollectionTemplateProbeModel.class)
public interface CollectionTemplateProbeMapper extends BaseModelMapper<CollectionTemplateProbeModel> {
    










    int deleteByTemplateId(@Param("templateId") String templateId);

    int deleteByTemplateIds(@Param("templateIds") String[] templateIds);

    @Deprecated
    List<CollectionTemplateProbeDto> listCollectionTemplateProbeDtos(@Param("templateId") String templateId, @Param("probeCondition") ProbeSearchCondition probeCondition);

    List<CollectionTemplateProbeModel> listCollectionTemplateProbeModels(@Param("templateId") String templateId, @Param("probeCondition") ProbeSearchCondition probeCondition);

    
    int countByTemplateId(@Param("templateId") String templateId, @Param("type") Integer type);



    List<ProbeModel> listCustomProbesNotInTemplate(@Param("templateId") String templateId, @Param("category") Integer category, @Param("probeName") String probeName);

    List<ProbeModel> listCustomProbes(@Param("category") Integer category, @Param("probeName") String probeName);
}
