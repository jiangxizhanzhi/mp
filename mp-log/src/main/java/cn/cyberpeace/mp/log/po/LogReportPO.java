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

    
    private String fileName;

    
    private Long fileSize;

    
    private String downloadUrl;

    
    @DefaultStr
    private String hostIp;

    
    private LogReportTypeEnum type;
}
