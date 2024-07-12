 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SessionSourceModel.java.
 // File size 1784.
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
public class SessionSourceModel {

    /**
     * 物理索引
     */
    private String index;

    private List<String> protocol;

    /**
     * 来源
     */
    private SessionSource source;

    /**
     * 目的地
     */
    private SessionDestination destination;

    /**
     * 网络
     */
    private SessionNetwork network;

    /**
     * suricata信息
     */
    private SessionSuricata suricata;

    /**
     * 创建时间
     */
    private Long timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class SessionSource{

        /**
         * IP
         */
        private String ip;

        /**
         * 端口
         */
        private Integer port;

        /**
         * MAC地址
         */
        private List<String> mac;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class SessionDestination{

        /**
         * IP
         */
        private String ip;

        /**
         * 端口
         */
        private Integer port;

        /**
         * MAC地址
         */
        private List<String> mac;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class SessionNetwork{

        private Long bytes;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class SessionSuricata{

        private List<String> category;

    }
}
