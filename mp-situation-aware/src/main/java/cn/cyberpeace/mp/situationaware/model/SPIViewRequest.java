package cn.cyberpeace.mp.situationaware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 聚合分析的实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SPIViewRequest {

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long stopTime;

    /**
     * 分组id
     */
    @NotNull
    private String groupId;
}
