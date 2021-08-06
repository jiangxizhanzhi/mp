package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.RouterPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;

public interface RouterConverter extends BaseConverter{

    /**
     * 分页查询路由信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    RouterPageConverterModel page(RouterPageRequest request, String... ignoreFields);

    /**
     * 获取路由总数
     * @return
     */
    Integer getTotalRouters(RouterPageRequest request);
}
