package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.model.OpTypeModel;
import cn.cyberpeace.mp.devops.log.repository.OpTypeMapper;
import cn.cyberpeace.mp.devops.log.service.OpTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class OpTypeServiceImpl implements OpTypeService {

    @Autowired
    private OpTypeMapper opTypeMapper;
    @Override
    public List<OpTypeModel> findAll() {
        return opTypeMapper.selectAll();
    }
}
