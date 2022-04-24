package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;

public interface CollectionTemplateMapper {
    int deleteByPrimaryKey(Integer pk);

    int insert(CollectionTemplateModel record);

    int insertSelective(CollectionTemplateModel record);

    CollectionTemplateModel selectByPrimaryKey(Integer pk);

    int updateByPrimaryKeySelective(CollectionTemplateModel record);

    int updateByPrimaryKey(CollectionTemplateModel record);
}