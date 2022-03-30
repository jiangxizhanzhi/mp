package cn.cyberpeace.mp.situationaware.model.sic;

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
    private String CPUCount;

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


}
