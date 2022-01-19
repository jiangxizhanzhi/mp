package cn.cyberpeace.mp.devops.cluster.thread.impl;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devops.cluster.thread.ITask;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 同步靶标任务类
 */
@Service
public class AsyncTargetTask implements ITask<ServiceResult<Integer>, List<Device>> {

    private TargetConverter targetConverter;

    private DevopsTargetService devopsTargetService;

    @Autowired
    public AsyncTargetTask(TargetConverter targetConverter, DevopsTargetService devopsTargetService) {
        this.targetConverter = targetConverter;
        this.devopsTargetService = devopsTargetService;
    }

    @Transactional
    @Override
    public ServiceResult execute(List<Device> deviceList, Map<String, Object> params) {
        ServiceResult serviceResult = new ServiceResult();
        if (CollectionUtils.isEmpty(deviceList)){
            return serviceResult.asResult();
        }

        List<Integer> deviceIds = deviceList.stream().map(Device::getId).collect(Collectors.toList());

        List<Integer> diffList = targetConverter.getAsyncDeviceIds(deviceIds);

        if (CollectionUtils.isEmpty(diffList)){
            return ServiceResult.successful();
        }

        List<Device> asyncDeviceList = deviceList.stream().filter(device -> diffList.contains(device.getId()))
                .collect(Collectors.toList());

        Integer num = devopsTargetService.insert(asyncDeviceList);

        serviceResult.setSource(num);

        return serviceResult.asResult();
    }

}
