 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPlatformListsPageResult.java.
 // File size 560.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.common;

import lombok.Data;

import java.util.List;

/**
 * @ClassName CloudPlatformListsPageResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
public class CloudPlatformListsPageResult<T> extends BaseCloudPlatformResult {

    private CloudPlatformListData<T> data;




    @Data
    public static class CloudPlatformListData<T> {
        private Integer total;
        private List<T> list;
    }
}
