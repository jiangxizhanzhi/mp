 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InstanceOsServiceModel.java.
 // File size 923.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName InstanceOsServiceModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceOsServiceModel implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * binary
     */
    private String binary;

    /**
     * host
     */
    private String host;

    /**
     * zone
     */
    private String zone;

    /**
     * status
     */
    private String status;

    /**
     * state
     */
    private String state;

    /**
     *
     */
    private String disabledReason;

    /**
     *
     */
    private String updatedAt;
}
