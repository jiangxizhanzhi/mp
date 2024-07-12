 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CpNodeMonitorScriptConfigModel.java.
 // File size 1535.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CpNodeMonitorScriptConfigModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_cpnode_monitor_script_config")
public class CpNodeMonitorScriptConfigModel extends BaseModel {

    // 安装成功
    public static final Integer STATUS_INSTALLED = 1;

    // 待安装
    public static final Integer STATUS_TO_INSTALL = 2;

    // 安装失败
    public static final Integer STATUS_FAILED = 3;

    /**
     * 探针名称
     */
    private String agentName;

    /**
     * 主机名称
     */
    private String hostname;

    /**
     * IP地址
     */
    @DefaultStr
    private String ip;

    /**
     * 1:已完成安装 2:已发送指令-待确认安装成功 3:安装失败
     */
    private Integer status;

    /**
     * 脚本配置JSON字符串，包括launchid, interval，url
     */
    private String config;

    private static final long serialVersionUID = 1L;


    @Data
    public static class ParamConfig {

        private String id;

        private String scriptUrl;

        private String interval;
    }

}
