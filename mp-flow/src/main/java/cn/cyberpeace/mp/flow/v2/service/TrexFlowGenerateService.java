package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.trex.FlowResourceData;
import cn.cyberpeace.mp.flow.v2.client.trex.InitInfoData;
import cn.cyberpeace.mp.flow.v2.client.trex.NetworkInfos;
import cn.cyberpeace.mp.flow.v2.client.trex.StartInfoData;
import cn.cyberpeace.mp.flow.v2.client.trex.TrexClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
@Service
public class TrexFlowGenerateService {
    public ServiceResult<Boolean> startFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        TrexClient trexClient = new TrexClient();
        // 上传flow配置
        List<FlowResourceData> flowResources = new ArrayList<>();
        List<TrexClient.ProtocolInfo> protocolInfos = new ArrayList<>();

//        for (var flow : flowTaskRecordBO.getFlows()) {
//            if (StringUtils.isNotEmpty(flow.getPcapFile())) {
//                FlowResourceData resource = new FlowResourceData();
//                byte[] fileByte = FdfsCommon.downloadRawContent(flow.getPcapFile());
//                resource.setPcapFile(fileByte);
//                resource.setFlowId(flow.getFlowId());
//                flowResources.add(resource);
//            } else {
//                TrexClient.ProtocolInfo protocolInfo = new TrexClient.ProtocolInfo();
//                protocolInfos.add(protocolInfo);
//            }
//        }
        // 初始化trex服务
        NetworkInfos networkInfos = new NetworkInfos();
        InitInfoData initInfo = new InitInfoData();
        initInfo.setMac(flowTaskRecordBO.getTrexMac());
        initInfo.setTaskId(flowTaskRecordBO.getId());
        networkInfos.getNetworkInfos().add(initInfo);
        trexClient.initTrexServer(networkInfos);

        File file = new File("D:\\data\\resource\\udp_flow2.pcap");
        FlowResourceData resource = new FlowResourceData();
        resource.setFileName("udp_flow2.pcap");
        resource.setFlowId("123456789");
        try {
            resource.setFile(readOnce(file));
        } catch (Exception e) {

        }

        FlowResourceData resource1 = new FlowResourceData();
        resource1.setFlowId("987654321");
        resource.setFileName("udp_flow1.pcap");
        try {
            resource1.setFile(readOnce(file));
        } catch (Exception e) {

        }
        flowResources.add(resource);
        flowResources.add(resource1);
        // 上传 pcap
        trexClient.uploadResource(flowResources);

        // 计算负载
        List<TrexClient.LoadInfo> loadInfos = trexClient.calculateLoad(10, 10, "BINOMIAL_DISTRIBUTION");
        System.out.println("负载");
        for (var loadInfo : loadInfos) {
            System.out.println(loadInfo.getBandwidth() + '\n');
        }
        List<TrexClient.LoadInfo> loadInfos1 = trexClient.calculateLoad(10, 10, "");
        System.out.println(loadInfos1);
        for (var loadInfo : loadInfos1) {
            System.out.println(loadInfo.getBandwidth() + '\n');
        }

        //启动流量
        StartInfoData startInfoData = new StartInfoData();
        startInfoData.setId("task111");
        startInfoData.setAction(1);
        startInfoData.setTime(LocalDateTime.now());
        startInfoData.setDuration(10);
        startInfoData.setBandwidth(10);
        startInfoData.setTargetIp("10.100.7.179");
        StartInfoData.Flow flow = new StartInfoData.Flow();
        flow.setFlowId("flowid111");
        flow.setWeight(1);
        flow.setResourceType(0);
        flow.setDelay(10);
        startInfoData.getFlows().add(flow);
        startInfoData.getLoadLists().addAll(loadInfos);

        trexClient.startTask(startInfoData);

        return new ServiceResult<>(true);
    }

    private List<TrexClient.LoadInfo> calLoadInfos() {
        return new ArrayList<>();
    }

    public static byte[] readOnce(File file) throws IOException {
        //check the file is Exists
        checkFileExists(file);
        // check the file is too long, if the file length is too long ,returned. because the byte array can not buffered.
        // byte array bufferSize=file.lenght,and  must between 0 and Integer_MAX_VALUE
        if (file.length() > Integer.MAX_VALUE) {
            System.err.println("file is too big ,not to read !");
            throw new IOException(file.getName() + " is too big ,not to read ");
        }
        int _bufferSize = (int) file.length();
        //定义buffer缓冲区大小
        byte[] buffer = new byte[_bufferSize];
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            int len = 0;
            if ((len = in.available()) <= buffer.length) {
                in.read(buffer, 0, len);
            }
        } finally {
            closeInputStream(in);
        }
        return buffer;
    }

    private static void checkFileExists(File file) throws FileNotFoundException {
        if (file == null || !file.exists()) {
            System.err.println("file is not null or exist !");
            throw new FileNotFoundException(file.getName());
        }
    }

    private static void closeInputStream(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
