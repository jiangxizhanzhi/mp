package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ContainerService;
import cn.cyberpeace.mp.devops.cluster.converter.bo.ContainerBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.common.ContainerModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContainerConverter implements BaseConverter {

    private final ContainerService containerService;

    /**
     * 分页查询容器列表
     * @param request
     * @param ignoreFields
     * @return
     */
    public BasePageBO<ContainerBO> page(ContainerPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        CloudPlatformPageResult<ContainerModel> model = containerService.page(getParams);
        BasePageBO<ContainerBO> convertModel = new BasePageBO();
        convertModel.setList(this.convertContainerModelList(model.getList()));
        convertModel.setTotal(Objects.isNull(model.getTotal()) ? NumberUtils.INTEGER_ZERO : model.getTotal());
        return convertModel;
    }

    /**
     * 获取容器总数量
     * @param request
     * @return
     */
    public Integer getTotalContainers(ContainerPageRequest request) {
        request.setPerPage(NumberUtils.INTEGER_ONE);
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        CloudPlatformPageResult page = containerService.page(getParams);
        return page.getTotal();
    }

    /**
     * 转换List<ContainerModel>集合对象
     * @param containers
     * @return
     */
    private List<ContainerBO> convertContainerModelList(List<ContainerModel> containers){
        if (CollectionUtils.isEmpty(containers)){
            return Lists.newArrayList();
        }
        return containers.stream().map(containerModel -> {
            ContainerBO response = ContainerBO.builder()
                        .name(containerModel.getName()).image(containerModel.getImage())
                        .node(containerModel.getHost())
                        .status(containerModel.getStatus())
                        .build();
                Map<String, Object> addresses = containerModel.getAddresses();
                if (MapUtils.isNotEmpty(addresses)) {
                    List<ContainerModel.Address> addressList = addresses.values().stream()
                            .map(v -> JSONArray.parseArray(JSONArray.toJSONString(v), ContainerModel.Address.class))
                            .flatMap(List::stream).collect(Collectors.toList());
                    response.setAddressList(addressList.stream().map(ContainerModel.Address::getAddr).collect(Collectors.toList()));
                    response.setVlanIdList(addressList.stream().filter(v -> Objects.nonNull(v.getVlanId())).map(ContainerModel.Address::getVlanId).collect(Collectors.toList()));
                }
                return response;
            }).collect(Collectors.toList());
    }
}
