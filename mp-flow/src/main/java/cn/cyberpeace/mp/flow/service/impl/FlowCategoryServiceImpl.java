package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.flow.model.FlowCategoryModel;
import cn.cyberpeace.mp.flow.repository.FlowCategoryMapper;
import cn.cyberpeace.mp.flow.service.FlowCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Service
public class FlowCategoryServiceImpl implements FlowCategoryService {

    @Autowired
    private FlowCategoryMapper flowCategoryMapper;

    @Override
    public List<FlowCategoryModel> findAll() {
        return flowCategoryMapper.selectAll();
    }
}
