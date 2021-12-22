package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ContainerService;
import cn.cyberpeace.mp.devops.cluster.converter.model.ContainerConvertModel;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.ContainerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ContainerModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContainerConverter implements BaseConverter {

    private ContainerService containerService;

    @Autowired
    public ContainerConverter(ContainerService containerService) {
        this.containerService = containerService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(ContainerPageRequest::getPage,ContainerPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(ContainerPageRequest::getOffset,
                ContainerPageRequest::getPage,ContainerPageRequest::getPerPage);
    }

    /**
     * 分页查询容器列表
     * @param request
     * @param ignoreFields
     * @return
     */
    public ContainerConvertModel page(ContainerPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        ContainerCloudPlatformModel model = containerService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getContainers())){
            return null;
        }
        List<ContainerConvertModel.Model> list =  model.getContainers().stream().map(containerModel -> {
            ContainerConvertModel.Model response = ContainerConvertModel.Model.builder().name(containerModel.getName()).image(containerModel.getImage())
                    .node(containerModel.getHost()).status(CloudPlatformEnum.fromValue(containerModel.getStatus())).build();
            Map<String,Object> addresses = containerModel.getAddresses();
            if (MapUtils.isNotEmpty(addresses)){
                List<ContainerModel.Address> addressList = addresses.values().stream()
                        .map(v -> JSONArray.parseArray(JSONArray.toJSONString(v),ContainerModel.Address.class))
                        .flatMap(List::stream).collect(Collectors.toList());
                response.setAddressList(addressList.stream().map(ContainerModel.Address::getAddr).collect(Collectors.toList()));
            }
            return response;
        }).collect(Collectors.toList());

        ContainerConvertModel convertModel = new ContainerConvertModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 获取容器总数量
     * @param request
     * @return
     */
    public Integer getTotalContainers(ContainerPageRequest request) {
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        ContainerCloudPlatformModel page = containerService.page(getParams);
        return Objects.isNull(page) || Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal();
    }
}
