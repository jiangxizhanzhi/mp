package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.model.OpModuleModel;
import cn.cyberpeace.mp.devops.log.repository.OpModuleMapper;
import cn.cyberpeace.mp.devops.log.service.OpModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class OpModuleServiceImpl implements OpModuleService {

    @Autowired
    private OpModuleMapper opModuleMapper;
    @Override
    public List<OpModuleModel> findAll() {
        return opModuleMapper.selectAll();
    }
}
