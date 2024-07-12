 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudPlatformItemsResult.java.
 // File size 598.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.log.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CloudPlatformItemsResult
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
public class CloudPlatformItemsResult extends BaseCloudPlatformResult {

    private List<?> items = new ArrayList<>();
}
