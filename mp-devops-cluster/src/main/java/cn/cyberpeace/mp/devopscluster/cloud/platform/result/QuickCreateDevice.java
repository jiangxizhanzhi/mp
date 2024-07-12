 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name QuickCreateDevice.java.
 // File size 491.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.result;

import lombok.Data;

/**
 * @ClassName QuickCreateDevice
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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