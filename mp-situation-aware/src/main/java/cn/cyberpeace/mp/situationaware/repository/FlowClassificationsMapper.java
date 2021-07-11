package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
@Repository
@ModelClass(FlowClassificationModel.class)
public interface FlowClassificationsMapper extends BaseModelMapper<FlowClassificationModel> {

    
    List<FlowClassificationModel> findAll();

    
    int deleteAll();

    
    Integer batchInsert(@Param("list") List<FlowClassificationModel> list, @Param("currentTime")LocalDateTime currentTime);
}
