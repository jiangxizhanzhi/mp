package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName OrgPathService
 * @Description 组织路径处理类
 * @Author qiaokang
 * @Date 2022/9/1 11:22
 * @Version 1.0.0
 */
@Service
public class OrgPathService {

    @Autowired
    private OrgMapper orgMapper;

    /**
     * 根据租户获取所有组织信息
     *
     * @param tenant
     * @return
     */
    @Cacheable(value = TenantUserServiceImpl.CACHE_VAL_ORGS_LIST_ALL)
    public List<OrgModel> listByTenant(String tenant) {
        HumanCondition humanCondition = new HumanCondition();
        humanCondition.filterEqual(OrgModel::getTenant, tenant);
        return orgMapper.list(humanCondition);
    }

}
