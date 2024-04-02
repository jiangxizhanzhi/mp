package cn.cyberpeace.mp.base.job.init;

import cn.cyberpeace.mp.base.job.MethodJobWrapper;
import cn.cyberpeace.mp.base.job.MpJobInfo;
import lombok.Getter;
import lombok.Setter;

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
