package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName TaiShiRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class TaiShiRequest {

    @ApiModelProperty("场景id,多个id用逗号分隔")
    @NotNull
    private String sceneId;

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "攻击事件查询条件")
    private EventRequest attack;

    @ApiModelProperty(value = "防御事件查询条件")
    private EventRequest defense;

    @ApiModelProperty(value = "是否查询流量数据")
    private Boolean flow;

    @ApiModelProperty(value = "是否查询主机数据")
    private Boolean host;

    public Integer getPage() {
        return page == null ? 1 : page;
    }

    public Integer getPageSize() {
        return pageSize == null ? 10 : pageSize;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventRequest {

        @ApiModelProperty("事件名称")
        private List<String> eventName;

        @ApiModelProperty("开始时间, 默认当前时间往前推30分钟")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @ApiModelProperty("结束时间，默认当前时间")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;

        @ApiModelProperty(value = "攻防类型")
        private List<String> defenseTypes;

        @ApiModelProperty(value = "攻击源节点,不传默认查询全部")
        private List<String> sourceNodeIds;

        @ApiModelProperty(value = "攻击目标节点,不传默认查询全部")
        private List<String> targetNodeIds;
    }
}
