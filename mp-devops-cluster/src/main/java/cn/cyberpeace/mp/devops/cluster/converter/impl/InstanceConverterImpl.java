package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.InstanceConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstanceConvertModel;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devops.cluster.service.VmService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InstanceConverterImpl implements InstanceConverter {

    private VmService vmService;

    @Autowired
    public InstanceConverterImpl(VmService vmService) {
        this.vmService = vmService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(InstancePageRequest::getPage,InstancePageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(InstancePageRequest::getOffset,
                InstancePageRequest::getPage,InstancePageRequest::getPerPage);
    }


    @Override
    public InstanceConvertModel page(InstancePageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = BeanUtils.wrapperGetParamsByObject(request,ignoreFields);
        InstanceCloudPlatformModel model = vmService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getInstances())){
            return null;
        }
        List<InstanceConvertModel.Model> list = model.getInstances().stream().map(instanceModel ->
                InstanceConvertModel.Model.builder().name(instanceModel.getName()).node(instanceModel.getHostname())
                        .addressList(instanceModel.getAddresses().stream().map(InstanceModel.Address::getIp)
                                .collect(Collectors.toList()))
                        .status(CloudPlatformEnum.fromValue(instanceModel.getStatus())).createTime(instanceModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        InstanceConvertModel convertModel = new InstanceConvertModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    @Override
    public Integer getTotalInstances(InstancePageRequest request) {
        String getParams = BeanUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        InstanceCloudPlatformModel page = vmService.page(getParams);
        return Objects.isNull(page) ||  Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal() ;
    }

}
