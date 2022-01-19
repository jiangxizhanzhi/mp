package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hypervisor implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * 主机IP
     */
    private String hostIp;

    /**
     * 主机名称
     */
    private String hostname;

    /**
     * 类型
     */
    private String type;

    /**
     * CPU核数
     */
    private Integer cpu;

    /**
     * 内存大小
     */
    private Long ram;

    /**
     * 磁盘大小
     */
    private Long disk;

    /**
     * 已使用CPU核数
     */
    private Integer cpuUsed;

    /**
     * 已使用内存大小
     */
    private Long ramUsed;

    /**
     * 已使用磁盘大小
     */
    private Long diskUsed;

    /**
     * 运行中的虚拟机数量
     */
    private Integer runningVms;

    /**
     * 服务器
     */
    private List<Server> servers;

    /**
     * 服务状态
     */
    private String status;

    /**
     * 节点管理
     */
    private String state;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Server implements Serializable{

        /**
         * UUID
         */
        private String uuid;

        /**
         * 名称
         */
        private String name;
    }
}

