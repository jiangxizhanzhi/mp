package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.ElasticIpModel;
import lombok.*;

import java.util.List;

/**
 * 弹性IP分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ElasticIpCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 弹性IP列表
     */
    private List<ElasticIpModel> floatingips;
}
