package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateProbeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


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
}
