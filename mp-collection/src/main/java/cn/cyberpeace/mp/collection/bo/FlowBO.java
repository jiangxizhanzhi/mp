package cn.cyberpeace.mp.collection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson.annotation.JSONField;

@Data
public class FlowBO {

    @ApiModelProperty(value = "时间")
    @JSONField(name = "@timestamp")
    private LocalDateTime time;

    @ApiModelProperty(value = "源IP")
    private String sourceIp;

    @ApiModelProperty(value = "目的IP")
    private String targetIp;

    @ApiModelProperty(value = "攻击方式")
    private String attackType;

    @ApiModelProperty(value = "告警级别")
    private String severity;

    @ApiModelProperty(value = "协议")
    private String protocol;

    @JSONField(name = "protocol")
    private List<String> protocols;

    private IP source;

    private IP destination;

    private Suricata suricata;

    /**
     * 填充属性值
     */
    public void fillProperties() {
        if (source != null) {
            sourceIp = source.getIp();
        }
        if (destination != null) {
            targetIp = destination.getIp();
        }
        if (suricata != null) {
            List<String> category = suricata.getCategory();
            if (CollectionUtils.isNotEmpty(category)) {
                attackType = category.get(0);
            }
            List<String> severity = suricata.getSeverity();
            if (CollectionUtils.isNotEmpty(severity)) {
                this.severity = severity.get(0);
            }
        }
        if (CollectionUtils.isNotEmpty(protocols)) {
            protocol = protocols.get(0);
        }
    }

    @Data
    public class IP {
        private String ip;
    }

    @Data
    public class Suricata {
        private List<String> category;
        private List<String> severity;
    }
}
