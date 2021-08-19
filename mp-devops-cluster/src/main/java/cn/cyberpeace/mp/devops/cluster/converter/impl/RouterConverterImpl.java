package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.RouterConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.RouterPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.RouterCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.RouterService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RouterConverterImpl implements RouterConverter {

    private RouterService routerService;

    public RouterConverterImpl(RouterService routerService) {
        this.routerService = routerService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(RouterPageRequest::getPage,RouterPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(RouterPageRequest::getOffset,
                RouterPageRequest::getPage,RouterPageRequest::getPerPage);
    }

    @Override
    public RouterPageConverterModel page(RouterPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = BeanUtils.wrapperGetParamsByObject(request,ignoreFields);
        RouterCloudPlatformModel model = routerService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getRouters())){
            return null;
        }
        List<RouterCloudPlatformModel.RouterModel> routers = model.getRouters();
        List<RouterPageConverterModel.Model> list = routers.stream().map(routerModel ->
                RouterPageConverterModel.Model.builder().name(routerModel.getName())
                        .status(CloudPlatformEnum.fromValue(routerModel.getStatus())).createdAt(routerModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        RouterPageConverterModel convertModel = new RouterPageConverterModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    @Override
    public Integer getTotalRouters(RouterPageRequest request) {
        String getParams = BeanUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        RouterCloudPlatformModel page = routerService.page(getParams);
        return Objects.isNull(page) || Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal();
    }
}
