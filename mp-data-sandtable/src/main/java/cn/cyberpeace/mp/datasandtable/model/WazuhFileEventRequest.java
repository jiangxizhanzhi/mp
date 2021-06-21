package cn.cyberpeace.mp.datasandtable.model;

import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeDeserializer;
import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WazuhFileEventRequest {

    /**
     * 查询开始时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty("开始时间-yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 查询
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty("结束时间-yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 查询条数默认限制
     */
    @ApiModelProperty("预警消息默认展示500条")
    private Integer pageSize = 500;

    /**
     * 分页页码
     */
    private Integer pageNum = 1;

    /**
     * wazuh agentId
     */
    private String agentId;

    /**
     * file 、registry_value
     */
    private String type;
}
