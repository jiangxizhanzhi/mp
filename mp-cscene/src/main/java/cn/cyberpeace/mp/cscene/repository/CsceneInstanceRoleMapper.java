package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneRoleMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CsceneInstanceRoleMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneRoleMapper
 */
@Mapper
@Repository
@ModelClass(CsceneInstanceRoleModel.class)
public interface CsceneInstanceRoleMapper extends BaseCsceneRoleMapper<CsceneInstanceRoleModel> {
}
