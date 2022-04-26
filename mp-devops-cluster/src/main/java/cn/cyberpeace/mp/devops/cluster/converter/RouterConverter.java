package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.RouterService;
import cn.cyberpeace.mp.devops.cluster.converter.model.RouterPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.RouterCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.RouterModel;
import cn.cyberpeace.mp.devops.cluster.request.BasePageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RouterConverter implements BaseConverter{

    private RouterService routerService;

    public RouterConverter(RouterService routerService) {
        this.routerService = routerService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getOffset,
                BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    /**
     * 分页查询路由信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public RouterPageConverterModel page(RouterPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        RouterCloudPlatformModel model = routerService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getRouters())){
            return null;
        }
        List<RouterModel> routers = model.getRouters();
        List<RouterPageConverterModel.Model> list = routers.stream().map(routerModel ->
                RouterPageConverterModel.Model.builder().name(routerModel.getName())
                        .status(CloudPlatformEnum.fromValue(routerModel.getStatus())).createdAt(routerModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        RouterPageConverterModel convertModel = new RouterPageConverterModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 获取路由总数
     * @return
     */
    public Integer getTotalRouters(RouterPageRequest request) {
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        RouterCloudPlatformModel page = routerService.page(getParams);
        return Objects.isNull(page) || Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal();
    }
}
