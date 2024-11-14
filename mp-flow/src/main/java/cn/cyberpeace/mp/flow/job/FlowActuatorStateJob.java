// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name VirtualMachine.java.
// File size 19310.
// File create date Fri Jul 12 12:55:31 CST 2024.
// File update date Fri Jul 12 12:55:31 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.job;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.protocolprobe.ProtocolProbe;
import cn.cyberpeace.mp.base.util.protocolprobe.SSHProbe;
import cn.cyberpeace.mp.flow.integration.cloud.base.ICloudClient;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.util.SshUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName VirtualMachine
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Service
public class FlowActuatorStateJob {

    @Resource
    private FlowActuatorMapper flowActuatorMapper;

    @Resource
    private ICloudClient cloudClient;

    @Scheduled(cron = "0/5 * * * * ?")
    public void queryFlowActuator() {
        HumanCondition condition = new HumanCondition().filterEqual(FlowActuatorModel::getStatus, 0);
        var list = flowActuatorMapper.list(condition);
        for (var m : list) {
            if (StringUtils.isEmpty(m.getIp())) {
                var res = cloudClient.getVirtualMachine(m.getInstanceId());
                if (res != null) {
                    m.setIp(res.getNetworks().get(0).getFixedIp());
                    m.setInstanceId(res.getId());
                    m.setNetworkId(res.getNetworks().get(0).getNetworkId());
                    flowActuatorMapper.update(m);
                }
            } else {
                String ip = m.getIp();
                ProtocolProbe protocolProbe = new SSHProbe(ip, 22);
                if (protocolProbe.probe()) {
                    // 更新trex代码
                    String commands = "./init.sh";
                    SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
                    serverInfo.setHost(ip);
                    serverInfo.setUser("root");
                    serverInfo.setPassword("111111");
                    String TGN_PATH = "D:\\MJ\\code2024\\30s\\mp\\mp-flow\\src\\main\\resources\\traffic_generators.zip";
                    SshUtil.refreshDir(serverInfo, commands, TGN_PATH, "/opt/project_dirs");
                    m.setStatus(1);
                    flowActuatorMapper.update(m);
                }
            }
        }
    }
}
