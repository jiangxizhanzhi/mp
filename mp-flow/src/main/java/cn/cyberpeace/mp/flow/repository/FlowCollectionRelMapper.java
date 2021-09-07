package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.flow.model.FlowCollectionRelModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(FlowCollectionRelModel.class)
public interface FlowCollectionRelMapper extends BaseModelMapper<FlowCollectionRelModel> {

    /**
     * 批量查询流量集合关联信息
     * @param collectionIds
     * @return
     */
    List<FlowCollectionRelModel> selectByCollectionIds(@Param("collectionIds") List<String> collectionIds);

}
