package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import lombok.Data;

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

