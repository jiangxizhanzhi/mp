package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.FlowClassificationModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wanggang
 */
@Mapper
@Repository
@ModelClass(FlowClassificationModel.class)
public interface FlowClassificationsMapper extends BaseModelMapper<FlowClassificationModel> {

    /**
     * 查询所有
     * @return
     */
    List<FlowClassificationModel> findAll();

    /**
     * 删除所有
     * @return
     */
    int deleteAll();

    /**
     * 新增
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<FlowClassificationModel> list);
}
