package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowPortModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(FlowPortModel.class)
public interface FlowPortMapper extends BaseModelMapper<FlowPortModel> {

    /**
     * 批量查询网口信息
     * @param flowPortIds
     * @return
     */
    List<FlowPortModel> selectByPortIds(@Param("flowPortIds") List<String> flowPortIds);

}
