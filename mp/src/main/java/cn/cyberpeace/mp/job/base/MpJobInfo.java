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
