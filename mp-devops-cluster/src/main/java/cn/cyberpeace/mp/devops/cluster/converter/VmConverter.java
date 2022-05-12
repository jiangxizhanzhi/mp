package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.VmService;
import cn.cyberpeace.mp.devops.cluster.converter.model.InstanceConvertModel;
import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 虚拟机管理
 * @author wanggang
 */
@Service
public class VmConverter implements BaseConverter{

    private VmService vmService;

    public VmConverter(VmService vmService) {
        this.vmService = vmService;
    }

    /**
     * 分页查询虚拟机信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public InstanceConvertModel page(InstancePageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        InstanceCloudPlatformModel model = vmService.page(getParams);
        List<InstanceConvertModel.Model> list = model.getInstances().stream().map(instanceModel ->
                InstanceConvertModel.Model.builder().name(instanceModel.getName()).node(instanceModel.getHost())
                        .addressList(instanceModel.getAddresses().stream().map(InstanceModel.Address::getIp)
                                .collect(Collectors.toList()))
                        .status(CloudPlatformEnum.fromValue(instanceModel.getStatus())).createTime(instanceModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        InstanceConvertModel convertModel = new InstanceConvertModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 获取虚拟机总数
     * @param request
     * @return
     */
    public Integer getTotalInstances(InstancePageRequest request) {
        request.setPerPage(NumberUtils.INTEGER_ONE);
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        InstanceCloudPlatformModel page = vmService.page(getParams);
        return Objects.isNull(page) ||  Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal() ;
    }
}
