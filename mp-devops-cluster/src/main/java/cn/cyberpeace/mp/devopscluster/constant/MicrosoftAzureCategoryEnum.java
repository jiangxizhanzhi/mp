package cn.cyberpeace.mp.devopscluster.constant;

import lombok.Data;

public enum MicrosoftAzureCategoryEnum {
    //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储
    ALL_COMPUTE_MP(2, "All_Compute_MP"),
    ALL_DATABASES_MP(4, "All_Databases_MP"),
    ALL_ANALYTICS_MP(5, "All_Analytics_MP"),
    ALL_SECURITY_MP(6, "All_Security_MP"),
    ALL_STORAGE_MP(7, "All_Storage_MP"),
    ALL_WEBANDMOBILE_MP(8, "All_WebAndMobile_MP");

    private final int code;
    private final String name;


    MicrosoftAzureCategoryEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
