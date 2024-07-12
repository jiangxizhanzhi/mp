 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HypervisorServiceStateConvertModel.java.
 // File size 754.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import lombok.Data;

/**
 * @ClassName HypervisorServiceStateConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class HypervisorServiceStateConvertModel {

    /**
     * 服务
     */
    private String name;

    /**
     * 是否存活
     */
    private Boolean alive;

    /**
     * 状态
     */
    private String state;

    public HypervisorServiceStateConvertModel(String name, Boolean alive) {
        this.name = name;
        this.alive = alive;
    }

    public HypervisorServiceStateConvertModel(String name, String state) {
        this.name = name;
        this.state = state;
    }
}
