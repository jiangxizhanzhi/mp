package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.VmService;
import cn.cyberpeace.mp.devopscluster.converter.bo.InstanceBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.InstancePageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.InstanceModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class VmConverter implements BaseConverter{

    private final VmService vmService;

    
    public BasePageBO<InstanceBO> page(InstancePageDTO dto, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(dto, ignoreFields);
        CloudPlatformPageResult<InstanceModel> model = vmService.page(getParams);
        List<InstanceBO> list = model.getList().stream().map(instanceModel ->
                InstanceBO.builder().name(instanceModel.getName()).node(instanceModel.getHost())
                        .id(instanceModel.getId())
                        .runningTime(instanceModel.getRunningTime())
                        .addressList(instanceModel.getAddresses().stream().map(InstanceModel.Address::getIp)
                                .collect(Collectors.toList()))
                        .status(instanceModel.getStatus()).createTime(instanceModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        BasePageBO<InstanceBO> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    
    public Integer getTotalInstances(InstancePageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        BasePageBO<InstanceBO> page = this.page(dto,this.getTotalPageIgnoreField());
        return page.getTotal() ;
    }
}
