package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NetworkService;
import cn.cyberpeace.mp.devops.cluster.converter.model.ElasticIpConvertModel;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 弹性IP接口层
 * @author wanggang
 */
@Service
public class ElasticIpConverter implements BaseConverter {

    private NetworkService networkService;

    @Autowired
    public ElasticIpConverter(NetworkService networkService) {
        this.networkService = networkService;
    }


    /**
     * 分页查询弹性IP信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public ElasticIpConvertModel page(ElasticIpPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        ElasticIpCloudPlatformModel model = networkService.pageElasticIp(getParams);
        List<ElasticIpConvertModel.Model> list = model.getFloatingips().stream().map(elasticIpModel ->
                ElasticIpConvertModel.Model.builder().floatIpAddress(elasticIpModel.getFloatingIpAddress())
                        .fixedIpAddress(elasticIpModel.getFixedIpAddress())
                        .status(CloudPlatformEnum.fromValue(elasticIpModel.getStatus()))
                        .createdAt(elasticIpModel.getCreatedAt()).build()
        ).collect(Collectors.toList());

        ElasticIpConvertModel convertModel = new ElasticIpConvertModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

}
