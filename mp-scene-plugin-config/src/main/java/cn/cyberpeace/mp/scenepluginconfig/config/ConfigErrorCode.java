package cn.cyberpeace.mp.scenepluginconfig.config;

import cn.cyberpeace.mp.base.service.code.ErrorCode;

public enum ConfigErrorCode implements ErrorCode {
    CREATE_CONFIG_AGENT_ERROR("MP-110900", "create_config_agent_error");

    ConfigErrorCode(String code, String message) {
        setCode(code, "scenepluginconfig.error_code." + message);
    }
}
