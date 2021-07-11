package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.devopscluster.cloud.platform.service.SerialPortService;
import cn.cyberpeace.mp.devopscluster.converter.model.SerialPortConvertModel;
import cn.cyberpeace.mp.devopscluster.model.SerialPortCloudPlatform;
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

    
    public List<SerialPortConvertModel> list() {
        SerialPortCloudPlatform model = serialPortService.list();
        if (Objects.isNull(model) || !model.isSuccess()){
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
