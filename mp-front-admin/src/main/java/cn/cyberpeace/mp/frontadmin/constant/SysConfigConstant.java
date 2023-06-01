package cn.cyberpeace.mp.frontadmin.constant;

import cn.cyberpeace.mp.base.message.rabbitmq.SimpleMessageConfig;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public class SysConfigConstant {

    // 新用户注册是否需要审核 key
    public static final String USER_AUDIT_KEY = "user_audit";
    public static final String LOG_FILE_MAX_HISTORY_KEY = "log_file_max_history";
    public static final String SYSTEM_BACKUP_INTERVAL_KEY = "system_backup_interval";

    // 新用户注册是否需要审核 value
    public static final String USER_AUDIT_VALUE_YES = "1";
    public static final String USER_AUDIT_VALUE_NO = "0";


    // 是否默认录屏 key
    public static final String SCREEN_RECORDING_KEY = "screen_recording";

    // 是否默认录屏 value
    public static final String SCREEN_RECORDING_VALUE_YES = "1";
    public static final String SCREEN_RECORDING_VALUE_NO = "0";


    // MQ
    public static final String SYS_CONFIG_EXCHANGE_NAME = SimpleMessageConfig.GROUP_NAME + ".sysconfig";

    public static final String MSG_TYPE_SYS_CONFIG_UPDATE = "sys_config_update";

    public static final String ROUTING_KEY_LOG_FILE_MAX_HISTORY_UPDATE = SYS_CONFIG_EXCHANGE_NAME + "log_max_history.update";

    public static final String ROUTING_KEY_BACKUP_INTERVAL_UPDATE = SYS_CONFIG_EXCHANGE_NAME + "backup_interval.update";

    // cloud日志保存天数变更地址
    public static final String CLOUD_LOG_CONF_URL = "/cpcloud/logger_app/v1/update_log_conf";





}
