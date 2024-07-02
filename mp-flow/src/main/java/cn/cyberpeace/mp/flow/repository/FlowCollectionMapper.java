package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @ClassName FlowCollectionMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(FlowCollectionModel.class)
public interface FlowCollectionMapper extends BaseModelMapper<FlowCollectionModel> {

    /**
     * 根据名称查询
     * @param title
     * @return
     */
    FlowCollectionModel selectByTitle(@Param("title") String title);

}
