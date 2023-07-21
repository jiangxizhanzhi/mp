package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.Cacheable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 组织路径处理类
 * @Author qiaokang
 * @Date：2022/9/1 11:22
 */
@Slf4j
@Service
public class OrgPathService {

    @Autowired
    private OrgMapper orgMapper;

    Map<String, List<OrgModel>> map = new ConcurrentHashMap<>();

    /**
     * 根据租户获取所有组织信息
     *
     * @param tenant
     * @return
     */
    public List<OrgModel> listByTenant(String tenant) {
        if (map.containsKey(tenant)) {
            log.info("maojun123456 hit map , {}" ,tenant);
            return map.get(tenant);
        } else {
            log.info("maojun123456 not hit map , {}" ,tenant);
            HumanCondition humanCondition = new HumanCondition();
            humanCondition.filterEqual(OrgModel::getTenant, tenant);
            var result = orgMapper.list(humanCondition);
            map.putIfAbsent(tenant, result);
            return result;
        }
    }

}
