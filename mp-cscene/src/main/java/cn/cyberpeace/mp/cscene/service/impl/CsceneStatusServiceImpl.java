package cn.cyberpeace.mp.cscene.service.impl;

import cn.cyberpeace.mp.cscene.model.CsceneStatusModel;
import cn.cyberpeace.mp.cscene.model.CsceneTypeModel;
import cn.cyberpeace.mp.cscene.repository.CsceneStatusMapper;
import cn.cyberpeace.mp.cscene.service.CsceneStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class CsceneStatusServiceImpl implements CsceneStatusService {

    @Autowired
    private CsceneStatusMapper csceneStatusMapper;
    @Override
    public List<CsceneStatusModel> findAll() {
        return csceneStatusMapper.selectAll();
    }
}
