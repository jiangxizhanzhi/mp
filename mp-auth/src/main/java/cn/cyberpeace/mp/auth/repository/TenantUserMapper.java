package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.bo.org.OrgIdAndCount;
import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(TenantUserModel.class)
public interface TenantUserMapper extends BaseModelMapper<TenantUserModel> {

    List<OrgIdAndCount> getGroupByOrg();
}
