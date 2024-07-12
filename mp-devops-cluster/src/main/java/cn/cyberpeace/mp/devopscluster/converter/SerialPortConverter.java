 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SerialPortConverter.java.
 // File size 2169.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.devopscluster.cloud.platform.service.SerialPortService;
import cn.cyberpeace.mp.devopscluster.converter.model.SerialPortConvertModel;
import cn.cyberpeace.mp.devopscluster.model.SerialPortCloudPlatform;
import cn.cyberpeace.mp.devopscluster.model.common.SerialPort;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName SerialPortConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
        if (Objects.isNull(model) || !model.isSuccess()){
            return Lists.newArrayList();
        }
        List<SerialPortConvertModel> list = new ArrayList<>();
        for (SerialPort serialPort : model.getInterfaces()) {
            SerialPortConvertModel serialPortConvertModel = getSerialPortConvertModel(serialPort);
            list.add(serialPortConvertModel);
        }
        return list;
    }

    /**
     * getSerialPortConvertModel
     *
     * @param serialPort
     * @return
     */
    private static SerialPortConvertModel getSerialPortConvertModel(SerialPort serialPort) {
        SerialPortConvertModel convertModel = new SerialPortConvertModel();
        convertModel.setComAliveT(serialPort.getComAliveTime());
        convertModel.setComLocalP(serialPort.getComLocalIp());
        convertModel.setHost(serialPort.getHost());
        convertModel.setTcpLinkMax(serialPort.getTcpLinkMax());
        convertModel.setInterfaceId(Objects.nonNull(serialPort.getInterfaceId()) ?
                String.valueOf(serialPort.getInterfaceId()) : "");
        return convertModel;
    }
}
