package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.flow.model.FlowCategoryModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Mapper
@Repository
public interface FlowCategoryMapper {
    /** 查询所有流量分类
     * @return
     */
    List<FlowCategoryModel> selectAll();
}
