 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MpJobInfo.java.
 // File size 452.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName MpJobInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Getter
@Setter
public abstract class MpJobInfo {
    private String jobId;

    private String description;

    public MpJobInfo(String jobId, String description) {
        this.jobId = jobId;
        this.description = description;
    }
}
