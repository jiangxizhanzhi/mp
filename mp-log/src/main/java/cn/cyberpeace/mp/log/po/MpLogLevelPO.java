 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MpLogLevelPO.java.
 // File size 1101.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.log.po;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_log_level")
public class MpLogLevelPO extends BaseModel implements Serializable {

    private static final long serialVersionUID = -4231845356224500806L;

    private String logName;

    private String logLevel;

    public MpLogLevelPO(String id, String logName, String logLevel){
        super.setId(id);
        this.logName = logName;
        this.logLevel = logLevel;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName == null ? null : logName.trim();
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel == null ? null : logLevel.trim();
    }

}