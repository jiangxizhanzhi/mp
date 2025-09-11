package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.model.PlatformModel;
import cn.cyberpeace.mp.devopscluster.vo.PlatformVO;
import cn.cyberpeace.mp.devopscluster.request.PlatformPageRequest;

/**
 * @Description:
 * @Author guquan
 * @Date：2025/9/9 11:13
 */
public interface PlatformService extends BaseModelService<PlatformModel, QueryParams> {

    PlatformVO platformModel2PlatformVO(PlatformModel platformModel);

    PlatformModel platformVO2PlatformModel(PlatformVO platformVO);

    ServiceResult page(PlatformPageRequest pageRequest);

    PlatformVO add(PlatformVO platformVO);

    PlatformVO modify(PlatformVO platformVO);

    Boolean deletePlatform(String id);

}
