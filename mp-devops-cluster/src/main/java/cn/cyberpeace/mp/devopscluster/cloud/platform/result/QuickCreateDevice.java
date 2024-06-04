package cn.cyberpeace.mp.devopscluster.cloud.platform.result;

import lombok.Data;

@Data
public class QuickCreateDevice {
    private String name;
    private String description;
    private String logo;
    private String imageType;
    private String category;
    private String secondCategory;
    private String l3Category;
    private String flavor;
}