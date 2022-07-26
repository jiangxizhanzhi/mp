package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.model.ProbeTypeModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface ProbeTypeService {

    /**
     * 查询所有探针类型
     * @return
     */
    List<ProbeTypeModel> findAll();
}
