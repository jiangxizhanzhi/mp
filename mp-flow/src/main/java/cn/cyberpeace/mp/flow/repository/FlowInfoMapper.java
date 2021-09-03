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

    /**
     * 批量查询流量信息
     * @param flowIds
     * @return
     */
    List<FlowInfoModel> selectByFlowIds(@Param("flowIds") List<String> flowIds);

    /**
     * 根据名称查询
     * @param title
     * @return
     */
    FlowInfoModel selectByTitle(@Param("title") String title);

}
