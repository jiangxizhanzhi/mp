package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(FlowInfoModel.class)
public interface FlowInfoMapper extends BaseModelMapper<FlowInfoModel> {

    
    List<FlowInfoModel> selectByFlowIds(@Param("flowIds") List<String> flowIds);

    
    FlowInfoModel selectByTitle(@Param("title") String title);

    
    List<FlowInfoModel> selectByTitles(@Param("titleList") List<String> titleList);

}
