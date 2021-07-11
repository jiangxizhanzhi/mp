package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.devopscluster.converter.bo.InstanceBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.InstancePageDTO;
import cn.cyberpeace.mp.devopscluster.request.DynamipsInstancePageRequest;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDynamipsListParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DynamipsConverter implements BaseConverter {

    
    public BasePageBO<InstanceBO> page(DynamipsInstancePageRequest request) {
        VirtualDynamips virtualDynamips = new VirtualDynamips();
        VirtualDynamipsListParam param = new VirtualDynamipsListParam();
        param.setIp(request.getAddr());
        param.setLimit(request.getLimit());
        param.setOffset(request.getOffset());
        param.setSortKey(request.getOrder());
        param.setSortDir(request.getSort());
        var result = virtualDynamips.list(param);
        if (result == null || CollectionUtils.isEmpty(result.getItems())) {
            BasePageBO<InstanceBO> convertModel = new BasePageBO<>();
            convertModel.setList(new ArrayList<>());
            convertModel.setTotal(result.getTotal());
            return convertModel;
        }
        List<InstanceBO> list = result.getItems().stream().map(v -> {
            InstanceBO instanceBO = new InstanceBO();
            instanceBO.setId(v.getInstanceId() == null ? StringUtils.EMPTY : v.getInstanceId());
            instanceBO.setName(v.getName());
            instanceBO.setCreateTime(v.getCreatedAt());
            var createTime = TimeUtils.stringToLocalDateTime(v.getCreatedAt(), TimeUtils.tzDateTimeFormatter);
            var utcCurrentTimeStr = TimeUtils.localDateTimeToUtcString(LocalDateTime.now());
            var utcCurrentTime = TimeUtils.stringToLocalDateTime(utcCurrentTimeStr, TimeUtils.tzDateTimeFormatter);
            instanceBO.setRunningTime(utcCurrentTime.toEpochSecond(ZoneOffset.UTC) - createTime.toEpochSecond(ZoneOffset.UTC));
            instanceBO.setStatus(v.getStatus());
            instanceBO.setNode(v.getHost());
            if (CollectionUtils.isEmpty(v.getNetworks())) {
                instanceBO.setAddressList(new ArrayList<>());
            } else {
                var ips = v.getNetworks().stream().map(n -> n.getFixedIp()).collect(Collectors.toList());
                instanceBO.setAddressList(ips);
            }
            return instanceBO;
        }).collect(Collectors.toList());

        BasePageBO<InstanceBO> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setTotal(result.getTotal());
        return convertModel;
    }

    
    public Integer getTotalInstances(InstancePageDTO dto) {
        return 0;
    }
}
