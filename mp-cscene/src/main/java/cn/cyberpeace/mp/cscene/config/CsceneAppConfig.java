package cn.cyberpeace.mp.cscene.config;

import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

    /**
     * 系统激活相关service
     */
    @Autowired
    private SysActivationService sysActivationService;

    /**
     * 系统配置
     */
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 标准版最大可编辑节点key
     */
    @Value("${mp.cscene.scene-edit-node-limit-key:LCS-RES-CR-EDITNODES}")
    private String sceneEditNodeLimitKey;

    /**
     * 最大并发启动场景节点key
     */
    @Value("${mp.cscene.scene-using-node-limit-key:LCS-RES-CR-ACTIVENODES}")
    private String sceneUsingNodeLimitKey;

    /**
     * TODO: 正确的key
     */
    @Value("${mp.cscene.scene-enable-recording-key:screen_recording}")
    private String sceneEnableRecordingKey;


    /**
     * 最大可编辑节点数
     */
    @Value("${mp.cscene.scene-edit-node-limit:1000}")
    private Integer sceneEditNodeLimit;

    /**
     * 最大并发启动场景节点数
     */
    @Value("${mp.cscene.scene-using-node-limit:1000}")
    private Integer sceneUsingNodeLimit;

    /**
     * 录屏 默认关闭
     */
    @Value("${mp.cscene.scene-enable-recording:false}")
    private Boolean sceneEnableRecording;

    /**
     * mq消息超时时间
     */
    @Value("${mp.cscene.scene-message-timeout:3600000}")
    private Long sceneMessageTimeout;

    /**
     * 独立的录屏配置
     */
    @Value("${mp.cscene.has-own-recording-set:213,214,236}")
    private String hasOwnRecordingSet;

    /**
     * 场景创建超时时间
     */
    @Value("${mp.cscene.scene-error-creating-seconds:3600}")
    private Integer sceneErrorCreatingSeconds;

    /**
     * 网元数据创建超时时间
     */
    @Value("${mp.cscene.element-error-creating-seconds:300}")
    private Integer elementErrorCreatingSeconds;

    /**
     * 服务启动之前场景已启动的时间（超过该时间则认为场景启动异常）
     */
    @Value("${mp.cscene.ignore-scene-creating-seconds:60}")
    private Integer ignoreSceneCreatingSeconds;

    /**
     * 场景相关定时任务开关（主要任务包含数据同步，状态翻转，资源释放等）
     */
    @Value("${mp.cscene.enable-scene-cron-job:true}")
    private Boolean enableSceneCronJob;

    /**
     * 初始化命令是否可变化开关
     */
    @Value("${mp.cscene.enable-command-change:false}")
    private Boolean enableCommandChange;

    /**
     * 定时任务执行时间
     */
    @Value("${mp.cscene.cron-job-expression:0 0/15 * * * ?}")
    private String cronJobExpression;

    /**
     * 场景节点限制检查开关
     */
    @Value("${mp.cscene.enable_node_limit_db_check:true}")
    private Boolean enableNodeLimitDbCheck;

    /**
     * 消息格式化工具类
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * @description: 获取场景最大可编辑节点数
     * @throws
     * @return int
     */
    public int getSceneEditNodeLimit() {
        String value = null;
        try {
            value = sysActivationService.getValueByKey(sceneEditNodeLimitKey);
        } catch (Exception e) {
            log.error("Get config {} error: {}", sceneEditNodeLimitKey, e);
        }
        if (value == null) {
            return sceneEditNodeLimit;
        }

        var limit = IntUtils.fromStr(value);
        if (limit.equals(0)) {
            log.warn("SceneEditNodeLimit({}) parse as 0", value);
        }
        return limit;
    }

    /**
     * @description: 获取最大并发启动场景节点
     * @throws
     * @return int
     */
    public int getSceneUsingNodeLimit() {
        String value = null;
        try {
            value = sysActivationService.getValueByKey(sceneUsingNodeLimitKey);
        } catch (Exception e) {
            log.error("Get config {} error: {}", sceneUsingNodeLimitKey, e);
        }
        if (value == null) {
            return sceneUsingNodeLimit;
        }

        var limit = IntUtils.fromStr(value);
        if (limit.equals(0)) {
            log.warn("SceneUsingNodeLimit({}) parse as 0", value);
        }
        return limit;
    }

    /**
     * @description: 是否开启录屏
     * @throws
     * @return boolean
     */
    public boolean getSceneEnableRecording() {
        var config = sysConfigService.getByKeyName(sceneEnableRecordingKey);
        if (config == null) {
            return sceneEnableRecording;
        }

        var value = config.getValue();
        return BooleanUtils.toBoolean(value);
    }


    /**
     * @description: 独立的录屏配置
     * @throws
     * @return Set<Integer>
     */
    public Set<Integer> getHasOwnRecordingSet() {
        String[] values = this.hasOwnRecordingSet.split(",");
        Set<Integer> set = new HashSet<>();
        Arrays.stream(values).forEach(v -> set.add(Integer.parseInt(v)));
        return set;
    }
}
