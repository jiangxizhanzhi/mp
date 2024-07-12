 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ElasticIpBO.java.
 // File size 655.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ElasticIpBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElasticIpBO {

    /**
     * 固定IP
     */
    private String fixedIpAddress;

    /**
     * 弹性IP
     */
    private String floatIpAddress;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private String createdAt;
}
