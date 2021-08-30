package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeDeserializer;
import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: 监控返回
 * @Author qiaokang
 * @Date：2021/7/9 11:10
 */
@Data
@ApiModel("流量监控返回")
public class FlowMonitorResponse {

    @ApiModelProperty("流量类型")
    private String protocol_type;

    @ApiModelProperty("源ip")
    private String source_ip;

    @ApiModelProperty("源mac")
    private String source_mac;

    @ApiModelProperty("源端口")
    private String src_port;

    @ApiModelProperty("目标ip")
    private String target_ip;

    @ApiModelProperty("目标mac")
    private String target_mac;

    @ApiModelProperty("目标端口")
    private String dst_port;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty("时间")
    private LocalDateTime timestamp;

    @ApiModelProperty("流量大小")
    private double total_bytes;

}
