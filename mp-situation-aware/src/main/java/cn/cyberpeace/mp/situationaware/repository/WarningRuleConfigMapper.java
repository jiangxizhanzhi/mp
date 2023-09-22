package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.WarningRuleConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName WarningRuleConfigMapper
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/22 9:19
 **/
@Mapper
@Repository
@ModelClass(WarningRuleConfigModel.class)
public interface WarningRuleConfigMapper extends BaseModelMapper<WarningRuleConfigModel> {
}
