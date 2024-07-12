 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkBO.java.
 // File size 989.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName NetworkBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkBO {

    /**
     * 网络名称
     */
    private String name;

    /**
     * 网络类型
     */
    private String networkType;

    /**
     * 状态
     */
    private String status;

    /**
     * 已连接子网
     */
    private List<NetworkSubnetBO> subnets;

    /**
     * 下行带宽(Kb/s)
     */
    private Long downloadSpeed;

    /**
     * 上行带宽(Kb/s)
     */
    private Long uploadSpeed;

    /**
     * 节点个数
     */
    private Integer nodeNum;

    /**
     * 创建时间
     */
    private String createdAt;
}
