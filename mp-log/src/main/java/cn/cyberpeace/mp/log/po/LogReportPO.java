 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LogReportPO.java.
 // File size 849.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.po;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_log_report")
public class LogReportPO extends BaseModel {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 节点ip
     */
    @DefaultStr
    private String hostIp;

    /**
     * 日志类型（mp/cr）
     */
    private LogReportTypeEnum type;
}
