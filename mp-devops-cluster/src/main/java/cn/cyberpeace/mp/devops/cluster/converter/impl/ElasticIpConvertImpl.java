package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.ElasticIpConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.ElasticIpConvertModel;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.ElasticIpService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ElasticIpConvertImpl implements ElasticIpConverter {

    private ElasticIpService elasticIpService;

    @Autowired
    public ElasticIpConvertImpl(ElasticIpService elasticIpService) {
        this.elasticIpService = elasticIpService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(ElasticIpPageRequest::getPage,ElasticIpPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(ElasticIpPageRequest::getLimit,ElasticIpPageRequest::getOffset,
                ElasticIpPageRequest::getPage,ElasticIpPageRequest::getPerPage);
    }

    @Override
    public ElasticIpConvertModel page(ElasticIpPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = BeanUtils.wrapperGetParamsByObject(request,ignoreFields);
        final ElasticIpCloudPlatformModel model = elasticIpService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getFloatingips())){
            return null;
        }
        List<ElasticIpConvertModel.Model> list = model.getFloatingips().stream().map(elasticIpModel ->
                ElasticIpConvertModel.Model.builder().floatIpAddress(elasticIpModel.getFloating_ip_address())
                        .fixedIpAddress(elasticIpModel.getFixed_ip_address())
                        .status(CloudPlatformEnum.fromValue(elasticIpModel.getStatus()))
                        .createdAt(elasticIpModel.getCreated_at()).build()
        ).collect(Collectors.toList());

        ElasticIpConvertModel convertModel = new ElasticIpConvertModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }
}
