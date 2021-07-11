package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceUserCountModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
@ModelClass(CsceneInstanceUserCountModel.class)
public interface CsceneInstanceUserCountMapper extends BaseModelMapper<CsceneInstanceUserCountModel> {

    
    int increaseCount(@Param("userId") String userId);

    
    int decreaseCount(@Param("userId") String userId);

}
