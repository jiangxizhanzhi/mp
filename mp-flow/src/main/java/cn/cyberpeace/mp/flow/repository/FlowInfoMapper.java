package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName FlowInfoMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
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

    /**
     * 根据名称批量查询流量
     * @param titleList
     * @return
     */
    List<FlowInfoModel> selectByTitles(@Param("titleList") List<String> titleList);

}
