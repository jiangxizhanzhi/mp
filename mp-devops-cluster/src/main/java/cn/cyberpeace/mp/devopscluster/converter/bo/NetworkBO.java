package cn.cyberpeace.mp.devopscluster.converter.bo;

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
     * 下行带宽(Kb/s)
     */
    private Long downloadSpeed;

    /**
     * 上行带宽(Kb/s)
     */
    private Long uploadSpeed;

    /**
     * 节点个数
     */
    private Integer nodeNum;

    /**
     * 创建时间
     */
    private String createdAt;
}
