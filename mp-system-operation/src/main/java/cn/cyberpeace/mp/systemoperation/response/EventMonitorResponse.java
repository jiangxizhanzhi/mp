package cn.cyberpeace.mp.systemoperation.response;

import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeDeserializer;
import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: 事件监控返回
 * @Autho qiaokang
 * @Date：2021/7/9 11:10
 */
@Data
@ApiModel("事件监控返回")
public class EventMonitorResponse {

    @ApiModelProperty("节点名称")
    private String node_name;

    @ApiModelProperty("文件事件动作类型")
    private String action;

    @ApiModelProperty("文件事件类型,可用值:FILE,REGISTRY")
    private String file_event_type;

    @ApiModelProperty("文件路径")
    private String file_path;

    @ApiModelProperty("文件大小")
    private String size;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty("文件时间")
    private LocalDateTime update_time;

    @ApiModelProperty("用户")
    private String user;

}
