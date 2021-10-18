package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.SerialPortConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.SerialPortConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.SerialPortCloudPlatform;
import cn.cyberpeace.mp.devops.cluster.service.SerialPortService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SerialPortConverterImpl implements SerialPortConverter {

    private SerialPortService serialPortService;

    @Autowired
    public SerialPortConverterImpl(SerialPortService serialPortService) {
        this.serialPortService = serialPortService;
    }

    @Override
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
