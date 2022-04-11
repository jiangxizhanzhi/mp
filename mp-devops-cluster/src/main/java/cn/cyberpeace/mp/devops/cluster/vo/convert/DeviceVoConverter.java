package cn.cyberpeace.mp.devops.cluster.vo.convert;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.vo.DeviceVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceVoConverter {

    @Mapping(source = "l3Category",target = "thirdCategory")
    DeviceVO deviceToDeviceVO(Device device);

    /**
     * Device集合对象转换为DeviceVO集合对象
     * @param deviceList
     * @return
     */
    List<DeviceVO> devicesToDeviceVOs(List<Device> deviceList);
}
