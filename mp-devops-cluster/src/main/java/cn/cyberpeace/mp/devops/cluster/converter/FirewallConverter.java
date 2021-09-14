package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.FirewallPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.FirewallPageRequest;

public interface FirewallConverter extends BaseConverter{

    /**
     * 分页查询
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    FirewallPageConverterModel page(FirewallPageRequest request, String... ignoreFields);
}
