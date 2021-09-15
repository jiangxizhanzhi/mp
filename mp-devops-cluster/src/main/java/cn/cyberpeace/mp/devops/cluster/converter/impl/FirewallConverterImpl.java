package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.FirewallConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.FirewallPageConverterModel;
import cn.cyberpeace.mp.devops.cluster.model.FirewallCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.FirewallPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.FirewallService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@Service
public class FirewallConverterImpl implements FirewallConverter {

    private FirewallService firewallService;

    @Autowired
    public FirewallConverterImpl(FirewallService firewallService) {
        this.firewallService = firewallService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(FirewallPageRequest::getPage,FirewallPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(FirewallPageRequest::getLimit,FirewallPageRequest::getOffset,
                FirewallPageRequest::getPage,FirewallPageRequest::getPerPage);
    }

    @Override
    public FirewallPageConverterModel page(FirewallPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = BeanUtils.wrapperGetParamsByObject(request,ignoreFields);
        FirewallCloudPlatformModel model = firewallService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getFirewalls())){
            return null;
        }
        FirewallPageConverterModel convertModel = new FirewallPageConverterModel();
        convertModel.setModelList(model.getFirewalls());
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }
}
