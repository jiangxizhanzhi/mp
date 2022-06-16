package cn.cyberpeace.mp.devops.log.po;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.devops.log.enums.LogReportTypeEnum;
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
     * 日志类型（mp/cr）
     */
    private LogReportTypeEnum type;
}
