package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.cscene.model.CsceneStatusModel;

import java.util.List;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
public interface CsceneStatusService extends BaseModelService<CsceneStatusModel, QueryParams> {
    /**
     * 查询类型
      * @return
     */
    List<CsceneStatusModel> findAll();
}
