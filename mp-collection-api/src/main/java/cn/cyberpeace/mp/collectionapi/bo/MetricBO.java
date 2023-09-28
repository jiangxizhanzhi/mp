package cn.cyberpeace.mp.collectionapi.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @author litian
 * @date 2023年09月28 11:13
 */
@Data
@Builder
public class MetricBO {
    private String deviceType;
    private String metric;
    private Integer value;
}
