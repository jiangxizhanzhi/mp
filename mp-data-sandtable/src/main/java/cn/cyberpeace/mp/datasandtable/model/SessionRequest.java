package cn.cyberpeace.mp.datasandtable.model;

import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeDeserializer;
import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionRequest {

    /**
     * 查询开始时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty("开始时间-yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 查询
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty("结束时间-yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 查询条件需要的分组
     */
    private List<String> groupList;

    private String sourceIp;

    private String srcPort;

    private String targetIp;

    /**
     * 查询条数默认限制
     */
    private Integer pageSize = 100;

    /**
     * 分页页码
     */
    private Integer pageNum = 1;
}
