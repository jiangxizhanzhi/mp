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
import cn.cyberpeace.mp.base.util.mptool.ExceptionUtil;
import cn.cyberpeace.mp.base.util.protocolprobe.ProtocolProbe;
import cn.cyberpeace.mp.base.util.protocolprobe.SSHProbe;
import cn.cyberpeace.mp.flow.integration.addtask.AddTaskByLocalFlowFacade;
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

    @Resource
    private AddTaskByLocalFlowFacade addTaskService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void queryFlowActuator() {
        HumanCondition condition = new HumanCondition().filterEqual(FlowActuatorModel::getStatus, 0);
        var list = flowActuatorMapper.list(condition);
        for (var m : list) {
            log.info("开始启动执行器剩余的流程 {} ", m.getId());
            String actuatorIp = m.getIp();
            if (StringUtils.isEmpty(actuatorIp)) {
                // 没有ip先更新ip
                log.info("更新ip");
                try {
                    this.updateActuatorIp(m);
                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                }
            }
            if (StringUtils.isEmpty(m.getIp())) {
                log.error("更新ip失败 , actuator id is {}");
                continue;
            }
            log.info("执行器更新ip结束 {} , {} ", m.getId(), m.getIp());

            // 检测ssh协议是否可用
            log.info("检测ssh是否可用");
            ProtocolProbe protocolProbe = new SSHProbe(actuatorIp, 22);
            if (!protocolProbe.probe()) {
                log.info("检测结束 {}  ， ssh还不可用", m.getId());
                continue;
            }

            log.info("ssh 可用 ， 更新发生器代码");
            // 更新 发生器代码
            this.updateActuatorCodesAndServer(actuatorIp);
            // 向执行器添加任务
            this.addTaskService.addTasksToActuator(m);

            log.info("执行器及其服务启动完成 {} ", m.getId());
        }
    }

    private void updateActuatorIp(FlowActuatorModel m) {
        var res = cloudClient.getVirtualMachine(m.getResourceId());
        if (res != null) {
            m.setIp(res.getNetwork().get(0).getFixedIp());
            m.setResourceId(res.getId());
            m.setNetworkId(res.getNetwork().get(0).getNetworkId());
            flowActuatorMapper.update(m);
        }
    }

    private void updateActuatorCodesAndServer(String actuatorIp) {
        // 更新trex代码和服务
        String commands = "./init.sh";
        SshUtil.ServerInfo serverInfo = new SshUtil.ServerInfo();
        serverInfo.setHost(actuatorIp);
        serverInfo.setUser("root");
        serverInfo.setPassword("111111");

        SshUtil.refreshDir(serverInfo, commands, "traffic_generators.zip", "/opt/project_dirs");
    }
}
