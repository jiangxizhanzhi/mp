 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CsceneAppConfig.java.
 // File size 5325.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.config;

import cn.cyberpeace.mp.base.model.SysConfigModel;
import cn.cyberpeace.mp.base.service.SysConfigService;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName CsceneAppConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.cscene")
@Data
public class CsceneAppConfig {

    /**
     * 云平台适配层接口地址和端口
     */
    private String cpIp;
    private String cpPort;


    @Autowired
    private SysConfigService sysConfigService;

    @Value("${mp.cscene.scene-edit-node-limit-key:LCS-RES-CR-EDITNODES}")
    private String sceneEditNodeLimitKey;

    @Value("${mp.cscene.scene-using-node-limit-key:LCS-RES-CR-ACTIVENODES}")
    private String sceneUsingNodeLimitKey;

    /**
     * TODO: 正确的key
     */
    @Value("${mp.cscene.scene-enable-recording-key:screen_recording}")
    private String sceneEnableRecordingKey;

    @Value("${mp.cscene.scene-edit-node-limit:1000}")
    private Integer sceneEditNodeLimit;

    @Value("${mp.cscene.scene-using-node-limit:1000}")
    private Integer sceneUsingNodeLimit;

    @Value("${mp.cscene.scene-enable-recording:false}")
    private Boolean sceneEnableRecording;

    @Value("${mp.cscene.scene-message-timeout:3600000}")
    private Long sceneMessageTimeout;

    @Value("${mp.cscene.has-own-recording-set:213,214,236}")
    private String hasOwnRecordingSet;

    @Value("${mp.cscene.scene-error-creating-seconds:3600}")
    private Integer sceneErrorCreatingSeconds;

    @Value("${mp.cscene.element-error-creating-seconds:300}")
    private Integer elementErrorCreatingSeconds;

    @Value("${mp.cscene.ignore-scene-creating-seconds:60}")
    private Integer ignoreSceneCreatingSeconds;

    @Value("${mp.cscene.enable-scene-cron-job:true}")
    private Boolean enableSceneCronJob;

    @Value("${mp.cscene.enable-command-change:false}")
    private Boolean enableCommandChange;

    @Value("${mp.cscene.cron-job-expression:0 0/15 * * * ?}")
    private String cronJobExpression;

    @Value("${mp.cscene.enable_node_limit_db_check:true}")
    private Boolean enableNodeLimitDbCheck;

    @Value("${mp.cscene.default-container-cpu-count:1}")
    private int defaultContainerCpuCount;

    @Value("${mp.cscene.default-container-ram-count:512}")
    private int defaultContainerRamCount;

    @Value("${mp.cscene.default-container-disk-count:10}")
    private int defaultContainerDiskCount;


    @Autowired
    private MessageSource messageSource;

    /**
     * getSceneEditNodeLimit
     *
     * @return int
     */
    public int getSceneEditNodeLimit() {
        return 10000;
    }

    /**
     * getSceneUsingNodeLimit
     *
     * @return int
     */
    public int getSceneUsingNodeLimit() {
        return 10000;
    }

    /**
     * getSceneEnableRecording
     *
     * @return boolean
     */
    public boolean getSceneEnableRecording() {
        return true;
    }

    /**
     * getHasOwnRecordingSet
     *
     * @return
     */
    public Set<Integer> getHasOwnRecordingSet() {
        String[] values = this.hasOwnRecordingSet.split(",");
        Set<Integer> set = new HashSet<>();
        for (String v : values) {
            set.add(Integer.parseInt(v));
        }
        return set;
    }
}
