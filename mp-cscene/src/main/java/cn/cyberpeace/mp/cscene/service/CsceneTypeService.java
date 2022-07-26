package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.model.CsceneTypeModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface CsceneTypeService {
    /**
     * 查询状态
     * @return
     */
    List<CsceneTypeModel> findAll();
}
