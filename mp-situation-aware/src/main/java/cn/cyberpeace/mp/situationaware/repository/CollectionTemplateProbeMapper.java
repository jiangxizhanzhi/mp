package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.dto.probe.CollectionTemplateProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
@ModelClass(CollectionTemplateProbeModel.class)
public interface CollectionTemplateProbeMapper extends BaseModelMapper<CollectionTemplateProbeModel> {
    //    int deleteByPrimaryKey(Integer pk);
//
//    int insert(CollectionTemplateProbeModel record);
//
//    int insertSelective(CollectionTemplateProbeModel record);
//
//    CollectionTemplateProbeModel selectByPrimaryKey(Integer pk);
//
//    int updateByPrimaryKeySelective(CollectionTemplateProbeModel record);
//
//    int updateByPrimaryKey(CollectionTemplateProbeModel record);
    int deleteByTemplateId(@Param("templateId") String templateId);

    int deleteByTemplateIds(@Param("templateIds") String[] templateIds);

    List<CollectionTemplateProbeDto> listCollectionTemplateProbeDtos(@Param("templateId") String templateId, @Param("probeCondition") ProbeSearchCondition probeCondition);

    /**
     *
     * @param templateId
     * @param type 探针类型
     * @return
     */
    int countByTemplateId(@Param("templateId") String templateId, @Param("type") Integer type);

//    List<CollectionTemplateProbeDto> listByCondition();

    List<ProbeModel> listCustomProbesNotInTemplate(@Param("templateId") String templateId, @Param("category") Integer category, @Param("probeName") String probeName);
}
