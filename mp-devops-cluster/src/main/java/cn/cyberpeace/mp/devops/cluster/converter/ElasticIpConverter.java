package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.ElasticIpConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;

/**
 * 弹性IP接口层
 */
public interface ElasticIpConverter extends BaseConverter {

    /**
     * 分页查询弹性IP信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    ElasticIpConvertModel page(ElasticIpPageRequest request, String... ignoreFields);
}
