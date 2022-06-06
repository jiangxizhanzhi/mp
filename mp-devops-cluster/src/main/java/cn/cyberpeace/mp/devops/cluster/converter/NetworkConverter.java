package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NetworkService;
import cn.cyberpeace.mp.devops.cluster.converter.bo.NetworkBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.NetworkSubnetBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class NetworkConverter implements BaseConverter{

    private final NetworkService networkService;

    /**
     * 分页查询网络信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public BasePageBO<NetworkBO> page(NetworkPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        CloudPlatformPageResult<NetworkModel> model = networkService.page(getParams);
        List<NetworkBO> list = model.getList().stream().map(networkModel -> {
            NetworkModel.Subnet subnet = networkModel.getSubnet();
            List<NetworkSubnetBO> subnets = Lists.newArrayList();
            if (Objects.nonNull(subnet)) {
                subnets = List.of(new NetworkSubnetBO(subnet.getId(), subnet.getCidr(), subnet.getGatewayIp()));
            }
            return  NetworkBO.builder().name(networkModel.getName()).networkType(networkModel.getNetworkType())
                    .status(networkModel.getStatus())
                    .subnets(subnets).createdAt(networkModel.getCreatedAt()).build();
        }).collect(Collectors.toList());
        BasePageBO<NetworkBO> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

}
