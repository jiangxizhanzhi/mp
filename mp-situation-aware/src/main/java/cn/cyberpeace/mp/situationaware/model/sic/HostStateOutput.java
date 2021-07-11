package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


@Data
public class HostStateOutput {

    @JSONField(name = "MemoryFree")
    private String memoryFree;

    @JSONField(name = "CPUCount")
    private String CPUCount;

    @JSONField(name = "CPUFreq")
    private String CPUFreq;

    @JSONField(name = "DiskTotal")
    private String diskTotal;

    @JSONField(name = "MemoryUsedPercent")
    private String memoryUsedPercent;

    @JSONField(name = "DiskUsed")
    private String diskUsed;

    @JSONField(name = "CPUPercent")
    private String CPUPercent;

    @JSONField(name = "MemoryTotal")
    private String memoryTotal;

    @JSONField(name = "DownLoadSpeed")
    private String downloadSpeed;

    @JSONField(name = "MaxDownLoadSpeed")
    private String maxDownloadSpeed;

    @JSONField(name = "DownLoadPercent")
    private String downloadPercent;

    @JSONField(name = "UploadSpeed")
    private String uploadSpeed;

    @JSONField(name = "MaxUploadSpeed")
    private String maxUploadSpeed;

    @JSONField(name = "UploadPercent")
    private String uploadPercent;

    @JSONField(name = "MaxNetSpeed")
    private String maxNetSpeed;

    @JSONField(name = "DiskReadBytes")
    private String diskReadBytes;

    @JSONField(name = "DiskWriteBytes")
    private String diskWriteBytes;

    @JSONField(name = "DiskIOPercent")
    private String diskIoPercent;
}
