package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.model.InterfaceModel;
import cn.cyberpeace.mp.devopscluster.vo.InterfaceVO;
import cn.cyberpeace.mp.devopscluster.request.InterfacePageRequest;

/**
 * @Description:
 * @Author guquan
 * @Date：2025/9/9 11:13
 */
public interface InterfaceService extends BaseModelService<InterfaceModel, QueryParams> {

    InterfaceVO interfaceModel2InterfaceVO(InterfaceModel interfaceModel);

    InterfaceModel interfaceVO2InterfaceModel(InterfaceVO interfaceVO);

    ServiceResult page(InterfacePageRequest pageRequest);

    InterfaceVO add(InterfaceVO interfaceVO);

    InterfaceVO modify(InterfaceVO interfaceVO);

    Boolean deleteInterface(String id);

}
