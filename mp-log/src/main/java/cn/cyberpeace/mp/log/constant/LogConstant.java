package cn.cyberpeace.mp.log.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogConstant {

    // 用户id
    public static final String USER_ID_KEY = "MP_LOG_UID";
    // 用户登录名
    public static final String USER_NAME_KEY = "MP_LOG_UNAME";
    // 用户类型
    public static final String USER_TYPE_KEY = "MP_LOG_UTYPE";

    public static final String USER_TYPE_USER = "USER";
    public static final String USER_TYPE_CLIENT = "CLIENT";

    public static final String LOG_OP_MODULE_PREFIX = "log.op.module";

    public static final String LOG_OP_TYPE_PREFIX = "log.op.type";

    public static final String LOG_OP_TARGET_PREFIX = "log.op.target";

    /**
     * 后台系统管理-系统管理-审计日志-类型
     */
    public static final String MP_COMMON_OPTION_TYPE_LOG_OP_TYPE = "op_type";
    /**
     * 后台系统管理-系统管理-审计日志-模块
     */
    public static final String MP_COMMON_OPTION_TYPE_LOG_OP_MODULE = "op_module";


    public static final String MP_COMMON_OPTION_TYPE_RUNTIME_LOG_TYPE = "runtime_log_type";

    /**
     * 日志存到es
     */
    public static final String LOG_DB_ES = "es";

    /**
     * 日志存到mysql
     */
    public static final String LOG_DB_SQL = "sql";

    public static final String EXCEL_EXPORT_NAME = "log.op.target.excel_name";

    /**
     * 审计日志模块
     */
    public static final String AUDIO_LOG= "audio-log";
    /**
     * 审计日志模块Code
     */
    public static final List<Integer> AUDIO_LOG_CODES = new ArrayList<>(Arrays.asList(0,4));

    /**
     * 操作日志模块
     */
    public static final String OPERATE_LOG= "operate-log";
    /**
     * 操作日志模块Code
     */
    public static final List<Integer> OPERATE_LOG_CODES = new ArrayList<>(Arrays.asList(1,13,14,15));

    public static final List<Integer> SCENE_TYPE_CODES = new ArrayList<>(Arrays.asList(236, 310));

}
