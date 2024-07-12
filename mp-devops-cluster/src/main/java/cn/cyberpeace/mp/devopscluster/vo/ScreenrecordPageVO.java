 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScreenrecordPageVO.java.
 // File size 1571.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ScreenrecordPageVO
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenrecordPageVO extends BaseCloudPlatformResult {

    private List<Record> record;

    private Integer totalnum;

    private Integer totalpage;

    private String requestId;

    @Data
    public static final class Record implements Serializable {

        /**
         * 用户名
         */
        private String username;

        /**
         * 组织
         */
        private String department;

        /**
         * 场景
         */
        private String scene;

        /**
         * 录屏文件名
         */
        private String recordingFile;

        /**
         * 截屏文件大小，单位:字节(B)
         */
        private Long fileSize;

        /**
         * 录屏时间
         */
        private String recordate;

        /**
         * 录屏时长
         */
        private Double recortime;

        /**
         * 节点名称
         */
        private String host;

        /**
         * 节点id
         */
        private String hostId;
    }

}
