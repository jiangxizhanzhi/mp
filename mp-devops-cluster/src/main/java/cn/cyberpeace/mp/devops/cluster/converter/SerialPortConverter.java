package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.SerialPortService;
import cn.cyberpeace.mp.devops.cluster.converter.model.SerialPortConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.SerialPortCloudPlatform;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SerialPortConverter {

    private final SerialPortService serialPortService;

    /**
     * 查询串口列表
     * @return
     */
    public List<SerialPortConvertModel> list() {
        SerialPortCloudPlatform model = serialPortService.list();
        if (Objects.isNull(model)){
            return Lists.newArrayList();
        }
        return model.getInterfaces().stream().map(serialPort -> {
            SerialPortConvertModel convertModel = new SerialPortConvertModel();
            convertModel.setComAliveT(serialPort.getComAliveTime());
            convertModel.setComLocalP(serialPort.getComLocalIp());
            convertModel.setHost(serialPort.getHost());
            convertModel.setTcpLinkMax(serialPort.getTcpLinkMax());
            convertModel.setInterfaceId(Objects.nonNull(serialPort.getInterfaceId()) ?
                    String.valueOf(serialPort.getInterfaceId()) : "");
            return convertModel;
        }).collect(Collectors.toList());
    }
}
