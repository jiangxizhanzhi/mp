package cn.cyberpeace.mp.cscene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneRoleMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName CsceneRoleMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneRoleMapper
 */
@Mapper
@Repository
@ModelClass(CsceneRoleModel.class)
public interface CsceneRoleMapper extends BaseCsceneRoleMapper<CsceneRoleModel> {
}
