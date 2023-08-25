package cn.cyberpeace.mp.aggregation.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/5/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("所有活动靶标信息")
public class AllActiveResponse {
    private Integer vm;
    private Integer container;
    private Integer entity;
}