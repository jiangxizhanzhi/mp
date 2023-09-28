package cn.cyberpeace.mp.collectionapi.vo;

import cn.cyberpeace.mp.collectionapi.bo.MetricBO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


/**
 * @author litian
 * @date 2023年09月28 9:39
 */
@Data
@AllArgsConstructor
public class MetricVO {
    private List<MetricBO> metrics;
}
