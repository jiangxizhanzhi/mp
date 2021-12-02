package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NetworkService;
import cn.cyberpeace.mp.devops.cluster.converter.NetworkConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.NetworkPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NetworkConverterImpl implements NetworkConverter {

    private NetworkService networkService;

    @Autowired
    public NetworkConverterImpl(NetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(NetworkPageRequest::getPage,NetworkPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(NetworkPageRequest::getLimit,NetworkPageRequest::getOffset,
                NetworkPageRequest::getPage,NetworkPageRequest::getPerPage);
    }

    @Override
    public NetworkPageConverterModel page(NetworkPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        NetworkCloudPlatformModel model = networkService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getNetworks())){
            return null;
        }
        List<NetworkPageConverterModel.Model> list = model.getNetworks().stream().map(networkModel -> {
            NetworkModel.Subnet subnet = networkModel.getSubnet();
            List<NetworkPageConverterModel.Model.Subnet> subnets = Lists.newArrayList();
            if (Objects.nonNull(subnet)) {
                subnets = List.of(new NetworkPageConverterModel.Model.Subnet(subnet.getId(), subnet.getCidr(), subnet.getGatewayIp()));
            }
            return  NetworkPageConverterModel.Model.builder().name(networkModel.getName()).networkType(networkModel.getNetworkType())
                    .status(CloudPlatformEnum.fromValue(networkModel.getStatus()))
                    .subnets(subnets).createdAt(networkModel.getCreatedAt()).build();
        }).collect(Collectors.toList());
        NetworkPageConverterModel convertModel = new NetworkPageConverterModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }
}
