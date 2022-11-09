package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ComputeStatusService;
import cn.cyberpeace.mp.devopscluster.converter.bo.ComputeStatusBO;
import cn.cyberpeace.mp.devopscluster.dto.ComputeStatusDTO;
import cn.cyberpeace.mp.devopscluster.model.common.ComputeStatusModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/11/8 15:58
 */
@Service
@RequiredArgsConstructor
public class ComputeStatusConverter {

    @Autowired
    private ComputeStatusService computeStatusService;

    /**
     * 查询computestatus信息
     *
     * @return
     */
    public ComputeStatusBO getComputeStatus(ComputeStatusDTO dto) {
        String getParams = ParamUtils.wrapperGetParamsByObject(dto);
        CloudPlatformResult<ComputeStatusModel> result = computeStatusService.getComputeStatus(getParams);
        ComputeStatusModel model = result.getData();
        return model2bo(model);
    }

    private ComputeStatusBO model2bo(ComputeStatusModel model) {
        if (Objects.isNull(model)) {
            return null;
        }
        ComputeStatusBO bo = new ComputeStatusBO();
        ComputeStatusBO.CpuData cpuData = new ComputeStatusBO.CpuData();
        if (Objects.nonNull(model.getCpuData())) {
            BeanUtils.copyProperties(model.getCpuData(), cpuData);
            bo.setCpuData(cpuData);
        }

        ComputeStatusBO.DiskData diskData = new ComputeStatusBO.DiskData();
        if (Objects.nonNull(model.getDiskData())) {
            BeanUtils.copyProperties(model.getDiskData(), diskData);
            bo.setDiskData(diskData);
        }

        ComputeStatusBO.DiskIo diskIo = new ComputeStatusBO.DiskIo();
        if (Objects.nonNull(model.getDiskIo())) {
            BeanUtils.copyProperties(model.getDiskIo(), diskIo);
            bo.setDiskIo(diskIo);
        }

        ComputeStatusBO.MemoryData memoryData = new ComputeStatusBO.MemoryData();
        if (Objects.nonNull(model.getMemoryData())) {
            BeanUtils.copyProperties(model.getMemoryData(), memoryData);
            bo.setMemoryData(memoryData);
        }

        ComputeStatusBO.NetIo netIo = new ComputeStatusBO.NetIo();
        if (Objects.nonNull(model.getNetIo())) {
            BeanUtils.copyProperties(model.getNetIo(), netIo);
            bo.setNetIo(netIo);
        }

        ComputeStatusBO.AssistStorage assistStorage = new ComputeStatusBO.AssistStorage();
        if (Objects.nonNull(model.getAssistStorage())) {
            BeanUtils.copyProperties(model.getAssistStorage(), assistStorage);
            bo.setAssistStorage(assistStorage);
        }

        return bo;
    }


}
