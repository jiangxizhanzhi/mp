package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhHostStateModel {
    @JSONField(name = "MemoryTotal")
    @ApiModelProperty("总内存")
    private long memoryTotal;

    @JSONField(name = "MemoryFree")
    @ApiModelProperty("空闲内存")
    private long memoryFree;

    @JSONField(name = "MemoryPercent")
    @ApiModelProperty("内存利用率")
    private double memoryPercent;

    @JSONField(name = "CPUTotalPercent")
    @ApiModelProperty("CPU利用率")
    private double cpuTotalPercent;

    @JSONField(name = "CPUFreq")
    @ApiModelProperty("CPU主频")
    private double cpuFreq;

    @JSONField(name = "CPUCount")
    @ApiModelProperty("CPU核数")
    private int cpuCount;

    @ApiModelProperty("磁盘总空间")
    private long diskTotal;

    @ApiModelProperty("磁盘已用空间")
    private long diskUsed;

    @ApiModelProperty("磁盘剩余空间")
    private long diskUnused;

    @ApiModelProperty("磁盘使用率")
    private double diskPercent;

    @ApiModelProperty("下载速度")
    private String downloadSpeed;

    @ApiModelProperty("单个网卡最大下载速度")
    private String maxDownloadSpeed;

    @ApiModelProperty("下行带宽/总带宽占比")
    private Double downloadPercent;

    @ApiModelProperty("上传速度")
    private String uploadSpeed;

    @ApiModelProperty("单个网卡最大上传速度")
    private String maxUploadSpeed;

    @ApiModelProperty("上行带宽/总带宽占比")
    private Double uploadPercent;

    @ApiModelProperty("最大网卡速率")
    private long maxNetSpeed;

    @ApiModelProperty("磁盘读取速度 Bytes")
    private long diskReadBytes;

    @ApiModelProperty("磁盘写速度 Bytes")
    private long diskWriteBytes;

    @ApiModelProperty("磁盘IO利用率")
    private double diskIoPercent;

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("agentName")
    private String agentName;

    private String agentIp;

    @ApiModelProperty("macId")
    private List<String> macIds;

    @JSONField(name = "timestamp")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    public Output toOuput(){
        Output output = new Output();
        output.setAgentIp(agentIp);
        output.setCpuPercent(cpuTotalPercent);
        output.setDiskUsePercent(diskPercent);
        output.setMemoryUsePercent(memoryPercent);

        var a = TimeUtils.localDateTimeToUtcString(this.getTime());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(a, inputFormatter);
        LocalDateTime localDateTime = offsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String aaa = localDateTime.format(outputFormatter);
        output.setTime(aaa);

        return output;
    }


    @Getter
    @Setter
    public static class Output {
        private String agentIp;
        private Double cpuPercent;
        private Double memoryUsePercent;
        private Double diskUsePercent;
        private String time;
    }
}
