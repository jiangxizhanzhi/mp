package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.NetworkPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;

public interface NetworkConverter extends BaseConverter{

    /**
     * 分页查询网络信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    NetworkPageConverterModel page(NetworkPageRequest request, String... ignoreFields);
}
