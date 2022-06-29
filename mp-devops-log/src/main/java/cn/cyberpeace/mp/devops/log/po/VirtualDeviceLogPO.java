package cn.cyberpeace.mp.devops.log.po;

import lombok.Data;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/29
 */
@Data
public class VirtualDeviceLogPO {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 虚拟机ID
     */
    private String xnjId;

    /**
     * 虚拟机名称
     */
    private String xnjName;

    /**
     * 01:新增、02：编辑、03：删除、04：下载、05：审核、06：同步、07：启动、08：停止、09：
     */
    private String type;

    /**
     * 执行状态
     */
    private String status;

    /**
     * 内容
     */
    private String content;
}
