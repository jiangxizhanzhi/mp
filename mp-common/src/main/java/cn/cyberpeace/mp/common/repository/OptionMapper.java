package cn.cyberpeace.mp.common.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.common.model.OptionModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
@ModelClass(OptionModel.class)
public interface OptionMapper extends BaseModelMapper<OptionModel> {

    
    Integer getMaxCode(@Param("type") String type);

    
    Integer getMaxSort(@Param("type") String type);

}
