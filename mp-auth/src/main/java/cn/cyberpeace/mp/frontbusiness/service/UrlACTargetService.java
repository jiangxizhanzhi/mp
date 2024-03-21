package cn.cyberpeace.mp.frontbusiness.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontbusiness.model.UrlACTargetModel;

/**
 * @Description
 * @Author shgang
 * @Date 2022/9/22
 */
public interface UrlACTargetService extends BaseModelService<UrlACTargetModel, QueryParams> {

    int deleteByUrlACId(String[] urlacId);
}
