package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.NetworkService;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.NetworkSubnetBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.NetworkPageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkModel;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
     * @param dto
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public BasePageBO<NetworkBO> page(NetworkPageDTO dto, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,ignoreFields);
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


    /**
     * 获取网络总数
     * @return
     */
    public Integer getTotalNetwork(NetworkPageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        BasePageBO<NetworkBO> page = this.page(dto, this.getTotalPageIgnoreField());
        return page.getTotal();
    }

}
