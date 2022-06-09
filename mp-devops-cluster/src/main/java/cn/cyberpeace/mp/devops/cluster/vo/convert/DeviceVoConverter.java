package cn.cyberpeace.mp.devops.cluster.vo.convert;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.vo.DeviceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DeviceVoConverter extends IConverter<Device, DeviceVO> {

    @Mapping(source = "l3Category",target = "thirdCategory")
    @Override
    DeviceVO sourceToTarget(Device device);
}
