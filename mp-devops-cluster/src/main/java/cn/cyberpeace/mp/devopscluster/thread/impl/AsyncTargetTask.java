package cn.cyberpeace.mp.devopscluster.thread.impl;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.DeviceAsyncBO;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.thread.ITask;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AsyncTargetTask implements ITask<ServiceResult<Integer>, List<Device>> {

    private final TargetConverter targetConverter;

    private final DevopsTargetService devopsTargetService;

    @Transactional
    @Override
    public ServiceResult execute(List<Device> deviceList, Map<String, Object> params) {
        ServiceResult serviceResult = new ServiceResult();
        if (CollectionUtils.isEmpty(deviceList)){
            return serviceResult.asResult();
        }

        List<Integer> deviceIds = deviceList.stream().map(Device::getId).collect(Collectors.toList());

        DeviceAsyncBO bo = targetConverter.getAsyncDeviceIds(deviceIds);
        List<Integer> diffList =  bo.getNotExistedList();

        if (CollectionUtils.isEmpty(diffList)){
            return ResultCommon.success();
        }

        List<Device> asyncDeviceList = deviceList.stream().filter(device -> diffList.contains(device.getId()))
                .collect(Collectors.toList());

        Integer num = devopsTargetService.insert(asyncDeviceList);

        serviceResult.setSource(num);

        return serviceResult.asResult();
    }

}
