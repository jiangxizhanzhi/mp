package cn.cyberpeace.mp.frontadminbusiness.constant;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/9 19:38
 */
public class AdminBusinessConstant {
    private AdminBusinessConstant() {
    }

    public static final String ERROR_CODE_PREFIX = "admin.business.error_code.";

    public static final String[] UNAUTH_SYS_INFO_KEYS = {"system_name", "system_lang", "copyright", "public_register"};

    /**
     * 前端常用的系统配置key_name
     */
    public static final List<String> COMMON_SYS_CONFIG_KEYS = List.of("copyright", "flag_length", "logo", "public_register",
            "system_lang", "system_name", "system_theme", "system_type", "user_audit");
}
