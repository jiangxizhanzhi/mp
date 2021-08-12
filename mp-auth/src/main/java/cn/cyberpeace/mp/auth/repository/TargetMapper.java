package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(TargetModel.class)
public interface TargetMapper extends BaseModelMapper<TargetModel> {

    List<TargetModel> listByTenantId(@Param("tenantId") String tenantId);

    List<TargetModel> listByParent(@Param("parentId") String parentId);

    List<TargetModel> listAll(@Param("tenantId") String tenantId, @Param("type") Integer type);

    List<TargetModel> listByMultiTypes(@Param("tenantId") String tenantId, @Param("types") Integer[] types);

}
