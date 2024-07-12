 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MpInitJobInfo.java.
 // File size 826.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.base.init;

import cn.cyberpeace.mp.job.base.MethodJobWrapper;
import cn.cyberpeace.mp.job.base.MpJobInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName MpInitJobInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see MpJobInfo
 */
@Getter
@Setter
public class MpInitJobInfo extends MpJobInfo {

    private int order;

    private boolean ignoreCurrentError;

    private MethodJobWrapper methodJobWrapper;

    public MpInitJobInfo(String jobId, String description, int order, boolean ignoreCurrentError, MethodJobWrapper methodJobWrapper) {
        super(jobId, description);
        this.order = order;
        this.ignoreCurrentError = ignoreCurrentError;
        this.methodJobWrapper = methodJobWrapper;
    }
}
