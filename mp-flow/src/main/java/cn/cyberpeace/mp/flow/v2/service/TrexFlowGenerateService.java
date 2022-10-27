package cn.cyberpeace.mp.flow.v2.service;

import cn.cyberpeace.mp.base.common.FdfsCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.v2.bo.FlowGeneratorBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import cn.cyberpeace.mp.flow.v2.client.cloud.VirtualMachine;
import cn.cyberpeace.mp.flow.v2.client.trex.FlowResourceData;
import cn.cyberpeace.mp.flow.v2.client.trex.InitInfoData;
import cn.cyberpeace.mp.flow.v2.client.trex.NetworkInfos;
import cn.cyberpeace.mp.flow.v2.client.trex.StartInfoData;
import cn.cyberpeace.mp.flow.v2.client.trex.TrexClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
@Service
public class TrexFlowGenerateService {
    public FlowCallBackTask startFlowTask(FlowTaskRecordBO flowTaskRecordBO, FlowGeneratorBO generatorBO) {
        TrexClient trexClient = new TrexClient(generatorBO.getServer());
        // 上传flow配置
        List<FlowResourceData> flowResources = new ArrayList<>();
        List<TrexClient.ProtocolInfo> protocolInfos = new ArrayList<>();
        //todo：真的有fdfs地址之后再用下面注掉的代码
        for (var flow : flowTaskRecordBO.getFlows()) {
            if (StringUtils.isNotEmpty(flow.getPcapFile())) {
                FlowResourceData resource = new FlowResourceData();
                byte[] fileByte = FdfsCommon.downloadRawContent(flow.getPcapFile());
                resource.setFile(fileByte);
                resource.setFlowId(flow.getFlowId());
                resource.setFileName(flow.getName());
                flowResources.add(resource);
            } else {
                TrexClient.ProtocolInfo protocolInfo = new TrexClient.ProtocolInfo();
                protocolInfos.add(protocolInfo);
            }
        }
        // 创建网卡
        VirtualMachine virtualMachine = new VirtualMachine();

        //flowTaskRecordBO.getTargetNetworkId()
//        var addPortResult = virtualMachine.addPort(generatorBO.getResourceId(), "0bc3cb2f-4812-41b3-bcaa-744e05b12ae8");

        // 初始化trex服务
        NetworkInfos networkInfos = new NetworkInfos();
        InitInfoData initInfo = new InitInfoData();
        initInfo.setMac("fa:16:3e:af:4b:04");//addPortResult.getMacAddress()
        initInfo.setTaskId(flowTaskRecordBO.getId());
        networkInfos.getNetworkInfos().add(initInfo);
        trexClient.initTrexServer(networkInfos);

        // 测试用的
//        File file = new File("D:\\data\\resource\\pcap\\qq音乐.pcap");
//        File file1 = new File("D:\\data\\resource\\pcap\\traffic.pcap");
//        FlowResourceData resource = new FlowResourceData();
//        resource.setFileName("qq音乐.pcap");
//        resource.setFlowId("8639vomdrgw4ibnf05es");
//        try {
//            resource.setFile(readOnce(file));
//        } catch (Exception e) {
//
//        }
//
//        FlowResourceData resource1 = new FlowResourceData();
//        resource1.setFlowId("1oizj8u79xch5bqgp64l");
//        resource1.setFileName("traffic.pcap");
//        try {
//            resource1.setFile(readOnce(file1));
//        } catch (Exception e) {
//
//        }
//        flowResources.add(resource);
//        flowResources.add(resource1);
        // 上传 pcap
//        trexClient.uploadResource(flowResources);

        // 计算负载
        List<TrexClient.LoadInfo> loadInfos = trexClient.calculateLoad(50, 10, flowTaskRecordBO.getLoadMode());
        System.out.println("负载");
        for (var loadInfo : loadInfos) {
            System.out.println(loadInfo.getBandwidth());
        }

        // 启动流量
        StartInfoData startInfoData = new StartInfoData();
        startInfoData.setId(flowTaskRecordBO.getId());
        startInfoData.setAction(1);
        startInfoData.setTime(LocalDateTime.now());
        // todo:持续时间是否可以指定
        startInfoData.setDuration(50);
        startInfoData.setBandwidth(10);//flowTaskRecordBO.getBandwidth()
        startInfoData.setTargetIp(flowTaskRecordBO.getTargetIp());

        // 测试用的
//        StartInfoData.Flow flow = new StartInfoData.Flow();
//        flow.setFlowId("flowid111");
//        flow.setWeight(1);
//        flow.setResourceType(0);
//        flow.setDelay(10);
//        startInfoData.getFlows().add(flow);

        var flows = flowTaskRecordBO.getFlows().stream().map(f -> {
            StartInfoData.Flow taskFlow = new StartInfoData.Flow();
            taskFlow.setFlowId(f.getFlowId());
            taskFlow.setWeight(f.getWeight());
            taskFlow.setResourceType(f.getResourceType());;
            taskFlow.setDelay(f.getDelay());
            return taskFlow;
        }).collect(Collectors.toList());
        startInfoData.getFlows().addAll(flows);

        startInfoData.getLoadLists().addAll(loadInfos);

//        trexClient.startTask(startInfoData);

        return new TrexFlowCallBackTask(flowTaskRecordBO, generatorBO);
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
