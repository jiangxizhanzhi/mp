package cn.cyberpeace.mp.agent.snmp.service;

public interface TaskService<T extends BasicAccessInfo> {

    /**
     * 执行节点配置
     * @return
     */
    Boolean execTask(TaskRequest<T> taskRequest);
}
