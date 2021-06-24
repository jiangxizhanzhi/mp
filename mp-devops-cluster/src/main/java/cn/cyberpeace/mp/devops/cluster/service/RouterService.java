package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.RouterOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
public interface RouterService extends BaseOpenStackService{

    RouterOpenStackModel page(RouterPageRequest request);
}
