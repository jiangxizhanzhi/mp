package cn.cyberpeace.mp.cscene.service.impl;

import cn.cyberpeace.mp.cscene.model.CsceneStatusModel;
import cn.cyberpeace.mp.cscene.model.CsceneTypeModel;
import cn.cyberpeace.mp.cscene.repository.CsceneStatusMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneTypeMapper;
import cn.cyberpeace.mp.cscene.service.CsceneStatusService;
import cn.cyberpeace.mp.cscene.service.CsceneTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class CsceneTypeServiceImpl implements CsceneTypeService {

    @Autowired
    private CsceneTypeMapper csceneTypeMapper;

    @Override
    public List<CsceneTypeModel> findAll() {
        return csceneTypeMapper.selectAll();
    }
}
