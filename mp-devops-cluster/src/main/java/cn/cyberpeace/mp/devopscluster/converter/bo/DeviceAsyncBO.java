 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DeviceAsyncBO.java.
 // File size 576.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.bo;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName DeviceAsyncBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class DeviceAsyncBO {

    /**
     * 不存在ID集合
     */
    private List<Integer> notExistedList = Lists.newArrayList();

    /**
     * 已存在ID集合
     */
    private List<Integer> existedList = Lists.newArrayList();
}
