package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.FirewallService;
import cn.cyberpeace.mp.devops.cluster.converter.model.FirewallPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.model.FirewallCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.FirewallPageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 防火墙
 * @author wanggang
 */
@Service
public class FirewallConverter implements BaseConverter{

    private FirewallService firewallService;

    @Autowired
    public FirewallConverter(FirewallService firewallService) {
        this.firewallService = firewallService;
    }

    /**
     * 分页查询
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public FirewallPageConverterModel page(FirewallPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        FirewallCloudPlatformModel model = firewallService.page(getParams);
        FirewallPageConverterModel convertModel = new FirewallPageConverterModel();
        convertModel.setModelList(model.getFirewalls());
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }
}
