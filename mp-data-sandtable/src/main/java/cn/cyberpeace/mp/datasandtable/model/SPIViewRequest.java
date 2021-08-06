package cn.cyberpeace.mp.datasandtable.model;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
