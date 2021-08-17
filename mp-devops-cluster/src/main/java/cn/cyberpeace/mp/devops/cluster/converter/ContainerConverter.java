package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.ContainerConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;

public interface ContainerConverter extends BaseConverter {

    /**
     * 分页查询容器信息
     * @param request
     * @param ignoreFields
     * @return
     */
    ContainerConvertModel page(ContainerPageRequest request, String... ignoreFields);

    Integer getTotalContainers(ContainerPageRequest request);
}
