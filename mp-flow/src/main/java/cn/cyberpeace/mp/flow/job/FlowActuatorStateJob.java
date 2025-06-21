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

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.mptool.ConcurrentHashSet;
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
import java.util.Set;

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

    private static final Set<String> ACTUATOR_IN_INIT_PROCESS = new ConcurrentHashSet<>();

    @Scheduled(cron = "0/5 * * * * ?")
    public void queryFlowActuator() {
        HumanCondition condition = new HumanCondition();
        var list = flowActuatorMapper.list(condition);
        for (var m : list) {
            TaskCommon.executeTask(() -> {
                String originCallChainId = SysContextHolder.getCallChainId();
                SysContextHolder.setCallChainId(SysContextHolder.getCallChainId() + "_" + m.getId());
                if (ACTUATOR_IN_INIT_PROCESS.contains(m.getId())) {
                    return;
                }
                ACTUATOR_IN_INIT_PROCESS.add(m.getId());
                log.info("处理执行器:{}",m.getId());
                if (m.getStatus() == 0) {
                    // 流量执行器未处理完成
                    log.info("执行器 {} 未启动完成，继续启动" ,m.getId());
                    updateActuatorAndDeliverTasks(m);
                } else {
                    // 流量执行器已处理完成，仅需添加任务
                    log.info("执行器 {} 已启动完成，查看是否有需要添加的任务" ,m.getId());
                    this.addTaskService.addTasksToActuator(m);
                }
                ACTUATOR_IN_INIT_PROCESS.remove(m.getId());

                SysContextHolder.setCallChainId(originCallChainId);
            });

        }
    }

    private void updateActuatorAndDeliverTasks(FlowActuatorModel m) {
        log.info("开始启动执行器剩余的流程 {} ", m.getId());
        if (StringUtils.isEmpty(m.getResourceId())) {
            log.info("创建虚拟机");
            // 提交创建请求
            ICloudClient cloudClient = BeanCommon.getBean(ICloudClient.class);
            var instance = cloudClient.createVirtualMachine();
            String resourceId = instance.getId();
            log.info("更新执行器数据库信息");
            var dbUpdate = new FlowActuatorModel();
            dbUpdate.setId(m.getId());
            dbUpdate.setResourceId(resourceId);
            dbUpdate.setNetworkId(instance.getNetwork().get(0).getNetworkId());
            dbUpdate.setIp(instance.getNetwork().get(0).getFixedIp());
            flowActuatorMapper.update(dbUpdate);
            m.setResourceId(dbUpdate.getResourceId());
            m.setNetworkId(dbUpdate.getNetworkId());
            m.setIp(dbUpdate.getIp());
        }
        String actuatorIp = m.getIp();
        if (StringUtils.isEmpty(actuatorIp)) {
            // 没有ip先更新ip
            log.info("更新ip");
            try {
                this.updateActuatorIp(m);
            } catch (Exception e) {
                log.error("更新失败", e);
            }
        }
        if (StringUtils.isEmpty(m.getIp())) {
            log.error("更新ip失败 , actuator id is {}", m.getId());
            return;
        }
        log.info("执行器更新ip结束 {} , {} ", m.getId(), m.getIp());

        // 检测ssh协议是否可用
        log.info("检测ssh是否可用");
        ProtocolProbe protocolProbe = new SSHProbe(actuatorIp, 22);
        if (!protocolProbe.probe()) {
            log.info("检测结束 {}  ， ssh还不可用", m.getId());
            return;
        }

        log.info("ssh 可用 ， 更新发生器代码");
        // 更新 发生器代码
        this.updateActuatorCodesAndServer(actuatorIp);

        // 设置流量执行器未启动结束状态
        this.updateFlowActuatorDbStatus(m.getId(), 1);

        log.info("执行器及其服务启动完成 {} ", m.getId());

        // 向执行器添加任务
        log.info("向执行器添加任务 开始");
        this.addTaskService.addTasksToActuator(m);
        log.info("向执行器添加任务 结束");
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

    private void updateFlowActuatorDbStatus(String id, int status) {
        FlowActuatorModel updateModel = new FlowActuatorModel();
        updateModel.setId(id);
        updateModel.setStatus(status);
        flowActuatorMapper.update(updateModel);
    }
}
