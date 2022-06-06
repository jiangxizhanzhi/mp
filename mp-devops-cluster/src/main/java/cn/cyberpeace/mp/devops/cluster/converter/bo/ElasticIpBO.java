package cn.cyberpeace.mp.devops.cluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElasticIpBO {

    /**
     * 固定IP
     */
    private String fixedIpAddress;

    /**
     * 弹性IP
     */
    private String floatIpAddress;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private String createdAt;
}
