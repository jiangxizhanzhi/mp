package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NetworkService;
import cn.cyberpeace.mp.devops.cluster.converter.bo.ElasticIpBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.common.ElasticIpModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 弹性IP接口层
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class ElasticIpConverter implements BaseConverter {

    private final NetworkService networkService;

    /**
     * 分页查询弹性IP信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public BasePageBO<ElasticIpBO> page(ElasticIpPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        CloudPlatformPageResult<ElasticIpModel> model = networkService.pageElasticIp(getParams);
        List<ElasticIpBO> list = model.getList().stream().map(elasticIpModel ->
                ElasticIpBO.builder().floatIpAddress(elasticIpModel.getFloatingIpAddress())
                        .fixedIpAddress(elasticIpModel.getFixedIpAddress())
                        .status(elasticIpModel.getStatus())
                        .createdAt(elasticIpModel.getCreatedAt()).build()
        ).collect(Collectors.toList());

        BasePageBO<ElasticIpBO> convertModel = new BasePageBO();
        convertModel.setList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

}
