package cn.cyberpeace.mp.devops.cluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkBO {

    /**
     * 网络名称
     */
    private String name;

    /**
     * 网络类型
     */
    private String networkType;

    /**
     * 状态
     */
    private String status;

    /**
     * 已连接子网
     */
    private List<NetworkSubnetBO> subnets;

    /**
     * 创建时间
     */
    private String createdAt;
}
