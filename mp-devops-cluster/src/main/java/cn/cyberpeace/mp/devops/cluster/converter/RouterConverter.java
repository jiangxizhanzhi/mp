package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.RouterService;
import cn.cyberpeace.mp.devops.cluster.converter.model.RouterPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.common.RouterModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouterConverter implements BaseConverter{

    private final RouterService routerService;

    /**
     * 分页查询路由信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public RouterPageConverterModel page(RouterPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        CloudPlatformPageResult<RouterModel> model = routerService.page(getParams);
        List<RouterModel> routers = model.getList();
        List<RouterPageConverterModel.Model> list = routers.stream().map(routerModel ->
                RouterPageConverterModel.Model.builder().name(routerModel.getName())
                        .status(routerModel.getStatus()).createdAt(routerModel.getCreatedAt()).build()
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
        CloudPlatformPageResult page = routerService.page(getParams);
        return page.getTotal();
    }
}
