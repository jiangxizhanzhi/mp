package cn.cyberpeace.mp.situationaware.service.impl;

import cn.cyberpeace.mp.situationaware.model.OsTypeModel;
import cn.cyberpeace.mp.situationaware.repository.OsTypeMapper;
import cn.cyberpeace.mp.situationaware.service.OsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class OsTypeServiceImpl implements OsTypeService {

    @Autowired
    private OsTypeMapper osTypeMapper;
    @Override
    public List<OsTypeModel> findAll() {
        return osTypeMapper.selectAll();
    }
}
