 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name IsoDevice.java.
 // File size 1192.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import lombok.Data;

/**
 * @ClassName IsoDevice
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class IsoDevice {
    private String id;
    private String uuid;
    private String createdAt;
    private String updatedAt;
    private Boolean deleted;
    private String deletedAt;
    private String name;
    private String owner;
    private Boolean shared;
    private String filename;
    private String logo;
    private String md5;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private Integer category;
    private Integer secondCategory;
    private Integer l3Category;
    private Integer parentSystemType;
    private Integer systemType;
    private String systemArchitecture;
    private Integer imageStatus;
    private String language;
    private String flavor;
    private Integer netIfCnt;
    private String description;
    private Integer instanceState;
    private String instanceUuid;
    private String extInfo;
    private ExtVm extVm;
    private ExtSystemInfo extSystemInfo;
}

