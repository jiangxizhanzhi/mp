package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.model.CsceneStatusModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface CsceneStatusService {
    /**
     * 查询类型
      * @return
     */
    List<CsceneStatusModel> findAll();
}
