package cn.cyberpeace.mp.situationaware.service.impl;

import cn.cyberpeace.mp.situationaware.model.ProbeTypeModel;
import cn.cyberpeace.mp.situationaware.repository.ProbeTypeMapper;
import cn.cyberpeace.mp.situationaware.service.ProbeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class ProbeTypeServiceImpl implements ProbeTypeService {

    @Autowired
    private ProbeTypeMapper probeTypeMapper;
    @Override
    public List<ProbeTypeModel> findAll() {
        return probeTypeMapper.selectAll();
    }
}
