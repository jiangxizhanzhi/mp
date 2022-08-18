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
public class InstanceBO {

    /**
     * 实例名称
     */
    private String name;

    /**
     * 节点名称
     */
    private String node;

    /**
     * IP地址
     */
    private List<String> addressList;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建者
     */
    private String userName;

    /**
     * 运行时间
     */
    private Long runningTime;

}
