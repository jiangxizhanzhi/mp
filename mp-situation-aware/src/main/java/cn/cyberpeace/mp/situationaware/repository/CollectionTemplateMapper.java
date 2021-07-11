package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplatePageItemDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(CollectionTemplateModel.class)
public interface CollectionTemplateMapper extends BaseModelMapper<CollectionTemplateModel> {












    List<CollectionTemplatePageItemDto> listTemplateItemDtos(@Param("condition") CollectionTemplateSearchCondition condition);

    CollectionTemplateModel getByName(@Param("name") String name);
}
