package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.InstanceConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;

public interface InstanceConverter extends BaseConverter{


    /**
     * 分页查询虚拟机信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    InstanceConvertModel page(InstancePageRequest request, String... ignoreFields);

    /**
     * 获取虚拟机总数
     * @return
     */
    Integer getTotalInstances(InstancePageRequest request);
}
