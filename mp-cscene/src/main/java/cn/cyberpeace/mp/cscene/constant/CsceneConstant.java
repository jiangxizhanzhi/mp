package cn.cyberpeace.mp.cscene.constant;

import java.time.format.DateTimeFormatter;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
public class CsceneConstant {
    /**
     * 后台系统管理-系统运维-靶场实例监控-状态
     */
    public static final String MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_STATUS = "cscene_instance_status";
    /**
     * 后台系统管理-系统运维-靶场实例监控-类型
     */
    public static final String MP_COMMON_OPTION_TYPE_CSCENE_INSTANCE_TYPE = "cscene_instance_type";

    public static final DateTimeFormatter M_D_HH_MM_SS_DATE_FORMAT = DateTimeFormatter.ofPattern("M-d HH:mm:ss");
    public static final DateTimeFormatter INTERNATIONAL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

}
