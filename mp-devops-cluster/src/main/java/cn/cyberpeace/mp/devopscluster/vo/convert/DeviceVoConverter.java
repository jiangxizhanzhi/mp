package cn.cyberpeace.mp.devopscluster.vo.convert;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.vo.DeviceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DeviceVoConverter extends IConverter<Device, DeviceVO> {

    @Mapping(source = "l3Category",target = "thirdCategory")
    @Override
    DeviceVO sourceToTarget(Device device);
}
