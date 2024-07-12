 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudUrlConstant.java.
 // File size 1513.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform;

/**
 * @ClassName CloudUrlConstant
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class CloudUrlConstant {
    public static final String MONITOR_NETWORKS = "/cloud/v1/monitor/networks";
    public static final String MONITOR_COMPUTE_NODES = "/cloud/v1/monitor/compute_nodes";
    public static final String BATCH_DELETE_INSTANCE = "/cloud/v1/monitor/batch_delete";

    public static final String VM_SNAPSHOT_URL = "/cloud/v1/vm/{vm_id}/snapshot";
    public static final String VM_SNAPSHOT_ACTION_URL = "/cloud/v1/vm/{vm_id}/snapshot/{snapshot_id}/{action}";
    public static final String VM_DELETE_SNAPSHOT_URL = "/cloud/v1/vm/{vm_id}/snapshot/{snapshot_id}";

    public static final String CONTAINER_SNAPSHOT_URL = "/cloud/v1/container/{container_id}/snapshot";
    public static final String CONTAINER_SNAPSHOT_ACTION_URL = "/cloud/v1/container/{container_id}/snapshot/{image_id}/{action}";
    public static final String CONTAINER_DELETE_SNAPSHOT_URL = "/cloud/v1/container/{container_id}/snapshot/{image_id}";

    public static final String GET_ISO_DEVICE_BY_UUID = "/cloud/v1/device/iso/{uuid}";

    public static final String QUICK_CREATE_DEVICE = "/cloud/v1/device/quick_create";

    public static final String GET_VM_SNAPSHOT_LIST = "/cloud/v1/vm/{vm_id}/snapshot";
    public static final String GET_CONTAINER_SNAPSHOT_LIST = "/cloud/v1/container/{container_id}/snapshot";
}
