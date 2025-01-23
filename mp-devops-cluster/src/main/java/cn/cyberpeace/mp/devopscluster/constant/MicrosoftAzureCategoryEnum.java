package cn.cyberpeace.mp.devopscluster.constant;

import lombok.Data;

public enum MicrosoftAzureCategoryEnum {
    //云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储,8:Web)
    ALL_NETWORK_MP(1, "All_Network_MP","net"),
    ALL_COMPUTE_MP(2, "All_Compute_MP","vm"),
    ALL_CONTAINERS_MP(3, "All_Containers_MP","container"),
    ALL_DATABASES_MP(4, "All_Databases_MP","vm"),
    ALL_ANALYTICS_MP(5, "All_Analytics_MP","vm"),
    ALL_SECURITY_MP(6, "All_Security_MP","vm"),
    ALL_STORAGE_MP(7, "All_Storage_MP","vm"),
    ALL_WEBANDMOBILE_MP(8, "All_WebAndMobile_MP","vm"),
    // 混合云/私有云 云服务类型(11:计算,12:数据+分析,13:安全性和标识,14:Web+移动,15:开发人员服务,16:管理,17:介质+CDN,18:数据+分析)
    COMPUTE_MP(11, "Compute_MP","vm"),
    DATA_STORAGE(12, "Data_Storage","vm"),
    SECURITY_IDENTITY(13, "Security_Identity","vm"),
    WEBANDMOBILE_MP(14, "WebAndMobile_MP","vm"),
    DEVELOPER_SERVICES(15, "DeveloperServices","vm"),
    MANAGEMENT(16, "Management","vm"),
    MEDIA_CDN(17, "Media_CDN","vm"),
    DATA_ANALYTICS(18, "Data_Analytics","vm");

    private final int code;
    private final String name;

    private final String type;

    MicrosoftAzureCategoryEnum(int code, String name,String type) {
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    // 根据code获取对应type的方法
    public static String getTypeByCode(int code) {
        for (MicrosoftAzureCategoryEnum category : MicrosoftAzureCategoryEnum.values()) {
            if (category.getCode() == code) {
                return category.getType();
            }
        }
        return null; // 或者抛出异常，根据需求决定
    }

}
