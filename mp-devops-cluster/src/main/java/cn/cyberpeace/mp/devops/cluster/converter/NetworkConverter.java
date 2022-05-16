package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NetworkService;
import cn.cyberpeace.mp.devops.cluster.converter.model.NetworkPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 网络管理
 * @author wanggang
 */
@Service
public class NetworkConverter implements BaseConverter{

    private NetworkService networkService;

    public NetworkConverter(NetworkService networkService) {
        this.networkService = networkService;
    }

    /**
     * 分页查询网络信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public NetworkPageConverterModel page(NetworkPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        CloudPlatformPageModel<NetworkModel> model = networkService.page(getParams);
        List<NetworkPageConverterModel.Model> list = model.getList().stream().map(networkModel -> {
            NetworkModel.Subnet subnet = networkModel.getSubnet();
            List<NetworkPageConverterModel.Model.Subnet> subnets = Lists.newArrayList();
            if (Objects.nonNull(subnet)) {
                subnets = List.of(new NetworkPageConverterModel.Model.Subnet(subnet.getId(), subnet.getCidr(), subnet.getGatewayIp()));
            }
            return  NetworkPageConverterModel.Model.builder().name(networkModel.getName()).networkType(networkModel.getNetworkType())
                    .status(networkModel.getStatus())
                    .subnets(subnets).createdAt(networkModel.getCreatedAt()).build();
        }).collect(Collectors.toList());
        NetworkPageConverterModel convertModel = new NetworkPageConverterModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

}
