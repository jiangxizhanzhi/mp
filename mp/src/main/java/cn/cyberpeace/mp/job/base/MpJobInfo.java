package cn.cyberpeace.mp.job.base;

import lombok.Getter;
import lombok.Setter;

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
