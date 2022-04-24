package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.model.CollectionTemplateProbeModel;

public interface CollectionTemplateProbeMapper {
    int deleteByPrimaryKey(Integer pk);

    int insert(CollectionTemplateProbeModel record);

    int insertSelective(CollectionTemplateProbeModel record);

    CollectionTemplateProbeModel selectByPrimaryKey(Integer pk);

    int updateByPrimaryKeySelective(CollectionTemplateProbeModel record);

    int updateByPrimaryKey(CollectionTemplateProbeModel record);
}