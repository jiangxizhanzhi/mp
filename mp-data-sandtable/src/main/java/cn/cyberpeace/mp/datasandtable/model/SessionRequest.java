package cn.cyberpeace.mp.datasandtable.model;

import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeDeserializer;
import cn.cyberpeace.mp.base.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
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
     * 查询条件需要的分组
     */
    @NotNull(message = "分组id列表不能为空")
    private List<String> groupList;

    private String sourceIp;

    private String srcPort;

    private String targetIp;

    private String dstPort;

    /**
     * 查询条数默认限制
     */
    @ApiModelProperty("不走分页场景的时候默认返回100条数据")
    private Integer pageSize = 100;

    /**
     * 分页页码
     */
    private Integer pageNum = 1;
}
