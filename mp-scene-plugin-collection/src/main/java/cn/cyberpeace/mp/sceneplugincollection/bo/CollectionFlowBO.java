package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
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
public class CollectionFlowBO {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("源节点")
    private String sourceNodeId;

    @ApiModelProperty("目的节点")
    private String targetNodeId;

    @ApiModelProperty("源节点名称")
    private String sourceNodeName;

    @ApiModelProperty("目的节点名称")
    private String targetNodeName;

    @ApiModelProperty("源IP")
    private String sourceIp;

    @ApiModelProperty("目的IP")
    private String targetIp;

    @ApiModelProperty("源Mac")
    private List<String> sourceMac;

    @ApiModelProperty("目的Mac")
    private List<String> targetMac;

    @ApiModelProperty("源端口")
    private String sourcePort;

    @ApiModelProperty("目的端口")
    private String targetPort;

    @ApiModelProperty("协议类型")
    private List<String> protocol;

    @ApiModelProperty("流量大小")
    private Long size;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("时间戳毫秒级")
    private Long timestamp;

    @ApiModelProperty("本条流量包含的攻击信息")
    private List<AttackInfo> attackInfos;

    @Getter
    @Setter
    public static class Output {
        private List<String> protocol;
        private String sourceIp;
        private String targetIp;
        private String sourcePort;
        private String targetPort;
        private Long size;
        private String time;
    }

    public Output toOutput(){
        Output output = new Output();
        output.setProtocol(this.protocol);
        output.setSourceIp(this.sourceIp);
        output.setTargetIp(this.targetIp);
        output.setSourcePort(this.sourcePort);
        output.setTargetPort(this.targetPort);
        output.setSize(this.size);

        var a = TimeUtils.localDateTimeToUtcString(this.getTime());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(a, inputFormatter);
        LocalDateTime localDateTime = offsetDateTime.atZoneSameInstant(ZoneId.of("Asia/Shanghai")).toLocalDateTime();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String aaa = localDateTime.format(outputFormatter);
        output.setTime(aaa);

        return output;
    }
}
