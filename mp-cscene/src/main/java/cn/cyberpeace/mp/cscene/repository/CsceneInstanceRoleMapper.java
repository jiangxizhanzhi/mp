package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneRoleMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(CsceneInstanceRoleModel.class)
public interface CsceneInstanceRoleMapper extends BaseCsceneRoleMapper<CsceneInstanceRoleModel> {
}
