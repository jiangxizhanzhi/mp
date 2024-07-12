 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseCloudPlatformConstant.java.
 // File size 1725.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.constants;

/**
 * @ClassName BaseCloudPlatformConstant
 * @Description 基础对接云平台接口
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public final class BaseCloudPlatformConstant {

    /**
     * BaseCloudPlatformConstant
     */
    private BaseCloudPlatformConstant(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * cpcloud平台URL前缀
     */
    public static final String CPCLOUD_URL = "/cloud";

    /**
     * cpcloud平台info模块URL前缀
     */
    public static final String CPCLOUD_INFO_URL = CPCLOUD_URL + "/info";

    /**
     * cpcloud平台device
     */
    public static final String CPCLOUD_DEVICE = CPCLOUD_URL + "/device";

    /**
     * cpcloud平台ccm
     */
    public static final String CPCLOUD_CCM = CPCLOUD_URL + "/ccm";

    /**
     * cpcloud平台Switch模块URL前缀
     */
    public static final String CPCLOUD_SWITCH_URL = CPCLOUD_URL + "/switch";

    /**
     * cpcloud平台流控模块URL前缀
     */
    public static final String CPCLOUD_OPENFLOW = CPCLOUD_URL + "/sdn_app";

    /**
     * 版本
     */
    public static final String URL_VERSION = "/v1";

    /**
     * 截屏模块URL前缀
     */
    public static final String SCREENRECORD_METHOD = CPCLOUD_URL + "/guacamole" + URL_VERSION;

    /**
     * 成功状态码
     */
    public static final String CODE = "0";



    public static final String GET_VM_SNAPSHOT_LIST = "/cloud/v1/vm/{vm_id}/snapshot";
    public static final String GET_CONTAINER_SNAPSHOT_LIST = "/cloud/v1/container/{container_id}/snapshot";



}
