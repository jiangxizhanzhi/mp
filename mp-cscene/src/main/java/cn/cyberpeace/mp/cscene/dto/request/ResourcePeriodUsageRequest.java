package cn.cyberpeace.mp.cscene.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: 资源使用请求体
 * @Author zhouxinghuai
 * @Date：2021/11/24 14:22
 */
@Data
public class ResourcePeriodUsageRequest extends ResourceUsageRequest {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer segmentCount;

}
