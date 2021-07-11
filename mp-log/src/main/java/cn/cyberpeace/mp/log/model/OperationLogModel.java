package cn.cyberpeace.mp.log.model;

import cn.cyberpeace.mp.log.enums.SiteTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogModel {

    
    private SiteTypeEnums siteType = SiteTypeEnums.MP;

    
    private String from;

    
    private Integer logType;

    
    private String opModule;

    
    private Integer opModuleID;

    
    private String opType;

    
    private Integer opTypeID;

    
    private String opTarget;

    
    private Integer opTargetID;

    
    private String title;

    
    private String userId;

    
    private String userName;

    
    private String userType;

    
    private String content;

    
    private String description;

    
    private LocalDateTime opTime = LocalDateTime.now();

    
    private String opIp;

}
