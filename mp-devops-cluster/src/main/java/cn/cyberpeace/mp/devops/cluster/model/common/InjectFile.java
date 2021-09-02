package cn.cyberpeace.mp.devops.cluster.model.common;

import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;

/**
 * 文件注入
 */
public class InjectFile extends BaseCloudPlatformModel {

    private String injectUuid;

    private String name;

    private String destPath;

    private Integer size;

    private String status;
}
