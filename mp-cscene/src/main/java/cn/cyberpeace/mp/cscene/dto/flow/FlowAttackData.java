package cn.cyberpeace.mp.cscene.dto.flow;

import cn.cyberpeace.mp.sceneplugincollection.bo.AttackInfo;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

@Data
@NoArgsConstructor
public class FlowAttackData {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExcelProperty("主机名称")
    private String targetNodeName;

    @ExcelProperty("类型")
    private String attackType;

    @ExcelProperty("时间")
    private String time;

    @ExcelProperty("级别")
    private String severity;

    @ExcelProperty("协议")
    private String protocolTypes;

    @ExcelProperty("源ip")
    private String sourceIp;

    @ExcelProperty("源端口")
    private String sourcePort;

    @ExcelProperty("目的ip")
    private String targetIp;

    @ExcelProperty("目的端口")
    private String targetPort;

    @ExcelProperty("文件")
    private String size;



    public FlowAttackData(FlowAttackDto flowAttackDto) {
        this.targetNodeName = flowAttackDto.getTargetNodeName();
        this.attackType = Optional.ofNullable(flowAttackDto.getAttackInfo()).map(AttackInfo::getAttackType).orElse("");
        this.time = flowAttackDto.getTime().format(DATE_TIME_FORMATTER);
        int severity = Optional.ofNullable(flowAttackDto.getAttackInfo()).map(AttackInfo::getSeverity).orElse(0);
        this.severity = severity == 1 ? "高" : severity == 2 ? "中" : severity == 3 ? "低" : "未知";
        this.protocolTypes = String.join(",", Optional.ofNullable(flowAttackDto.getProtocol()).orElse(new ArrayList<>()));
        this.sourceIp = flowAttackDto.getSourceIp();
        this.sourcePort = flowAttackDto.getSourcePort();
        this.targetIp = flowAttackDto.getTargetIp();
        this.targetPort = flowAttackDto.getTargetPort();
        long size = Optional.ofNullable(flowAttackDto.getSize()).orElse(0L);
        var fileSize = ((double) size / 1024.0);
        // 保留value 小数点后两位
        this.size = String.format("%.2f", fileSize) + "KB";
    }


}
