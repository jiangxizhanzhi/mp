 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogConstant.java.
 // File size 1386.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.constant;

/**
 * @ClassName LogConstant
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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

}
