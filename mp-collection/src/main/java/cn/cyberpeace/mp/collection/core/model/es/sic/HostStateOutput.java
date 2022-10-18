package cn.cyberpeace.mp.collection.core.model.es.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/3/25 10:41
 */
@Data
public class HostStateOutput {

    @JSONField(name = "MemoryFree")
    private String memoryFree;

    @JSONField(name = "CPUCount")
    private String cpuCount;

    @JSONField(name = "CPUFreq")
    private String cpuFreq;

    @JSONField(name = "DiskTotal")
    private String diskTotal;

    @JSONField(name = "MemoryUsedPercent")
    private String memoryUsedPercent;

    @JSONField(name = "DiskUsed")
    private String diskUsed;

    @JSONField(name = "CPUPercent")
    private String cpuPercent;

    @JSONField(name = "MemoryTotal")
    private String memoryTotal;

    @JSONField(name = "DownLoadSpeed")
    private String downloadSpeed;

    @JSONField(name = "UploadSpeed")
    private String uploadSpeed;

    @JSONField(name = "DiskReadBytes")
    private String diskReadBytes;

    @JSONField(name = "DiskWriteBytes")
    private String diskWriteBytes;
}
