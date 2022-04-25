package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CollectionTemplateModel.class)
public interface CollectionTemplateMapper extends BaseModelMapper<CollectionTemplateModel> {
//    int deleteByPrimaryKey(Integer pk);
//
//    int insert(CollectionTemplateModel record);
//
//    int insertSelective(CollectionTemplateModel record);
//
//    CollectionTemplateModel selectByPrimaryKey(Integer pk);
//
//    int updateByPrimaryKeySelective(CollectionTemplateModel record);
//
//    int updateByPrimaryKey(CollectionTemplateModel record);
}
