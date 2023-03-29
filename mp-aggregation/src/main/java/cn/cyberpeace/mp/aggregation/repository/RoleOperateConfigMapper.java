package cn.cyberpeace.mp.aggregation.repository;

import cn.cyberpeace.mp.aggregation.model.RoleOperateConfigModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(RoleOperateConfigModel.class)
public interface RoleOperateConfigMapper extends BaseModelMapper<RoleOperateConfigModel> {
}
