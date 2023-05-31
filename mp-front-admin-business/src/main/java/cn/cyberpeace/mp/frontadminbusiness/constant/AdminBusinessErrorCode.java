package cn.cyberpeace.mp.frontadminbusiness.constant;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

/**
 * @Description: 全局错误枚举
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public enum AdminBusinessErrorCode implements ErrorCode {
    SYS_CONFIG_ERROR("MP-120001", "sys_config_error"),
    LOG_FILE_MAX_HISTORY("MP-120002", "log_file_max_history"),
    BACKUP_INTERVAL("MP-120003", "backup_interval");

    AdminBusinessErrorCode(String code, String message) {
        setCode(code, AdminBusinessConstant.ERROR_CODE_PREFIX + message);
    }
}
