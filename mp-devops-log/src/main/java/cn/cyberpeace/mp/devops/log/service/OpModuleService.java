package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.model.OpModuleModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface OpModuleService {
    List<OpModuleModel> findAll();
}
