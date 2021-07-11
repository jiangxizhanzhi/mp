package cn.cyberpeace.mp.log.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_devops_log_operation")
public class OperateLogSqlModel extends BaseModel {

    
    @DefaultInt
    private Integer siteType;

    
    @DefaultStr
    private String from;

    
    @DefaultInt
    private Integer logType;

    
    @DefaultStr
    private String opModule;

    
    @DefaultInt(-1)
    private Integer opModuleId;

    
    @DefaultStr
    private String opType;

    
    @DefaultInt(-1)
    private Integer opTypeId;

    
    @DefaultStr
    private String opTarget;

    
    @DefaultInt(-1)
    private Integer opTargetId;

    
    @DefaultStr
    private String title;

    
    @DefaultStr
    private String userId;

    
    @DefaultStr
    private String userName;

    
    @DefaultStr
    private String userType;

    
    @DefaultStr
    private String content;

    
    @DefaultStr
    private String description;

    
    private LocalDateTime opTime = LocalDateTime.now();

    
    @DefaultStr
    private String opIp;

}
