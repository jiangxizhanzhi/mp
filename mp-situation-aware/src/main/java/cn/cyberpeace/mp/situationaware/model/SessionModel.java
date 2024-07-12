 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SessionModel.java.
 // File size 639.
 // File create date Fri Jul 12 12:55:21 CST 2024.
 // File update date Fri Jul 12 12:55:21 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * es真是的bean
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionModel {

    /**
     * 物理索引
     */
    private String index;

    private String srcIp;

    private List<String> srcMac;

    private String dstIp;

    private List<String> dstMac;

    private List<String> protocol;

    private Integer srcPort;

    private Integer dstPort;

    private Long totBytes;

    private Long timestamp;
}
