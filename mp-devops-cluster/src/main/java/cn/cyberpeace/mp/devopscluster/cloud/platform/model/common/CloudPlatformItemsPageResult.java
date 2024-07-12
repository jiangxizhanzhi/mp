 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPlatformItemsPageResult.java.
 // File size 849.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName CloudPlatformItemsPageResult
 * @Description 分页
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlatformItemsPageResult<T> extends BaseCloudPlatformResult {

    private CloudPlatformItemData<T> data;

    private List<?> items;

    private Integer total;







    @Data
    public static class CloudPlatformItemData<T> {
        private Integer total;
        private List<T> items;

        private List<T> list;

    }
}