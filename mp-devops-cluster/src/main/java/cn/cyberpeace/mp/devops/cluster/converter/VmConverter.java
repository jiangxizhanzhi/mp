package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.VmService;
import cn.cyberpeace.mp.devops.cluster.converter.bo.InstanceBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.InstancePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 虚拟机管理
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class VmConverter implements BaseConverter{

    private final VmService vmService;

    /**
     * 分页查询虚拟机信息
     * @param dto
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public BasePageBO<InstanceBO> page(InstancePageDTO dto, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,ignoreFields);
        CloudPlatformPageResult<InstanceModel> model = vmService.page(getParams);
        List<InstanceBO> list = model.getList().stream().map(instanceModel ->
                InstanceBO.builder().name(instanceModel.getName()).node(instanceModel.getHost())
                        .addressList(instanceModel.getAddresses().stream().map(InstanceModel.Address::getIp)
                                .collect(Collectors.toList()))
                        .status(instanceModel.getStatus()).createTime(instanceModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        BasePageBO<InstanceBO> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 获取虚拟机总数
     * @param dto
     * @return
     */
    public Integer getTotalInstances(InstancePageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        BasePageBO<InstanceBO> page = this.page(dto,this.getTotalPageIgnoreField());
        return page.getTotal() ;
    }
}
